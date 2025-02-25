package drugdrop.BE.common.Util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingResponse {

    private List<Address> addresses;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String x;
        private String y;
    }
}
