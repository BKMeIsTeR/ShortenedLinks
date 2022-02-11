package shortened.links.ShortenedLinks.ShortenedLinks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shortened.links.ShortenedLinks.ShortenedLinks.DTO.ResponseLinkDTO;
import shortened.links.ShortenedLinks.ShortenedLinks.entity.Links;
import shortened.links.ShortenedLinks.ShortenedLinks.exceptions.BaseException;
import shortened.links.ShortenedLinks.ShortenedLinks.repository.LinksRepository;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class LinksService {
    private LinksRepository linksRepository;

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz1234567890";
    //Минимальное длина токена
    private static final byte MIN_LENGTH = 8;

    @Autowired
    public LinksService(LinksRepository linksRepository) {
        this.linksRepository = linksRepository;
    }

    public List<Links> showAll() {
        return linksRepository.findAll();
    }

    /**
     * Метод добавления полных URl в БД
     * @param longURL полный URL
     * @param date дата создания короткой ссылки
     * @return короткую ссылку
     */
    public String addShort(String longURL, Date date) {
        if (longURL.trim().isEmpty())
            throw new BaseException("Исходный URL пуст");

        Optional<Links> link = linksRepository.findByLongURL(longURL);

        if (checkExistence(link)) {
            throw new BaseException("Короткая ссылка с предоставленным URL уже существует: " + shortURL(link.get().getToken()));
        }

        String token = createToken();

        linksRepository.save(new Links(token, longURL, date));

        return shortURL(token);
    }

    /**
     * Метод для удаления полного URL из БД по токену
     * @param token токен
     */
    public void deleteLink(String token) {
        if (token.trim().isEmpty())
            throw new BaseException("Исходный токен пуст");

        Optional<Links> link = linksRepository.findByToken(token);

        if (!link.isPresent()) {
            throw new BaseException("Полного URL по предоставленному токену : " + token + " - не существует");
        }

        linksRepository.delete(link.get());
    }

    /**
     * Метод для редиректа с короткой ссылки на полный URL
     * @param token токен
     * @return полный URL
     */
    public String transitionLongURL(String token) {
        if (token.trim().isEmpty())
            throw new BaseException("Исходный токен пуст");

        Optional<Links> link = linksRepository.findByToken(token);

        if (!checkExistence(link)) {
            throw new BaseException("Полного URL по предоставленному токену : " + token + " - не существует");
        }

        //Увеличить счетчик переходов и сохранить новое значение
        link.get().getCount().incrementAndGet();
        linksRepository.save(link.get());

        return link.get().getLongURL();
    }

    /**
     * Метод получения статистики переходов по ссылкам
     * @return список со статистикой в порядке убывания количества переходов вида (токен, кол-во переходов)
     */
    public List<ResponseLinkDTO> metrics() {
        List<Links> links = new ArrayList<>();

        links = linksRepository.findAll();

        if (links.isEmpty())
            throw new BaseException("В базе данных нет коротких ссылок");

        List<ResponseLinkDTO> response = new ArrayList<>();

        for (int i = 0; i < links.size(); i++)
            response.add(new ResponseLinkDTO(links.get(i)));

        //Отсортировать в порядке убывания
        return response.stream().sorted(Comparator.comparingInt(ResponseLinkDTO::getCount).reversed()).collect(Collectors.toList());
    }

    /**
     * Метод проверки ссылки на существование
     * @param link ссылка
     * @return true/false
     */
    private boolean checkExistence(Optional<Links> link) {
        if (link.isPresent()) {
            Timestamp endLifeTime = Timestamp.from(link.get().getCreatedDate().toInstant().plusSeconds(link.get().getLifetime()));

            if (endLifeTime.compareTo(new Timestamp(System.currentTimeMillis())) == -1) {
                linksRepository.delete(link.get());
                return false;
            }

            return true;
        }

        return false;
    }

    /**
     * Метод генерации токена короткой ссылки
     * @return токен
     */
    private String createToken() {
        StringBuilder sb = new StringBuilder();

        Optional<Links> link = null;

        do {
            sb.setLength(0);

            while (sb.length() < MIN_LENGTH)
                sb.append(ALPHABET.charAt(ThreadLocalRandom.current().nextInt(36)));

            link = linksRepository.findByToken(sb.toString());
        } while (checkExistence(link));

        return sb.toString();
    }

    /**
     * Метод для вывода короткой ссылки
     * @param token токен
     * @return короткую ссылку
     */
    private String shortURL(String token) {
        return "http://localhost:8080/" + token;
    }

    /**region Другой способ генерации token`а
    private String encode(String longURL) {
        Matcher match = Pattern.compile(".+(:\\/\\/)(.*)").matcher(longURL);

        if (!match.find())
            throw new BaseException("Некорректный Url");

        String encodedUrl = Base64.getUrlEncoder().encodeToString(match.group(2).getBytes());

        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedUrl);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < decodedBytes.length; i++)
            sb.append(ALPHABET.charAt(decodedBytes[i] % 36));

        while (sb.length() < 8) {
            sb.append(ALPHABET.charAt(ThreadLocalRandom.current().nextInt(256) % 36));
        }

        String res = sb.toString();

        if (res.length() > 8)
            return res.substring(0, 8);

        return res;
    }*/
}
