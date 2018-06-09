package jVVO.Models;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import philippmatthes.com.manni.jVVO.Connection;
import philippmatthes.com.manni.jVVO.Endpoint;
import philippmatthes.com.manni.jVVO.Result;

@ToString
@AllArgsConstructor
public class RouteChange implements Comparable<RouteChange> {

    @NonNull @SerializedName("Id") @Getter @Setter private String id;
    @SerializedName("Type") @Getter @Setter private Kind kind;
    @SerializedName("TripRequestInclude") @Getter @Setter private Boolean tripRequestInclude;
    @NonNull @SerializedName("Title") @Getter @Setter private String title;
    @NonNull @SerializedName("Description") @Getter @Setter private String htmlDescription;
    @NonNull @SerializedName("ValidityPeriods") @Getter @Setter private List<ValidityPeriod> validityPeriods;
    @NonNull @SerializedName("LineIds") @Getter @Setter private List<String> lineIds;
    @NonNull @SerializedName("PublishDate") @Getter @Setter private Date publishDate;

    @Override
    public int compareTo(RouteChange o) {
        return o.getId().compareTo(id);
    }

    public class Line {
        @NonNull @SerializedName("Id") @Getter @Setter private String id;
        @NonNull @SerializedName("Name") @Getter @Setter private String name;
        @NonNull @SerializedName("TransportationCompany") @Getter @Setter private String transportationCompany;
        @NonNull @SerializedName("Mot") @Getter @Setter private Mode mode;
        @NonNull @SerializedName("Divas") @Getter @Setter private List<Diva> divas;
        @NonNull @SerializedName("Changes") @Getter @Setter private List<String> changes;
    }

    public class ValidityPeriod {
        @NonNull @SerializedName("Begin") @Getter @Setter private Date begin;
        @SerializedName("End") @Getter @Setter private Date end;
    }

    public enum Kind {
        @SerializedName("Scheduled") scheduled("Scheduled"),
        @SerializedName("AmplifyingTransport") amplifyingTransport("AmplifyingTransport"),
        @SerializedName("ShortTerm") shortTerm("ShortTerm"),
        @SerializedName("Unknown") unknown("Unknown");

        private final String rawValue;

        Kind(String s) {
            rawValue = s;
        }

        public String getRawValue() {
            return rawValue;
        }
    }

    public static void get(
            Boolean shortterm,
            RequestQueue queue,
            Response.Listener<Result<RouteChangeResponse>> listener
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("shortterm", shortterm);
        Connection.post(Endpoint.routeChanges, data, listener, RouteChangeResponse.class, queue);
    }

    public static void get(
            RequestQueue queue,
            Response.Listener<Result<RouteChangeResponse>> listener
    ) {
        get(
                true,
                queue,
                listener
        );
    }

}
