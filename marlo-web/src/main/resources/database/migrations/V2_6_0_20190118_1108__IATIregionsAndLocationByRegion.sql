SET FOREIGN_KEY_CHECKS=0;

-- New IATI regions
CREATE TABLE iati_regions (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  code BIGINT(5) NULL,
  name TEXT NOT NULL,
  is_active TINYINT(1) NOT NULL,
  PRIMARY KEY (id));
  
-- Mapping of Regions IATI with Loc_elements
CREATE TABLE IF NOT EXISTS iati_regions_locations (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  loc_element_id BIGINT(20) NOT NULL,
  iati_region_id BIGINT(20) NOT NULL,
  is_active TINYINT(1) NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_iati_regions_locations_loc_elements1_idx (loc_element_id ASC),
  INDEX fk_iati_regions_locations_iati_regions1_idx (iati_region_id ASC),
  CONSTRAINT fk_iati_regions_locations_loc_elements1
    FOREIGN KEY (loc_element_id)
    REFERENCES loc_elements (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_iati_regions_locations_iati_regions1
    FOREIGN KEY (iati_region_id)
    REFERENCES iati_regions (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- global Units ids update
update global_units set smo_code = 'CRP-22' where id = 1;
update global_units set smo_code = 'CRP-23' where id = 3;
update global_units set smo_code = 'CRP-24' where id = 4;
update global_units set smo_code = 'CRP-21' where id = 5;
update global_units set smo_code = 'CRP-13' where id = 7;
update global_units set smo_code = 'CRP-12' where id = 11;
update global_units set smo_code = 'CRP-15' where id = 16;
update global_units set smo_code = 'CRP-16' where id = 17;
update global_units set smo_code = 'CRP-17' where id = 21;
update global_units set smo_code = 'CRP-14' where id = 22;
update global_units set smo_code = 'CRP-11' where id = 27;
update global_units set smo_code = 'CRP-18' where id = 28;
update global_units set smo_code = 'PTF-32' where id = 24;
update global_units set smo_code = 'PTF-31' where id = 25;
update global_units set smo_code = 'PTF-33' where id = 26;
update global_units set smo_code = 'CENTER-1' where id = 30;
update global_units set smo_code = 'CENTER-2' where id = 31;
update global_units set smo_code = 'CENTER-3' where id = 29;
update global_units set smo_code = 'CENTER-4' where id = 32;
update global_units set smo_code = 'CENTER-5' where id = 38;
update global_units set smo_code = 'CENTER-6' where id = 39;
update global_units set smo_code = 'CENTER-7' where id = 33;
update global_units set smo_code = 'CENTER-8' where id = 42;
update global_units set smo_code = 'CENTER-9' where id = 34;
update global_units set smo_code = 'CENTER-10' where id = 35;
update global_units set smo_code = 'CENTER-11' where id = 36;
update global_units set smo_code = 'CENTER-12' where id = 37;
update global_units set smo_code = 'CENTER-13' where id = 40;
update global_units set smo_code = 'CENTER-14' where id = 41;
update global_units set smo_code = 'CENTER-15' where id = 43;
update global_units set smo_code = '1.3' where id = 8;
update global_units set smo_code = '3.6' where id = 9;
update global_units set smo_code = '1.1' where id = 10;
update global_units set smo_code = '3.5' where id = 12;
update global_units set smo_code = '1.2' where id = 13;

-- update 2 region UNM49 id
update loc_elements set iso_numeric=419 where id = 825;
update loc_elements set iso_numeric=202 where id = 829;

-- Loc_element mapping to regions
insert into iati_regions (code, name, is_active) values (88,'States Ex-Yugoslavia unspecified',1);
insert into iati_regions (code, name, is_active) values (89,'Europe, regional',1);
insert into iati_regions (code, name, is_active) values (189,'North of Sahara, regional',1);
insert into iati_regions (code, name, is_active) values (289,'South of Sahara, regional',1);
insert into iati_regions (code, name, is_active) values (298,'Africa, regional',1);
insert into iati_regions (code, name, is_active) values (380,'West Indies, regional',1);
insert into iati_regions (code, name, is_active) values (389,'North & Central America, regional',1);
insert into iati_regions (code, name, is_active) values (489,'South America, regional',1);
insert into iati_regions (code, name, is_active) values (498,'America, regional',1);
insert into iati_regions (code, name, is_active) values (589,'Middle East, regional',1);
insert into iati_regions (code, name, is_active) values (619,'Central Asia, regional',1);
insert into iati_regions (code, name, is_active) values (679,'South Asia, regional',1);
insert into iati_regions (code, name, is_active) values (689,'South & Central Asia, regional',1);
insert into iati_regions (code, name, is_active) values (789,'Far East Asia, regional',1);
insert into iati_regions (code, name, is_active) values (798,'Asia, regional',1);
insert into iati_regions (code, name, is_active) values (889,'Oceania, regional',1);
insert into iati_regions (code, name, is_active) values (998,'Developing countries, unspecified',1);




insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (705,(select id from iati_regions where code = 489),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (2,(select id from iati_regions where code = 289),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (704,(select id from iati_regions where code = 389),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (1,(select id from iati_regions where code = 289),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (701,(select id from iati_regions where code = 189),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (700,(select id from iati_regions where code = 289),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (702,(select id from iati_regions where code = 289),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (706,(select id from iati_regions where code = 389),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (703,(select id from iati_regions where code = 389),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (708,(select id from iati_regions where code = 798),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (3,(select id from iati_regions where code = 798),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (5,(select id from iati_regions where code = 798),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (712,(select id from iati_regions where code = 89),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (714,(select id from iati_regions where code = 889),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (715,(select id from iati_regions where code = 889),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (716,(select id from iati_regions where code = 889),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (717,(select id from iati_regions where code = 889),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (707,(select id from iati_regions where code = 619),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (709,(select id from iati_regions where code = 798),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (710,(select id from iati_regions where code = 89),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (711,(select id from iati_regions where code = 89),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (713,(select id from iati_regions where code = 89),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (829,(select id from iati_regions where code = 289),1);
insert into iati_regions_locations(loc_element_id, iati_region_id, is_active) values (825,(select id from iati_regions where code = 489),1);

-- mapeo de las nuevas regiones en locations
update loc_elements set parent_id =717 where id=680;
update loc_elements set parent_id =717 where id=49;
update loc_elements set parent_id =717 where id=233;
update loc_elements set parent_id =717 where id=232;
update loc_elements set parent_id =717 where id=217;
update loc_elements set parent_id =717 where id=214;
update loc_elements set parent_id =717 where id=210;
update loc_elements set parent_id =717 where id=18;
update loc_elements set parent_id =717 where id=170;
update loc_elements set parent_id =717 where id=165;
update loc_elements set parent_id =716 where id=91;
update loc_elements set parent_id =716 where id=695;
update loc_elements set parent_id =716 where id=676;
update loc_elements set parent_id =716 where id=179;
update loc_elements set parent_id =716 where id=164;
update loc_elements set parent_id =716 where id=146;
update loc_elements set parent_id =716 where id=140;
update loc_elements set parent_id =716 where id=116;
update loc_elements set parent_id =715 where id=74;
update loc_elements set parent_id =715 where id=231;
update loc_elements set parent_id =715 where id=187;
update loc_elements set parent_id =715 where id=171;
update loc_elements set parent_id =715 where id=157;
update loc_elements set parent_id =714 where id=678;
update loc_elements set parent_id =714 where id=667;
update loc_elements set parent_id =714 where id=57;
update loc_elements set parent_id =714 where id=43;
update loc_elements set parent_id =714 where id=20;
update loc_elements set parent_id =714 where id=166;
update loc_elements set parent_id =713 where id=80;
update loc_elements set parent_id =713 where id=78;
update loc_elements set parent_id =713 where id=60;
update loc_elements set parent_id =713 where id=47;
update loc_elements set parent_id =713 where id=26;
update loc_elements set parent_id =713 where id=19;
update loc_elements set parent_id =713 where id=161;
update loc_elements set parent_id =713 where id=135;
update loc_elements set parent_id =713 where id=132;
update loc_elements set parent_id =713 where id=127;
update loc_elements set parent_id =712 where id=96;
update loc_elements set parent_id =712 where id=89;
update loc_elements set parent_id =712 where id=84;
update loc_elements set parent_id =712 where id=7;
update loc_elements set parent_id =712 where id=71;
update loc_elements set parent_id =712 where id=674;
update loc_elements set parent_id =712 where id=668;
update loc_elements set parent_id =712 where id=23;
update loc_elements set parent_id =712 where id=197;
update loc_elements set parent_id =712 where id=193;
update loc_elements set parent_id =712 where id=183;
update loc_elements set parent_id =712 where id=178;
update loc_elements set parent_id =712 where id=149;
update loc_elements set parent_id =712 where id=137;
update loc_elements set parent_id =712 where id=12;
update loc_elements set parent_id =712 where id=108;
update loc_elements set parent_id =711 where id=77;
update loc_elements set parent_id =711 where id=73;
update loc_elements set parent_id =711 where id=689;
update loc_elements set parent_id =711 where id=67;
update loc_elements set parent_id =711 where id=673;
update loc_elements set parent_id =711 where id=666;
update loc_elements set parent_id =711 where id=654;
update loc_elements set parent_id =711 where id=62;
update loc_elements set parent_id =711 where id=59;
update loc_elements set parent_id =711 where id=190;
update loc_elements set parent_id =711 where id=162;
update loc_elements set parent_id =711 where id=131;
update loc_elements set parent_id =711 where id=109;
update loc_elements set parent_id =711 where id=107;
update loc_elements set parent_id =711 where id=102;
update loc_elements set parent_id =711 where id=100;
update loc_elements set parent_id =710 where id=98;
update loc_elements set parent_id =710 where id=40;
update loc_elements set parent_id =710 where id=28;
update loc_elements set parent_id =710 where id=220;
update loc_elements set parent_id =710 where id=195;
update loc_elements set parent_id =710 where id=184;
update loc_elements set parent_id =710 where id=182;
update loc_elements set parent_id =710 where id=174;
update loc_elements set parent_id =710 where id=136;
update loc_elements set parent_id =709 where id=8;
update loc_elements set parent_id =709 where id=830;
update loc_elements set parent_id =709 where id=82;
update loc_elements set parent_id =709 where id=690;
update loc_elements set parent_id =709 where id=58;
update loc_elements set parent_id =709 where id=29;
update loc_elements set parent_id =709 where id=234;
update loc_elements set parent_id =709 where id=22;
update loc_elements set parent_id =709 where id=215;
update loc_elements set parent_id =709 where id=186;
update loc_elements set parent_id =709 where id=181;
update loc_elements set parent_id =709 where id=167;
update loc_elements set parent_id =709 where id=13;
update loc_elements set parent_id =709 where id=125;
update loc_elements set parent_id =709 where id=121;
update loc_elements set parent_id =709 where id=111;
update loc_elements set parent_id =709 where id=106;
update loc_elements set parent_id =709 where id=105;
update loc_elements set parent_id =709 where id=101;
update loc_elements set parent_id =708 where id=671;
update loc_elements set parent_id =708 where id=670;
update loc_elements set parent_id =708 where id=658;
update loc_elements set parent_id =708 where id=657;
update loc_elements set parent_id =708 where id=52;
update loc_elements set parent_id =708 where id=144;
update loc_elements set parent_id =708 where id=112;
update loc_elements set parent_id =707 where id=224;
update loc_elements set parent_id =707 where id=212;
update loc_elements set parent_id =707 where id=209;
update loc_elements set parent_id =707 where id=123;
update loc_elements set parent_id =707 where id=114;
update loc_elements set parent_id =706 where id=85;
update loc_elements set parent_id =706 where id=698;
update loc_elements set parent_id =706 where id=42;
update loc_elements set parent_id =706 where id=33;
update loc_elements set parent_id =706 where id=222;
update loc_elements set parent_id =706 where id=175;
update loc_elements set parent_id =705 where id=93;
update loc_elements set parent_id =705 where id=718;
update loc_elements set parent_id =705 where id=686;
update loc_elements set parent_id =705 where id=685;
update loc_elements set parent_id =705 where id=66;
update loc_elements set parent_id =705 where id=663;
update loc_elements set parent_id =705 where id=662;
update loc_elements set parent_id =705 where id=655;
update loc_elements set parent_id =705 where id=53;
update loc_elements set parent_id =705 where id=50;
update loc_elements set parent_id =705 where id=36;
update loc_elements set parent_id =705 where id=35;
update loc_elements set parent_id =705 where id=227;
update loc_elements set parent_id =705 where id=223;
update loc_elements set parent_id =705 where id=200;
update loc_elements set parent_id =705 where id=180;
update loc_elements set parent_id =705 where id=17;
update loc_elements set parent_id =705 where id=169;
update loc_elements set parent_id =704 where id=95;
update loc_elements set parent_id =704 where id=90;
update loc_elements set parent_id =704 where id=54;
update loc_elements set parent_id =704 where id=41;
update loc_elements set parent_id =704 where id=202;
update loc_elements set parent_id =704 where id=168;
update loc_elements set parent_id =704 where id=160;
update loc_elements set parent_id =704 where id=153;
update loc_elements set parent_id =703 where id=97;
update loc_elements set parent_id =703 where id=81;
update loc_elements set parent_id =703 where id=720;
update loc_elements set parent_id =703 where id=719;
update loc_elements set parent_id =703 where id=684;
update loc_elements set parent_id =703 where id=683;
update loc_elements set parent_id =703 where id=675;
update loc_elements set parent_id =703 where id=665;
update loc_elements set parent_id =703 where id=64;
update loc_elements set parent_id =703 where id=63;
update loc_elements set parent_id =703 where id=55;
update loc_elements set parent_id =703 where id=37;
update loc_elements set parent_id =703 where id=24;
update loc_elements set parent_id =703 where id=228;
update loc_elements set parent_id =703 where id=21;
update loc_elements set parent_id =703 where id=216;
update loc_elements set parent_id =703 where id=205;
update loc_elements set parent_id =703 where id=177;
update loc_elements set parent_id =703 where id=148;
update loc_elements set parent_id =703 where id=126;
update loc_elements set parent_id =703 where id=122;
update loc_elements set parent_id =703 where id=11;
update loc_elements set parent_id =703 where id=118;
update loc_elements set parent_id =703 where id=110;
update loc_elements set parent_id =703 where id=10;
update loc_elements set parent_id =702 where id=39;
update loc_elements set parent_id =702 where id=236;
update loc_elements set parent_id =702 where id=204;
update loc_elements set parent_id =702 where id=156;
update loc_elements set parent_id =702 where id=130;
update loc_elements set parent_id =701 where id=69;
update loc_elements set parent_id =701 where id=68;
update loc_elements set parent_id =701 where id=65;
update loc_elements set parent_id =701 where id=213;
update loc_elements set parent_id =701 where id=189;
update loc_elements set parent_id =701 where id=134;
update loc_elements set parent_id =701 where id=133;
update loc_elements set parent_id =700 where id=88;
update loc_elements set parent_id =700 where id=79;
update loc_elements set parent_id =700 where id=659;
update loc_elements set parent_id =700 where id=51;
update loc_elements set parent_id =700 where id=45;
update loc_elements set parent_id =700 where id=44;
update loc_elements set parent_id =700 where id=206;
update loc_elements set parent_id =700 where id=201;
update loc_elements set parent_id =700 where id=15;
update loc_elements set parent_id =5 where id=99;
update loc_elements set parent_id =5 where id=693;
update loc_elements set parent_id =5 where id=691;
update loc_elements set parent_id =5 where id=656;
update loc_elements set parent_id =5 where id=230;
update loc_elements set parent_id =5 where id=208;
update loc_elements set parent_id =5 where id=191;
update loc_elements set parent_id =5 where id=172;
update loc_elements set parent_id =5 where id=154;
update loc_elements set parent_id =5 where id=143;
update loc_elements set parent_id =5 where id=124;
update loc_elements set parent_id =5 where id=115;
update loc_elements set parent_id =3 where id=9;
update loc_elements set parent_id =3 where id=38;
update loc_elements set parent_id =3 where id=25;
update loc_elements set parent_id =3 where id=173;
update loc_elements set parent_id =3 where id=163;
update loc_elements set parent_id =3 where id=151;
update loc_elements set parent_id =3 where id=128;
update loc_elements set parent_id =3 where id=103;
update loc_elements set parent_id =2 where id=92;
update loc_elements set parent_id =2 where id=87;
update loc_elements set parent_id =2 where id=86;
update loc_elements set parent_id =2 where id=83;
update loc_elements set parent_id =2 where id=56;
update loc_elements set parent_id =2 where id=48;
update loc_elements set parent_id =2 where id=31;
update loc_elements set parent_id =2 where id=27;
update loc_elements set parent_id =2 where id=207;
update loc_elements set parent_id =2 where id=198;
update loc_elements set parent_id =2 where id=196;
update loc_elements set parent_id =2 where id=192;
update loc_elements set parent_id =2 where id=159;
update loc_elements set parent_id =2 where id=158;
update loc_elements set parent_id =2 where id=147;
update loc_elements set parent_id =2 where id=142;
update loc_elements set parent_id =2 where id=129;
update loc_elements set parent_id =16 where id=16;
update loc_elements set parent_id =1 where id=72;
update loc_elements set parent_id =1 where id=70;
update loc_elements set parent_id =1 where id=687;
update loc_elements set parent_id =1 where id=681;
update loc_elements set parent_id =1 where id=664;
update loc_elements set parent_id =1 where id=61;
update loc_elements set parent_id =1 where id=30;
update loc_elements set parent_id =1 where id=238;
update loc_elements set parent_id =1 where id=237;
update loc_elements set parent_id =1 where id=235;
update loc_elements set parent_id =1 where id=221;
update loc_elements set parent_id =1 where id=219;
update loc_elements set parent_id =1 where id=199;
update loc_elements set parent_id =1 where id=188;
update loc_elements set parent_id =1 where id=185;
update loc_elements set parent_id =1 where id=155;
update loc_elements set parent_id =1 where id=152;
update loc_elements set parent_id =1 where id=150;
update loc_elements set parent_id =1 where id=139;
update loc_elements set parent_id =1 where id=117;
update loc_elements set parent_id =1 where id=113;
update loc_elements set parent_id =1 where id=104;

