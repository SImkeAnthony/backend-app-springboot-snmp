package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.Interface;
import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.domain.entities.PersistentStorage;
import be.heh.backendappspringbootsnmp.domain.entities.Service;
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
public class MachinePersistanceAdaptater implements MachinePortOut {

    private final MachineRepository machineRepository;
    private final MachineMapper machineMapper;
    private final InterfacePersistanceAdaptater interfacePersistanceAdaptater;
    private final ProcessorPersistanceAdaptater processorPersistanceAdaptater;
    private final PersistentStoragePersistanceAdaptater persistentStoragePersistanceAdaptater;
    private final VolatileStoragePersistanceAdaptater volatileStoragePersistanceAdaptater;
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
            machine.setInterfaces(interfacePersistanceAdaptater.getAllInterfacesByIdMachine(machine.getId()));
            machine.setProcessors(processorPersistanceAdaptater.getAllProcessorsByIdMachine(machine.getId()));
            machine.setPersistentStorages(persistentStoragePersistanceAdaptater.getAllPersistentStoragesByIdMachine(machine.getId()));
            machine.setVolatileStorages(volatileStoragePersistanceAdaptater.getAllVolatileStoragesByIdMachine(machine.getId()));
            machine.setServices(servicePersistenceAdaptater.getAllServiceByIdMachine(machine.getId()));
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
        interfacePersistanceAdaptater.registerInterfaces(machineEntity.getInterfaces(),id);
        processorPersistanceAdaptater.registerProcessors(machineEntity.getProcessors(),id);
        persistentStoragePersistanceAdaptater.registerPersistentStorages(machineEntity.getPersistentStorages(),id);
        volatileStoragePersistanceAdaptater.registerVolatileStorages(machineEntity.getVolatileStorages(),id);
        servicePersistenceAdaptater.registerServices(machineEntity.getServices(),id);
    }

}
