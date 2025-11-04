package ejournal.ejournal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Сутність "Глобальне налаштування".
 * Для збереження ключ-значення, наприклад шаблонів дат ("01-09").
 */
@Entity
@Table(name = "global_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalSetting {

    @Id
    @Column(length = 100)
    private String settingKey; // Ключ, наприклад "default_start_month_day"

    @Column(length = 255)
    private String settingValue; // Значення, наприклад "01-09"
}

