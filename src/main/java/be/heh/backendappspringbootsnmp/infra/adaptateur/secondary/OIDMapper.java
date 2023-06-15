package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MOVariable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.snmp4j.agent.MOAccess;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

import java.util.Map;

@RequiredArgsConstructor
public class OIDMapper {

    @Getter
    private final Map<String, MOAccess> access;
    public MOVariable mapJsonMOtoMOVariable(JSONObject jsonObject){
        return new MOVariable(
                Integer.parseInt(jsonObject.get("id").toString()),
                jsonObject.get("name").toString(),
                new OID(jsonObject.get("oid").toString()),
                getAccess().get(jsonObject.get("maxaccess")),
                new OctetString("default"),
                jsonObject.get("description").toString()
        );
    }
}
