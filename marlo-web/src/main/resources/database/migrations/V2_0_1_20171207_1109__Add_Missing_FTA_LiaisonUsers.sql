INSERT INTO liaison_users (`user_id`, `institution_id`, `crp_id`, `is_active`) VALUES ((SELECT u.id FROM users u WHERE LOWER(u.email) =  'r.jamnadass@cgiar.org'), (SELECT li.id FROM liaison_institutions li WHERE li.acronym = 'FP1' AND li.crp_id = 11), 11, 1);
INSERT INTO liaison_users (`user_id`, `institution_id`, `crp_id`, `is_active`) VALUES ((SELECT u.id FROM users u WHERE LOWER(u.email) =  'f.sinclair@cgiar.org'), (SELECT li.id FROM liaison_institutions li WHERE li.acronym = 'FP2' AND li.crp_id = 11), 11, 1);
INSERT INTO liaison_users (`user_id`, `institution_id`, `crp_id`, `is_active`) VALUES ((SELECT u.id FROM users u WHERE LOWER(u.email) =  'p.pacheco@cgiar.org'), (SELECT li.id FROM liaison_institutions li WHERE li.acronym = 'FP3' AND li.crp_id = 11), 11, 1);
INSERT INTO liaison_users (`user_id`, `institution_id`, `crp_id`, `is_active`) VALUES ((SELECT u.id FROM users u WHERE LOWER(u.email) =  'a.minang@cgiar.org'), (SELECT li.id FROM liaison_institutions li WHERE li.acronym = 'FP4' AND li.crp_id = 11), 11, 1);
INSERT INTO liaison_users (`user_id`, `institution_id`, `crp_id`, `is_active`) VALUES ((SELECT u.id FROM users u WHERE LOWER(u.email) =  'c.martius@cgiar.org'), (SELECT li.id FROM liaison_institutions li WHERE li.acronym = 'FP5' AND li.crp_id = 11), 11, 1);
