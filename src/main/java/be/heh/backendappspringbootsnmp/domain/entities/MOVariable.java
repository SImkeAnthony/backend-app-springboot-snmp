package be.heh.backendappspringbootsnmp.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.snmp4j.agent.MOAccess;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.smi.OID;

public class MOVariable {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String oid;
    @Getter
    @Setter
    private MOAccess moAccess;
    @Getter
    @Setter
    private String description;
    public MOVariable(String name, String oid, MOAccess access, String description){
        setName(name);
        setOid(oid);
        setMoAccess(access);
        setDescription(description);
    }

    @Override
    public String toString(){
        return "name : "+getName()+";oid : "+getOid()+";access : "+getMoAccess().toString()+";description : "+getDescription();
    }
}
