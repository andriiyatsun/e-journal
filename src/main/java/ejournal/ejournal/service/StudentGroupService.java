package ejournal.ejournal.service;

import ejournal.ejournal.model.*; // ✅ Змінено на імпорт всіх моделей
import ejournal.ejournal.repo.AcademicYearRepository;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.repo.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentGroupService {

    private final AcademicYearRepository academicYearRepo;
    private final DepartmentRepository departmentRepo;
    private final SubjectRepository subjectRepo;
    private final StudentGroupRepository studentGroupRepo;

    @Transactional
    public StudentGroupEntity createSubjectAndJournal(String subjectName, Long departmentId, Long academicYearId) {
        // 1. Знаходимо відділ
        DepartmentEntity department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Відділ не знайдено: " + departmentId));

        // 2. Знаходимо ОБРАНИЙ навчальний рік
        AcademicYearEntity chosenYear = academicYearRepo.findById(academicYearId)
                .orElseThrow(() -> new IllegalArgumentException("Обраний навчальний рік не знайдено."));

        // 3. Перевірка на дублікат
        subjectRepo.findByDepartmentAndName(department, subjectName).ifPresent(s -> {
            throw new IllegalArgumentException(
                    "Предмет з назвою \"" + subjectName + "\" вже існує у цьому відділі.");
        });

        // 4. Створюємо "Предмет" (шаблон)
        SubjectEntity newSubject = SubjectEntity.builder()
                .name(subjectName)
                .department(department)
                .build();
        subjectRepo.save(newSubject);

        // 5. ✅ ФІКС: Використовуємо конструктор, а не білдер, щоб обійти помилку
        StudentGroupEntity newJournal = new StudentGroupEntity(
                subjectName + " (" + chosenYear.getName() + ")", // name
                null, // programName
                null, // programApprovalDate
                null, // studyLevel
                null, // studyYear
                null, // hoursPerWeek
                null, // scheduleJson
                null, // groupNumber
                chosenYear.getStartDate(), // startDate
                chosenYear.getEndDate(),   // endDate
                chosenYear, // academicYear
                newSubject  // subject
        );

        // Поля зі зв'язками (@Builder.Default) ініціалізуються автоматично

        return studentGroupRepo.save(newJournal);
    }

    /**
     * Видаляє Журнал (StudentGroup) за його ID.
     */
    @Transactional
    public void deleteJournal(Long journalId) {
        StudentGroupEntity journal = studentGroupRepo.findById(journalId)
                .orElseThrow(() -> new IllegalArgumentException("Журнал з ID " + journalId + " не знайдено."));

        // 1. Розриваємо зв'язок з викладачами (Teachers)
        for (UserEntity teacher : new HashSet<>(journal.getTeachers())) {
            teacher.getTeachingGroups().remove(journal);
            journal.getTeachers().remove(teacher);
        }

        // 2. Розриваємо зв'язок з вихованцями (Students)
        for (StudentEntity student : new HashSet<>(journal.getStudents())) {
            student.getStudentGroups().remove(journal);
            journal.getStudents().remove(student);
        }

        // 3. Розриваємо зв'язок з Предметом (Subject)
        if (journal.getSubject() != null && journal.getSubject().getStudentGroups() != null) {
            journal.getSubject().getStudentGroups().remove(journal);
        }
        journal.setSubject(null);

        // 4. Видаляємо сам журнал
        studentGroupRepo.delete(journal);
    }
}