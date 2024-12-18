package drugdrop.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapResponse {
    private String locationName;
    private String locationAddress;
    private String latitude;
    private String longitude;
    private String locationPhoto;
}
