[#ftl]
[#-- Global Unit  --]
[#if currentCrp??]${(currentCrp.acronym)!} | [/#if]
[#-- CRP Program --]
[#if (selectedProgram??)!false]${(selectedProgram.acronym)!} - [/#if]
[#-- Project > Deliverable --]
[#if (project??)!false]P${(project.id)!} - [#if (deliverable??)!false]D${(deliverable.id)!} - [/#if] [/#if]
[#-- Funding Source--]
[#if (fundingSource??)!false]FS${(fundingSource.id)!} - [/#if]
[#-- POWB Synthesis --]
[#if (powbSynthesis??)!false]${(powbSynthesis.liaisonInstitution.acronym)!} - [/#if]

[#-- Title --]
${(title)!"MARLO"}

[#-- Testing Environment --]
[#if !config.production][Testing Environment][/#if]