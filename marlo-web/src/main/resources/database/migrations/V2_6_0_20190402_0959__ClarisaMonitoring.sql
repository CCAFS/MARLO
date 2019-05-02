
/************************************************
 *  New Filds of external users on API
 ************************************************/
alter table partner_requests 
ADD external_user_mail varchar(255) after reject_justification,
ADD external_user_name text after external_user_mail
ADD external_user_comments text after external_user_name;