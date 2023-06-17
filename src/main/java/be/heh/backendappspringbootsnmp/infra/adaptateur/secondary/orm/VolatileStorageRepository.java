package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.VolatileStorageJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolatileStorageRepository extends JpaRepository<VolatileStorageJpa,Long> {
}
