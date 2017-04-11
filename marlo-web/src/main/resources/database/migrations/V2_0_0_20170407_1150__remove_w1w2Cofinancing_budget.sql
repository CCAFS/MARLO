-- Delete W1/W2 Co-Financing Budget type
DELETE FROM budget_types where name="W1/W2 Co-Financing";

-- Update name of Budget type
UPDATE budget_types SET `name`='Center Funds' WHERE `id`='4';