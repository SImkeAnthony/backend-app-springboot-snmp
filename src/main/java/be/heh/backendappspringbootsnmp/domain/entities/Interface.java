package be.heh.backendappspringbootsnmp.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
public class Interface {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String macAddress;
    @Getter
    @Setter
    private String ipAddress;
    @Getter
    @Setter
    @JsonBackReference
    private MachineEntity machineEntity;

    public Interface(String macAddress,String description,String ipAddress){
        setDescription(description);
        setIpAddress(ipAddress);
        setMacAddress(macAddress);
    }
    public Interface(Long id,String description,String macAddress,String ipAddress){
        setId(id);
        setDescription(description);
        setIpAddress(ipAddress);
        setMacAddress(macAddress);
    }

    @Override
    public String toString(){
        return String.join(" : ",description,ipAddress,macAddress);
    }
}
