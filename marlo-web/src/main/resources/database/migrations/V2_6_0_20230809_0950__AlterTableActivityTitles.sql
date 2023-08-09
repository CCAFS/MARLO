update activities_titles set start_year= 2020, end_year=2029 where start_year is null;

ALTER TABLE activities_titles MODIFY COLUMN start_year int DEFAULT 2020 NOT NULL;
ALTER TABLE activities_titles MODIFY COLUMN end_year int DEFAULT 2029 NOT NULL;