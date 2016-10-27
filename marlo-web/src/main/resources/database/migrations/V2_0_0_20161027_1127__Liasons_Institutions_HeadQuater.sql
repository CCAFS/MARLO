UPDATE liaison_institutions li INNER JOIN institutions ins on ins.id=li.institution_id
set li.institution_id=ins.headquarter
where ins.headquarter is  not null;