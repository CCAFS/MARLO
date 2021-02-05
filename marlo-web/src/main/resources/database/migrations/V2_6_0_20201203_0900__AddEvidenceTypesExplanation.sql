alter table study_types add description text;
alter table study_types add key_identifier text;
alter table study_types add for_narrative text;
alter table study_types add example text;

update study_types set description = "studies that estimate the causal effects of research outputs and related activities on one or more development parameters of interest, and assess the costs of the intervention with the magnitude of impact achieved. They are applied after or long after that the development intervention has been completed. Due to the nature of research, EPIAs often measure benefits to large numbers of people, orto large areas of land or water.\n\nNormally one component of an EPIA study will be multiple rounds of survey data representative of a policy-relevant scale –state, national or regional. If a study measures adoption as well as impact (this is common) it is counted as an EPIA.",
key_identifier = "Impact of long term, sustained use of a CGIAR innovation on IDO/SLO outcomes.",
for_narrative = "Underpinning CGIAR claims of sustained results (OICRs level 3).",
example = "Impact of adoption of cassava varieties in Nigeria, varieties were introduced from 1999-2005 and the study took place in 2015."
where id = 3;

update study_types set description = "Assesses the adoption/use of a CGIAR output and/or methodology in order to make a case for CGIAR contribution to the outcome, relative to other potential influencing factors. They are applied after or long after that the development intervention has been completed. It usually measures adoption at a policy relevant scale (crop-growing area, national, regional).",
key_identifier = "Long-term, sustained adoption/use of CGIAR innovation.",
for_narrative = "Underpinning CGIAR claims of sustained results (OICRs level 3).",
example = "National adoption study for conservation agriculture practices."
where id = 4;

update study_types set description = "at the program or project level,  assesses the adoption/use of a CG output and/or methodology in order to make a case for CGIAR contribution to the outcome, relative to other potential influencing factors.Such studies generally have a control or comparison group that was not treated or included in the intervention, and the analysis involves comparing outcomes among the groups.\n\nWhile theresults observed in the context of the program are valid, they cannot easily be extrapolated to the broader population outside the project context. It includes effectiveness studies (RCTs), and other evaluation of specific interventions.",
key_identifier = "Impact assessment of a program or project that is disseminating a CGIAR innovation(s) as part of the intervention, conducting during or shortly after the program.",
for_narrative = "Underpinning CGIAR claims of sustained results (OICRs level 2).",
example = "Impact of adoption of CGIAR-designed behavior change communication approaches on child nutrition carried out under Project XX."
where id = 10;

update study_types set key_identifier = "Identify factors associated with/influence uptake and impact."
where id = 12;

update study_types set description = "Focuses on a particular unit -a person, a site, a project-by using mainly qualitative data. They are particularly useful for understanding  how  different  elements  fit  together  and  how  different  elements  (implementation,  contextand  other  factors)  have  produced  the observed changes in practice/ behaviour.\n\nThese are usually studies of influence of CGIAR innovations or findings on policy or behavior of next users.  They may use methods such as contribution analysis or process tracing (but may not).",
key_identifier = "Mostly for policy and advocacy. Qualitative methods such as process tracing, contribution analysis.",
for_narrative = "Underpinning CGIAR claims of sustained results (OICRs level 2).",
example = "Study of influence of CRP XXX on policy change XXX."
where id = 11;

update study_types set description = "assess a set of interventions, marshaled to attain specific global, regional, country, or sector development objectives. Program/project evaluation are often made by independent evaluators or experts which look at the performance of either a particular entity -for example, a project, flagship, research programme or platform -or a theme. It nvolves evaluating/ reviewing past performance, by using a systematic approach against formal evaluation criteria.",
key_identifier = "Review or evaluation of a component of the CRP/center (e.g. a Flagship), or a project; IEA-type study."
where id = 5;

update study_types set description = "Reviews, systematic reviews, evidence gap maps.",
key_identifier = "Secondary studies pulling together findings from primary studies."
where id = 7;

update study_types set description = "Analytical  description  /  evaluation  performed  before  implementation  of  a  development  intervention, against which progress can be assessed or comparisons made. Ithelps for projected benefits and prioritization."
where id = 13;

update study_types set example = "Learning workshop, internal review, training in MELIA, installation of MIS, development of MELIA methods."
where id = 9;