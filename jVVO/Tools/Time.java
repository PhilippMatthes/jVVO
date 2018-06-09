package jVVO.Tools;

import java.util.Date;

public final class Time {
    public static int hoursUntil(Date date) {
        if (date == null) { return 0; }

        long diff = new Date().getTime() - date.getTime();
        return (int) (diff / 3600000);
    }

    public static int minutesUntil(Date date) {
        if (date == null) { return 0; }

        long diff = new Date().getTime() - date.getTime();
        return (int) (diff / 60000);
    }

    public static int secondsUntil(Date date) {
        if (date == null) { return 0; }

        long diff = new Date().getTime() - date.getTime();
        return (int) (diff / 1000);
    }

    public static int millisUntil(Date date) {
        if (date == null) { return 0; }

        long diff = new Date().getTime() - date.getTime();
        return (int) diff;
    }
}
