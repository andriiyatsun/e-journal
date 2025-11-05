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
                // ❌ 'isActive' видалено
                .build();
        return academicYearRepo.save(year);
    }

    @Transactional
    public AcademicYearEntity updateAcademicYear(Long yearId, String name, LocalDate startDate, LocalDate endDate) {
        // ... (код залишається без змін)
        AcademicYearEntity year = academicYearRepo.findById(yearId)
                .orElseThrow(() -> new IllegalArgumentException("Навчальний рік не знайдено: " + yearId));
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
        academicYearRepo.deleteById(yearId);
    }

    // ❌ Метод 'setActiveAcademicYear' видалено

    // --- Holidays ---
    // ... (код залишається без змін) ...
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
    // ... (решта коду) ...
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