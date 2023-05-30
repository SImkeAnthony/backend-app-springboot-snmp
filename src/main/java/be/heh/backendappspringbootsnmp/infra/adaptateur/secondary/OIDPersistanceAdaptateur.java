package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.OIDEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class OIDPersistanceAdaptateur {

    @Getter
    private final OIDMapper oidMapper;
    @Getter
    @Setter
    private List<OIDEntity> oidEntities = new ArrayList<>();

    public List<OIDEntity> getOIDs(){
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oid.txt");
        assert inputStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            List<String> oidText = new ArrayList<>();
            String line;
            while ((line = reader.readLine())!=null){
                oidText.add(line);
            }
            return getOidMapper().mapOIDTextToOIDObject(oidText);
        } catch (IOException e) {
            System.err.println("Error IO : "+e.getMessage());
            return new ArrayList<>();
        }
    }

    public int getIdToOid(String oid){
        if(getOidEntities().isEmpty()){
            setOidEntities(getOIDs());
        }
        for(OIDEntity oidEntity : getOidEntities()){
            if(Objects.equals(oidEntity.getOid(), oid)){
                return oidEntity.getUniqueIdentifier();
            }
        }
        return 0;
    }
}
