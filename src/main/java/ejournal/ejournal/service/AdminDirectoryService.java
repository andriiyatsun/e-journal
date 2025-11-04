package ejournal.ejournal.service;

import ejournal.ejournal.model.DepartmentEntity;
import ejournal.ejournal.model.RoleEntity; // ✅ Додано
import ejournal.ejournal.model.SubjectEntity;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.SubjectRepository;
import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.RoleRepository;
import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// import java.util.Set; // Більше не потрібен

@Service
@RequiredArgsConstructor
public class AdminDirectoryService {

    private final DepartmentRepository departmentRepo;
    private final SubjectRepository subjectRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder pe;

    // ====== Departments & Subjects ======

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DepartmentEntity createDepartment(String code, String name) {
        var d = DepartmentEntity.builder().code(code).name(name).build();
        return departmentRepo.save(d);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public SubjectEntity createSubject(Long departmentId, String subjectName) {
        var dep = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));
        subjectRepo.findByDepartmentAndName(dep, subjectName).ifPresent(s -> {
            throw new IllegalArgumentException("Subject already exists in this department: " + subjectName);
        });
        var s = SubjectEntity.builder().department(dep).name(subjectName).build();
        return subjectRepo.save(s);
    }

    // ====== Users ======

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserEntity createAdmin(String username, String email, String rawPassword) {
        var rAdmin = roleRepo.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN missing"));
        var u = baseUser(username, email, rawPassword);
        // ✅ ЗМІНА: Використовуємо setRole()
        u.setRole(rAdmin);
        return userRepo.save(u);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserEntity createHead(String username, String email, String rawPassword, Long departmentId) {
        var rHead = roleRepo.findByName("ROLE_HEAD")
                .orElseThrow(() -> new IllegalStateException("ROLE_HEAD missing"));
        var dep = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));
        var u = baseUser(username, email, rawPassword);
        u.setDepartment(dep);
        // ✅ ЗМІНА: Використовуємо setRole()
        u.setRole(rHead);
        return userRepo.save(u);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserEntity createTeacherDraft(String username, String email, String rawPassword, Long departmentId) {
        var rTeacher = roleRepo.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new IllegalStateException("ROLE_TEACHER missing"));
        var dep = departmentRepo.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));
        var u = baseUser(username, email, rawPassword);
        u.setDepartment(dep);
        // ✅ ЗМІНА: Використовуємо setRole()
        u.setRole(rTeacher);
        // subject ще не призначаємо (draft)
        return userRepo.save(u);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserEntity assignTeacherSubject(Long userId, Long subjectId) {
        var u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        var s = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));

        // перевірка: департамент викладача має збігатися з департаментом предмета
        if (u.getDepartment() == null || !u.getDepartment().getId().equals(s.getDepartment().getId())) {
            throw new IllegalArgumentException("Teacher's department mismatch with subject's department");
        }
        u.setSubject(s);
        return userRepo.save(u);
    }

    // ====== helpers ======

    private UserEntity baseUser(String username, String email, String rawPassword) {
        // ✅ Змінено: ім'я, прізвище та пошту додано в базового юзера
        return UserEntity.builder()
                .username(username) // Встановлюємо username
                .email(email)       // Встановлюємо email
                .name(username)     // Встановлюємо ім'я (тимчасово як username)
                .surname("Surname") // Встановлюємо прізвище (тимчасово)
                .password(pe.encode(rawPassword))
                .enabled(true)
                .build();
    }
}