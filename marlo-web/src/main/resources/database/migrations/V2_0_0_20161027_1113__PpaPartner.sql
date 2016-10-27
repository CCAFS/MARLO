DELETE ppa.*


from crp_ppa_partners ppa INNER JOIN institutions ins on ins.id=ppa.institution_id
where ins.headquarter is not null
