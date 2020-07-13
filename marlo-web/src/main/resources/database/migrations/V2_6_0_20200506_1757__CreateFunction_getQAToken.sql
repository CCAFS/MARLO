DROP FUNCTION if exists  getQAToken;

DELIMITER $$

CREATE FUNCTION getQAToken (
	name text,
	username text,
	email text,
	smocode text,
	appuser text
) RETURNS text DETERMINISTIC
BEGIN
	INSERT INTO qa_token_auth (
		createdAt,
		updatedAt,
		token,
		expiration_date,
		crp_id,
		username,
		email,
		name,
		app_user
	)
VALUES
	(
		NOW(),
		NOW(),
		MD5(
			CONCAT(appuser, username, NOW())
		),
		(NOW() + INTERVAL 60 SECOND),
		smocode,
		username,
		email,
		name,
		appuser
	);

RETURN (@@identity);
END