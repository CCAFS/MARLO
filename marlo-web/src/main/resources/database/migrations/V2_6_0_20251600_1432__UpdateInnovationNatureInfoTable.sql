INSERT INTO rep_ind_innovation_natures (name,definition,is_active,is_old_type)
  VALUES ('Incremental innovation','Innovations that already exist and undergo constant, steady progress, and improvement.',1,0);
INSERT INTO rep_ind_innovation_natures (name,definition,is_active,is_old_type)
  VALUES ('Radical innovation','Innovations that are new and replace existing products, systems, services and/or policies but do not cause or require major reconfiguration of farming, market and/or policy/ business models.',1,0);
INSERT INTO rep_ind_innovation_natures (name,definition,is_active,is_old_type)
  VALUES ('Disruptive innovation','Innovations that are new and cause or require major reconfiguration of farming, market and/or policy/ business models.',1,0);
UPDATE rep_ind_innovation_natures
  SET is_old_type=1
  WHERE id=1;
UPDATE rep_ind_innovation_natures
  SET is_old_type=1
  WHERE id=2;
UPDATE rep_ind_innovation_natures
  SET is_old_type=1
  WHERE id=3;
UPDATE rep_ind_innovation_natures
  SET is_old_type=1,definition='Unknown or the characterization does not work for my innovation.'
  WHERE id=4;
  
INSERT INTO rep_ind_innovation_types (name,is_old_type)
  VALUES ('Bundled CSA and CIS',0);
UPDATE rep_ind_innovation_types
  SET name='Climate informed Agro advisories (CIS and CSA)'
  WHERE id=11;

