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
public class RouteChangeResponse {
    @NonNull @Getter @Setter @SerializedName("Lines") private List<RouteChange.Line> lines;
    @NonNull @Getter @Setter @SerializedName("Changes") private List<RouteChange> changes;
}
