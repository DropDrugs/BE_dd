package drugdrop.BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPlaceResponse {

    private List<Item> items;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String mapx; // WGS84 좌표계
        private String mapy;
        private String roadAddress;
        private String title;
    }
}
