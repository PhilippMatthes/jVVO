package jVVO.Models;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class Diva {
    @NonNull @SerializedName("Number")@Getter @Setter private String number;
    @NonNull @SerializedName("Network")@Getter @Setter private String network;
}
