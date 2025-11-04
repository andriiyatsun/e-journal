package ejournal.ejournal.service;

import ejournal.ejournal.model.AcademicYearEntity;
import ejournal.ejournal.model.GlobalSetting;
import ejournal.ejournal.model.Holiday;
import ejournal.ejournal.model.VacationPeriod;
import ejournal.ejournal.repo.AcademicYearRepository;
import ejournal.ejournal.repo.GlobalSettingRepository;
import ejournal.ejournal.repo.HolidayRepository;
import ejournal.ejournal.repo.VacationPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервіс для керування "Ядром" - Навчальними роками,
 * канікулами, святами та глобальними налаштуваннями.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CalendarAdminService {

    private final AcademicYearRepository academicYearRepo;
    private final HolidayRepository holidayRepo;
    private final VacationPeriodRepository vacationRepo;
    private final GlobalSettingRepository settingRepo;

    // --- Academic Year ---

    @Transactional
    public AcademicYearEntity createAcademicYear(String name, LocalDate startDate, LocalDate endDate) {
        academicYearRepo.findByName(name).ifPresent(y -> {
            throw new IllegalArgumentException("Навчальний рік з назвою '" + name + "' вже існує.");
        });
        AcademicYearEntity year = AcademicYearEntity.builder()
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .isActive(false) // Нові роки ніколи не активні за замовчуванням
                .build();
        return academicYearRepo.save(year);
    }

    @Transactional
    public AcademicYearEntity updateAcademicYear(Long yearId, String name, LocalDate startDate, LocalDate endDate) {
        AcademicYearEntity year = academicYearRepo.findById(yearId)
                .orElseThrow(() -> new IllegalArgumentException("Навчальний рік не знайдено: " + yearId));

        // Перевірка на унікальність назви (якщо назва змінилася)
        if (!year.getName().equals(name)) {
            academicYearRepo.findByName(name).ifPresent(y -> {
                throw new IllegalArgumentException("Навчальний рік з назвою '" + name + "' вже існує.");
            });
        }

        year.setName(name);
        year.setStartDate(startDate);
        year.setEndDate(endDate);
        return academicYearRepo.save(year);
    }

    @Transactional
    public void deleteAcademicYear(Long yearId) {
        // Канікули та свята видаляться автоматично завдяки cascade/orphanRemoval
        academicYearRepo.deleteById(yearId);
    }

    @Transactional
    public void setActiveAcademicYear(Long yearId) {
        AcademicYearEntity newActiveYear = academicYearRepo.findById(yearId)
                .orElseThrow(() -> new IllegalArgumentException("Навчальний рік не знайдено: " + yearId));

        // 1. Знаходимо старий активний рік (якщо він є) і деактивуємо його
        Optional<AcademicYearEntity> oldActiveYearOpt = academicYearRepo.findByIsActiveTrue();
        if (oldActiveYearOpt.isPresent()) {
            AcademicYearEntity oldActiveYear = oldActiveYearOpt.get();
            if (!oldActiveYear.getId().equals(yearId)) {
                oldActiveYear.setActive(false);
                academicYearRepo.save(oldActiveYear);
            }
        }

        // 2. Активуємо новий рік
        newActiveYear.setActive(true);
        academicYearRepo.save(newActiveYear);
    }

    // --- Holidays ---

    @Transactional
    public Holiday addHoliday(Long yearId, String name, LocalDate date) {
        AcademicYearEntity year = academicYearRepo.findById(yearId)
                .orElseThrow(() -> new IllegalArgumentException("Навчальний рік не знайдено: " + yearId));

        Holiday holiday = Holiday.builder()
                .name(name)
                .date(date)
                .academicYear(year)
                .build();
        return holidayRepo.save(holiday);
    }

    @Transactional
    public void deleteHoliday(Long holidayId) {
        holidayRepo.deleteById(holidayId);
    }

    // --- Vacation Periods ---

    @Transactional
    public VacationPeriod addVacation(Long yearId, String name, LocalDate startDate, LocalDate endDate) {
        AcademicYearEntity year = academicYearRepo.findById(yearId)
                .orElseThrow(() -> new IllegalArgumentException("Навчальний рік не знайдено: " + yearId));

        VacationPeriod vacation = VacationPeriod.builder()
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .academicYear(year)
                .build();
        return vacationRepo.save(vacation);
    }

    @Transactional
    public void deleteVacation(Long vacationId) {
        vacationRepo.deleteById(vacationId);
    }

    // --- Global Settings ---

    @Transactional
    public void saveGlobalSettings(Map<String, String> settings) {
        settings.forEach((key, value) -> {
            GlobalSetting setting = settingRepo.findById(key)
                    .orElse(new GlobalSetting(key, value));
            setting.setSettingValue(value);
            settingRepo.save(setting);
        });
    }

    public List<GlobalSetting> getAllGlobalSettings() {
        return settingRepo.findAll();
    }
}
