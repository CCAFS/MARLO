#script migration email_logs source action

/*
 * email_logs does not say which part of MARLO sent an email, so System Admin -> Emails cannot tell a role
 * notification from a submission receipt or a crash report. SendMailS now records where the send came from: the
 * Struts action as "<namespace>/<action>" (for example "/projects/partners"), or the URI of a REST request. A send
 * from a background job has neither and stays NULL.
 *
 * The column is nullable and there is no backfill: nothing in an existing row says where it was sent from.
 * No id is hardcoded, so this applies the same on every database.
 */

ALTER TABLE `email_logs`
ADD COLUMN `source_action` VARCHAR (255) NULL AFTER `global_unit_id`;
