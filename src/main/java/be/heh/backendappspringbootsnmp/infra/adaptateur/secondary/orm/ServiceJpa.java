package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "service")
public class ServiceJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "port")
    private String port;

    public ServiceJpa(){}
    public ServiceJpa(String name, String description, String port){this.name=name;this.description=description;this.port=port;}
    public ServiceJpa(Long id, String name, String description, String port){this.id=id;this.name=name;this.description=description;this.port=port;}
}
