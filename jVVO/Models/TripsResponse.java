package jVVO.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class TripsResponse {
    @SerializedName("Stops") @Getter @Setter private List<TripStop> tripStops;
    @SerializedName("ExpirationTime") @Getter @Setter private Date expirationTime;
}
