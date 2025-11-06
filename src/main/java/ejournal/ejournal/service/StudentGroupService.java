package ejournal.ejournal.service;

import ejournal.ejournal.model.*;
import ejournal.ejournal.repo.AcademicYearRepository;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.repo.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set; // ✅ Імпорт

@Service
@RequiredArgsConstructor
public class StudentGroupService {

    private final AcademicYearRepository academicYearRepo;
    private final DepartmentRepository departmentRepo;
    private final SubjectRepository subjectRepo;
    private final StudentGroupRepository studentGroupRepo;

    /**
     * ✅ НОВИЙ МЕТОД: Створює лише "Предмет" (шаблон) у відділі.
     */
    @Transactional
    public SubjectEntity createSubject(String subjectName, Long departmentId) {
        // ... (Код без змін)
        DepartmentEntity department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Відділ не знайдено: " + departmentId));
        subjectRepo.findByDepartmentAndName(department, subjectName).ifPresent(s -> {
            throw new IllegalArgumentException(
                    "Предмет з назвою \"" + subjectName + "\" вже існує у цьому відділі.");
        });
        SubjectEntity newSubject = SubjectEntity.builder()
                .name(subjectName)
                .department(department)
                .build();
        return subjectRepo.save(newSubject);
    }


    /**
     * ✅ ОНОВЛЕНИЙ МЕТОД: Створює "Журнал" для існуючого предмета та навчального року.
     */
    @Transactional
    public StudentGroupEntity createJournal(Long subjectId, Long academicYearId) {
        // ... (Код без змін)
        SubjectEntity subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не знайдено: " + subjectId));
        AcademicYearEntity chosenYear = academicYearRepo.findById(academicYearId)
                .orElseThrow(() -> new IllegalArgumentException("Обраний навчальний рік не знайдено."));
        studentGroupRepo.findBySubjectIdAndAcademicYearId(subjectId, academicYearId).ifPresent(sg -> {
            throw new IllegalArgumentException(
                    "Журнал для предмету \"" + subject.getName() + "\" у " + chosenYear.getName() + " році вже існує.");
        });
        StudentGroupEntity newJournal = new StudentGroupEntity(
                subject.getName() + " (" + chosenYear.getName() + ")", // name
                null, null, null, null, null, null, null,
                chosenYear.getStartDate(),
                chosenYear.getEndDate(),
                chosenYear,
                subject
        );
        return studentGroupRepo.save(newJournal);
    }


    /**
     * ✅ НОВИЙ МЕТОД: Видаляє Предмет і каскадно видаляє всі пов'язані з ним Журнали.
     */
    @Transactional
    public void deleteSubject(Long subjectId) {
        // 1. Знаходимо "Предмет"
        SubjectEntity subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не знайдено: " + subjectId));

        // 2. Створюємо копію списку журналів, щоб уникнути ConcurrentModificationException
        Set<StudentGroupEntity> journalsToDelete = new HashSet<>(subject.getStudentGroups());

        // 3. Видаляємо кожен журнал, використовуючи вже існуючу безпечну логіку
        // (Це гарантує, що всі зв'язки ManyToMany з викладачами/учнями будуть розірвані)
        for (StudentGroupEntity journal : journalsToDelete) {
            deleteJournal(journal.getId());
        }

        // 4. Після видалення всіх дочірніх журналів, видаляємо сам "Предмет"
        subjectRepo.delete(subject);
    }


    /**
     * Видаляє Журнал (StudentGroup) за його ID.
     */
    @Transactional
    public void deleteJournal(Long journalId) {
        // ... (Код без змін)
        StudentGroupEntity journal = studentGroupRepo.findById(journalId)
                .orElseThrow(() -> new IllegalArgumentException("Журнал з ID " + journalId + " не знайдено."));

        for (UserEntity teacher : new HashSet<>(journal.getTeachers())) {
            teacher.getTeachingGroups().remove(journal);
            journal.getTeachers().remove(teacher);
        }
        for (StudentEntity student : new HashSet<>(journal.getStudents())) {
            student.getStudentGroups().remove(journal);
            journal.getStudents().remove(student);
        }
        if (journal.getSubject() != null && journal.getSubject().getStudentGroups() != null) {
            journal.getSubject().getStudentGroups().remove(journal);
        }
        journal.setSubject(null);
        studentGroupRepo.delete(journal);
    }
}