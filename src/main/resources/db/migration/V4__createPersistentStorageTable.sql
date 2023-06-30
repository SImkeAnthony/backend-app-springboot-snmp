CREATE TABLE IF NOT EXISTS persistent_storage(
    id SERIAL PRIMARY KEY,
    reference VARCHAR(255) NOT NULL,
    available DOUBLE PRECISION NOT NULL,
    used DOUBLE PRECISION NOT NULL,
    id_machine int NOT NULL,
    CONSTRAINT fk_pers_storage_machine FOREIGN KEY (id_machine) REFERENCES machine_entity (id)
);