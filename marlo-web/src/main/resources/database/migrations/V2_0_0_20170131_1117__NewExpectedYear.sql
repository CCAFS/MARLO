UPDATE deliverables set new_expected_year=2016 where
`status`=4 and new_expected_year is null and is_active=1 and `year`<=2015;