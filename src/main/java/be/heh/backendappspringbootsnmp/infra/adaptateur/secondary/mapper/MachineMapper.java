package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.MachineJpaEntity;

import java.util.ArrayList;
import java.util.List;

public class MachineMapper {

    public List<MachineEntity> mapMachineJpaToDomain(List<MachineJpaEntity> machineJpaEntities){
        List<MachineEntity> machineEntities = new ArrayList<>();
        for(MachineJpaEntity machineJpaEntity : machineJpaEntities){
            machineEntities.add(mapMachineJpaToDomain(machineJpaEntity));
        }
        return machineEntities;
    }

    public List<MachineJpaEntity> mapMachineDomainToJpa(List<MachineEntity> machineEntities){
        List<MachineJpaEntity> machineJpaEntities = new ArrayList<>();
        for(MachineEntity machineEntity : machineEntities){
            machineJpaEntities.add(mapMachineDomainToJpa(machineEntity));
        }
        return machineJpaEntities;
    }

    public MachineJpaEntity mapMachineDomainToJpa(MachineEntity machineEntity){
        if(machineEntity.getId()!=null){
            return new MachineJpaEntity(machineEntity.getId(),machineEntity.getHostname(),machineEntity.getOs(),machineEntity.getSnmp());
        }else {
            return new MachineJpaEntity(machineEntity.getHostname(),machineEntity.getOs(),machineEntity.getSnmp());
        }
    }

    public MachineEntity mapMachineJpaToDomain(MachineJpaEntity machineJpaEntity){
        return new MachineEntity(machineJpaEntity.getId(),machineJpaEntity.getHostName(),machineJpaEntity.getOs(),machineJpaEntity.isSnmp());
    }
}
