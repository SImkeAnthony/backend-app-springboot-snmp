package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.VolatileStorage;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.VolatileStorageMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.VolatileStorageRepository;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.VolatileStorageJpa;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class VolatileStoragePersistenceAdaptater {
    @Getter
    private final VolatileStorageRepository volatileStorageRepository;
    @Getter
    private final VolatileStorageMapper volatileStorageMapper;

    @Getter
    @Setter
    private List<VolatileStorage> volatileStorages = new ArrayList<>();
    @Getter
    @Setter
    private List<VolatileStorageJpa> volatileStorageJpaList = new ArrayList<>();

    public List<VolatileStorage> getAllVolatileStoragesByIdMachine(Long idMachine){
        setVolatileStorageJpaList(volatileStorageRepository.findAllByIdMachine(idMachine));
        return volatileStorageMapper.mapVolatileStorageJpaToDomain(getVolatileStorageJpaList());
    }

    public void registerVolatileStorages(List<VolatileStorage> volatileStorages,Long idMachine){
        setVolatileStorageJpaList(volatileStorageMapper.mapVolatileStorageDomainToJpa(volatileStorages,idMachine));
        for(VolatileStorageJpa volatileStorageJpa : getVolatileStorageJpaList()){
            try {
                volatileStorageRepository.save(volatileStorageJpa);
            }catch (Exception e){
                System.err.println("Error register entities : "+e.getMessage());
            }

        }
    }
}
