package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "volatile_storage")
public class VolatileStorageJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "reference")
    private String reference;
    @Column(name = "available")
    private Double available;
    @Column(name = "frequency")
    private Double frequency;
    @Column(name = "latency")
    private Double latency;
    @Column(name = "id_machine")
    private Long idMachine;

    public VolatileStorageJpa(){}
    public VolatileStorageJpa(String reference,Double available,Double frequency,Double latency,Long idMachine){this.reference = reference;this.available=available;this.frequency=frequency;this.latency=latency;this.idMachine=idMachine;}
    public VolatileStorageJpa(Long id,String reference,Double available,Double frequency,Double latency,Long idMachine){this.id=id;this.reference = reference;this.available=available;this.frequency=frequency;this.latency=latency;this.idMachine=idMachine;}
}
