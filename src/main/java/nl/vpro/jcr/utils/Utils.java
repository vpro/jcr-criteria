package nl.vpro.jcr.utils;

import java.time.*;
import java.util.GregorianCalendar;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class Utils {



    public static Object toCalendarIfPossible(Object o, ZoneId zoneId) {
        if (o instanceof Instant) {
            return GregorianCalendar.from(((Instant) o).atZone(zoneId));
        } else if (o instanceof LocalDateTime) {
            return GregorianCalendar.from(((LocalDateTime) o).atZone(zoneId));
        } else if (o instanceof LocalDate) {
            return GregorianCalendar.from(ZonedDateTime.of((LocalDate) o, LocalTime.MIDNIGHT, zoneId));
        } else {
            return o;
        }

    }
}
