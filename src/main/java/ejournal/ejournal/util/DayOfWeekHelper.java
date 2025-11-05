package ejournal.ejournal.util;

import java.time.DayOfWeek;
import java.util.Map;

public class DayOfWeekHelper {

    private static final Map<String, DayOfWeek> DAY_MAP = Map.of(
            "ПН", DayOfWeek.MONDAY,
            "ВТ", DayOfWeek.TUESDAY,
            "СР", DayOfWeek.WEDNESDAY,
            "ЧТ", DayOfWeek.THURSDAY,
            "ПТ", DayOfWeek.FRIDAY,
            "СБ", DayOfWeek.SATURDAY,
            "НД", DayOfWeek.SUNDAY
    );

    public static DayOfWeek parseDay(String ukrainianAbbreviation) {
        return DAY_MAP.get(ukrainianAbbreviation.toUpperCase());
    }
}