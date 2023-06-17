package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MOVariable;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.OIDMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class OIDPersistanceAdaptateur {

    @Getter
    @Setter
    JSONObject jsonObject;
    @Getter
    private final OIDMapper oidMapper;
    @Getter
    @Setter
    private List<MOVariable> moVariables = new ArrayList<>();

    public List<MOVariable> getMoVariablesList() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("oid.json");
        try {
            String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.ISO_8859_1);
            setJsonObject(new JSONObject(jsonContent));
            return formatJsonMibToMoVariables();
        } catch (IOException e) {
            System.err.println("Error IO : "+e.getMessage());
            return new ArrayList<>();
        }
    }
    private List<MOVariable> formatJsonMibToMoVariables(){
        List<MOVariable> moVariables = new ArrayList<>();
        Iterator<String> keys = getJsonObject().keys();
        while (keys.hasNext()){
            JSONObject mo = getJsonObject().getJSONObject(keys.next());
            moVariables.add(getOidMapper().mapJsonMOtoMOVariable(mo));
        }
        return moVariables;
    }

    public int getIdToOid(String oid){
        if(getMoVariables().isEmpty()){
            setMoVariables(getMoVariablesList());
        }
        for(MOVariable moVariable : getMoVariables()){
            if(Objects.equals(moVariable.getOid().format(), oid)){
                return moVariable.getId();
            }
        }
        return 0;
    }
}
