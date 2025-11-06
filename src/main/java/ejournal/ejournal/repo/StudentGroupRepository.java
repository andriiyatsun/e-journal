package ejournal.ejournal.repo;

import ejournal.ejournal.model.StudentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional; // ✅ Імпорт

public interface StudentGroupRepository extends JpaRepository<StudentGroupEntity, Long> {

    /**
     * Знаходить всі журнали (групи) для конкретного відділу,
     * дивлячись на зв'язок "через" предмет.
     * Це буде корисно для вкладки "Журнали" в адмін-панелі.
     */
    @Query("SELECT sg FROM StudentGroupEntity sg WHERE sg.subject.department.id = :departmentId")
    List<StudentGroupEntity> findAllByDepartmentId(Long departmentId);

    /**
     * ✅ НОВИЙ МЕТОД: Знаходить журнал за ID предмета та ID навчального року.
     * Використовується для перевірки на унікальність (Один предмет - один журнал на рік).
     */
    Optional<StudentGroupEntity> findBySubjectIdAndAcademicYearId(Long subjectId, Long academicYearId);
}