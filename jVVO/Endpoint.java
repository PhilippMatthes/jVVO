package jVVO;

public class Endpoint {
    public static final String base = "https://webapi.vvo-online.de/";
    private static final String tr = "tr/";
    public static final String pointfinder = base + tr + "pointfinder";
    public static final String route = base + tr + "trips";
    public static final String departureMonitor = base + "dm/";
    public static final String trip = departureMonitor + "trip";
    public static final String stt = base + "stt/";
    public static final String lines = stt + "lines";
    public static final String map = base + "map/";
    public static final String poiSearch = map + "pins";
    public static final String routeChanges = base + "rc";
}
