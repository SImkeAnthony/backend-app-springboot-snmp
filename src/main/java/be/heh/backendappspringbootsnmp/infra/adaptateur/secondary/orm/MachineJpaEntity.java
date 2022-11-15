package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Data
@Table(name = "machine")
public class MachineJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip_add")
    private String ipAdd;

    @Column(name = "snmp")
    private boolean snmp;

    //constructor
    public MachineJpaEntity(){}
    public MachineJpaEntity(String ipAdd,boolean snmp){this.ipAdd=ipAdd;this.snmp=snmp;}
}
