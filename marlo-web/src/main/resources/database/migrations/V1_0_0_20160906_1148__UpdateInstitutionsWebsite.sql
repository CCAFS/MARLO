/*
MySQL Data Transfer

Date: 2016-09-06 11:48:00
*/
START TRANSACTION;
UPDATE `institutions` SET `website_link`='http://irri.org/' WHERE `id`='5';
UPDATE `institutions` SET `website_link`='http://www.cirad.fr/' WHERE `id`='9';
UPDATE `institutions` SET `website_link`='http://www.iita.org/' WHERE `id`='45';
UPDATE `institutions` SET `website_link`='https://ciat.cgiar.org/' WHERE `id`='46';
UPDATE `institutions` SET `website_link`='http://www.bioversityinternational.org/' WHERE `id`='49';
UPDATE `institutions` SET `website_link`='http://www.cimmyt.org/es/' WHERE `id`='50';
UPDATE `institutions` SET `website_link`='https://www.ilri.org/' WHERE `id`='66';
UPDATE `institutions` SET `website_link`='http://cipotato.org/' WHERE `id`='67';
UPDATE `institutions` SET `website_link`='http://www.worldagroforestry.org/' WHERE `id`='88';
UPDATE `institutions` SET `website_link`='http://www.ifpri.org/' WHERE `id`='89';
UPDATE `institutions` SET `website_link`='http://www.worldfishcenter.org/' WHERE `id`='99';
UPDATE `institutions` SET `website_link`='http://www.icrisat.org/' WHERE `id`='103';
UPDATE `institutions` SET `website_link`='https://www.leeds.ac.uk/' WHERE `id`='134';
UPDATE `institutions` SET `website_link`='http://www.ox.ac.uk/' WHERE `id`='150';
UPDATE `institutions` SET `website_link`='http://www.iwmi.cgiar.org/' WHERE `id`='172';
UPDATE `institutions` SET `website_link`='http://www.iiasa.ac.at/' WHERE `id`='218';
UPDATE `institutions` SET `website_link`='http://iri.columbia.edu/' WHERE `id`='299';
UPDATE `institutions` SET `website_link`='http://www.cimmyt.org/' WHERE `id`='650';
UPDATE `institutions` SET `website_link`='http://www.cimmyt.org/' WHERE `id`='680';
UPDATE `institutions` SET `website_link`='http://www.ifpri.org/' WHERE `id`='1085';
UPDATE `institutions` SET `website_link`='http://www.worldagroforestry.org/' WHERE `id`='1143';
UPDATE `institutions` SET `website_link`='https://www.ilri.org/' WHERE `id`='1213';
UPDATE `institutions` SET `website_link`='http://www.iwmi.cgiar.org/' WHERE `id`='1214';
UPDATE `institutions` SET `website_link`='http://www.bioversityinternational.org/' WHERE `id`='1215';
UPDATE `institutions` SET `website_link`='http://www.icrisat.org/' WHERE `id`='1216';
UPDATE `institutions` SET `website_link`='https://ciat.cgiar.org/' WHERE `id`='1221';
UPDATE `institutions` SET `website_link`='http://www.worldagroforestry.org/' WHERE `id`='1232';
COMMIT;