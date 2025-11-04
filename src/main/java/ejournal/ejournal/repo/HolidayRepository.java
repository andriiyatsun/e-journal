package ejournal.ejournal.repo;

import ejournal.ejournal.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    /**
     * Знаходить всі свята для конкретного навчального року.
     */
    List<Holiday> findAllByAcademicYearId(Long yearId);
}

