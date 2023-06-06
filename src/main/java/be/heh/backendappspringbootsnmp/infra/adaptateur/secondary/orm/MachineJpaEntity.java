package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Data
@Table(name = "machine")
public class MachineJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mac_addr")
    private String macAdd;
    @Column(name = "ip_addr")
    private String ipAdd;

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "os")
    private String os;
    @Column(name = "snmp")
    private boolean snmp;

    //constructor
    public MachineJpaEntity(){}
    public MachineJpaEntity(String macAdd,String ipAdd,String hostName,String os,boolean snmp){this.macAdd=macAdd;this.ipAdd=ipAdd;this.hostName=hostName;this.os=os;this.snmp=snmp;}
}
