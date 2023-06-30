CREATE TABLE IF NOT EXISTS processor(
    id SERIAL PRIMARY KEY,
    reference VARCHAR(255) NOT NULL,
    core int NOT NULL,
    vcore int NOT NULL,
    frequency DOUBLE PRECISION,
    id_machine int NOT NULL,
    CONSTRAINT fk_processor_machine FOREIGN KEY (id_machine) REFERENCES machine_entity (id)
);