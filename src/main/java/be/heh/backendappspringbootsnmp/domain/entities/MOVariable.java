package be.heh.backendappspringbootsnmp.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.snmp4j.agent.MOAccess;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

@RequiredArgsConstructor
public class MOVariable {
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private OID oid;
    @Getter
    @Setter
    private MOAccess moAccess;
    @Getter
    @Setter
    private OctetString value;
    @Getter
    @Setter
    private String description;
    public MOVariable(int id,String name,OID oid, MOAccess access, OctetString value,String description){
        setId(id);
        setName(name);
        setOid(oid);
        setMoAccess(access);
        setValue(value);
        setDescription(description);
    }
    @Override
    public String toString(){
        return "id : "+getId()+";name : "+getName()+";oid : "+getOid()+";access : "+getMoAccess().toString()+";value : "+getValue()+";description : "+getDescription();
    }
}
