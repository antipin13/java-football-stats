package ru.local.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.local.service.parser.MatchParserService;
import ru.local.service.scrap.WebScrapingService;

@RestController
@RequestMapping("/web-scrap/html")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class WebScrappingController {
    final WebScrapingService webScrapingService;
    final MatchParserService matchParserService;

    @GetMapping
    public String getHtml(@RequestParam String url) {
        webScrapingService.printRawHtml(url);

        return "HTML выведен в консоль! Проверьте логи вашего приложения.";
    }

    @GetMapping("/matches")
    public String getParseMatches(@RequestParam String url) {
        String htmlContent = webScrapingService.printRawHtml(url);

        matchParserService.parseMatchesFromHtml(htmlContent);

        return "Матчи распарсены и добавлены в БД";
    }
}
