[#ftl]
[#-- Global Unit  --]
[#if currentCrp??]${(currentCrp.acronym)!} | [/#if]
[#-- CRP Program --]
[#if (selectedProgram??)!false]${(selectedProgram.acronym)!} - [/#if]
[#-- Project > Deliverable --]
[#if (project??)!false]P${(project.id)!} - 
  [#if (deliverable??)!false]D${(deliverable.id)!} - [/#if]
  [#if (expectedID??)!false]S${(expectedID)!} - [/#if]
  [#if (innovationID??)!false]I${(innovationID)!} - [/#if]
  [#if (highlightID??)!false]H${(highlightID)!} - [/#if]
  
[/#if]
[#-- Funding Source--]
[#if (fundingSource??)!false]FS${(fundingSource.id)!} - [/#if]
[#-- POWB Synthesis --]
[#if (powbSynthesis??)!false]${(powbSynthesis.liaisonInstitution.acronym)!} - [/#if]
[#-- Annual Report Synthesis --]
[#if (reportSynthesis??)!false]${(reportSynthesis.liaisonInstitution.acronym)!} - [/#if]

[#-- Title --]
${(title)!"MARLO"}

[#-- Testing Environment --]
[#if !config.production][Testing Environment][/#if]