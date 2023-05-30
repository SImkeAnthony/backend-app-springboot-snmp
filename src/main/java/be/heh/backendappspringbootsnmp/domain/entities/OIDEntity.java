package be.heh.backendappspringbootsnmp.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OIDEntity {
    @Getter
    private final int uniqueIdentifier;
    @Getter
    private final String name;
    @Getter
    private final String oid;
}
