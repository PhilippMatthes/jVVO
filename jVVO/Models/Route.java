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

import java.util.*;
import java.util.stream.Collectors;

@ToString
@AllArgsConstructor
public class Route implements Comparable<Route> {
    @SerializedName("PriceLevel") @Getter @Setter private Integer priceLevel;
    @SerializedName("Price") @Getter @Setter private String price;
    @NonNull @SerializedName("Duration") @Getter @Setter private Integer duration;
    @NonNull @SerializedName("Interchanges") @Getter @Setter private Integer interchanges;
    @NonNull @SerializedName("MotChain") @Getter @Setter private List<ModeElement> modeChain;
    @SerializedName("FareZoneOrigin") @Getter @Setter private Integer fareZoneOrigin;
    @SerializedName("FareZoneDestination") @Getter @Setter private Integer fareZoneDestination;
    @NonNull @SerializedName("MapPdfId") @Getter @Setter private String mapPdfId;
    @NonNull @SerializedName("RouteId") @Getter @Setter private Integer routeId;
    @NonNull @SerializedName("PartialRoutes") @Getter @Setter private List<RoutePartial> partialRoutes;
    @NonNull @SerializedName("MapData") @Getter @Setter private List<String> mapData;

    @Override
    public int compareTo(Route o) {
        return o.getRouteId().compareTo(routeId);
    }

    @ToString
    @AllArgsConstructor
    public class ModeElement {
        @SerializedName("Name") @Getter @Setter private String name;
        @SerializedName("Type") @Getter @Setter private Mode mode;
        @SerializedName("Direction") @Getter @Setter private String direction;
        @SerializedName("Changes") @Getter @Setter private List<String> changes;
        @SerializedName("Diva") @Getter @Setter private Diva diva;
    }

    @ToString
    @AllArgsConstructor
    public class RoutePartial {
        @SerializedName("PartialRouteId") @Getter @Setter private Integer partialRouteId;
        @SerializedName("Duration") @Getter @Setter private Integer duration;
        @NonNull @SerializedName("Mot") @Getter @Setter private ModeElement mode;
        @NonNull @SerializedName("MapDataIndex") @Getter @Setter private Integer mapDataIndex;
        @NonNull @SerializedName("Shift") @Getter @Setter private String shift;
        @SerializedName("RegularStops") @Getter @Setter private List<RouteStop> regularStops;
    }

    @ToString
    @AllArgsConstructor
    public class RouteStop {
        @NonNull @SerializedName("ArrivalTime") @Getter @Setter private Date arrivalTime;
        @NonNull @SerializedName("DepartureTime") @Getter @Setter private Date departureTime;
        @NonNull @SerializedName("Place") @Getter @Setter private String place;
        @NonNull @SerializedName("Name") @Getter @Setter private String name;
        @NonNull @SerializedName("Type") @Getter @Setter private String type;
        @NonNull @SerializedName("DataId") @Getter @Setter private String dataId;
        @SerializedName("Platform") @Getter @Setter private Platform platform;
        @SerializedName("Latitude") @Getter @Setter private Double wgsLatitude;
        @SerializedName("Longitude") @Getter @Setter private Double wgsLongitude;
        @SerializedName("MapPdfId") @Getter @Setter private String mapPdfId;
    }

    @Override
    public int hashCode() {
        return routeId.hashCode();
    }

    public static void find(
            String originId,
            String destinationId,
            Date time,
            Boolean dateIsArrival,
            Boolean allowShortTermChanges,
            RequestQueue queue,
            Response.Listener<Result<RoutesResponse>> listener
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("origin", originId);
        data.put("destination", destinationId);
        data.put("time", ISO8601.fromDate(time));
        data.put("isarrivaltime", dateIsArrival);
        data.put("shorttermchanges", allowShortTermChanges);
        data.put("mobilitySettings", "None");
        data.put("includeAlternativeStops", true);
        Map<String, Object> standardSettings = new HashMap<>();
        standardSettings.put("maxChanges", "Unlimited");
        standardSettings.put("walkingSpeed", "Normal");
        standardSettings.put("footpathToStop", 5);
        List<String> modeIdentifiers = Mode.getAll().stream().map(Mode::getRawValue).collect(Collectors.toList());
        standardSettings.put("mot", modeIdentifiers);
        data.put("standardSettings", standardSettings);
        Connection.post(Endpoint.route, data, listener, RoutesResponse.class, queue);
    }

    public static void find(
            String originId,
            String destinationId,
            RequestQueue queue,
            Response.Listener<Result<RoutesResponse>> listener
    ) {
        find(
                originId,
                destinationId,
                new Date(),
                false,
                true,
                queue,
                listener
        );
    }

    public static void findByName(
            String origin,
            String destination,
            Date time,
            Boolean dateIsArrival,
            Boolean allowShortTermChanges,
            RequestQueue queue,
            Response.Listener<Result<RoutesResponse>> listener
    ) {

        Stop.find(origin, queue,
            originResponse -> {
                if (!originResponse.getResponse().isPresent()) {
                    listener.onResponse(new Result<>(Optional.empty(), originResponse.getError()));
                    return;
                }
                FindResponse originFindResponse = originResponse.getResponse().get();
                if (originFindResponse.getStops().size() == 0) {
                    listener.onResponse(new Result<>(Optional.empty(), Optional.of(DVBError.response)));
                    return;
                }
                String originId = originFindResponse.getStops().get(0).getId();

                Stop.find(destination, queue,
                    destinationResponse -> {
                        if (!destinationResponse.getResponse().isPresent()) {
                            listener.onResponse(new Result<>(Optional.empty(), destinationResponse.getError()));
                            return;
                        }
                        FindResponse destinationFindResponse = destinationResponse.getResponse().get();
                        if (destinationFindResponse.getStops().size() == 0) {
                            listener.onResponse(new Result<>(Optional.empty(), Optional.of(DVBError.response)));
                            return;
                        }
                        String destinationId = destinationFindResponse.getStops().get(0).getId();

                        find(
                                originId,
                                destinationId,
                                time,
                                dateIsArrival,
                                allowShortTermChanges,
                                queue,
                                listener
                        );
                    }
                );
            }
        );
    }

    public static void findByName(
            String origin,
            String destination,
            RequestQueue queue,
            Response.Listener<Result<RoutesResponse>> listener
    ) {
        findByName(
                origin,
                destination,
                new Date(),
                false,
                true,
                queue,
                listener
        );
    }

}
