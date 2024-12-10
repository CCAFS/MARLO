INSERT INTO tool_function_categories (name,description,is_active)
  VALUES ('Improved performance (e.g. productivity, resource efficiency)','7',1);
INSERT INTO tool_function_categories (name,description,is_active,active_since)
  VALUES ('Decision-making support','3',1,'2024-11-21 11:01:18');
INSERT INTO tool_function_categories (name,description,is_active,active_since)
  VALUES ('Facilitate accessibility (e.g. to finance, inputs, etc)','8',1,'2024-11-21 11:01:18');
INSERT INTO tool_function_categories (name,description,is_active,active_since)
  VALUES ('Improved usability, ease and operability (e.g. machinery, tailored radio agro-advisories)','9',1,'2024-11-21 11:01:18');
UPDATE tool_function_categories
  SET description='1'
  WHERE id=1;
UPDATE tool_function_categories
  SET description='2'
  WHERE id=2;
UPDATE tool_function_categories
  SET description='4'
  WHERE id=3;
UPDATE tool_function_categories
  SET description='5'
  WHERE id=4;
UPDATE tool_function_categories
  SET description='6'
  WHERE id=5;
UPDATE tool_function_categories
  SET is_active=0
  WHERE id=6;