package ru.itskekoff.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ProtocolType {
    PROTOCOL_UNKNOWN(0, "UNKNOWN"),
    PROTOCOL_1_12_2(340, "1.12.2");

    private final int protocol;
    private final String prefix;

    public static ProtocolType getByProtocolID(int protocol) {
        return Arrays.stream(values())
                .filter(p -> p.protocol == protocol)
                .findFirst().orElse(PROTOCOL_UNKNOWN);
    }
}