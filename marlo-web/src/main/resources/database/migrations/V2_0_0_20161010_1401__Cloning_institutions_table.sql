DROP TABLE IF EXISTS `institutions_backup20161010`;

CREATE TABLE institutions_backup20161010 LIKE institutions; 
INSERT institutions_backup20161010 SELECT * FROM institutions;