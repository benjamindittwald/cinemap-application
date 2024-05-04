package de.dittwald.cinemap.repositoryui.movies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dittwald.cinemap.repositoryui.properties.Properties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TmdbClient {

    private final Properties properties;

    private final int timeout = 3000;

    public TmdbClient(Properties properties) {
        this.properties = properties;
    }

    public MovieTmdb getMovieTmdb(int id) {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.timeout)
                .responseTimeout(Duration.ofMillis(this.timeout))
                .doOnConnected(connection -> connection.addHandlerLast(
                                new ReadTimeoutHandler(this.timeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(this.timeout, TimeUnit.MILLISECONDS)));

        WebClient client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(this.properties.getTmdbApiBaseUrl())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        MovieTmdb movieTmdb;
        URL posterPath = null;
        String overview = null;
        String tagline = null;
        String imdbId;
        URL imdbMovieUrl = null;

        try {
            JsonNode node = objectMapper.readTree(client.get()
                    .uri(id + "?language=en-Us")
                    .headers(h -> h.setBearerAuth(this.properties.getTmdbApiReadToken()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block());

            try {
                posterPath = ((node.has("poster_path") && node.get("poster_path") == null) ?
                        new URI(this.properties.getTmdbImageBaseUrl() + "/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg").toURL() :
                        new URI(this.properties.getTmdbImageBaseUrl() + "/w300/" + node.get("poster_path").asText() +
                                ".jpg").toURL());
            } catch (MalformedURLException | URISyntaxException e) {
                log.debug("Error parsing poster_path for movie {}", id, e);
            }

            overview = (node.has("overview") ? node.get("overview").asText() : null);
            tagline = (node.has("tagline") ? node.get("tagline").asText(): null);
            imdbId = (node.has("imdb_id") ? node.get("imdb_id").asText(): null);

            try {
                imdbMovieUrl = new URI("https://www.imdb.com/title/" + imdbId).toURL();
            } catch (MalformedURLException | URISyntaxException e) {
                log.debug("Error parsing IMDB movie URL for movie {}", id, e);
            }


            movieTmdb = new MovieTmdb(posterPath, overview, tagline, imdbMovieUrl);

            return movieTmdb;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
