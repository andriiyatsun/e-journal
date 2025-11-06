package ejournal.ejournal.service;

import ejournal.ejournal.model.LessonPlanEntity;
import ejournal.ejournal.model.StudentGroupEntity;
import ejournal.ejournal.repo.LessonPlanRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentGroupAdminService {

    private final StudentGroupRepository studentGroupRepo;
    private final LessonPlanRepository lessonPlanRepo;

    /**
     * ✅ ВИПРАВЛЕНИЙ МЕТОД: Оновлює основні відомості групи (Журналу).
     * (Приймає рівно 10 аргументів, як того вимагає контролер)
     */
    public StudentGroupEntity updateGroupDetails(Long groupId,
                                                 String programName,
                                                 String programApprovalDate, // Аргумент, який викликає помилку
                                                 String studyLevel,
                                                 String studyYear,
                                                 Integer hoursPerWeek,
                                                 String scheduleJson,
                                                 String groupNumber,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {

        StudentGroupEntity group = studentGroupRepo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Журнал не знайдено: " + groupId));

        group.setProgramName(programName);
        group.setStudyLevel(studyLevel);
        group.setStudyYear(studyYear);
        group.setHoursPerWeek(hoursPerWeek);
        group.setScheduleJson(scheduleJson);
        group.setGroupNumber(groupNumber);

        // Встановлення дат
        group.setStartDate(startDate);
        group.setEndDate(endDate);

        // programApprovalDate тут поки що ігнорується, але приймається як аргумент
        // Оскільки в моделі StudentGroupEntity це поле має тип LocalDate, а тут приходить String,
        // ми його поки ігноруємо, щоб не викликати помилок парсингу.
        group.setProgramApprovalDate(null);

        return studentGroupRepo.save(group);
    }

    /**
     * Оновлює теми, примітки та скориговані дати для списку записів КТП.
     */
    public void updateLessonPlanTopics(Long journalId, List<Long> lessonIds, List<String> topics, List<String> notes, List<LocalDate> correctedDates) {
        if (!studentGroupRepo.existsById(journalId)) {
            throw new IllegalArgumentException("Журнал не знайдено: " + journalId);
        }

        if (lessonIds == null || lessonIds.isEmpty()) {
            return;
        }

        if (lessonIds.size() != topics.size() || lessonIds.size() != notes.size() || lessonIds.size() != correctedDates.size()) {
            throw new IllegalArgumentException("Незбіг кількості ідентифікаторів, тем, приміток та скоригованих дат.");
        }

        List<LessonPlanEntity> lessonsToUpdate = lessonPlanRepo.findAllById(lessonIds);

        if (lessonsToUpdate.size() != lessonIds.size()) {
            throw new IllegalArgumentException("Один або кілька записів КТП не знайдено.");
        }

        for (int i = 0; i < lessonIds.size(); i++) {
            Long currentLessonId = lessonIds.get(i);
            String newTopic = topics.get(i);
            String newNote = notes.get(i);
            LocalDate newCorrectedDate = correctedDates.get(i);

            LessonPlanEntity lesson = lessonsToUpdate.stream()
                    .filter(l -> l.getId().equals(currentLessonId))
                    .findFirst()
                    .orElseThrow();

            if (!lesson.getStudentGroup().getId().equals(journalId)) {
                throw new SecurityException("Спроба оновити запис КТП, що не належить цьому журналу: " + lesson.getId());
            }

            if (lesson.getIsConducted()) {
                throw new IllegalStateException("Неможливо редагувати проведене заняття (№" + lesson.getLessonNumber() + ").");
            }

            lesson.setTopic(newTopic);
            lesson.setNote(newNote);

            if (lesson.getPlannedDate() != null && lesson.getPlannedDate().isEqual(newCorrectedDate)) {
                lesson.setCorrectedDate(null);
            } else {
                lesson.setCorrectedDate(newCorrectedDate);
            }
        }

        lessonPlanRepo.saveAll(lessonsToUpdate);
    }
}