package ejournal.ejournal.service;

import ejournal.ejournal.model.DepartmentEntity;
import ejournal.ejournal.model.RoleEntity;
import ejournal.ejournal.model.StudentGroupEntity; // ✅ Імпортуємо StudentGroupEntity
import ejournal.ejournal.model.SubjectEntity;
import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.DepartmentRepository;
import ejournal.ejournal.repo.RoleRepository;
import ejournal.ejournal.repo.StudentGroupRepository; // ✅ Імпортуємо StudentGroupRepository
import ejournal.ejournal.repo.SubjectRepository;
import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections; // ✅ Імпортуємо Collections
import java.util.HashSet;   // ✅ Імпортуємо HashSet
import java.util.List;      // ✅ Імпортуємо List
import java.util.Set;       // ✅ Імпортуємо Set

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final DepartmentRepository departmentRepo;
    private final SubjectRepository subjectRepo;
    private final PasswordEncoder passwordEncoder;
    private final StudentGroupRepository studentGroupRepo; // ✅ Додаємо репозиторій журналів

    // ====================== CREATE ======================

    public UserEntity createTeacher(String name,
                                    String surname,
                                    String email,
                                    String rawPassword,
                                    Long departmentId,
                                    Long subjectId) {
        // ... (Логіка створення залишається без змін, хоча її теж варто оновити)
        // ... (поточна логіка методу)
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
        u.setDepartment(dep);
        u.setSubject(sub);
        u.setRole(r);

        return userRepo.save(u);
    }

    // ... (Методи createHead та createAdmin залишаються без змін)
    public UserEntity createHead(String name,
                                 String surname,
                                 String email,
                                 String rawPassword,
                                 Long departmentId) {
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
        u.setDepartment(dep);
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

    /**
     * ✅ ОНОВЛЕНИЙ МЕТОД:
     * Оновлює викладача, використовуючи 'journalIds' замість 'departmentId' та 'subjectId'.
     */
    public UserEntity updateTeacher(Long id,
                                    String name,
                                    String surname,
                                    String email,
                                    String rawPassword,
                                    Set<Long> journalIds) { // ✅ ЗМІНА: 'journalIds'

        UserEntity u = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        u.setName(name);
        u.setSurname(surname);
        u.setEmail(email);
        u.setUsername(email);

        if (rawPassword != null && !rawPassword.isBlank()) {
            u.setPassword(passwordEncoder.encode(rawPassword));
        }

        // ✅ ЗМІНА: Очищуємо застарілі поля, оскільки тепер ми керуємо доступом
        // через 'teachingGroups' (зв'язок ManyToMany)
        u.setDepartment(null);
        u.setSubject(null);

        // ✅ НОВА ЛОГІКА: Оновлюємо зв'язок ManyToMany
        // Оскільки UserEntity є інверсною стороною (mappedBy),
        // нам потрібно оновити "володіючу" сторону (StudentGroupEntity.teachers)

        // 1. Створюємо копію поточного списку, щоб уникнути ConcurrentModificationException
        Set<StudentGroupEntity> oldJournals = new HashSet<>(u.getTeachingGroups());

        // 2. Видаляємо викладача зі ВСІХ старих журналів
        for (StudentGroupEntity oldJournal : oldJournals) {
            oldJournal.getTeachers().remove(u);
        }

        // 3. Завантажуємо нові журнали і додаємо до них викладача
        if (journalIds != null && !journalIds.isEmpty()) {
            List<StudentGroupEntity> newJournals = studentGroupRepo.findAllById(journalIds);
            for (StudentGroupEntity newJournal : newJournals) {
                newJournal.getTeachers().add(u);
            }
            // Hibernate автоматично оновить інверсну сторону (u.getTeachingGroups())
        }

        u.setRole(getRole("ROLE_TEACHER"));

        return userRepo.save(u);
    }

    // ... (Методи updateHead, updateAdmin, deleteUser, getRole залишаються без змін)
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
        DepartmentEntity dep = null;
        if (departmentId != null) {
            dep = departmentRepo.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Department not found: " + departmentId));
        }
        u.setDepartment(dep);
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

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    private RoleEntity getRole(String name) {
        return roleRepo.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + name));
    }
}