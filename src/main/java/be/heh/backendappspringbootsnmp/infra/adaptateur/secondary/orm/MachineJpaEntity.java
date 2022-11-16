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

    @Column(name = "mac_add")
    private String macAdd;
    @Column(name = "ip_add")
    private String ipAdd;

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "type_")
    private String type;
    @Column(name = "snmp")
    private boolean snmp;

    //constructor
    public MachineJpaEntity(){}
    public MachineJpaEntity(String macAdd,String ipAdd,String hostName,String type,boolean snmp){this.macAdd=macAdd;this.ipAdd=ipAdd;this.hostName=hostName;this.type=type;this.snmp=snmp;}
}
