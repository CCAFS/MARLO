ALTER TABLE actors MODIFY COLUMN is_active tinyint(1) DEFAULT 1 NOT NULL;
INSERT INTO actors (name,prms_name_equivalent)
	VALUES ('Researchers ','National Agricultural Research Organizations');
INSERT INTO actors (name,prms_name_equivalent)
	VALUES ('Researchers ','Universities or agricultural training centers');
INSERT INTO actors (name,prms_name_equivalent)
	VALUES ('Researchers ','General researchers');
INSERT INTO actors (name,is_active,active_since,prms_name_equivalent)
	VALUES ('Policy actors (public or private)',1,'2024-12-10 09:33:45','Meteorological Agencies');
INSERT INTO actors (name,is_active,active_since,prms_name_equivalent)
	VALUES ('Policy actors (public or private)',1,'2024-12-10 09:33:45','Foundations (i.e. Gates)');
INSERT INTO actors (name,is_active,active_since,prms_name_equivalent)
	VALUES ('Policy actors (public or private)',1,'2024-12-10 09:33:45','Development Organisations (i.e. GIZ, FCDO)');
INSERT INTO actors (name,is_active,active_since,prms_name_equivalent)
	VALUES ('Policy actors (public or private)',1,'2024-12-10 09:33:45','General policy actors');
INSERT INTO actors (name,is_active,active_since,prms_name_equivalent)
	VALUES ('Policy actors (public or private)',1,'2024-12-10 09:33:45','Agribusinesses (SMEs)');
INSERT INTO actors (name,is_active,active_since,prms_name_equivalent)
	VALUES ('Policy actors (public or private)',1,'2024-12-10 09:33:45','Private service providers (i.e. Lersha, Kuza Biashara)');
INSERT INTO actors (name,is_active,active_since,prms_name_equivalent)
	VALUES ('Banks/Investors',1,'2024-12-10 09:33:45','Formal banks');
INSERT INTO actors (name,is_active,active_since,prms_name_equivalent)
	VALUES ('Banks/Investors',1,'2024-12-10 09:33:45','Investors');
INSERT INTO actors (name,prms_name_equivalent)
	VALUES ('Other','Other');
UPDATE actors
	SET prms_name_equivalent='CGIAR'
	WHERE id=1;
UPDATE actors
	SET prms_name_equivalent='Farmers/(agro)pastoralists/herders/fishers'
	WHERE id=2;
UPDATE actors
	SET prms_name_equivalent='Ministries (Agriculture, Livestock, Fisheries, Finance)'
	WHERE id=3;
UPDATE actors
	SET prms_name_equivalent='Agricultural extension agents'
	WHERE id=4;
UPDATE actors
	SET prms_name_equivalent='Informal credit institutes'
	WHERE id=5;
	