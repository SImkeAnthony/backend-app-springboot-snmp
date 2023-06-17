CREATE TABLE IF NOT EXISTS persistent_storage(
    id SERIAL PRIMARY KEY,
    reference VARCHAR(255) NOT NULL,
    available VARCHAR(255) NOT NULL,
    used VARCHAR(255) NOT NULL,
    id_machine int,
    CONSTRAINT fk_pers_storage_machine FOREIGN KEY (id_machine) REFERENCES machine_entity (id)
);