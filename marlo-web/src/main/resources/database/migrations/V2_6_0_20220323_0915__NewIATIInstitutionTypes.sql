alter table institution_types add column is_legacy tinyint(1) DEFAULT '0' after description;

update institution_types set is_legacy = '1';

insert into institution_types (name) values ('Subnational Government'), ('Regional NGO'),
('Academic, Training and Research'), ('Public Private Partnership'), ('Multilateral');