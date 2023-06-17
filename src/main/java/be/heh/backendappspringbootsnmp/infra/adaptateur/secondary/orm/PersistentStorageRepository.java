package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm;

import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.orm.jpaEntity.PersistentStorageJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistentStorageRepository extends JpaRepository<PersistentStorageJpa,Long> {
}
