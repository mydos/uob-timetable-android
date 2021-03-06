package com.ak.uobtimetable.Utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

/**
 * General purpose Java utility methods.
 */
public class GeneralUtilities {

    /**
     * Convert a Date in to human readable string relative to the current time (eg x days ago)
     * @param date
     * @return String formatted string
     */
    public static String getDateTimeAgo(Date date) {

        // This is really rough, but whatever
        int msPerDay = 24 * 60 * 60 * 1000;
        int days = Math.round((new Date().getTime() - date.getTime()) / msPerDay);

        if (days == 0)
            return "today";
        else if (days == 1)
            return "yesterday";
        else
            return days + " days ago";
    }

    /**
     * Gets the variable name for a constant from it's value. Useful when an API uses constants
     * instead of enums. Should be used for logging only as it is not entirely robust (eg no error
     * when multiple constants have the same value).
     * @param type Class type containing the constant
     * @param value Value to get the name of
     * @param scope Prefix if the variable name
     * @param <T> Class type
     * @param <U> Value type
     * @return Variable name for value
     * @throws Exception If no constant matches the value provided
     */
    public static <T, U> String getConstantName(Class<T> type, U value, String scope) throws Exception {

        for (Field field : type.getDeclaredFields()) {
            int modifier = field.getModifiers();
            if (Modifier.isStatic(modifier) && Modifier.isPublic(modifier)
                    && Modifier.isFinal(modifier) && field.getName().startsWith(scope)){

                U fieldValue = (U)field.get(null);

                if (fieldValue.equals(value))
                    return field.getName();
            }
        }

        throw new Exception("Field not found!");
    }

    /**
     * Calls ToString on the throwable and all child throwables
     * @param throwable
     * @return
     */
    public static String nestedThrowableToString(Throwable throwable){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(throwable.toString());

        while (throwable.getCause() != null) {
            stringBuilder.append("\n>>> " + throwable.getCause().toString());
            throwable = throwable.getCause();
        }

        return stringBuilder.toString();
    }
}
