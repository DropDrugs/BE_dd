package drugdrop.BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchImageResponse {

    private List<Item> items;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String link;
    }
}
