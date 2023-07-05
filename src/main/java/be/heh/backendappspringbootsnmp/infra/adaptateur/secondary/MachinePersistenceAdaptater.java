package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.port.out.MachinePortOut;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.*;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class MachinePersistenceAdaptater implements MachinePortOut {

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;
    private final InterfacePersistenceAdaptater interfacePersistenceAdaptater;
    private final ProcessorPersistenceAdaptater processorPersistenceAdaptater;
    private final PersistentStoragePersistenceAdaptater persistentStoragePersistenceAdaptater;
    private final VolatileStoragePersistenceAdaptater volatileStoragePersistenceAdaptater;
    private final ServicePersistenceAdaptater servicePersistenceAdaptater;
    @Getter
    @Setter
    private List<MachineEntity> machineEntities = new ArrayList<>();
    @Getter
    @Setter
    private List<MachineJpaEntity> machineJpaEntities = new ArrayList<>();
    @Override
    public List<MachineEntity> getAllMachineEntities() {
        setMachineJpaEntities(machineRepository.findAll());
        setMachineEntities(machineMapper.mapMachineJpaToDomain(getMachineJpaEntities()));
        getMachineEntities().forEach(machine->{
            machine.setInterfaces(interfacePersistenceAdaptater.getAllInterfacesByIdMachine(machine.getId()));
            machine.setProcessors(processorPersistenceAdaptater.getAllProcessorsByIdMachine(machine.getId()));
            machine.setPersistentStorages(persistentStoragePersistenceAdaptater.getAllPersistentStoragesByIdMachine(machine.getId()));
            machine.setVolatileStorages(volatileStoragePersistenceAdaptater.getAllVolatileStoragesByIdMachine(machine.getId()));
            machine.setServices(servicePersistenceAdaptater.getAllServiceByIdMachine(machine.getId()));
        });
        //set reference to machineEntity
        getMachineEntities().forEach(machineEntity -> {
            machineEntity.getInterfaces().forEach(anInterface -> {anInterface.setMachineEntity(machineEntity);});
            machineEntity.getProcessors().forEach(processor -> {processor.setMachineEntity(machineEntity);});
            machineEntity.getPersistentStorages().forEach(persistentStorage -> {persistentStorage.setMachineEntity(machineEntity);});
            machineEntity.getVolatileStorages().forEach(volatileStorage -> {volatileStorage.setMachineEntity(machineEntity);});
            machineEntity.getServices().forEach(service -> {service.setMachineEntity(machineEntity);});
        });
        return getMachineEntities();
    }
    @Override
    public void registerMachineEntities(List<MachineEntity> machineEntities) {
        for(MachineEntity machineEntity : machineEntities){
            try {
                registerMachineEntity(machineEntity);
            }catch (Exception e){
                System.err.println("Error register entities : "+e.getMessage());
            }
        }
    }

    @Override
    public void updateMachineEntity(MachineEntity machineEntity) {
        //get current registered machine entity
    }

    @Override
    public void registerMachineEntity(MachineEntity machineEntity) {
        machineRepository.save(machineMapper.mapMachineDomainToJpa(machineEntity));
        Long id = machineEntity.getId();
        if(machineEntity.getId()==null){
            id = machineRepository.findByHostName(machineEntity.getHostname()).getId();
        }
        interfacePersistenceAdaptater.registerInterfaces(machineEntity.getInterfaces(),id);
        processorPersistenceAdaptater.registerProcessors(machineEntity.getProcessors(),id);
        persistentStoragePersistenceAdaptater.registerPersistentStorages(machineEntity.getPersistentStorages(),id);
        volatileStoragePersistenceAdaptater.registerVolatileStorages(machineEntity.getVolatileStorages(),id);
        servicePersistenceAdaptater.registerServices(machineEntity.getServices(),id);
    }

}
