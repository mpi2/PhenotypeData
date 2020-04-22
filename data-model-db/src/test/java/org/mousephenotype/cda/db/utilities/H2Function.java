package org.mousephenotype.cda.db.utilities;

import org.h2.expression.function.Function;

import java.lang.reflect.Field;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;


/**
 * This class is used in the testing ENV in order to harmonize the functionality of the DATEDIFF
 * function between H2 database engine and MySQL.
 *
 * The H2 DATEDIFF function takes 3 parameters: Time Unit, Date1 and Date2
 * The MySQL DATEDIFF function takes 2 parameters: Date1, Date2
 *
 * Need to use the SQL specific for MySQL, so this class redefines the H2 DATEDIFF function to have the same
 * function definition and operation as the MySQL version
 */
public class H2Function {

    @SuppressWarnings("rawtypes")
    public static int removeDateDifference() {
        try {
            Field field = Function.class.getDeclaredField("FUNCTIONS_BY_NAME");
            field.setAccessible(true);
            ((Map)field.get(null)).remove("DATEDIFF");
        } catch (Exception e) {
            throw new RuntimeException("failed to remove date-difference");
        }
        return 0;
    }

    public static long dateDifference(Date date1, Date date2) {
        Objects.nonNull(date1);
        Objects.nonNull(date2);
        return ChronoUnit.DAYS.between(date1.toLocalDate(), date2.toLocalDate());
    }
}
