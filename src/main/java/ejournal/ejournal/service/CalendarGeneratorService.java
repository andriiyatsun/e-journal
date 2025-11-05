package ejournal.ejournal.service;

import ejournal.ejournal.model.AcademicYearEntity;
import ejournal.ejournal.model.StudentGroupEntity;
import ejournal.ejournal.model.Holiday;
import ejournal.ejournal.model.VacationPeriod;
import ejournal.ejournal.model.LessonPlanEntity;
import ejournal.ejournal.repo.LessonPlanRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.util.ScheduleParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarGeneratorService {

    private final StudentGroupRepository studentGroupRepo;
    private final LessonPlanRepository lessonPlanRepo;

    /**
     * Перевіряє, чи є дана дата святковим або канікулярним днем, використовуючи
     * дані з AcademicYear (Core).
     */
    private boolean isHolidayOrVacation(LocalDate date, AcademicYearEntity academicYear) {
        // 1. Перевірка на свята
        Set<LocalDate> holidayDates = academicYear.getHolidays().stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());
        if (holidayDates.contains(date)) {
            return true;
        }

        // 2. Перевірка на канікули
        for (VacationPeriod vp : academicYear.getVacationPeriods()) {
            if (!date.isBefore(vp.getStartDate()) && !date.isAfter(vp.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Основний метод для генерації та збереження записів КТП (LessonPlan)
     * для конкретного Журналу.
     * @param studentGroupId ID журналу
     * @param defaultTopic Тема-заглушка для нових записів.
     * @return Список згенерованих записів LessonPlanEntity.
     */
    @Transactional
    public List<LessonPlanEntity> generateLessonPlanEntries(Long studentGroupId, String defaultTopic) {
        StudentGroupEntity group = studentGroupRepo.findById(studentGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Журнал не знайдено: " + studentGroupId));

        // 1. Очищаємо всі існуючі записи КТП для перезапису
        // Це важливо для коректної регенерації КТП
        group.getLessonPlans().stream()
                .filter(lp -> !lp.getIsConducted()) // Залишаємо проведені уроки (якщо не потрібно, можна видалити цей фільтр)
                .forEach(lessonPlanRepo::delete);

        // Оновлюємо колекцію у пам'яті
        group.getLessonPlans().removeIf(lp -> !lp.getIsConducted());


        // 2. Зчитуємо необхідні дані
        LocalDate startDate = group.getStartDate();
        LocalDate endDate = group.getEndDate();
        AcademicYearEntity academicYear = group.getAcademicYear();
        Map<DayOfWeek, String> schedule = ScheduleParser.parseSchedule(group.getScheduleJson());

        Integer totalHours = group.getHoursPerWeek();
        int hoursPerLesson = (totalHours != null && totalHours > 0 && !schedule.isEmpty())
                ? totalHours / schedule.size()
                : 2; // Якщо дані некоректні, припускаємо 2 години

        if (schedule.isEmpty() || startDate == null || endDate == null) {
            throw new IllegalStateException("Для генерації КТП необхідно заповнити Розклад, Дату початку та Дату закінчення у 'Основних відомостях'.");
        }
        if (academicYear == null) {
            throw new IllegalStateException("Журнал не прив'язаний до Навчального року.");
        }

        // 3. Генеруємо дати занять
        Set<LocalDate> lessonDates = new HashSet<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek currentDay = currentDate.getDayOfWeek();

            // Перевіряємо, чи день відповідає розкладу І чи не є святковим/канікулярним
            if (schedule.containsKey(currentDay) && !isHolidayOrVacation(currentDate, academicYear)) {
                lessonDates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        // 4. Сортуємо дати та створюємо LessonPlanEntity
        List<LocalDate> sortedDates = lessonDates.stream().sorted().collect(Collectors.toList());
        int lessonNumber = 1;
        List<LessonPlanEntity> newLessons = new java.util.ArrayList<>();

        for (LocalDate date : sortedDates) {
            LessonPlanEntity lesson = LessonPlanEntity.builder()
                    .studentGroup(group)
                    .lessonNumber(lessonNumber++)
                    .plannedDate(date)
                    .hours(hoursPerLesson)
                    .topic(defaultTopic)
                    .isConducted(false)
                    .build();

            newLessons.add(lesson);
        }

        // 5. Зберігаємо в базу та оновлюємо зворотний зв'язок
        List<LessonPlanEntity> savedLessons = lessonPlanRepo.saveAll(newLessons);
        group.getLessonPlans().addAll(savedLessons);
        studentGroupRepo.save(group);

        return savedLessons;
    }
}
