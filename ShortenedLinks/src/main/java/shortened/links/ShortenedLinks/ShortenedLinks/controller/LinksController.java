package shortened.links.ShortenedLinks.ShortenedLinks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shortened.links.ShortenedLinks.ShortenedLinks.DTO.LongURLDto;
import shortened.links.ShortenedLinks.ShortenedLinks.service.LinksService;

import java.util.Date;

@Controller
public class LinksController {
    private LinksService linksService;

    @Autowired
    public LinksController(LinksService linksService) {
        this.linksService = linksService;
    }

    @GetMapping("/{shortURL}")
    public String transitionLongURL(@PathVariable(value = "shortURL") String token) {
        return "redirect:"+linksService.transitionLongURL(token);
    }

    @PostMapping("/links")
    public ResponseEntity<String> addShort(@RequestBody LongURLDto longURL) {
        return new ResponseEntity<>("Короткая ссылка на URL: " + longURL.getUrl() + "\n" +
                "Успешно создана: \t" + linksService.addShort( longURL.getUrl(), new Date()), HttpStatus.CREATED);
    }
}
