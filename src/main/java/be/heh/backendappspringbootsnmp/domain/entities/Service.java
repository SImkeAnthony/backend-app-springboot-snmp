package be.heh.backendappspringbootsnmp.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

public class Service {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private  String description;
    @Getter
    @Setter
    private String port;
    @Getter
    @Setter
    @JsonBackReference
    private MachineEntity machineEntity;

    public Service(String name,String description,String port){
        setName(name);
        setDescription(description);
        setPort(port);
    }
    public Service(Long id,String name,String description,String port){
        setId(id);
        setName(name);
        setDescription(description);
        setPort(port);
    }

    @Override
    public String toString(){
        return String.join(" : ",name,description,port);
    }
}
