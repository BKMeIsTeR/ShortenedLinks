package shortened.links.ShortenedLinks.ShortenedLinks.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
@Entity
@Table(name = "links")
public class Links {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long  id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "long_URL", nullable = false, columnDefinition="text", unique = true)
    private String longURL;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    //7948800 sec = 3 mouth
    @Column(name = "lifetime", nullable = false, columnDefinition = "integer default 7948800")
    private Integer lifetime;

    @Column(name = "count", nullable = false)
    private AtomicInteger count = new AtomicInteger(0);

    public Links(String token, String longURL, Date createdDate) {
        this(token, longURL, createdDate, 7_948_800);
    }

    public Links(String token, String longURL, Date createdDate, Integer lifetime) {
        this.token = token;
        this.longURL = longURL;
        this.createdDate = createdDate;
        this.lifetime = lifetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Links )) return false;
        return id != null && id.equals(((Links) o).getId());
    }
}
