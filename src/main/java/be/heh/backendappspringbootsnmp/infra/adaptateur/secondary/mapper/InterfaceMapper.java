package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper;

import be.heh.backendappspringbootsnmp.domain.entities.Interface;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.InterfaceJpa;

import java.util.ArrayList;
import java.util.List;

public class InterfaceMapper {

    public List<Interface> mapInterfaceJpaToDomain(List<InterfaceJpa> interfaceJpaList){
        List<Interface> interfaces = new ArrayList<>();
        for(InterfaceJpa jpa : interfaceJpaList){
            interfaces.add(mapInterfaceJpaToDomain(jpa));
        }
        return interfaces;
    }
    public List<InterfaceJpa> mapInterfaceDomainToJpa(List<Interface> interfaceList,Long idMachine){
        List<InterfaceJpa> interfaceJpaList = new ArrayList<>();
        for(Interface interfaceDomain : interfaceList){
            interfaceJpaList.add(mapInterfaceDomainToJpa(interfaceDomain,idMachine));
        }
        return interfaceJpaList;
    }
    public Interface mapInterfaceJpaToDomain(InterfaceJpa interfaceJpa){
        return new Interface(interfaceJpa.getId(),interfaceJpa.getDescription(),interfaceJpa.getMacAddress(),interfaceJpa.getIpAddress());
    }
    public InterfaceJpa mapInterfaceDomainToJpa(Interface interfaceDomain,Long idMachine){
        if(interfaceDomain.getId()!=null){
            return new InterfaceJpa(interfaceDomain.getId(),interfaceDomain.getDescription(),interfaceDomain.getMacAddress(),interfaceDomain.getIpAddress(),idMachine);
        }else{
            return new InterfaceJpa(interfaceDomain.getDescription(),interfaceDomain.getMacAddress(),interfaceDomain.getIpAddress(),idMachine);
        }
    }
}
