/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repositoryui.tmdb;

import de.dittwald.cinemap.repositoryui.properties.ConfigConstants;
import de.dittwald.cinemap.repositoryui.properties.Properties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private final int timeout = 3000;
    private final Properties properties;

    public WebClientConfig(Properties properties) {
        this.properties = properties;
    }

    HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.timeout)
            .responseTimeout(Duration.ofMillis(this.timeout))
            .doOnConnected(
                    connection -> connection.addHandlerLast(new ReadTimeoutHandler(this.timeout, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(this.timeout, TimeUnit.MILLISECONDS)));

    @Bean
    public WebClient repositoryWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(properties.getRepositoryUrl() + ":" + this.properties.getRepositoryPort())
                .build();
    }

    @Bean
    public WebClient tmdbWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(ConfigConstants.TMDB_API_BASE_URL)
                .build();
    }
}
