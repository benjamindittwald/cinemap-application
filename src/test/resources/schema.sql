/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

create sequence movie_scenes_seq
    increment by 50;

create sequence movies_seq
    increment by 50;

create table movies
(
    id               bigint not null
        primary key,
    version          bigint,
    imdb_website_url varchar(255)
);

create table movie_scenes
(
    id       bigint not null
        primary key,
    lat      bigint not null,
    lon      bigint not null,
    movie_id bigint
        constraint fks0o0tnmdgg48w6ufup3l3pvt4
            references movies,
    version  bigint
);

create table description_locale_mapping
(
    locale_id          bigint       not null
        constraint fkshjtu4vts0lygsyqthc78873k
            references movie_scenes,
    description        varchar(255),
    description_locale varchar(255) not null,
    primary key (locale_id, description_locale)
);

create table title_locale_mapping
(
    locale_id    bigint       not null
        constraint fkmiyn8ju2algnd7mpopq03f6gd
            references movies,
    title        varchar(255),
    title_locale varchar(255) not null,
    primary key (locale_id, title_locale)
);
