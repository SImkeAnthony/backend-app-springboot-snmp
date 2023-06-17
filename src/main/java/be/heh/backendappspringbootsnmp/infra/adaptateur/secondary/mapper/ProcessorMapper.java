package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.mapper;

import be.heh.backendappspringbootsnmp.domain.entities.Processor;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.ProcessorJpa;

import java.util.ArrayList;
import java.util.List;

public class ProcessorMapper {

    public List<ProcessorJpa> mapProcessorDomainToJpa(List<Processor> processors,Long idMachine){
        List<ProcessorJpa> processorJpaList = new ArrayList<>();
        for(Processor processor : processors){
            processorJpaList.add(mapProcessorDomainToJpa(processor,idMachine));
        }
        return processorJpaList;
    }
    public List<Processor> mapProcessorJpaToDomain(List<ProcessorJpa> processorJpaList){
        List<Processor> processors = new ArrayList<>();
        for(ProcessorJpa processorJpa : processorJpaList){
            processors.add(mapProcessorJpaToDomain(processorJpa));
        }
        return processors;
    }
    public ProcessorJpa mapProcessorDomainToJpa(Processor processor,Long idMachine){
        if(processor.getId()!=null){
            return new ProcessorJpa(processor.getId(),processor.getReference(),processor.getCore(),processor.getVCore(),processor.getFrequency(),idMachine);
        }else {
            return new ProcessorJpa(processor.getReference(),processor.getCore(),processor.getVCore(),processor.getFrequency(),idMachine);
        }
    }
    public Processor mapProcessorJpaToDomain(ProcessorJpa processorJpa){
        return new Processor(processorJpa.getId(),processorJpa.getReference(),processorJpa.getCore(),processorJpa.getVCore(),processorJpa.getFrequency());
    }
}
