package ejournal.ejournal.util;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScheduleParser {

    // Regex для пошуку "ДД: ЧАС" (наприклад: "ЧТ: 16:00-17:45")
    private static final Pattern SCHEDULE_PATTERN = Pattern.compile("(\\p{L}+):\\s*(\\d{2}:\\d{2}-\\d{2}:\\d{2})");

    /**
     * Парсить рядок розкладу у мапу DayOfWeek -> Час.
     */
    public static Map<DayOfWeek, String> parseSchedule(String scheduleJson) {
        if (scheduleJson == null || scheduleJson.isBlank()) {
            return Collections.emptyMap();
        }

        Map<DayOfWeek, String> scheduleMap = new HashMap<>();
        Matcher matcher = SCHEDULE_PATTERN.matcher(scheduleJson);

        while (matcher.find()) {
            String dayAbbreviation = matcher.group(1).trim();
            String timeSlot = matcher.group(2).trim();

            DayOfWeek day = DayOfWeekHelper.parseDay(dayAbbreviation);
            if (day != null) {
                scheduleMap.put(day, timeSlot);
            } else {
                // Виводимо попередження або ігноруємо невідомий формат
                System.err.println("Warning: Unknown day abbreviation in schedule: " + dayAbbreviation);
            }
        }

        return scheduleMap;
    }
}
