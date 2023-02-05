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
            List<String> macAddr = Arrays.asList(machineJpaEntity.getMacAdd().split("-"));
            List<String> ipAddr = Arrays.asList(machineJpaEntity.getIpAdd().split("-"));
            machineEntities.add(new MachineEntity(macAddr,ipAddr,machineJpaEntity.getHostName(),machineJpaEntity.getOs(),machineJpaEntity.isSnmp()));
        }
        return machineEntities;
    }

    //Map Domain -> Jpa

    public List<MachineJpaEntity> mapMachineDomainToJpa(List<MachineEntity> machineEntities){
        List<MachineJpaEntity> machineJpaEntities = new ArrayList<>();
        for (MachineEntity machineEntity:machineEntities){
            String macAddr = "";
            String ipAddr="";
            //Convert list to string for macAddress
            for (String mac:machineEntity.getMacAddr()) {
                macAddr+=mac;
                if(machineEntity.getMacAddr().indexOf(mac) != machineEntity.getMacAddr().size()-1){
                    macAddr+='-';
                }
            }
            //Convert list to string for ipAddress
            for (String ip:machineEntity.getIpAddr()){
                ipAddr+=ip;
                if(machineEntity.getIpAddr().indexOf(ip) != machineEntity.getIpAddr().size()-1){
                    ipAddr+='-';
                }
            }
            machineJpaEntities.add(new MachineJpaEntity(macAddr,ipAddr,machineEntity.getHostname(),machineEntity.getOs(),machineEntity.getSnmp()));
        }
        return machineJpaEntities;
    }
}
