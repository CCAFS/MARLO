#script migration email_logs global unit

/*
 * email_logs records every email SendMailS hands to the mail server, but not the global unit it was sent for.
 * SendMailS.send() and sendTemporalMethod() drop the email of a global unit whose crp_enable_email_notification
 * is off, yet the failed-email retry of System Admin -> Emails resends the logged emails of every global unit at
 * once, from the session of whichever super administrator runs it. Without the global unit on the row the retry
 * cannot tell whose switch applies, so a global unit that turned its notifications off after a send failed would
 * still get that email. The column records it from now on.
 *
 * The column is nullable and there is no backfill: nothing in an existing row says which global unit it belonged
 * to, and an email sent outside a global unit (no CRP in the session) has none either. The retry keeps treating a
 * row without a global unit exactly as before.
 *
 * No global unit id is hardcoded, and the foreign key validates nothing on the existing rows since they are all
 * NULL, so this applies the same on every database.
 */

ALTER TABLE `email_logs`
ADD COLUMN `global_unit_id`  BIGINT (20) NULL AFTER `message_id`;

ALTER TABLE `email_logs` ADD CONSTRAINT `email_logs_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
