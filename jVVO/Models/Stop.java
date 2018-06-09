package jVVO.Models;

import com.android.volley.RequestQueue;
import com.android.volley.Response;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import philippmatthes.com.manni.jVVO.Connection;
import philippmatthes.com.manni.jVVO.DVBError;
import philippmatthes.com.manni.jVVO.Endpoint;
import philippmatthes.com.manni.jVVO.GKCoordinate;
import philippmatthes.com.manni.jVVO.Result;
import philippmatthes.com.manni.jVVO.WGSCoordinate;

@ToString
@AllArgsConstructor
public class Stop implements Comparable<Stop> {

    @Getter @Setter private String id;
    @Getter @Setter private String name;
    @Getter @Setter private String region;
    @Getter @Setter private WGSCoordinate location;

    public static void find(
            String query,
            RequestQueue queue,
            Response.Listener<Result<FindResponse>> listener
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("limit", 0);
        data.put("query", query);
        data.put("stopsOnly", true);
        data.put("dvb", true);
        Connection.post(Endpoint.pointfinder, data, listener, FindResponse.class, queue);
    }

    public static void findNear(
            Double lat,
            Double lng,
            RequestQueue queue,
            Response.Listener<Result<FindResponse>> listener
    ) {
        WGSCoordinate coordinate = new WGSCoordinate(lat, lng);
        findNear(
                coordinate,
                queue,
                listener
        );
    }

    public static void findNear(
            WGSCoordinate coordinate,
            RequestQueue queue,
            Response.Listener<Result<FindResponse>> listener
    ) {
        Optional<GKCoordinate> gkCoordinate = coordinate.asGK();
        if (!gkCoordinate.isPresent()) {
            listener.onResponse(new Result<>(Optional.empty(), Optional.of(DVBError.coordinate)));
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("limit", 10);
        data.put("assignedStops", true);
        data.put("query", "coord:" + gkCoordinate.get().getX().intValue() + ":" + gkCoordinate.get().getY().intValue());
        Connection.post(Endpoint.pointfinder, data, listener, FindResponse.class, queue);
    }

    public void monitor(
            Date date,
            Departure.DateType dateType,
            List<Mode> modes,
            Boolean allowShorttermChanges,
            RequestQueue queue,
            Response.Listener<Result<MonitorResponse>> listener
    ) {
        Departure.monitor(id, date, dateType, modes, allowShorttermChanges, queue, listener);
    }


    @Override
    public int compareTo(Stop o) {
        return o.getId().compareTo(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
