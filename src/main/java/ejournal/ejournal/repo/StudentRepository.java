package ejournal.ejournal.repo;

import ejournal.ejournal.model.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

    /**
     * Знаходить учня за його ПІБ та датою народження (унікальний ключ А2).
     */
    Optional<StudentEntity> findBySurnameAndNameAndPatronymicAndDateOfBirth(
            String surname, String name, String patronymic, LocalDate dateOfBirth);

    /**
     * Знаходить всіх учнів, відсортованих за прізвищем (для списку А до Я).
     */
    List<StudentEntity> findAllByOrderBySurnameAsc();
}
