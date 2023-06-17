package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "processor")
public class ProcessorJpa {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "reference")
    private String reference;
    @Column(name = "core")
    private Integer core;
    @Column(name = "vcore")
    private Integer vCore;
    @Column(name = "frequency")
    private Double frequency;
    @Column(name = "id_machine")
    private Long idMachine;

    public ProcessorJpa(){}
    public ProcessorJpa(String reference,Integer core,Integer vCore,Double frequency,Long idMachine){this.reference=reference;this.core=core;this.vCore=vCore;this.frequency=frequency;this.idMachine=idMachine;}
    public ProcessorJpa(Long id,String reference,Integer core,Integer vCore,Double frequency,Long idMachine){this.id=id;this.reference=reference;this.core=core;this.vCore=vCore;this.frequency=frequency;this.idMachine=idMachine;}
}
