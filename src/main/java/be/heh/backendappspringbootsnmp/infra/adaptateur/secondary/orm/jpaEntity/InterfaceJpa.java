package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "interface")
public class InterfaceJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "description")
    private String description;
    @Column(name = "mac_address")
    private String macAddress;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "id_machine")
    private Long idMachine;

    //constructor
    public InterfaceJpa(){}
    public InterfaceJpa(String description,String macAddress,String ipAddress,Long idMachine){this.description=description;this.macAddress=macAddress;this.ipAddress=ipAddress;this.idMachine=idMachine;}
    public InterfaceJpa(Long id,String description,String macAddress,String ipAddress,Long idMachine){this.id=id;this.description=description;this.macAddress=macAddress;this.ipAddress=ipAddress;this.idMachine=idMachine;}
}
