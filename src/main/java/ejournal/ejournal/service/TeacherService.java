package ejournal.ejournal.service;

import ejournal.ejournal.model.StudentGroupEntity;
import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserRepository userRepository;

    /**
     * Знаходить всі журнали, до яких прив'язаний викладач.
     * @Transactional(readOnly = true) гарантує, що LAZY-зв'язок
     * user.getTeachingGroups() буде успішно завантажений.
     */
    @Transactional(readOnly = true)
    public Set<StudentGroupEntity> getJournalsForTeacher(String email) {
        // Знаходимо користувача за email
        UserEntity user = userRepository.findByEmail(email)
                .orElse(null); // .orElseThrow(...)

        if (user == null) {
            // Або кидаємо виняток, або повертаємо порожній список
            return Collections.emptySet();
        }

        // Завдяки @Transactional, ми можемо просто звернутися до LAZY-поля
        // і Hibernate завантажить пов'язані журнали.
        return user.getTeachingGroups();
    }
}

