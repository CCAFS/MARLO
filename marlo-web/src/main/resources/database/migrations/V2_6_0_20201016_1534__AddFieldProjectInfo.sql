ALTER TABLE `projects_info`
ADD COLUMN `previous_project_id`  bigint(20) NULL AFTER `activities_csv_file`;