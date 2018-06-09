package jVVO.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class RoutesResponse {
    @NonNull @SerializedName("Routes") @Getter @Setter private List<Route> routes;
    @NonNull @SerializedName("SessionId") @Getter @Setter private String sessionId;
}
