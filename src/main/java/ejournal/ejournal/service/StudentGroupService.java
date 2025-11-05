package ejournal.ejournal.service;

import ejournal.ejournal.model.AcademicYearEntity;
import ejournal.ejournal.model.DepartmentEntity;
import ejournal.ejournal.model.StudentGroupEntity;
import ejournal.ejournal.model.SubjectEntity;
import ejournal.ejournal.model.UserEntity; // ✅ Імпортуємо
import ejournal.ejournal.repo.AcademicYearRepository;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.repo.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet; // ✅ Імпортуємо
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentGroupService {

    private final AcademicYearRepository academicYearRepo;
    private final DepartmentRepository departmentRepo;
    private final SubjectRepository subjectRepo;
    private final StudentGroupRepository studentGroupRepo;

    /**
     * ✅ ОНОВЛЕНО:
     * Створює "Предмет" (Subject) і "Журнал" (StudentGroup)
     * для КОНКРЕТНОГО обраного навчального року.
     *
     * @param subjectName   Назва нового предмету (напр., "3D-друк")
     * @param departmentId  ID відділу
     * @param academicYearId ID обраного навчального року
     * @return новостворений Журнал (StudentGroup)
     */
    @Transactional
    public StudentGroupEntity createSubjectAndJournal(String subjectName, Long departmentId, Long academicYearId) {
        // 1. Знаходимо відділ
        DepartmentEntity department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Відділ не знайдено: " + departmentId));

        // 2. ✅ ОНОВЛЕНО: Знаходимо ОБРАНИЙ навчальний рік
        AcademicYearEntity chosenYear = academicYearRepo.findById(academicYearId)
                .orElseThrow(() -> new IllegalArgumentException("Обраний навчальний рік не знайдено."));

        // 3. Перевірка на дублікат (як і раніше)
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

        // 5. Створюємо "Журнал" (StudentGroup)
        StudentGroupEntity newJournal = StudentGroupEntity.builder()
                .subject(newSubject)
                .academicYear(chosenYear) // ✅ Використовуємо обраний рік
                .name(subjectName + " (" + chosenYear.getName() + ")") // ✅ Використовуємо назву обраного року
                .build();

        return studentGroupRepo.save(newJournal);
    }

    /**
     * ✅ НОВИЙ МЕТОД:
     * Видаляє Журнал (StudentGroup) за його ID.
     */
    @Transactional
    public void deleteJournal(Long journalId) {
        StudentGroupEntity journal = studentGroupRepo.findById(journalId)
                .orElseThrow(() -> new IllegalArgumentException("Журнал з ID " + journalId + " не знайдено."));

        // 1. Розриваємо зв'язок з викладачами (Teachers)
        // Ми повинні очистити колекцію на "володіючій" стороні (StudentGroup)
        // або на інверсній (User), щоб розірвати зв'язок у join-table.
        // Очищення 'journal.teachers' - найпростіший спосіб.
        for (UserEntity teacher : new HashSet<>(journal.getTeachers())) {
            teacher.getTeachingGroups().remove(journal);
            journal.getTeachers().remove(teacher);
        }

        // 2. Розриваємо зв'язок з Предметом (Subject)
        // Це потрібно, щоб 'orphanRemoval=true' у SubjectEntity
        // не спрацював неправильно (хоча він і на inverse-side).
        // Це також гарантує, що Subject не буде видалено.
        if (journal.getSubject() != null && journal.getSubject().getStudentGroups() != null) {
            journal.getSubject().getStudentGroups().remove(journal);
        }
        journal.setSubject(null);

        // 3. Видаляємо сам журнал
        // Зв'язок з AcademicYear не є володіючим і не має каскадів,
        // тому він розірветься автоматично.
        studentGroupRepo.delete(journal);
    }
}