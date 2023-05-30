package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.OIDEntity;

import java.util.ArrayList;
import java.util.List;

public class OIDMapper {
    public List<OIDEntity> mapOIDTextToOIDObject(List<String> oidText){
        List<OIDEntity> oidEntities = new ArrayList<>();
        for(String oidString : oidText){
            String[] subString = oidString.split("\\+");
            OIDEntity oidEntity = new OIDEntity(Integer.parseInt(subString[0]), subString[1], subString[2]);
            oidEntities.add(oidEntity);
        }
        return oidEntities;
    }
}
