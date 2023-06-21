package be.heh.backendappspringbootsnmp.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
public class MOManager {
    @Getter
    private final String name;
    @Getter
    private final String oidRoot;
    @Getter
    @Setter
    private List<MOVariable> moVariables;
    @Getter
    @Setter
    private List<MOTable> moTables;

}
