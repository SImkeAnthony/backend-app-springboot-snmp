CREATE TABLE IF NOT EXISTS machine_entity(
    id SERIAL PRIMARY KEY,
    hostname varchar(255) NOT NULL DEFAULT 'default hostname',
    os varchar(255) NOT NULL DEFAULT 'default os',
    snmp boolean DEFAULT False
);