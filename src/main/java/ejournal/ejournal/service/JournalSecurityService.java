package ejournal.ejournal.service;

import ejournal.ejournal.model.StudentGroupEntity;
import ejournal.ejournal.model.UserEntity;
import ejournal.ejournal.repo.StudentGroupRepository;
import ejournal.ejournal.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("journalSecurityService") // Назва "journalSecurityService" важлива!
@RequiredArgsConstructor
public class JournalSecurityService {

    private final UserRepository userRepository;
    private final StudentGroupRepository studentGroupRepository;

    /**
     * Перевіряє, чи має поточний викладач доступ до конкретного журналу.
     * Викликається з @PreAuthorize.
     */
    @Transactional(readOnly = true)
    public boolean canViewJournal(Authentication authentication, Long journalId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // 1. Знаходимо поточного користувача
        UserEntity user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return false;
        }

        // 2. Знаходимо журнал
        StudentGroupEntity journal = studentGroupRepository.findById(journalId).orElse(null);
        if (journal == null) {
            return false; // Або true, якщо адмін має бачити 404
        }

        // 3. Перевіряємо, чи є користувач у списку викладачів журналу
        // Завдяки @Transactional, .getTeachers() завантажиться
        return journal.getTeachers().contains(user);
    }
}
