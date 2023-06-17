package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.VolatileStorage;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.VolatileStorageJpa;

import java.util.ArrayList;
import java.util.List;

public class VolatileStoragePersistanceAdaptater {
    public List<VolatileStorageJpa> mapVolatileStorageDomainToJpa(List<VolatileStorage> volatileStorages, Long idMachine){
        List<VolatileStorageJpa> volatileStorageJpaList = new ArrayList<>();
        for(VolatileStorage volatileStorage : volatileStorages){
            volatileStorageJpaList.add(mapVolatileStorageDomainToJpa(volatileStorage,idMachine));
        }
        return volatileStorageJpaList;
    }
    public List<VolatileStorage> mapVolatileStorageJpaToDomain(List<VolatileStorageJpa> volatileStorageJpaList){
        List<VolatileStorage> volatileStorages = new ArrayList<>();
        for(VolatileStorageJpa volatileStorageJpa : volatileStorageJpaList){
            volatileStorages.add(mapVolatileStorageJpaToDomain(volatileStorageJpa));
        }
        return volatileStorages;
    }

    public VolatileStorageJpa mapVolatileStorageDomainToJpa(VolatileStorage volatileStorage,Long idMachine){
        if(volatileStorage.getId()!=null){
            return new VolatileStorageJpa(volatileStorage.getId(),volatileStorage.getReference(),volatileStorage.getAvailable(),volatileStorage.getFrequency(),volatileStorage.getLatency(),idMachine);
        }else {
            return new VolatileStorageJpa(volatileStorage.getReference(),volatileStorage.getAvailable(),volatileStorage.getFrequency(),volatileStorage.getLatency(),idMachine);
        }
    }
    public VolatileStorage mapVolatileStorageJpaToDomain(VolatileStorageJpa volatileStorageJpa){
        return new VolatileStorage(volatileStorageJpa.getId(),volatileStorageJpa.getReference(),volatileStorageJpa.getAvailable(),volatileStorageJpa.getFrequency(),volatileStorageJpa.getLatency());
    }
}
