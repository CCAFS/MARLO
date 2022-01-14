ALter table rep_ind_geographic_scopes add is_onecgiar tinyint(1) DEFAULT 0;
ALter table rep_ind_geographic_scopes add is_marlo tinyint(1) DEFAULT 1;

update rep_ind_geographic_scopes set is_onecgiar=1 where id in (3,4,5);