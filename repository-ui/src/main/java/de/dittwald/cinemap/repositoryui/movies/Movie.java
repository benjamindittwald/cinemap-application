package de.dittwald.cinemap.repositoryui.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private String uuid;
    private String imdbWebsiteUrl;
    private Map<String, String> title;

//    public Movie() {
//    }
//
//    public Movie(String uuid, String imdbWebsiteUrl, Map<String, String> title) {
//        this.uuid = uuid;
//        this.imdbWebsiteUrl = imdbWebsiteUrl;
//        this.title = title;
//    }
//
//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }
//
//    public String getImdbWebsiteUrl() {
//        return imdbWebsiteUrl;
//    }
//
//    public void setImdbWebsiteUrl(String imdbWebsiteUrl) {
//        this.imdbWebsiteUrl = imdbWebsiteUrl;
//    }
//
//    public Map<String, String> getTitle() {
//        return title;
//    }
//
//    public void setTitle(Map<String, String> title) {
//        this.title = title;
//    }
}
