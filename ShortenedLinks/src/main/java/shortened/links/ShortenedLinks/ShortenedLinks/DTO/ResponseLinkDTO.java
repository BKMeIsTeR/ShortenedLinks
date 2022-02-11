package shortened.links.ShortenedLinks.ShortenedLinks.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shortened.links.ShortenedLinks.ShortenedLinks.entity.Links;

@NoArgsConstructor
@Getter
@Setter
public class ResponseLinkDTO {
    private String token;

    private Integer count;

    public ResponseLinkDTO(Links link) {
        this.token = link.getToken();
        this.count = link.getCount().get();
    }
}
