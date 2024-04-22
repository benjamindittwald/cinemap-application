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

-- we don't know how to generate root <with-no-name> (class Root) :(

comment on database postgres is 'default administrative connection database';

create sequence movies_seq
    increment by 50;

create table movies
(
    id                     bigint not null
        primary key,
    version                bigint,
    imdb_website_url varchar(255)
);

create table titel_locale_mapping
(
    locale_id    bigint       not null
        constraint fkabrk02mhbf0u9kt8l8s85io1o
            references movies,
    title        varchar(255),
    title_locale varchar(255) not null,
    primary key (locale_id, title_locale)
);
