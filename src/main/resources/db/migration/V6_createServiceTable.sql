CREATE TABLE IF NOT EXISTS service(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    port VARCHAR(255),
    id_machine int,
    CONSTRAINT fk_service_machine FOREIGN KEY (id_machine) REFERENCES machine_entity (id)
);