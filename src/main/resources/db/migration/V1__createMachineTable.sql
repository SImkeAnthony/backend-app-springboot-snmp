CREATE TABLE IF NOT EXISTS machine_entity(
    id SERIAL PRIMARY KEY,
    hostname varchar(255),
    os varchar(255),
    snmp boolean
);