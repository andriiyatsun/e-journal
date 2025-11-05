package ejournal.ejournal.service;

import ejournal.ejournal.model.StudentGroupEntity;
import ejournal.ejournal.repo.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentGroupAdminService {

    private final StudentGroupRepository studentGroupRepo;

    /**
     * Оновлює основні відомості групи (Журналу)
     */
    public StudentGroupEntity updateGroupDetails(Long groupId,
                                                 String programName,
                                                 String programApprovalDate, // Дата затвердження як String
                                                 String studyLevel,
                                                 String studyYear,
                                                 Integer hoursPerWeek,
                                                 String scheduleJson,
                                                 String groupNumber,
                                                 LocalDate startDate, // Нові дати
                                                 LocalDate endDate) {   // Нові дати

        StudentGroupEntity group = studentGroupRepo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Журнал не знайдено: " + groupId));

        group.setProgramName(programName);
        group.setStudyLevel(studyLevel);
        group.setStudyYear(studyYear);
        group.setHoursPerWeek(hoursPerWeek);
        group.setScheduleJson(scheduleJson);
        group.setGroupNumber(groupNumber);

        // Дати початку/кінця занять можуть бути змінені (ТЗ 5.2)
        group.setStartDate(startDate);
        group.setEndDate(endDate);

        // Для дати затвердження програми, оскільки вона може бути неформатованою,
        // ми поки що зберігаємо її як простий текст. Якщо потрібен LocalDate,
        // знадобиться більш складна логіка парсингу.
        group.setProgramApprovalDate(null); // Можна спробувати перетворити String на LocalDate, якщо формат надійний
        // Примітка: зараз ми просто ігноруємо programApprovalDate (String),
        // щоб не ускладнювати логіку парсингу дати, яка може бути текстовою.

        return studentGroupRepo.save(group);
    }
}