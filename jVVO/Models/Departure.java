package jVVO.Models;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import philippmatthes.com.manni.jVVO.Connection;
import philippmatthes.com.manni.jVVO.DVBError;
import philippmatthes.com.manni.jVVO.Endpoint;
import philippmatthes.com.manni.jVVO.Result;
import philippmatthes.com.manni.jVVO.Tools.ISO8601;
import philippmatthes.com.manni.jVVO.Tools.Time;

import java.util.*;
import java.util.stream.Collectors;

@ToString
@AllArgsConstructor
public class Departure implements Comparable<Departure> {
    @NonNull @Getter @Setter @SerializedName("Id") private String id;
    @NonNull @Getter @Setter @SerializedName("LineName") private String line;
    @NonNull @Getter @Setter @SerializedName("Direction") private String direction;
    @Getter @Setter @SerializedName("Platform") private Platform platform;
    @NonNull @Getter @Setter @SerializedName("Mot") private Mode mode;
    @Getter @Setter @SerializedName("RealTime") private Date realTime;
    @NonNull @Getter @Setter @SerializedName("ScheduledTime") private Date scheduledTime;
    @Getter @Setter @SerializedName("State") private State state;
    @Getter @Setter @SerializedName("RouteChanges") private List<String> routeChanges;
    @Getter @Setter @SerializedName("Diva") private Diva diva;

    public Integer getETA() {
        return realTime != null ? Time.minutesUntil(realTime) : getScheduledETA();
    }

    public Integer getScheduledETA() {
        return Time.minutesUntil(scheduledTime);
    }

    public String fancyETA() {
        if (realTime == null) {
            return getScheduledETA().toString();
        }

        Integer diff = Time.minutesUntil(realTime);
        if (diff < 0) {
            return getScheduledETA().toString()+diff.toString();
        } else if (diff == 0) {
            return getScheduledETA().toString();
        } else {
            return getScheduledETA().toString()+"+"+diff.toString();
        }
    }

    @Override
    public int compareTo(Departure o) {
        return o.getId().compareTo(id);
    }

    public enum State {
        @SerializedName("InTime") onTime ("InTime"),
        @SerializedName("Delayed") delayed ("Delayed"),
        @SerializedName("Unknown") unknown ("Unknown");

        private final String rawValue;

        State(String s) {
            rawValue = s;
        }

        public String getValue() {
            return rawValue;
        }

        public boolean equalsState(String rawValue) {
            return this.rawValue.equals(rawValue);
        }

        public String toString() {
            return rawValue;
        }

        public State[] getAll() {
            return new State[]{State.onTime, State.delayed};
        }
    }

    public enum DateType {
        @SerializedName("arrival") arrival ("arrival"),
        @SerializedName("departure") departure ("departure");

        private final String rawValue;

        DateType(String s) { rawValue = s; }

        public String getValue() {
            return rawValue;
        }

        public Boolean requestVal() {
            return this == arrival;
        }
    }

    public static void monitor(
         String stopId,
         Date date,
         DateType dateType,
         List<Mode> allowedModes,
         Boolean allowShorttermChanges,
         RequestQueue queue,
         Response.Listener<Result<MonitorResponse>> listener
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("stopid", stopId);
        data.put("time", ISO8601.fromDate(date));
        data.put("isarrival", dateType.requestVal());
        data.put("limit", "0");
        data.put("shorttermchanges", allowShorttermChanges);
        List<String> modes = allowedModes.stream()
                .map(mode -> mode.getRawValue())
                .collect(Collectors.toList());
        data.put("mot", modes);
        Connection.post(Endpoint.departureMonitor, data, listener, MonitorResponse.class, queue);
    }

    public static void monitor(
        String stopWithId,
        RequestQueue queue,
        Response.Listener<Result<MonitorResponse>> listener
    ) {
        Departure.monitor(
            stopWithId,
            new Date(),
            DateType.arrival,
            Mode.getAll(),
            true,
                queue,
            listener
        );
    }

    public static void monitorByName(
        String stopName,
        Date date,
        DateType dateType,
        List<Mode> allowedModes,
        Boolean allowShorttermChanges,
        RequestQueue queue,
        Response.Listener<Result<MonitorResponse>> listener
    ) {
        Stop.find(stopName, queue,
            response -> {
                if (!response.getResponse().isPresent()) {
                    listener.onResponse(new Result<>(Optional.empty(), response.getError()));
                    return;
                }
                List<Stop> stops = response.getResponse().get().getStops();
                if (stops.size() == 0) {
                    listener.onResponse(new Result<>(Optional.empty(), Optional.of(DVBError.response)));
                    return;
                }
                Stop firstStop = stops.get(0);
                monitor(
                        firstStop.getId(),
                        date,
                        dateType,
                        allowedModes,
                        allowShorttermChanges,
                        queue,
                        listener
                );
            }
        );
    }

    public static void monitorByName(
            String stopName,
            RequestQueue queue,
            Response.Listener<Result<MonitorResponse>> listener
    ) {
        monitorByName(
                stopName,
                new Date(),
                DateType.departure,
                Mode.getAll(),
                true,
                queue,
                listener
        );
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
