CREATE TABLE IF NOT EXISTS porcessor(
    id SERIAL PRIMARY KEY,
    reference VARCHAR(255) NOT NULL,
    core int NOT NULL,
    vcore int NOT NULL,
    frequency double,
    id_machine int,
    CONSTRAINT fk_processor_machine FOREIGN KEY (id_machine) REFERENCES machine_entity (id)
);