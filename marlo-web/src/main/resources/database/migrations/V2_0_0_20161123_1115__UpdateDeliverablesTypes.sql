  SET FOREIGN_KEY_CHECKS=0;
  delete  from deliverable_types;
  INSERT into  deliverable_types (id,name)values(42,'Data, models and tools');
  INSERT into  deliverable_types (id,name)values(43,'Reports');
  INSERT into  deliverable_types (id,name)values(44,'Outreach products');
  INSERT into  deliverable_types (id,name)values(46,'Training materials');
  INSERT into  deliverable_types (id,name)values(49,'Articles and Books');
  INSERT into  deliverable_types (id,name,parent_id)values(51,'Database/Dataset/Data documentation',42);
  INSERT into  deliverable_types (id,name,parent_id)values(52,'Data portal/Tool/Model code/Computer software',42);
  INSERT into  deliverable_types (id,name,parent_id)values(53,'Thesis',43);
  INSERT into  deliverable_types (id,name,parent_id)values(54,'Research workshop report',43);
  INSERT into  deliverable_types (id,name,parent_id)values(55,'Policy brief/policy note/briefing paper',44);
  INSERT into  deliverable_types (id,name,parent_id)values(56,'Discussion paper/Working paper/White paper',43);
  INSERT into  deliverable_types (id,name,parent_id)values(57,'Conference paper / Seminar paper',43);
  INSERT into  deliverable_types (id,name,parent_id)values(58,'Lecture/Training Course Material',46);
  INSERT into  deliverable_types (id,name,parent_id)values(59,'Guidebook/Handbook/Good Practice Note',46);
  INSERT into  deliverable_types (id,name,parent_id)values(60,'User manual/Technical Guide',46);
  INSERT into  deliverable_types (id,name,parent_id)values(61,'Article for media/Magazine/Other (not peer-reviewed)',46);
  INSERT into  deliverable_types (id,name,parent_id)values(62,'Outcome case study',43);
  INSERT into  deliverable_types (id,name,parent_id)values(63,'Journal Article (peer reviewed)',49);
  INSERT into  deliverable_types (id,name,parent_id)values(64,'Book (peer reviewed)',49);
  INSERT into  deliverable_types (id,name,parent_id)values(65,'Book (non-peer reviewed)',49);
  INSERT into  deliverable_types (id,name,parent_id)values(66,'Book chapter (peer reviewed)',49);
  INSERT into  deliverable_types (id,name,parent_id)values(67,'Book chapter (non-peer reviewed)',49);
  INSERT into  deliverable_types (id,name,parent_id)values(68,'Newsletter',44);
  INSERT into  deliverable_types (id,name,parent_id)values(69,'Social Media Output',44);
  INSERT into  deliverable_types (id,name,parent_id)values(70,'Blog',44);
  INSERT into  deliverable_types (id,name,parent_id)values(71,'Website',44);
  INSERT into  deliverable_types (id,name,parent_id)values(72,'Presentation/Poster',44);
  INSERT into  deliverable_types (id,name,parent_id)values(73,'Multimedia',44);
  INSERT into  deliverable_types (id,name,parent_id)values(74,'Maps/Geospatial data',42);
  INSERT into  deliverable_types (id,name,parent_id)values(75,'Brochure',44);
  INSERT into  deliverable_types (id,name,parent_id)values(76,'Outcome note',44);
  INSERT into  deliverable_types (id,name,parent_id)values(77,'Factsheet, Project Note',44);
  INSERT into  deliverable_types (id,name,parent_id)values(78,'Infographic',44);
  INSERT into  deliverable_types (id,name,parent_id)values(79,'Journal Article (non-peer reviewed)',49);
  INSERT into  deliverable_types (id,name,parent_id)values(80,'Special issue',49);
  INSERT into  deliverable_types (id,name,parent_id)values(81,'Policy workshop/Dialogue report',43);
  INSERT into  deliverable_types (id,name,parent_id)values(82,'Donor report',43);
  INSERT into  deliverable_types (id,name,parent_id)values(83,'Concept note',43);


  update 

  deliverables
  set type_id =
  case type_id
  when 10 then  51
  when 11 then  51
  when 12 then  51
  when 37 then 52
  when 14 then 54
  when 15 then 55
  when 16 then 56
  when 17 then 57
  when 18 then 57
  when 19 then 56
  when 20 then 56
  when 22 then 61
  when 30 then 62
  when 21 then 63
  when 23 then 65
  when 24 then 67
  when 26 then 61
  when 27 then 69
  when 28 then 72
  when 29 then 72
  when 31 then 73
  when 32 then 73
  when 33 then 73
  when 34 then 52
  when 35 then 74
  when 36  then 52
  when 41 then 71
  when 39 then 54
  when 40 then 58
  else type_id
  end 


