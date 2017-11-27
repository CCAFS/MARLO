[#ftl]
[#assign title = "Summaries Section" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2","font-awesome","jsUri"] /]


[#assign customJS = ["${baseUrl}/global/js/utils.js", 
                    "${baseUrlMedia}/js/summaries/boardSummaries.js", 
                    "${baseUrlMedia}/js/capDev/capdevSummaries.js", 
                    "${baseUrlMedia}/js/capDev/year-select.js"] /]


[#assign customCSS = ["${baseUrlMedia}/css/summaries/summaries.css"] /]
[#assign currentSection = "summaries" /]

[#assign breadCrumb = [
  {"label":"summaries", "nameSpace":"summaries", "action":"summaries"}
]/]


[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]

[#assign years = ["2014","2015", "2016","2017" ]/]
    
<section class="container">
  <article id="" class="fullBlock col-md-12" > 
    <br /> 
    <div class="summariesButtons clearfix">
      <div id="impactPathway" class="summariesSection current"><span></span><a href="">[@s.text name="summaries.board.options.impactPathway" /]</a></div>
      <div id="projects" class="summariesSection" style="opacity:0.5;" ><span></span><a href="">[@s.text name="summaries.board.options.projects" /]</a></div>
      <div id="monitoring" class="summariesSection" style="opacity:0.5;"><span></span><a href="">[@s.text name="summaries.board.options.monitoring" /]</a> </div>
      <div id="capdev" class="summariesSection" style="opacity:0.5;"><span></span><a href="">[@s.text name="summaries.board.options.capdev" /]</a> </div>
    </div>
    <div class="summariesContent borderBox col-md-12">
      <div class="loading" style="display:none"></div>
      <form action="">
      
      <h4 class="col-md-12 selectReport-title">[@s.text name="summaries.board.selectReportType" /]</h4>
      <div class="summariesOptions col-md-12">
        
        [#-- -- -- ImpactPathway reports -- -- --]
        <div id="impactPathway-contentOptions">
        
          [#--impactPathway by research program --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="impactPathwaySubmissions"/>
              <label for="">[@s.text name="summaries.board.report.ipFullReport" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.ipFullReport.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              <span class="hidden fileTypes excelType">leadProjectInstitutionsSummary-leadProjectInstitutionsSummary</span>
              [@customForm.select name="programID" header=false   label=""  i18nkey="Select a Research Program"  listName="programs"  keyFieldName="id"  displayFieldName="composedName" className="allPrograms"   multiple=false required=true   editable=true/]
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
          
          [#--outcomes by research program --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="centerOutcomes"/>
              <label for="">[@s.text name="summaries.board.report.outcome" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.outcome.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              <span class="hidden fileTypes excelType">leadProjectInstitutionsSummary-leadProjectInstitutionsSummary</span>
              [@customForm.select name="programID" header=false   label=""  i18nkey="Select a Research Program"  listName="programs"  keyFieldName="id"  displayFieldName="composedName" className="allPrograms"   multiple=false required=true   editable=true/]
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
          
        </div>
        
        [#-- -- -- Projects reports -- -- --]
        <div id="projects-contentOptions" style="display:none">
        
        [#--Project PDF report --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="projectSummary"/>
              <label for="">[@s.text name="summaries.board.report.project" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.project.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              [@customForm.select name="projectID" header=true   label=""  i18nkey="Select a Project"  listName="allProjects"  keyFieldName="id"  displayFieldName="composedName" className="allPrograms"   multiple=false required=true   editable=true/]
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
        
          [#--Deliverables report --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="centerDeliverables"/>
              <label for="">[@s.text name="summaries.board.report.deliverable" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.deliverable.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              [@customForm.select name="programID" header=false   label=""  i18nkey="Select a Research Program"  listName="programs"  keyFieldName="id"  displayFieldName="composedName" className="allPrograms"   multiple=false required=true   editable=true/]
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
        
          
        </div>
        
        [#-- Monitoring reports --]
        <div id="monitoring-contentOptions" style="display:none">
        
        [#--Monitoring Outcomes--]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="outcomesContributionSummary" value="centerMonitoringOutcomes"/>
              <label for="">[@s.text name="summaries.board.report.monitoringOutcome" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.monitoringOutcome.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              <span class="hidden fileTypes excelType">leadProjectInstitutionsSummary-leadProjectInstitutionsSummary</span>
              [@customForm.select name="programID" header=false   label=""  i18nkey="Select a Research Program"  listName="programs"  keyFieldName="id"  displayFieldName="composedName" className="allPrograms"   multiple=false required=true   editable=true/]
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
        
          [#--Outcomes Contribution --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="outcomesContributionSummary" value="outcomesContributions"/>
              <label for="">[@s.text name="summaries.board.report.outcomeContribution" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.outcomeContribution.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              <span class="hidden fileTypes excelType">leadProjectInstitutionsSummary-leadProjectInstitutionsSummary</span>
              [@customForm.select name="programID" header=false   label=""  i18nkey="Select a Research Program"  listName="programs"  keyFieldName="id"  displayFieldName="composedName" className="allPrograms"   multiple=false required=true   editable=true/]
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
          
          [#-- Expected deliverables 
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="allDeliverables" value="allDeliverables"/>
            <label for="allDeliverables">All Deliverables<span>XLSx</span></label>
          </div>
          --]
          
          
        </div>

        [#-- -- -- capdev reports -- -- --]
        <div id="capdev-contentOptions" style="display:none">
        
        [#--  capdev  report by research area --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="projectSummary"/>
              <label for="">Full capacity development interventions report by Research Areas </label>
            </div>
            <div class="col-md-12"> 
              <div class="col-md-6 ">
                [@customForm.select name="" header=false   label=""  i18nkey="Select a Research Area"  listName="researchAreas"  keyFieldName="id"  displayFieldName="name" className="researchAreasSelect"   multiple=false required=true placeholder="ALL"  editable=true/]
              </div>
             
              <div class="col-md-6 capdevYearSelect">
                [@customForm.select name="" header=false   label=""  i18nkey="Select a Year"  listName=""  keyFieldName=""  displayFieldName="" className="yearArea"   multiple=false required=true   editable=true/]
              </div>
              
              <div class="pull-right">
                <a id="generarReportCapdevByArea" target="_blank" class=" addButton pull-right  generarReportCapdev" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
            
          </div>


          [#-- capdev report by research program --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="projectSummary"/>
              <label for="">Full capacity development interventions report by Research Program </label>
            </div>
            <div class="col-md-12"> 
              
              <div class="col-md-6 ">
                [@customForm.select name="" header=false   label=""  i18nkey="Select a Research Program"  listName="programs"  keyFieldName="id"  displayFieldName="name" className="researchProgramSelect"   multiple=false required=true placeholder="ALL"  editable=true/]
              </div>
              <div class="col-md-6 capdevYearSelect">
                [@customForm.select name="" header=false   label=""  i18nkey="Select a Year"  listName=""  keyFieldName=""  displayFieldName="" className="yearProgram"   multiple=false required=true   editable=true/]
              </div>
              
              <div class="pull-right">
                <a id="generarReportCapdevByProgram" target="_blank" class=" addButton pull-right  generarReportCapdev" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
            
          </div>
          
        </div>

      </div>
      <br />
      
      
      [#-- END FORM --]
      </form>
    </div> 
  </article>
</section>

[#-- POPUP TO OPTIONS --]
<div id="optionsPopUp"  style="display:none;" >
<span class="glyphicon glyphicon-remove-circle close-dialog"></span>
  <h4 style="text-align:center;">Generate the report</h4>
  <hr />
  <div class="row">
    <div class="col-md-7">
      <h5 class="col-md-12">[@s.text name="summaries.board.projectResearchCycle" /] <span class="red">*</span></h5>
          
          <div class="summariesOption col-md-12">
            <input type="radio" name="cycle" id="planning" value="Planning" checked="checked"/>
            <span >[@s.text name="summaries.board.projectResearchCycle.planning" /]</span>
          </div>
          <div class="summariesOption col-md-12">
            <input type="radio" name="cycle" id="reporting" value="Reporting"   />
            <span for="reporting">[@s.text name="summaries.board.projectResearchCycle.reporting" /]</span>
          </div>
    </div>
    <div class="col-md-5">
      <h5 class="col-md-12">[@s.text name="Choose the type of file" /] <span class="red">*</span></h5>
      <div class="col-md-4 notChoose">
      <span class="fa fa-file-pdf-o col-md-12 pdfIcon file"></span>
      <span class="col-md-12">Pdf</span>
      </div>
      <div class="col-md-4 notChoose">
      <span class="fa fa-file-excel-o col-md-12 excelIcon file"></span>
      <span class="col-md-12">Excel</span>
      </div>
    </div>
  </div>
  <br />
  <div class="col-md-12 form-group projectSelectWrapper" style="display:none;">
    [@customForm.select name=""   label=""  i18nkey="Select a project"  listName=""  keyFieldName="id"  displayFieldName="composedName" className="allProjects"   multiple=false required=true   editable=true/]
  </div>
  <div class="row" style="margin-bottom:20px;">
  <div class="col-md-12">
    <div class="col-md-6">
      [@customForm.select name="reportYears"   label=""  i18nkey="Select a specific year"  listName=""  keyFieldName="id"  displayFieldName="composedName" className="reportYear"   multiple=false required=false   editable=true/]
    </div>
    <div class="col-md-6 okButton">
    <span class="blockButton"></span>
    <a style="" target="_blank" class=" addButton" style="" href="#">[@s.text name="Ok" /]</a>
    </div>
  </div>
  </div>
</div>


[#--  terms template--]
<div id="term" class="terms" style="display:none;">
  <span class="text"></span><span class="removeTerm glyphicon glyphicon-remove"></span>
</div>

[#include "/WEB-INF/center/pages/footer.ftl"]