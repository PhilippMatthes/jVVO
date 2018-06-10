package jVVO.Tools;

import java.util.Date;

public final class Time {
    public static int minutesUntil(Date date) {
        if (date == null) { return 0; }

        long diff = date.getTime() - new Date().getTime();
        return (int) (diff / 60000);
    }
}
