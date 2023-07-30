package be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.unitaire;

import be.heh.backendappspringbootsnmp.domain.port.out.DeviceScannerPortOut;
import be.heh.backendappspringbootsnmp.infra.adaptateur.secondary.DeviceScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nmap4j.core.nmap.NMapExecutionException;
import org.nmap4j.core.nmap.NMapInitializationException;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(DeviceScanner.class)
public class DeviceScannerTest {

    @MockBean
    private DeviceScannerPortOut deviceScannerPortOut;

    @Test
    public void getAllIpOnNetworkTest() throws NMapExecutionException, NMapInitializationException {
        String ipRange="192.168.0.1-192.168.0.254";
        List<String> ipAddresses = Arrays.asList("192.168.0.1","192.168.0.15","192.168.0.9","192.168.0.23","192.168.0.28");
        Mockito.when(deviceScannerPortOut.getAllIpOnNetwork(ipRange)).thenReturn(ipAddresses);

        List<String> actualIpAddresses = deviceScannerPortOut.getAllIpOnNetwork(ipRange);

        Assertions.assertEquals(ipAddresses,actualIpAddresses);
    }
}
