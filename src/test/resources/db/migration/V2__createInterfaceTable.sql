CREATE TABLE IF NOT EXISTS interface(
    id SERIAL PRIMARY KEY,
    description VARCHAR(1024),
    mac_address VARCHAR(255) NOT NULL DEFAULT '00-00-00-00-00-00',
    ip_address VARCHAR(255),
    id_machine int NOT NULL,
    CONSTRAINT fk_interface_machine FOREIGN KEY (id_machine) REFERENCES machine_entity (id)
);