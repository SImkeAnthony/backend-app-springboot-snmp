package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper;

import be.heh.backendappspringbootsnmp.domain.entities.Service;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.ServiceJpa;

import java.util.ArrayList;
import java.util.List;

public class ServiceMapper {
    public List<Service> mapServiceJpaToDomain(List<ServiceJpa> serviceJpaList){
        List<Service> services = new ArrayList<>();
        for(ServiceJpa serviceJpa : serviceJpaList){
            services.add(mapServiceJpaToDomain(serviceJpa));
        }
        return services;
    }
    public List<ServiceJpa> mapServiceDomainToJpa(List<Service> services, Long idMachine){
        List<ServiceJpa> serviceJpaList = new ArrayList<>();
        for(Service service : services){
            serviceJpaList.add(mapServiceDomainToJpa(service,idMachine));
        }
        return serviceJpaList;
    }
    public Service mapServiceJpaToDomain(ServiceJpa serviceJpa){
        return new Service(serviceJpa.getId(),serviceJpa.getName(),serviceJpa.getDescription(),serviceJpa.getPort());
    }
    public ServiceJpa mapServiceDomainToJpa(Service service, Long idMachine){
        if(service.getId()!=null){
            return new ServiceJpa(service.getId(),service.getName(),service.getDescription(),service.getPort(),idMachine);
        }else {
            return new ServiceJpa(service.getName(),service.getDescription(),service.getPort(),idMachine);
        }
    }
}
