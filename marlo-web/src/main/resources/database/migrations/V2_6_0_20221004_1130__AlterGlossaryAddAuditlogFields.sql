alter table glossary add created_by text after definition;
alter table glossary add modification_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP after created_by;
alter table glossary add modified_by text after modification_date;
alter table glossary add modification_justification text after modified_by;