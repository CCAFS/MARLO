UPDATE
 deliverables_info df 
INNER JOIN crp_cluster_key_outputs ky on ky.composed_id like CONCAT('%-',df.key_output_id)
inner join crp_cluster_of_activities cl on cl.id=ky.cluster_activity_id and df.id_phase=cl.id_phase
set df.key_output_id=ky.id
;