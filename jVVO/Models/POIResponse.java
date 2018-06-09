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
public class POIResponse {
    @NonNull @SerializedName("Pins") @Getter @Setter private List<POI> pins;
    @NonNull @SerializedName("ExpirationTime") @Getter @Setter private Date expirationTime;
}
