package be.heh.backendappspringbootsnmp.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class MachineEntity {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String hostname;
    @Getter
    @Setter
    private String os;
    @Getter
    @Setter
    private Boolean snmp;
    @Getter
    @Setter
    @JsonManagedReference
    private List<Interface> interfaces = new ArrayList<>();
    @Getter
    @Setter
    @JsonManagedReference
    private List<Processor> processors = new ArrayList<>();
    @Getter
    @Setter
    @JsonManagedReference
    private List<PersistentStorage> persistentStorages = new ArrayList<>();
    @Getter
    @Setter
    @JsonManagedReference
    private List<VolatileStorage> volatileStorages = new ArrayList<>();
    @Getter
    @Setter
    @JsonManagedReference
    private List<Service> services = new ArrayList<>();

    public MachineEntity(String hostname,String os,Boolean snmp){
        setHostname(hostname);
        setOs(os);
        setSnmp(snmp);
    }
    public MachineEntity(Long id,String hostname,String os,Boolean snmp){
        setId(id);
        setHostname(hostname);
        setOs(os);
        setSnmp(snmp);
    }
    public MachineEntity(Long id,String hostname,String os,Boolean snmp,List<Interface> interfaces,List<Processor> processors,List<PersistentStorage> persistentStorages,List<VolatileStorage> volatileStorages,List<Service> services){
        setId(id);
        setHostname(hostname);
        setOs(os);
        setSnmp(snmp);
        setInterfaces(interfaces);
        setProcessors(processors);
        setPersistentStorages(persistentStorages);
        setVolatileStorages(volatileStorages);
        setServices(services);
    }

    @Override
    public String toString(){
        return String.join(" : ",hostname,os,snmp.toString());
    }
}
