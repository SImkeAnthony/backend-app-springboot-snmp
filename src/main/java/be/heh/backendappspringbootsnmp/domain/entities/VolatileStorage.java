package be.heh.backendappspringbootsnmp.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

public class VolatileStorage {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String reference;
    @Getter
    @Setter
    private Double available;
    @Getter
    @Setter
    private Double frequency;
    @Getter
    @Setter
    private Double latency;
    @Getter
    @Setter
    @JsonBackReference
    private MachineEntity machineEntity;

    public VolatileStorage(String reference,Double available,Double frequency,Double latency){
        setReference(reference);
        setAvailable(available);
        setFrequency(frequency);
        setLatency(latency);
    }
    public VolatileStorage(Long id,String reference,Double available,Double frequency,Double latency){
        setId(id);
        setReference(reference);
        setAvailable(available);
        setFrequency(frequency);
        setLatency(latency);
    }

    @Override
    public String toString(){
        return String.join(" : ",reference,available.toString(),frequency.toString(), latency.toString());
    }
}
