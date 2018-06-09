package jVVO;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Result<T> {
    @Getter @Setter private Optional<T> response;
    @Getter @Setter private Optional<DVBError> error;
}
