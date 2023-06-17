package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "machine_entity")
public class MachineJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "hostname")
    private String hostName;

    @Column(name = "os")
    private String os;
    @Column(name = "snmp")
    private boolean snmp;

    //constructor
    public MachineJpaEntity(){}
    public MachineJpaEntity(String hostName,String os,boolean snmp){this.hostName=hostName;this.os=os;this.snmp=snmp;}
    public MachineJpaEntity(Long id,String hostName,String os,boolean snmp){this.id=id;this.hostName=hostName;this.os=os;this.snmp=snmp;}
}
