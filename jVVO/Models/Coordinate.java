package jVVO.Models;

import java.util.Optional;

import lombok.ToString;
import philippmatthes.com.manni.jVVO.GKCoordinate;
import philippmatthes.com.manni.jVVO.WGSCoordinate;

@ToString
public abstract class Coordinate {
    abstract public Optional<GKCoordinate> asGK();
    abstract public Optional<WGSCoordinate> asWGS();
}
