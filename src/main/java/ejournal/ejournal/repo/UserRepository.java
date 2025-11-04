package ejournal.ejournal.repo;

import ejournal.ejournal.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    /**
     * ✅ ВИПРАВЛЕНО:
     * Назва методу змінена з "findAllByRoles_Name" (множина) на "findAllByRole_Name" (однина),
     * щоб відповідати полю "role" у UserEntity.
     */
    List<UserEntity> findAllByRole_Name(String roleName);
}
