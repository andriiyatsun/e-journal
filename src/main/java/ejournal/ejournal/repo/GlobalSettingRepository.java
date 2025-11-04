package ejournal.ejournal.repo;

import ejournal.ejournal.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, String> {
    // Стандартних методів JpaRepository (наприклад findById) тут достатньо.
}

