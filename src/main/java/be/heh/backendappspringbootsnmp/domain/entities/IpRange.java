package be.heh.backendappspringbootsnmp.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import java.util.Objects;

public class IpRange {
    @Getter
    @Setter
    private String ipRange="";

    public IpRange(){}
    public IpRange(JSONObject jsonObject){
        setIpRange(jsonObject.getString("ipRange"));
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        IpRange other = (IpRange) obj;
        return Objects.equals(ipRange, other.ipRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipRange);
    }

}
