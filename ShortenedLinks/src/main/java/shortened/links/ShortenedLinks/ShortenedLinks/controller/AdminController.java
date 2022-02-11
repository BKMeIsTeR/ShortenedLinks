package shortened.links.ShortenedLinks.ShortenedLinks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shortened.links.ShortenedLinks.ShortenedLinks.DTO.ShortURLDto;
import shortened.links.ShortenedLinks.ShortenedLinks.entity.Links;
import shortened.links.ShortenedLinks.ShortenedLinks.service.LinksService;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    LinksService linksService;

    @Autowired
    public AdminController(LinksService linksService) {
        this.linksService = linksService;
    }

    @GetMapping("/links")
    @ResponseBody
    public List<Links> showAllLinks() {
        return linksService.showAll();
    }

    @DeleteMapping("/links")
    public ResponseEntity<String> deleteLink(@RequestBody ShortURLDto shortURL) {
        linksService.deleteLink(shortURL.getUrl());

        return new ResponseEntity<>("Короткая ссылка: " + shortURL.getUrl() + "\n Успешно удалена!", HttpStatus.OK);
    }

    @GetMapping(value = "/metric")
    @ResponseBody
    public List getMetric() {
        return linksService.metrics();
    }
}
