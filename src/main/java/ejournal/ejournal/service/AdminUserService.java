package ejournal.ejournal.service;

import ejournal.ejournal.model.DepartmentEntity;
import ejournal.ejournal.model.RoleEntity;
import ejournal.ejournal.model.SubjectEntity;
import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.RoleRepository;
import ejournal.ejournal.repo.SubjectRepository;
import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Не використовується
import org.springframework.security.crypto.password.PasswordEncoder; // Використовуємо інтерфейс
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final DepartmentRepository departmentRepo;
    private final SubjectRepository subjectRepo;
    private final PasswordEncoder passwordEncoder;

    // ====================== CREATE ======================

    public UserEntity createTeacher(String name,
                                    String surname,
                                    String email,
                                    String rawPassword,
                                    Long departmentId,
                                    Long subjectId) {

        // ✅ ЗМІНА: Department та Subject стали необов'язковими
        DepartmentEntity dep = null;
        if (departmentId != null) {
            dep = departmentRepo.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));
        }

        SubjectEntity sub = null;
        if (subjectId != null) {
            sub = subjectRepo.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));
        }

        // Перевірка, що предмет належить кафедрі (якщо обидва вказані)
        if (dep != null && sub != null && !sub.getDepartment().getId().equals(dep.getId())) {
            throw new IllegalArgumentException("Subject " + sub.getName() + " does not belong to department " + dep.getName());
        }

        RoleEntity r = getRole("ROLE_TEACHER");

        UserEntity u = new UserEntity();
        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setUsername(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setEnabled(true);
        u.setDepartment(dep); // Встановить null, якщо ID не надано
        u.setSubject(sub);   // Встановить null, якщо ID не надано
        u.setRole(r);

        return userRepo.save(u);
    }

    public UserEntity createHead(String name,
                                 String surname,
                                 String email,
                                 String rawPassword,
                                 Long departmentId) {

        // ✅ ЗМІНА: Department став необов'язковим
        DepartmentEntity dep = null;
        if (departmentId != null) {
            dep = departmentRepo.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));
        }

        RoleEntity r = getRole("ROLE_HEAD");

        UserEntity u = new UserEntity();
        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setUsername(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setEnabled(true);
        u.setDepartment(dep); // Встановить null, якщо ID не надано
        u.setSubject(null);
        u.setRole(r);

        return userRepo.save(u);
    }

    public UserEntity createAdmin(String name,
                                  String surname,
                                  String email,
                                  String rawPassword) {
        RoleEntity r = getRole("ROLE_ADMIN");

        UserEntity u = new UserEntity();
        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setUsername(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setEnabled(true);
        u.setDepartment(null);
        u.setSubject(null);
        u.setRole(r);

        return userRepo.save(u);
    }

    // ====================== UPDATE ======================

    public UserEntity updateTeacher(Long id,
                                    String name,
                                    String surname,
                                    String email,
                                    String rawPassword,
                                    Long departmentId,
                                    Long subjectId) {

        UserEntity u = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setUsername(email);

        if (rawPassword != null && !rawPassword.isBlank()) {
            u.setPassword(passwordEncoder.encode(rawPassword));
        }

        // ✅ ЗМІНА: Логіка для оновлення необов'язкових полів
        DepartmentEntity dep = null;
        if (departmentId != null) {
            dep = departmentRepo.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));
        }
        u.setDepartment(dep); // Оновлюємо (може бути null)

        SubjectEntity sub = null;
        if (subjectId != null) {
            sub = subjectRepo.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));
        }

        // Перевірка, що предмет належить кафедрі (якщо обидва вказані)
        if (dep != null && sub != null && !sub.getDepartment().getId().equals(dep.getId())) {
            throw new IllegalArgumentException("Subject " + sub.getName() + " does not belong to department " + dep.getName());
        }

        // Якщо кафедра скидається, предмет теж має скинутися
        if (dep == null) {
            sub = null;
        }
        u.setSubject(sub); // Оновлюємо (може бути null)

        u.setRole(getRole("ROLE_TEACHER"));

        return userRepo.save(u);
    }

    public UserEntity updateHead(Long id,
                                 String name,
                                 String surname,
                                 String email,
                                 String rawPassword,
                                 Long departmentId) {

        UserEntity u = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setUsername(email);

        if (rawPassword != null && !rawPassword.isBlank()) {
            u.setPassword(passwordEncoder.encode(rawPassword));
        }

        // ✅ ЗМІНА: Логіка для оновлення необов'язкового поля
        DepartmentEntity dep = null;
        if (departmentId != null) {
            dep = departmentRepo.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));
        }
        u.setDepartment(dep); // Оновлюємо (може бути null)
        u.setSubject(null);

        u.setRole(getRole("ROLE_HEAD"));

        return userRepo.save(u);
    }

    public UserEntity updateAdmin(Long id,
                                  String name,
                                  String surname,
                                  String email,
                                  String rawPassword) {

        UserEntity u = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setUsername(email);

        if (rawPassword != null && !rawPassword.isBlank()) {
            u.setPassword(passwordEncoder.encode(rawPassword));
        }

        u.setDepartment(null);
        u.setSubject(null);
        u.setRole(getRole("ROLE_ADMIN"));

        return userRepo.save(u);
    }

    // ====================== DELETE ======================

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    // ====================== HELPERS ======================

    private RoleEntity getRole(String name) {
        return roleRepo.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + name));
    }
}

