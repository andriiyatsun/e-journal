package ejournal.ejournal.controller;
import ejournal.ejournal.model.*;
import ejournal.ejournal.repo.LessonPlanRepository;
import ejournal.ejournal.repo.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalController {

    private final StudentGroupRepository studentGroupRepository;
    private final LessonPlanRepository lessonPlanRepository;
    private final ejournal.ejournal.repo.AchievementRepository achievementRepo;
    private final ejournal.ejournal.repo.OrgWorkRepository orgWorkRepo;
    private final ejournal.ejournal.repo.MethodicalWorkRepository methodicalWorkRepo;
    private final ejournal.ejournal.repo.RemarkRepository remarkRepo;

    private final ejournal.ejournal.repo.SafetyTopicRepository safetyTopicRepo;
    private final ejournal.ejournal.repo.IntroSafetyRepository introSafetyRepo;
    private final ejournal.ejournal.repo.SafetyAttendanceRepository safetyAttendanceRepo;
    private final ejournal.ejournal.repo.StudentRepository studentRepo;

    /**
     * Обробляє запити GET /journal/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("@journalSecurityService.canViewJournal(authentication, #id) or hasRole('ADMIN') or hasRole('HEAD')")
    @Transactional(readOnly = true)
    public String getJournalPage(@PathVariable Long id, Model model, HttpServletRequest request) {

        // 1. Знаходимо журнал (StudentGroup) в базі за ID
        var journal = studentGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Журнал не знайдено: " + id));

        // 2. Завантажуємо ВСЕ КТП
        List<LessonPlanEntity> allLessonPlans = lessonPlanRepository.findAllByStudentGroupIdOrderByLessonNumberAsc(id);

        // 3. Фільтруємо на КП-1 (Вересень - Грудень) та КП-2 (Січень - Червень)
        List<LessonPlanEntity> kp1Plans = allLessonPlans.stream()
                .filter(lp -> lp.getActualDate() != null)
                .filter(lp -> {
                    int monthValue = lp.getActualDate().getMonthValue();
                    return monthValue >= 9 && monthValue <= 12;
                })
                .collect(Collectors.toList());

        List<LessonPlanEntity> kp2Plans = allLessonPlans.stream()
                .filter(lp -> lp.getActualDate() != null)
                .filter(lp -> {
                    int monthValue = lp.getActualDate().getMonthValue();
                    return monthValue >= 1 && monthValue <= 8;
                })
                .collect(Collectors.toList());

        // 4. Кладемо базові дані у модель
        model.addAttribute("journal", journal);
        model.addAttribute("kp1Plans", kp1Plans);
        model.addAttribute("kp2Plans", kp2Plans);

        // Кладемо дані нових таблиць у модель
        model.addAttribute("achievements", achievementRepo.findAllByStudentGroupId(id));
        model.addAttribute("orgWorks", orgWorkRepo.findAllByStudentGroupIdOrderByDateAsc(id));
        model.addAttribute("methodicalWorks", methodicalWorkRepo.findAllByStudentGroupIdOrderByDateAsc(id));
        model.addAttribute("remarks", remarkRepo.findAllByStudentGroupIdOrderByDateDesc(id));

        // Перевіряємо ролі на бекенді і передаємо результат у фронтенд
        boolean canAddRemark = request.isUserInRole("HEAD") || request.isUserInRole("ROLE_HEAD") || request.isUserInRole("ADMIN") || request.isUserInRole("ROLE_ADMIN");
        model.addAttribute("canAddRemark", canAddRemark);


        boolean isHead = request.isUserInRole("HEAD") || request.isUserInRole("ROLE_HEAD");
        model.addAttribute("isHead", isHead);

        // Завантажуємо теми інструктажів
        java.util.List<ejournal.ejournal.model.SafetyTopicEntity> safetyTopics = safetyTopicRepo.findAllByStudentGroupIdOrderByDateAsc(id);
        model.addAttribute("safetyTopics", safetyTopics);

        // Завантажуємо вступний інструктаж (список ID учнів, які його пройшли)
        java.util.List<ejournal.ejournal.model.IntroSafetyEntity> introRecords = introSafetyRepo.findAllByStudentGroupId(id);
        java.util.List<Long> instructedStudentIds = new java.util.ArrayList<>();
        if (introRecords != null) { // <--- ДОДАНО ПЕРЕВІРКУ НА NULL
            instructedStudentIds = introRecords.stream().map(r -> r.getStudent().getId()).toList();
        }
        model.addAttribute("instructedStudentIds", instructedStudentIds);

        // Завантажуємо сітку відвідування інструктажів
        java.util.List<ejournal.ejournal.model.SafetyAttendanceEntity> attendances = safetyAttendanceRepo.findAllByStudentGroupId(id);
        java.util.Map<String, Boolean> attendanceMap = new java.util.HashMap<>();
        if (attendances != null) { // <--- ДОДАНО ПЕРЕВІРКУ НА NULL
            for (ejournal.ejournal.model.SafetyAttendanceEntity att : attendances) {
                attendanceMap.put(att.getSafetyTopic().getId() + "_" + att.getStudent().getId(), true);
            }
        }
        model.addAttribute("attendanceMap", attendanceMap);

        // 5. Повертаємо назву нашого HTML-файлу (лише один раз, в самому кінці)
        return "journal";
    }

    @org.springframework.web.bind.annotation.PostMapping("/{id}/add-remark")
    @PreAuthorize("hasRole('HEAD') or hasRole('ADMIN')")
    public String addRemark(@PathVariable Long id,
                            @org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
                            @org.springframework.web.bind.annotation.RequestParam String text,
                            jakarta.servlet.http.HttpServletRequest request) {

        // Знаходимо журнал
        var group = studentGroupRepository.findById(id).orElseThrow();

        // Створюємо та зберігаємо зауваження напряму
        var remark = new ejournal.ejournal.model.RemarkEntity();
        remark.setDate(date);
        remark.setText(text);
        remark.setAuthorName(request.getUserPrincipal().getName()); // Зберігає email того, хто залишив
        remark.setStudentGroup(group);

        remarkRepo.save(remark);

        return "redirect:/journal/" + id + "#remarks";
    }

    @org.springframework.web.bind.annotation.PostMapping("/{id}/add-safety-topic")
    @org.springframework.security.access.prepost.PreAuthorize("!hasRole('HEAD')")
    public String addSafetyTopic(@org.springframework.web.bind.annotation.PathVariable Long id,
                                 @org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
                                 @org.springframework.web.bind.annotation.RequestParam String content,
                                 java.security.Principal principal) {
        var group = studentGroupRepository.findById(id).orElseThrow();

        ejournal.ejournal.model.SafetyTopicEntity topic = new ejournal.ejournal.model.SafetyTopicEntity();
        topic.setStudentGroup(group);
        topic.setDate(date);
        topic.setContent(content);
        // Беремо ім'я користувача, або ставимо заглушку
        topic.setInstructorName(principal != null ? principal.getName() : "Викладач");

        safetyTopicRepo.save(topic);
        return "redirect:/journal/" + id + "#safety";
    }

    @org.springframework.web.bind.annotation.PostMapping("/{id}/save-intro-safety")
    @org.springframework.security.access.prepost.PreAuthorize("!hasRole('HEAD')")
    @org.springframework.transaction.annotation.Transactional
    public String saveIntroSafety(@org.springframework.web.bind.annotation.PathVariable Long id,
                                  @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate introDate,
                                  @org.springframework.web.bind.annotation.RequestParam(required = false) String instructor,
                                  @org.springframework.web.bind.annotation.RequestParam(required = false) java.util.List<Long> studentIds) {
        var group = studentGroupRepository.findById(id).orElseThrow();

        // Очищаємо старі записи вступного інструктажу
        introSafetyRepo.deleteAllByStudentGroupId(id);

        if (studentIds != null && !studentIds.isEmpty() && introDate != null) {
            for (Long studentId : studentIds) {
                ejournal.ejournal.model.StudentEntity student = studentRepo.findById(studentId).orElseThrow();
                ejournal.ejournal.model.IntroSafetyEntity record = new ejournal.ejournal.model.IntroSafetyEntity();
                record.setStudentGroup(group);
                record.setStudent(student);
                record.setIntroDate(introDate);
                record.setInstructorName(instructor);
                introSafetyRepo.save(record);
            }
        }
        return "redirect:/journal/" + id + "#safety";
    }

    @org.springframework.web.bind.annotation.PostMapping("/{id}/save-safety-grid")
    @org.springframework.security.access.prepost.PreAuthorize("!hasRole('HEAD')")
    @org.springframework.transaction.annotation.Transactional
    public String saveSafetyGrid(@org.springframework.web.bind.annotation.PathVariable Long id, jakarta.servlet.http.HttpServletRequest request) {
        var group = studentGroupRepository.findById(id).orElseThrow();

        // Очищаємо стару сітку
        safetyAttendanceRepo.deleteAllByStudentGroupId(id);

        // Зчитуємо всі чекбокси з HTML
        request.getParameterMap().forEach((key, values) -> {
            if (key.startsWith("att_")) {
                String[] parts = key.split("_");
                if (parts.length == 3) {
                    Long topicId = Long.parseLong(parts[1]);
                    Long studentId = Long.parseLong(parts[2]);

                    ejournal.ejournal.model.SafetyAttendanceEntity attendance = new ejournal.ejournal.model.SafetyAttendanceEntity();
                    attendance.setStudentGroup(group);

                    ejournal.ejournal.model.SafetyTopicEntity topicRef = safetyTopicRepo.getReferenceById(topicId);
                    ejournal.ejournal.model.StudentEntity studentRef = studentRepo.getReferenceById(studentId);

                    attendance.setSafetyTopic(topicRef);
                    attendance.setStudent(studentRef);

                    safetyAttendanceRepo.save(attendance);
                }
            }
        });

        return "redirect:/journal/" + id + "#safety";
    }
}