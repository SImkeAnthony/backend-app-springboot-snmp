package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.PersistentStorage;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.PersistentStorageMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.PersistentStorageRepository;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.PersistentStorageJpa;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PersistentStoragePersistenceAdaptater {
    @Getter
    private final PersistentStorageRepository persistentStorageRepository;
    @Getter
    private final PersistentStorageMapper persistentStorageMapper;

    @Getter
    @Setter
    private List<PersistentStorage> persistentStorages = new ArrayList<>();
    @Getter
    @Setter
    private List<PersistentStorageJpa> persistentStorageJpaList = new ArrayList<>();

    public List<PersistentStorage> getAllPersistentStoragesByIdMachine(Long idMachine){
        setPersistentStorageJpaList(persistentStorageRepository.findAllByIdMachine(idMachine));
        return persistentStorageMapper.mapPersiStorageJpaToDomain(getPersistentStorageJpaList());
    }

    public void registerPersistentStorages(List<PersistentStorage> persistentStorages,Long idMachine){
        setPersistentStorageJpaList(persistentStorageMapper.mapPersiStorageDomainToJpa(persistentStorages,idMachine));
        for(PersistentStorageJpa persistentStorageJpa : getPersistentStorageJpaList()){
            try {
                persistentStorageRepository.save(persistentStorageJpa);
            }catch (Exception e){
                System.err.println("Error register entities : "+e.getMessage());
            }
        }
    }

    public List<PersistentStorage> getAllPersistentStorages(){
        setPersistentStorageJpaList(persistentStorageRepository.findAll());
        return persistentStorageMapper.mapPersiStorageJpaToDomain(getPersistentStorageJpaList());
    }

}
