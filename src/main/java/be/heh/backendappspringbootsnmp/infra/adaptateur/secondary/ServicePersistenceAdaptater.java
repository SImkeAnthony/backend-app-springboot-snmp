package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.Service;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.ServiceMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.ServiceRepository;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.ServiceJpa;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServicePersistenceAdaptater {
    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Getter
    @Setter
    private List<Service> services = new ArrayList<>();
    @Getter
    @Setter
    private List<ServiceJpa> serviceJpaList = new ArrayList<>();

    public List<Service> getAllServiceByIdMachine(Long idMachine){
        setServiceJpaList(serviceRepository.findAllByIdMachine(idMachine));
        return serviceMapper.mapServiceJpaToDomain(getServiceJpaList());
    }

    public void registerServices(List<Service> services,Long idMachine){
        setServiceJpaList(serviceMapper.mapServiceDomainToJpa(services,idMachine));
        for(ServiceJpa serviceJpa : getServiceJpaList()){
            try {
                serviceRepository.save(serviceJpa);
            }catch (Exception e){
                System.err.println("Error register entities : "+e.getMessage());
            }
        }
    }
}
