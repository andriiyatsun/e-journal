package ejournal.ejournal.repo;

import ejournal.ejournal.model.VacationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacationPeriodRepository extends JpaRepository<VacationPeriod, Long> {

    /**
     * Знаходить всі канікули для конкретного навчального року.
     */
    List<VacationPeriod> findAllByAcademicYearId(Long yearId);
}
