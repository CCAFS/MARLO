[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "trumbowyg" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20190621"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#import "/WEB-INF/crp/views/annualReport2018/macros-AR2018.ftl" as macrosAR /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisFlagshipProgress" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport2018/submenu-AR2018.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- POWB Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="borderBox">
            [#if flagship]
              [#-- Progress by flagships --]
              <div class="form-group">
                [#-- Word Document Tag --]
                [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                [@customForm.textArea name="${customName}.progressByFlagships" i18nkey="${customLabel}.progressByFlagships" help="${customLabel}.progressByFlagships.help" className="limitWords-200" helpIcon=false required=false editable=editable allowTextEditor=true /]
              </div>
              [#-- Detailed annex --]
              <div class="form-group">
                <br />
                [#-- Word Document Tag --]
                [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                [@customForm.textArea name="${customName}.detailedAnnex" i18nkey="${customLabel}.detailedAnnex" className="limitWords-800" helpIcon=false required=false editable=editable allowTextEditor=true /]
              </div>
            [#else]
              
              [#-- Overall CRP progress --]
              <div class="form-group">
              [#if (hasFlagshipProgress)!false]
                [#assign limit="250"]
                [#else]
                [#assign limit="1000"]
              [/#if]
               
                [#-- Word Document Tag --]
                [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
                [@customForm.textArea name="${customName}.overallProgress" i18nkey="${customLabel}.overallProgress" help="${customLabel}.overallProgress.help" className="limitWords-${limit}" helpIcon=false required=true editable=editable allowTextEditor=true /]
              </div>
              
              [#-- Flagship Synthesis (1.2.2)--]
              </br>
              [#-- Missing fields in FPs --]
              [#if listOfFlagships?has_content && PMU]]                
                <div class="missingFieldFp">
                  <div><span class="glyphicon glyphicon-exclamation-sign mffp-icon" title="Incomplete"></span> Missing fields in
                  [#list listOfFlagships as fp]
                   ${fp}[#if fp?index !=(listOfFlagships?size-1) ],[/#if]
                  [/#list]
                  </div>
                 </div>
                 </br>
              [/#if]
              <div class="form-group">
                  [#-- Word Document Tag --]
                [#if PMU]
                  [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                [/#if]
                <h4 class="simpleTitle">[@s.text name="${customLabel}.progressByFlagships" /]</h4>
                [@macrosAR.tableFPSynthesis tableName="${customLabel}.tableflagshipSynthesis" list=flagshipsReportSynthesisFlagshipProgress columns=["progressByFlagships", "detailedAnnex"] showTitle=false allInOne=true /]
              </div>
            [/#if]
           
            <div class="form-group">
             </br>
              [#-- 1.2.2b Relevance to Covid-19 --]
              [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
              [@customForm.textArea name="${customName}.relevanceCovid" i18nkey="${customLabel}.relevanceCovid" help="${customLabel}.relevanceCovid.help" className="limitWords-300" helpIcon=false required=true editable=editable allowTextEditor=false /]
             </br>
            </div>
            
            
            
            
            [#-- test --]
            
            [#-- Evidences table with types and their descriptions --]
              <div class="form-group evidenceTypeMessage">
                <div id="dialog" title="Evidence types" style="display: none">
                  <table id="evidenceTypes" style="height:700px; width:950px;">
                    <th> [@s.text name="study.ARdialogMessage.part1" /] </th>
                    <th> [@s.text name="study.ARdialogMessage.part2" /] </th>
                    <th> [@s.text name="study.ARdialogMessage.part3" /] </th>
                    <th>  [@s.text name="study.ARdialogMessage.part4"/] </th>
                    [#if covidAnalysisStudies?has_content]
                      [#list covidAnalysisStudies as st]
                        <tr>
                          [#--if st_index == 0]
                          <th rowspan="${action.getDeliverablesSubTypes(mt.id).size()}" class="text-center"> ${mt.name} </th>
                          [/#if--]
                          <td> 
                            ${st.projectExpectedStudyInfo.title} 
                          </td>
                          <td>
                          [#if (st.projectExpectedStudyInfo.studyType.name?has_content)!false]
                            ${st.projectExpectedStudyInfo.studyType.name}
                          [#else]
                            <i>([@s.text name="study.dialogMessage.notProvided" /])</i>
                          [/#if]
                          </td>
                          <td>
                          [#if (st.projectExpectedStudyInfo.status.name?has_content)!false]
                             ${st.projectExpectedStudyInfo.status.name}
                          [/#if]
                          </td>
                          <td>
                          [#if (st.projectExpectedStudyInfo.status.name?has_content)!false]
                            
                          [/#if]
                          </td>
                        </tr>
                      [/#list]
                    [/#if]  
                  </table>
                </div> <!-- End dialog-->
                      
                  <div class="modal fade" id="evidenceModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                  <div class="modal-dialog modal-dialog-scrollable" style=" width:80%" role="document">
                    <div class="modal-content">
                      <div class="modal-header">
                        <!-- <h5 class="modal-title" id="exampleModalLabel">Modal title</h5> -->
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                          <span aria-hidden="true">&times;</span>
                        </button>
                      </div>
                      <div class="modal-body">
                        
                        <table id="evidenceTypes" class="table ">
                          <thead style="background-color: #0b7ba6; font-weight: 500; color: white;">
                            <tr>
                              <th> [@s.text name="study.ARdialogMessage.part1" /]</th>
                              <th > [@s.text name="study.ARdialogMessage.part2"/]</th>
                              <th> [@s.text name="study.ARdialogMessage.part3" /]</th>
                              <th> [@s.text name="study.ARdialogMessage.part4" /]</th>
                            </tr>
                          </thead>
                
                          [#if covidAnalysisStudies?has_content]
                          [#list covidAnalysisStudies as st]
                          <tr>
                            [#--if st_index == 0]
                            <th rowspan="${action.getDeliverablesSubTypes(mt.id).size()}" class="text-center"> ${mt.name} </th>
                            [/#if--]
                            <td>
                             (P${st.project.id})
                              ${st.id} -
                              ${st.projectExpectedStudyInfo.title}
                            </td>
                            <td style="max-width: 90vw !important;">
                            [#if (st.projectExpectedStudyInfo.studyType.name?has_content)!false]
                              ${st.projectExpectedStudyInfo.studyType.name}
                            [#else]
                              <i>([@s.text name="study.dialogMessage.notProvided" /])</i>
                            [/#if]
                            </td>
                            <td>
                              [#if (st.projectExpectedStudyInfo.status.name?has_content)!false]
                                ${st.projectExpectedStudyInfo.status.name}
                              [#else]
                               <i>([@s.text name="study.dialogMessage.notProvided" /])</i>
                              [/#if]                                              
                            </td>
                            <td>
                              [#if (st.flagships?has_content)!false]                               
                                 [#list st.flagships as fp]                                                                                                        
                                       [#if (fp.crpProgram?has_content)!false]
                                        ${fp.crpProgram.acronym}
                                       [/#if]                                            
                                 [/#list]                                                                  
                              [#else]
                               <i>([@s.text name="study.dialogMessage.notProvided" /])</i>
                              [/#if]                                              
                            </td>
                          </tr>
                          [/#list]
                          [/#if]
                        </table>
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                      </div>
                    </div>
                  </div>
                </div>
                       
                <div class="note left">
                  <div id="popup" class="helpMessage3">
                    <p><a style="cursor: pointer;" data-toggle="modal" data-target="#evidenceModal" > <span class="glyphicon glyphicon-info-sign"></span> [@s.text name="annualReport2018.flagshipProgress.covidAnalysis" /]</a></p>
                  </div>
                </div>
                <div class="clearfix"></div>
              </div>
        
            </div>
            [#-- end test --]
            

            [#-- 1.2.3 Variance from Planned Program for this year --]
            <h4 class="simpleTitle headTitle annualReport-table">[@s.text name="${customLabel}.variance" /]</h4>
            [@customForm.helpLabel name="${customLabel}.variance.help" showIcon=false editable=editable/]
            
            [#-- Expandend research areas --]
            <div class="form-group">
              [#-- Word Document Tag --]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              [@customForm.textArea name="${customName}.expandedResearchAreas" i18nkey="${customLabel}.expandedResearchAreas" help="${customLabel}.expandedResearchAreas.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Dropped research lines --]
            <div class="form-group">
              [#-- Word Document Tag --]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              [@customForm.textArea name="${customName}.droppedResearchLines" i18nkey="${customLabel}.droppedResearchLines" help="${customLabel}.droppedResearchLines.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#-- Changed direction --]
            <div class="form-group">
              [#-- Word Document Tag --]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              [@customForm.textArea name="${customName}.changedDirection" i18nkey="${customLabel}.changedDirection" help="${customLabel}.changedDirection.help" className="limitWords-${calculateLimitWords(200)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            
            [#if PMU]
            [#-- Flagships - Synthesis (Variance from Planned Program) --]
            <div class="form-group">
              [@macrosAR.tableFPSynthesis tableName="${customLabel}.tableFlagshipVariance" list=flagshipsReportSynthesisFlagshipProgress columns=["expandedResearchAreas", "droppedResearchLines", "changedDirection"] allInOne=true /]
            </div>
            [/#if]
            
            [#-- Altmetric Score --]
            [#if PMU]
            <div class="form-group">
              [#-- Word Document Tag --]
              [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
              [@customForm.textArea name="${customName}.altmetricScore" i18nkey="${customLabel}.altmetricScore" help="${customLabel}.altmetricScore.help" className="limitWords-${calculateLimitWords(400)}" helpIcon=false required=true editable=editable allowTextEditor=true /]
            </div>
            [/#if]
            
            [#-- Flagships - Synthesis (Altmetric Score) 
            <div class="form-group">
              [@macrosAR.tableFPSynthesis tableName="${customLabel}.tableFlagshipAltmetric" list=flagshipsReportSynthesisFlagshipProgress columns=["altmetricScore"] /]
            </div>
            --]
            
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form]
      </div> 
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]