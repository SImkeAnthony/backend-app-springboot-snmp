package be.heh.backendappspringbootsnmp.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class MOTable {
    @Getter
    private final String name;
    @Getter
    private final String oid;
    @Getter
    private final String description;
    @Getter
    @Setter
    private String oidIndex;
    @Getter
    @Setter
    private String oidNumber;
    @Getter
    @Setter
    private List<MOVariable> columns = new ArrayList<>();

}
