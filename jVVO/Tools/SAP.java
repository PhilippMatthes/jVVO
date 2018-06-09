package jVVO.Tools;

import java.util.Date;

public final class SAP {

    public static String fromDate(Date date) {
        long seconds = System.currentTimeMillis() / 1000l;
        return "/Date(" + seconds + "+0100)/";
    }

}
