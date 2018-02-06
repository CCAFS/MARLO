-- UPDATE Nyando
UPDATE project_locations SET loc_element_id = 726 Where loc_element_id = 239;
UPDATE loc_elements SET is_active = 0 where id = 239;

-- UPDATE Albertine Rift
UPDATE project_locations SET loc_element_id = 727 Where loc_element_id = 241;
UPDATE loc_elements SET is_active = 0 where id = 241;

-- UPDATE Makueni
UPDATE project_locations SET loc_element_id = 728 Where loc_element_id = 240;
UPDATE loc_elements SET is_active = 0 where id = 240;

-- UPDATE Kagera Basin
UPDATE project_locations SET loc_element_id = 729 Where loc_element_id = 242;
UPDATE loc_elements SET is_active = 0 where id = 242;

-- UPDATE Borana
UPDATE project_locations SET loc_element_id = 730 Where loc_element_id = 244;
UPDATE loc_elements SET is_active = 0 where id = 244;

-- UPDATE Usambara
UPDATE project_locations SET loc_element_id = 731 Where loc_element_id = 243;
UPDATE loc_elements SET is_active = 0 where id = 243;

-- UPDATE Yatenga
UPDATE project_locations SET loc_element_id = 732 Where loc_element_id = 245;
UPDATE loc_elements SET is_active = 0 where id = 245;

-- UPDATE Lawra-Jirapa
UPDATE project_locations SET loc_element_id = 733 Where loc_element_id = 246;
UPDATE loc_elements SET is_active = 0 where id = 246;

-- UPDATE Segou
UPDATE project_locations SET loc_element_id = 734 Where loc_element_id = 247;
UPDATE loc_elements SET is_active = 0 where id = 247;

-- UPDATE Kollo
UPDATE project_locations SET loc_element_id = 735 Where loc_element_id = 248;
UPDATE loc_elements SET is_active = 0 where id = 248;

-- UPDATE Kaffrine
UPDATE project_locations SET loc_element_id = 736 Where loc_element_id = 249;
UPDATE loc_elements SET is_active = 0 where id = 249;

-- UPDATE Cauca
UPDATE project_locations SET loc_element_id = 752 Where loc_element_id = 601;
UPDATE loc_elements SET is_active = 0 where id = 601;

-- UPDATE Santa Rita
UPDATE project_locations SET loc_element_id = 753 Where loc_element_id = 258;
UPDATE loc_elements SET is_active = 0 where id = 258;

-- UPDATE Olopa
UPDATE project_locations SET loc_element_id = 754 Where loc_element_id = 257;
UPDATE loc_elements SET is_active = 0 where id = 257;

-- UPDATE El Tuma-la Dalia
UPDATE project_locations SET loc_element_id = 755 Where loc_element_id = 256;
UPDATE loc_elements SET is_active = 0 where id = 256;

-- UPDATE Ma
UPDATE loc_elements SET is_active = 0 where id = 259;

-- UPDATE Ekxang
UPDATE loc_elements SET is_active = 0 where id = 260;

-- UPDATE Khulna
UPDATE loc_elements SET is_active = 0 where id = 250;

-- UPDATE Haryana
UPDATE loc_elements SET is_active = 0 where id = 251;

-- UPDATE Bihar
UPDATE loc_elements SET is_active = 0 where id = 252;

-- SET CCAFS SITES is_active = 0
UPDATE loc_element_types SET is_active = 0 where id=11;
