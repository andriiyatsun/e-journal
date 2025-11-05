// File: StudentGroupAdminService.java

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

    // ... (існуючий метод updateGroupDetails) ...

    /**
     * ✅ ОНОВЛЕНО: Оновлює теми, примітки та скориговані дати для списку записів КТП.
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

            // Якщо нова дата = плановій даті, зберігаємо null
            if (lesson.getPlannedDate() != null && lesson.getPlannedDate().isEqual(newCorrectedDate)) {
                lesson.setCorrectedDate(null);
            } else {
                lesson.setCorrectedDate(newCorrectedDate); // Зберігаємо нову дату
            }
        }

        lessonPlanRepo.saveAll(lessonsToUpdate);
    }
}