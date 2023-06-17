CREATE TABLE IF NOT EXISTS volatile_storage(
    id SERIAL PRIMARY KEY,
    reference VARCHAR(255) NOT NULL,
    available int NOT NULL,
    frequency int,
    latency int,
    id_machine int NOT NULL,
    CONSTRAINT fk_vol_storage_machine FOREIGN KEY (id_machine) REFERENCES machine_entity (id)
);