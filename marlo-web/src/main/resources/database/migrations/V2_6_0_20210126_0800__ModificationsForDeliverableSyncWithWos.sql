create table deliverable_metadata_external_sources(
    id bigint not null auto_increment,
    deliverable_id bigint default null,
    phase_id bigint default null,
    source text,
    url text,
    doi text,
    title text,
    publication_type text,
    publication_year int,
    is_open_access text,
    open_access_link text,
    is_isi text,
    journal_name text,
    volume text,
    pages text,

    primary key (id),

    constraint dmwos_ibfk_1 foreign key (deliverable_id) references deliverables (id),
    constraint dmwos_ibfk_2 foreign key (phase_id) references phases (id)
);

alter table deliverable_affiliations add institution_match_confidence int;
alter table deliverable_affiliations add institution_name_web_of_science text default null;
alter table deliverable_affiliations add deliverable_metadata_external_sources_id bigint default null;
alter table deliverable_affiliations add constraint deliverable_affiliations_ibfk_7 foreign key (deliverable_metadata_external_sources_id) references deliverable_metadata_external_sources (id);

create table external_source_author(
    id bigint not null auto_increment,
    deliverable_metadata_external_sources_id bigint default null,
    full_name text,

    primary key (id),

    constraint esa_ibfk_1 foreign key (deliverable_metadata_external_sources_id) references deliverable_metadata_external_sources (id)
);

create table deliverable_affiliations_not_mapped(
    id bigint not null auto_increment,
    deliverable_metadata_external_sources_id bigint default null,
    institution_id bigint default null,
    name text,
    country text,
    full_address text,
    institution_match_confidence int,

    primary key (id),

    constraint danm_ibfk_1 foreign key (deliverable_metadata_external_sources_id) references deliverable_metadata_external_sources (id)
    constraint danm_ibfk_2 foreign key (institution_id) references institutions (id)
);
