package nl.vpro.jcr.criteria.query.utils;

import java.time.*;
import java.util.GregorianCalendar;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class Utils {



    public static Object toCalendarIfPossible(Object o) {
        if (o instanceof Instant) {
            return GregorianCalendar.from(((Instant) o).atZone(ZoneId.systemDefault()));
        } else if (o instanceof LocalDateTime) {
            return GregorianCalendar.from(((LocalDateTime) o).atZone(ZoneId.systemDefault()));
        } else if (o instanceof LocalDate) {
            return GregorianCalendar.from(ZonedDateTime.of((LocalDate) o, LocalTime.MIDNIGHT, ZoneId.systemDefault()));
        } else {
            return o;
        }

    }
}
