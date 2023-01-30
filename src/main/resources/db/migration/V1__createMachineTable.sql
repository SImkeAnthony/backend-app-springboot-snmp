CREATE TABLE IF NOT EXISTS machine(
    id SERIAL PRIMARY KEY,
    mac_addr varchar(17),
    ip_addr varchar(15),
    host_name varchar(256),
    type_ varchar(256),
    snmp boolean
);