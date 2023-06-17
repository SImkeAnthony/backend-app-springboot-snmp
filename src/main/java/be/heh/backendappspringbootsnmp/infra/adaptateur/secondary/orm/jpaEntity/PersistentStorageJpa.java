package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "persistent_storage")
public class PersistentStorageJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "reference")
    private String reference;
    @Column(name = "available")
    private Double available;
    @Column(name = "used")
    private Double used;
    @Column(name = "id_machine")
    private Long idMachine;

    public PersistentStorageJpa(){}
    public PersistentStorageJpa(String reference,Double available,Double used,Long idMachine){this.reference = reference;this.available=available;this.used=used;this.idMachine=idMachine;}
    public PersistentStorageJpa(Long id,String reference,Double available,Double used,Long idMachine){this.id=id;this.reference = reference;this.available=available;this.used=used;this.idMachine=idMachine;}
}
