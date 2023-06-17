package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper;

import be.heh.backendappspringbootsnmp.domain.entities.PersistentStorage;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.PersistentStorageJpa;

import java.util.ArrayList;
import java.util.List;

public class PersistentStorageMapper {

    public List<PersistentStorageJpa> mapPersiStorageDomainToJpa(List<PersistentStorage> persistentStorages,Long idMachine){
        List<PersistentStorageJpa> persistentStorageJpaList = new ArrayList<>();
        for(PersistentStorage persistentStorage : persistentStorages){
            persistentStorageJpaList.add(mapPersiStorageDomainToJpa(persistentStorage,idMachine));
        }
        return persistentStorageJpaList;
    }
    public List<PersistentStorage> mapPersiStorageJpaToDomain(List<PersistentStorageJpa> persistentStorageJpaList){
        List<PersistentStorage> persistentStorages = new ArrayList<>();
        for(PersistentStorageJpa persistentStorageJpa : persistentStorageJpaList){
            persistentStorages.add(mapPersiStorageJpaToDomain(persistentStorageJpa));
        }
        return persistentStorages;
    }

    public PersistentStorageJpa mapPersiStorageDomainToJpa(PersistentStorage persistentStorage,Long idMachine){
        if(persistentStorage.getId()!=null){
            return new PersistentStorageJpa(persistentStorage.getId(),persistentStorage.getReference(),persistentStorage.getAvailable(),persistentStorage.getUsed(),idMachine);
        }else {
            return new PersistentStorageJpa(persistentStorage.getReference(),persistentStorage.getAvailable(),persistentStorage.getUsed(),idMachine);
        }
    }
    public PersistentStorage mapPersiStorageJpaToDomain(PersistentStorageJpa persistentStorageJpa){
        return new PersistentStorage(persistentStorageJpa.getId(),persistentStorageJpa.getReference(),persistentStorageJpa.getAvailable(),persistentStorageJpa.getUsed());
    }
}
