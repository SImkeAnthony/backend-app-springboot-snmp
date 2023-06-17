package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.snmp.responder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class LockResponseCounter {
    @Getter
    private final int expectedResponse;
    @Getter
    @Setter
    private int receivedResponse = 0;

    @Getter
    private final Object lock = new Object();

    public void increment(){
        synchronized (getLock()){
            setReceivedResponse(getReceivedResponse()+1);
            if(getReceivedResponse()>=getExpectedResponse()){
                getLock().notify();
            }
        }
    }
    public void waitResponse() throws InterruptedException {
        synchronized (getLock()){
            if(getReceivedResponse()<getExpectedResponse()){
                getLock().wait();
            }
        }
    }
}
