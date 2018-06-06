INSERT INTO `rep_ind_type_participants` (`id`, `name`, `definition`) VALUES ('8', 'Mixture of participant types', 'Variety of participants with no dominant background');
INSERT INTO `rep_ind_type_participants` (`id`, `name`, `definition`) VALUES ('9', 'Other', 'CRPs should include an opportunity/text box for suggestions here. If there are many suggestions of the same type, we can create a new category. If not, we revise existing categories to incorporate suggestions.');
UPDATE `rep_ind_type_participants` SET `name`='CGIAR staff', `definition`='' WHERE `id`='7';
