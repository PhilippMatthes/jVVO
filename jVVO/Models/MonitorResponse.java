package jVVO.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class MonitorResponse {
    @NonNull @Getter @Setter @SerializedName("Name") private String stopName;
    @NonNull @Getter @Setter @SerializedName("Place") private String place;
    @NonNull @Getter @Setter @SerializedName("ExpirationTime") private Date expirationTime;
    @NonNull @Getter @Setter @SerializedName("Departures") private List<Departure> departures;
}
