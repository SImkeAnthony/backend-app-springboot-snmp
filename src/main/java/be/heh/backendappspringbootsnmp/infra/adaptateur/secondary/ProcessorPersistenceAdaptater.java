package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary;

import be.heh.backendappspringbootsnmp.domain.entities.Processor;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper.ProcessorMapper;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.ProcessorRepository;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.ProcessorJpa;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Proc;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProcessorPersistenceAdaptater {
    @Getter
    private final ProcessorRepository processorRepository;
    @Getter
    private final ProcessorMapper processorMapper;

    @Getter
    @Setter
    private List<Processor> processors = new ArrayList<>();
    @Getter
    @Setter
    private List<ProcessorJpa> processorJpaList = new ArrayList<>();

    public List<Processor> getAllProcessorsByIdMachine(Long idMachine){
        setProcessorJpaList(processorRepository.findAllByIdMachine(idMachine));
        return processorMapper.mapProcessorJpaToDomain(getProcessorJpaList());
    }

    public void registerProcessors(List<Processor> processors,Long idMachine){
        setProcessorJpaList(processorMapper.mapProcessorDomainToJpa(processors,idMachine));
        for(ProcessorJpa processorJpa:getProcessorJpaList()){
            try{
                processorRepository.save(processorJpa);
            }catch (Exception e){
                System.err.println("Error register entities : "+e.getMessage());
            }
        }
    }

    public List<Processor> getAllProcessors(){
        setProcessorJpaList(processorRepository.findAll());
        return processorMapper.mapProcessorJpaToDomain(getProcessorJpaList());
    }

}
