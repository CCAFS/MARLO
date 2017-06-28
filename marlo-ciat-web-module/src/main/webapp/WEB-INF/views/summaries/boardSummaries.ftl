[#ftl]
[#assign title = "Summaries Section" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2","font-awesome","jsUri"] /]
[#assign customJS = ["${baseUrl}/js/global/utils.js", "${baseUrl}/js/summaries/boardSummaries.js"] /]
[#assign customCSS = ["${baseUrl}/css/summaries/summaries.css"] /]
[#assign currentSection = "summaries" /]

[#assign breadCrumb = [
  {"label":"summaries", "nameSpace":"summaries", "action":"summaries"}
]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign years = ["2014","2015", "2016","2017" ]/]
    
<section class="container">
  <article id="" class="fullBlock col-md-12" > 
    <br /> 
    <div class="summariesButtons clearfix">
      <div id="impactPathway" class="summariesSection current"><span></span><a href="">[@s.text name="summaries.board.options.impactPathway" /]</a></div>
      <div id="projects" class="summariesSection" style="opacity:0.5;" ><span></span><a href="">[@s.text name="summaries.board.options.projects" /]</a> <div class="blockTab"></div> </div>
      <div id="monitoring" class="summariesSection" style="opacity:0.5;"><span></span><a href="">[@s.text name="summaries.board.options.monitoring" /]</a> </div>
    </div>
    <div class="summariesContent borderBox col-md-12">
      <div class="loading" style="display:none"></div>
      <form action="">
      
      <h4 class="col-md-12 selectReport-title">[@s.text name="summaries.board.selectReportType" /]</h4>
      <div class="summariesOptions col-md-12">
        
        [#-- -- -- ImpactPathway reports -- -- --]
        <div id="impactPathway-contentOptions">
        
          [#--impactPathway full report --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="impactPathwayOutcomes"/>
              <label for="">[@s.text name="summaries.board.report.ipFullReport" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.ipFullReport.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              <span class="hidden forPlanningCycle forCycle"></span>
              <span class="hidden forReportingCycle forCycle"></span>
              <span class="hidden fileTypes excelType">leadProjectInstitutionsSummary-leadProjectInstitutionsSummary</span>
              [@customForm.select name="programID" header=false   label=""  i18nkey="Select a Research Program"  listName="programs"  keyFieldName="id"  displayFieldName="composedName" className="allPrograms"   multiple=false required=true   editable=true/]
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
          
          [#--impactPathway by research program --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="impactPathwaySubmissions"/>
              <label for="">[@s.text name="summaries.board.report.ipByProgram" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.ipByProgram.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              <span class="hidden forPlanningCycle forCycle"></span>
              <span class="hidden forReportingCycle forCycle"></span>
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
        
        
        [#-- Full Project Report (PDF) --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input type="radio" name="formOptions" id="projectPortfolio" value="reportingSummary" class="hidden"/>
              <label for="">[@s.text name="summaries.board.report.fullProjectReport" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.fullProjectReport.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none">
              <span class="hidden fileTypes pdfType">reportingSummary</span>
              <span class="hidden forPlanningCycle forCycle"></span>
              <span class="hidden forReportingCycle forCycle"></span>
              <input type="hidden" id="projectID" name="projectID" value="" />
              <div class="row">                
                <div class="col-md-12">
                  <a id="generateProject" style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
                </div>
              </div>
            </div>
          </div>
          
          [#-- Search terms summary --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="searchTermsSummary" value="searchTermsSummary"/>
              <label for="">[@s.text name="summaries.board.report.searchTerms" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.searchTerms.description" /] You can to add terms and generate the report, or simply generate the report without add terms.</span>
            <div class="extraOptions col-md-12" style="display:none">
            <span class="hidden fileTypes excelType">searchTermsSummary-searchTermsSummary</span>
            <span class="hidden forPlanningCycle forCycle"></span>
            <span class="hidden forReportingCycle forCycle"></span>
            [#-- predefined terms --]
            <label for="">Predefined terms:</label>
            <br />
            <div class="col-md-12">
              <label for="gender">Gender <input id="gender" type="checkbox" class="notview" /></label>
            </div>
            <div class="clearfix"></div>
            <hr />
            [#-- content --]
            <div class="simpleBox wordContent">
              
            </div>
            [#-- input --]
            <label for="">Write the term:</label>
            <div class="row">
              <div class="col-md-9">
                <input class="form-control inputTerm" type="text" placeholder="Press enter to add a new term " />
              </div>
              <div class="col-md-3 pull right">
                <a  style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
              
            </div>
            </div>
          </div>
          
          [#-- Outputs Contributions --] 
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="outputsContributionsSummary"/>
              <label for="">[@s.text name="summaries.board.report.outputsContributionsSummary" /]</label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.outputsContributionsSummary.description" /]</span>
            <div class="extraOptions" style="display:none"> 
            <span class="hidden fileTypes excelType">OutcomesContributionsSummary-OutcomesContributionsSummary</span>
            <span class="hidden forPlanningCycle forCycle"></span>
            <div class="pull-right">
              <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
            </div>
            </div>
          </div>
          
        </div>
        
        [#-- Monitoring reports --]
        <div id="monitoring-contentOptions" style="display:none">
          [#--Outcomes Contribution --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="outcomesContributionSummary" value="outcomesContributions"/>
              <label for="">[@s.text name="summaries.board.report.outcomeContribution" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.outcomeContribution.description" /]</span>
            <div class="extraOptions col-md-12" style="display:none"> 
              <span class="hidden forPlanningCycle forCycle"></span>
              <span class="hidden forReportingCycle forCycle"></span>
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

[#include "/WEB-INF/global/pages/footer.ftl"]