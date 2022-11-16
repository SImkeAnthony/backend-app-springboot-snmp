package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.MachineJpaEntity;

import java.util.ArrayList;
import java.util.List;

public class MachineMapper {

    //Map Jpa -> Domain

    public List<MachineEntity> mapMachineJpaToDomain(List<MachineJpaEntity> machineJpaEntities){
        List<MachineEntity> machineEntities = new ArrayList<>();
        for (MachineJpaEntity machineJpaEntity : machineJpaEntities){
            machineEntities.add(new MachineEntity(machineJpaEntity.getMacAdd(),machineJpaEntity.getIpAdd(),machineJpaEntity.getHostName(),machineJpaEntity.getType(),machineJpaEntity.isSnmp()));
        }
        return machineEntities;
    }

    //Map Domain -> Jpa

    public List<MachineJpaEntity> mapMachineDomainToJpa(List<MachineEntity> machineEntities){
        List<MachineJpaEntity> machineJpaEntities = new ArrayList<>();
        for (MachineEntity machineEntity:machineEntities){
            machineJpaEntities.add(new MachineJpaEntity(machineEntity.macAdd(),machineEntity.ipAdd(),machineEntity.hostName(),machineEntity.type(),machineEntity.snmp()));
        }
        return machineJpaEntities;
    }
}
