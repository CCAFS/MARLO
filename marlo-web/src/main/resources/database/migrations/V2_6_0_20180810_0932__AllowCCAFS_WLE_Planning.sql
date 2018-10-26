SET FOREIGN_KEY_CHECKS=0;


-- ----------------------------
-- Update CCAFS and WLE phases
-- ----------------------------
SET @planning_phase = 'Planning';
SET @powb_name='POWB';
SET @global_unit_ccafs='1';
SET @global_unit_wle='4';

UPDATE `phases` SET `editable`='1', `visible`='0' 
WHERE (`name`=@powb_name and `description`=@planning_phase and `year`=2018 and (global_unit_id = @global_unit_ccafs or global_unit_id = @global_unit_wle));