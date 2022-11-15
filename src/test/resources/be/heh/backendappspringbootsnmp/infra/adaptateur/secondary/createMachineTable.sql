CREATE TABLE IF NOT EXISTS machine(
    id SERIAL PRIMARY KEY;
    ip_add varchar(15);
    snmp boolean;
);