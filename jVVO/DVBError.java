package jVVO;

public enum DVBError {
    network ("Network"),
    server ("Server"),
    response ("Response"),
    request ("Request"),
    decode ("Decode"),
    coordinate ("Coordinate");

    private final String description;

    DVBError(String s) {
        description = s;
    }

    public String getDescription() {
        return description;
    }
}
