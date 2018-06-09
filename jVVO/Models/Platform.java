package jVVO.Models;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class Platform {
    @NonNull @SerializedName("Name") @Getter @Setter private String name;
    @NonNull @SerializedName("Type") @Getter @Setter private String type;
}
