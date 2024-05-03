package de.dittwald.cinemap.repositoryui.movies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RepositoryClient {

    int timeout = 3000;

    public List<Movie> getAllMovies() throws JsonProcessingException {
        ObjectMapper organizationsMapper = new ObjectMapper();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.timeout)
                .responseTimeout(Duration.ofMillis(this.timeout))
                .doOnConnected(connection -> connection.addHandlerLast(
                                new ReadTimeoutHandler(this.timeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(this.timeout, TimeUnit.MILLISECONDS)));

        WebClient client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://localhost:8080/")
                .build();


        ObjectMapper objectMapper = new ObjectMapper();
        List<Movie> movies = new ArrayList<>();

        try {
            organizationsMapper.readTree(client.get().uri("api/v1/movies").retrieve().bodyToMono(String.class).block())
                    .forEach(node -> {

                        try {
                            movies.add(objectMapper.treeToValue(node, Movie.class));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return movies;
    }

}
