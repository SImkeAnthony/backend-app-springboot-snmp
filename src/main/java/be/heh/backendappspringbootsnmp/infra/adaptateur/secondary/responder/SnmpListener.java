package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.responder;

import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.smi.Address;

public class SnmpListener implements ResponseListener {
    @Override
    public <A extends Address> void onResponse(ResponseEvent<A> event) {
        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
        System.out.println("Received message : "+event.getResponse());
    }
}
