package shortened.links.ShortenedLinks.ShortenedLinks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shortened.links.ShortenedLinks.ShortenedLinks.entity.Links;

import java.util.Optional;

public interface LinksRepository extends JpaRepository<Links, Long>{
    Optional<Links> findByLongURL(String longURL);
    Optional<Links> findByToken(String token);
}



