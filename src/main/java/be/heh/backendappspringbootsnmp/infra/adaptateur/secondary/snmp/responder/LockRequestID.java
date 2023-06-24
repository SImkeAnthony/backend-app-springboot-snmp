package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder;

public class LockRequestID {
    private int requestID;

    public LockRequestID(int requestID){
        this.requestID = requestID;
    }
    public synchronized void setRequestID(int requestID){
        this.requestID = requestID;
    }
    public synchronized int getRequestID(){
        return this.requestID;
    }


}
