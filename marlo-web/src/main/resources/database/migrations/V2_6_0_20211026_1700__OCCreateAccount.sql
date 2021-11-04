create table oc_accounts (
id bigint(20) NOT NULL AUTO_INCREMENT,
financial_code VARCHAR(20) NOT NULL,
description TEXT,
account_type_id BIGINT NOT NULL,
parent_id BIGINT,
PRIMARY KEY (id),
KEY `account_type_id` (account_type_id) USING BTREE,
KEY `parent_id` (parent_id),
INDEX `oc_account_id` (`id`) USING BTREE,
CONSTRAINT `oc_accounts_ibfk_1` FOREIGN KEY (`account_type_id`) REFERENCES `oc_account_types` (`id`),
CONSTRAINT `oc_accounts_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `oc_accounts` (`id`)
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8;

insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A1-0008', 'Income', 1, null);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A1-0009', 'Expenses', 1, null);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0090', 'Personnel', 2, 2);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0091', 'Supplies and services', 2, 2);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0092', 'Partners', 2, 2);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0093', 'Depreciation CGIAR', 2, 2);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0094', 'Travel Expenses', 2, 2);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0095', 'Charge Back - Project', 2, 2);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0096', 'Overheads - Project', 2, 2);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0080', 'Grant Income', 2, 1);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A2-0081', 'Other  Income', 2, 1);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0950', 'Charge back', 3, 8);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0930', 'Depreciation ', 3, 6);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0931', 'Depreciation project assets', 3, 6);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0800', 'W3 Unrestricted', 3, 10);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0801', 'W3 Restricted', 3, 10);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0802', 'Bilateral Unrestricted', 3, 10);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0803', 'Bilateral Restricted', 3, 10);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0804', 'Pooled Funding', 3, 10);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0805', 'Pipeline', 3, 10);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0810', 'Commodity / Livestock sales', 3, 11);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0811', 'Hosting service Revenue', 3, 11);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0812', 'Interest income', 3, 11);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0813', 'Forex income (or loss)', 3, 11);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0814', 'Other revenues', 3, 11);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0960', 'Overheads', 3, 9);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0920', 'Partners - CG', 3, 5);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0921', 'Partners Non CG', 3, 5);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0900', 'Salaries and Wages', 3, 3);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0901', 'Employee Benefits', 3, 3);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0910', 'Consultants', 3, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0911', 'Workshops and conferences ', 3, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0912', 'Supplies and services', 3, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0914', 'Other expenses and losses', 3, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A3-0940', 'Travel', 3, 7);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8030', 'Bilateral - Restricted', 4, 18);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8020', 'Bilateral - Unrestricted', 4, 17);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8100', 'Interest Income, Coupons', 4, 21);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9100', 'Consultants - Fees', 4, 31);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9101', 'Consultants - Travel and Other', 4, 31);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9300', 'Depreciation - for assets funded', 4, 13);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9310', 'Equipments and or assets funded by projects.', 4, 14);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9010', 'Medical Insurance.', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9011', 'Life and other Insurances.', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9012', 'Housing and Utilities allowances.', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9013', 'Education Allowance.', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9014', 'Retirement fund.', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9015', 'Repatr - Travel, Leave & Arrival', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9016', 'Transport allowance.', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9017', 'Overtime', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9018', 'Leave days not taken', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9019', 'Recruitment related fees.', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9020', 'Other allowances', 4, 30);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8110', 'Catering income', 4, 22);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8111', 'Transport income', 4, 22);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8112', 'Other income ', 4, 22);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8120', 'Gain/Loss on Disposal of Fixed Assets', 4, 23);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8121', 'Gain/Loss on Sale of Stock', 4, 23);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8122', 'Realized Foreign Exchange gain general', 4, 23);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8123', 'Unrealized Foreign Exchange gain general', 4, 23);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9140', 'Write offs/ Unallowables', 4, 34);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9141', 'Realized Foreign Exchange loss general', 4, 34);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9142', 'Unrealized Foreign Exchange loss general', 4, 34);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9143', 'Interest expense', 4, 34);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9200', 'CG collaborators', 4, 27);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9210', 'Non CG collaborators', 4, 28);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8050', 'Pipeline (w3 - Bilateral)', 4, 20);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8040', 'Pooled Funding (W1-W2)', 4, 19);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9000', 'International Staff - Salaries', 4, 29);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9001', 'National Staff - Salaries', 4, 29);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9002', 'Graduates/Students', 4, 29);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9003', 'Collaborative/Visiting Scientists', 4, 29);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9120', 'Supplies Various field, Engineering, Health and office', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9121', 'Supplies  for services', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9122', 'Communication supplies', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9123', 'General supplies and other insurance', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9124', 'Stock for vehicles', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9125', 'Material repairs and maintenance, communication', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9126', 'Bank charges', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9127', 'Miscellaneous supplies ', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9128', 'Casual workers?', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9129', 'Unbillable expenses', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9130', 'Supplies for professional services ', 4, 4);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9400', 'Air tickets- international and local', 4, 35);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9401', 'Perdiems or subsistence', 4, 35);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9402', 'Taxis, Trains, Buses, Vehicle rentals & Others ', 4, 35);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9403', 'Hotel accomodation', 4, 35);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8010', 'W3 - Restricted', 4, 16);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-8000', 'W3 - Unrestricted', 4, 15);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9110', 'Workshop general expenses', 4, 32);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9111', 'Participants perdiems, stipends/honoraria, ', 4, 32);
insert into oc_accounts(financial_code, description, account_type_id, parent_id) value ('A4-9112', 'Participants accomodation', 4, 32);
