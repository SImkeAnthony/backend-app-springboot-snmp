package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.Interface;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.InterfaceMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.InterfaceRepository;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.InterfaceJpa;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class InterfacePersistanceAdaptater {
    private final InterfaceRepository interfaceRepository;
    private final InterfaceMapper interfaceMapper;

    @Getter
    @Setter
    private List<Interface> interfaces = new ArrayList<>();
    @Getter
    @Setter
    private List<InterfaceJpa> interfaceJpaList = new ArrayList<>();

    public List<Interface> getAllInterfacesByIdMachine(Long idMachine){
        setInterfaceJpaList(interfaceRepository.findAllByIdMachine(idMachine));
        return interfaceMapper.mapInterfaceJpaToDomain(getInterfaceJpaList());
    }

    public void registerInterfaces(List<Interface> interfaces,Long idMachine){
        setInterfaceJpaList(interfaceMapper.mapInterfaceDomainToJpa(interfaces,idMachine));
        for(InterfaceJpa interfaceJpa : getInterfaceJpaList()){
            try{
                interfaceRepository.save(interfaceJpa);
            }catch (Exception e){
                System.err.println("Error register entities : "+e.getMessage());
            }
        }
    }

}
