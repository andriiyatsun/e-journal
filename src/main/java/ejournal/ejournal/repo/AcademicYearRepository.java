package ejournal.ejournal.repo;

import ejournal.ejournal.model.AcademicYearEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYearEntity, Long> {

    /**
     * Знаходить навчальний рік за його назвою.
     */
    Optional<AcademicYearEntity> findByName(String name);
}