package ejournal.ejournal.service;

import ejournal.ejournal.model.AcademicYearEntity;
import ejournal.ejournal.model.DepartmentEntity;
import ejournal.ejournal.model.StudentGroupEntity;
import ejournal.ejournal.model.SubjectEntity;
import ejournal.ejournal.repo.AcademicYearRepository;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.repo.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional; // ✅ Імпортуємо Optional

@Service
@RequiredArgsConstructor
public class StudentGroupService {

    private final AcademicYearRepository academicYearRepo;
    private final DepartmentRepository departmentRepo;
    private final SubjectRepository subjectRepo;
    private final StudentGroupRepository studentGroupRepo;

    /**
     * "Тригер": Створює Предмет (Subject) і автоматично
     * створює Журнал (StudentGroup) для активного навчального року.
     *
     * @param subjectName  Назва нового предмету (напр., "3D-друк")
     * @param departmentId ID відділу, до якого належить предмет
     * @return новостворений Журнал (StudentGroup)
     */
    @Transactional
    public StudentGroupEntity createSubjectAndJournal(String subjectName, Long departmentId) {
        // 1. Знаходимо відділ
        DepartmentEntity department = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));

        // 2. Знаходимо АКТИВНИЙ навчальний рік (критично)
        // ✅ ВИПРАВЛЕННЯ: Викликаємо правильний метод findByIsActiveTrue()
        Optional<AcademicYearEntity> activeYearOpt = academicYearRepo.findByIsActiveTrue();

        if (activeYearOpt.isEmpty()) {
            throw new IllegalStateException("Не знайдено активного навчального року. " +
                    "Будь ласка, встановіть один рік як 'активний' у вкладці 'Календар'.");
        }
        AcademicYearEntity activeYear = activeYearOpt.get();


        // 3. Створюємо "Предмет" (шаблон)
        SubjectEntity newSubject = SubjectEntity.builder()
                .name(subjectName)
                .department(department)
                .build();
        subjectRepo.save(newSubject);

        // 4. Створюємо "Журнал" (StudentGroup)
        StudentGroupEntity newJournal = StudentGroupEntity.builder()
                .subject(newSubject)
                .academicYear(activeYear)
                // Генеруємо назву журналу, як ви просили
                .name(subjectName + " (" + activeYear.getName() + ")")
                .build();

        return studentGroupRepo.save(newJournal);
    }
}

