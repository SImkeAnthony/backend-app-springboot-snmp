package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.MachineEntity;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.MachineJpaEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MachineMapper {

    //Map Jpa -> Domain

    public List<MachineEntity> mapMachineJpaToDomain(List<MachineJpaEntity> machineJpaEntities){
        List<MachineEntity> machineEntities = new ArrayList<>();
        for (MachineJpaEntity machineJpaEntity : machineJpaEntities){
            List<String> macAddr = Arrays.asList(machineJpaEntity.getMacAdd().split("/"));
            List<String> ipAddr = Arrays.asList(machineJpaEntity.getIpAdd().split("/"));
            machineEntities.add(new MachineEntity(macAddr,ipAddr,machineJpaEntity.getHostName(),machineJpaEntity.getOs(),machineJpaEntity.isSnmp()));
        }
        return machineEntities;
    }

    //Map Domain -> Jpa

    public List<MachineJpaEntity> mapMachineDomainToJpa(List<MachineEntity> machineEntities){
        List<MachineJpaEntity> machineJpaEntities = new ArrayList<>();
        for (MachineEntity machineEntity:machineEntities){
            String macAddr = String.join("/",machineEntity.getMacAddr());
            String ipAddr= String.join("/",machineEntity.getIpAddr());
            machineJpaEntities.add(new MachineJpaEntity(macAddr,ipAddr,machineEntity.getHostname(),machineEntity.getOs(),machineEntity.getSnmp()));
        }
        return machineJpaEntities;
    }

    public MachineJpaEntity mapMachineDomainToJpa(MachineEntity machineEntity){
        String macAddr = String.join("/",machineEntity.getMacAddr());
        String ipAddr= String.join("/",machineEntity.getIpAddr());
        return new MachineJpaEntity(macAddr,ipAddr,machineEntity.getHostname(),machineEntity.getOs(),machineEntity.getSnmp());
    }

    public MachineEntity mapMachineJpaToDomain(MachineJpaEntity machineJpaEntity){
        List<String> macAddr = Arrays.asList(machineJpaEntity.getMacAdd().split("/"));
        List<String> ipAddr = Arrays.asList(machineJpaEntity.getIpAdd().split("/"));
        return new MachineEntity(macAddr,ipAddr,machineJpaEntity.getHostName(),machineJpaEntity.getOs(),machineJpaEntity.isSnmp());
    }
}
