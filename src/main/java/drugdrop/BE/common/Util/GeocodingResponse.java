package drugdrop.BE.common.Util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GeocodingResponse {

    private List<Document> documents;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Document {
        private String x;
        private String y;
    }
}
