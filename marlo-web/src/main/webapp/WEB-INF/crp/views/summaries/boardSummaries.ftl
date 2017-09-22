[#ftl]
[#assign title = "Summaries Section" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2","font-awesome","jsUri"] /]
[#assign customJS = [
  "${baseUrl}/global/js/utils.js", 
  "${baseUrlMedia}/js/summaries/boardSummaries.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/summaries/summaries.css"] /]
[#assign currentSection = "summaries" /]

[#assign breadCrumb = [
  {"label":"summaries", "nameSpace":"summaries", "action":"summaries"}
]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign years = ["2014","2015", "2016","2017" ]/]

<span class="hidden planningYear">${(action.getPlanningYear())!}</span>
<span class="hidden reportingYear">${(action.getReportingYear())!}</span>
    
<section class="container">
  <article id="" class="fullBlock col-md-12" > 
    <br /> 
    <div class="summariesButtons clearfix">
      <div id="projects" class="summariesSection current"><span></span><a href="">[@s.text name="summaries.board.options.projects" /]</a></div>
      <div id="partners" class="summariesSection"><span></span><a href="">[@s.text name="summaries.board.options.partners" /]</a></div>
      <div id="deliverables" class="summariesSection"><span></span><a href="">[@s.text name="summaries.board.options.deliverables" /]</a></div>
      <div id="budget" class="summariesSection"><span></span><a href="">[@s.text name="summaries.board.options.budget" /]</a></div>
    </div>
    <div class="summariesContent borderBox col-md-12">
      <div class="loading" style="display:none"></div>
      <form action="">
      
      <h4 class="col-md-12 selectReport-title">[@s.text name="summaries.board.selectReportType" /]</h4>
      <div class="summariesOptions col-md-12">
        [#-- -- -- Projects reports -- -- --]
        <div id="projects-contentOptions">
        
        
        [#-- Full Project Report (PDF) --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input type="radio" name="formOptions" id="projectPortfolio" value="reportingSummary" class="hidden"/>
              <label for="">[@s.text name="summaries.board.report.projectPortfolio" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.projectPortfolio.description" /]</span>
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
          
          [#-- Gender Contribution Project Level Summary --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="searchTermsSummary" value="searchTermsSummary"/>
              <label for="">[@s.text name="summaries.board.report.genderContributionSummary" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.genderContributionSummary.description" /] You can to add terms and generate the report, or simply generate the report without add terms.</span>
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
          
          [#-- Impact Pathways Contributions --] 
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="OutcomesContributionsSummary"/>
              <label for="">[@s.text name="summaries.board.report.impactPathwayContributionsSummary" /]</label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.impactPathwayContributionsSummary.description" /]</span>
            <div class="extraOptions" style="display:none"> 
            <span class="hidden fileTypes excelType">OutcomesContributionsSummary-OutcomesContributionsSummary</span>
            <span class="hidden forPlanningCycle forCycle"></span>
            <div class="pull-right">
              <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
            </div>
            </div>
          </div>
        
        [#-- caseStudies By Year Summary --] 
          <div class="summariesFiles borderBox col-md-12" >
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="caseStudiesByYearSummary"/>
              <label for="">[@s.text name="summaries.board.report.outcomeCaseStudies" /]</label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.outcomeCaseStudies.description" /]</span>
              <div class="extraOptions" style="display:none">
              <span class="hidden fileTypes pdfType">caseStudySummary</span>
              <span class="hidden fileTypes excelType">caseStudiesByYearSummary-caseStudiesByYearSummary</span>
              <span class="hidden forReportingCycle forCycle"></span>
              <span class="hidden specificYears">2015-2016</span>
                <div class="pull-right">
                  <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
                </div>
            </div>
          </div>
          
          [#-- PROJECT HIGHLIGHTS --]
          <div class="summariesFiles borderBox col-md-12" >
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="projectHighlightsPDFSummary"/>
              <label for="">[@s.text name="summaries.board.report.projectHighlights" /]</label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.projectHighlights.description" /]</span>
              <div class="extraOptions" style="display:none">
              <span class="hidden fileTypes pdfType">projectHighlightsPDFSummary</span>
              <span class="hidden fileTypes excelType">projectHighlightsExcelSummary-projectHighlightsExcelSummary</span>
              <span class="hidden forReportingCycle forCycle"></span>
              <span class="hidden specificYears">2015-2016</span>
                <div class="pull-right">
                  <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
                </div>
            </div>
          </div>
          
          [#-- PROJECT Leverages --]
            [#if action.hasSpecificities("crp_leverages_module") ] 
          <div class="summariesFiles borderBox col-md-12" >
            <div class="col-md-12 title-file">
           
              <input class="hidden" type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="LeveragesReportingSummary"/>
              <label for="">[@s.text name="summaries.board.report.leverages" /]</label>
            
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.leverages.description" /]</span>
              <div class="extraOptions" style="display:none">
              <span class="hidden fileTypes excelType">LeveragesReportingSummary-LeveragesReportingSummary</span>
              <span class="hidden forReportingCycle forCycle"></span>
              <span class="hidden specificYears">2015-2016</span>
                <div class="pull-right">
                  <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
                </div>
            </div>
          </div>
            [/#if]
          [#-- Synthesis by Outcome 
          <div class="summariesFiles borderBox col-md-12" >
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="OutcomeSynthesisReportingSummary"/>
              <label for="">[@s.text name="summaries.board.report.synthesisByOutcome" /]</label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.synthesisByOutcome.description" /]</span>
              <div class="extraOptions" style="display:none">
              <span class="hidden fileTypes excelType">OutcomeSynthesisReportingSummary-OutcomeSynthesisReportingSummary</span>
              <span class="hidden forReportingCycle forCycle"></span>
                <div class="pull-right">
                  <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
                </div>
            </div>
          </div>
          --]
          
          [#-- caseStudies By Year Summary PDF
          <div class="summariesFiles borderBox col-md-3 reportingCycle" style="display:none;">
            <span title="[@s.text name="" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="caseStudySummary"/>
              <label for="">[@s.text name="Project outcomes case studies" /]</label>
            </div>
            <span class="fa fa-file-pdf-o col-md-12 pdfIcon"></span>
              <div class="extraOptions" style="display:none"> 
            </div>
          </div>
          --] 
          
        
        
        </div>
        [#-- -- -- Partners reports -- -- --]
        <div id="partners-contentOptions" style="display:none">
          [#-- Partners and lead projects --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="leadProjectInstitutionsSummary"/>
              <label for="">[@s.text name="summaries.board.report.leadProjectInstitutionsSummary" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.leadProjectInstitutionsSummary.description" /]</span>
            <div class="extraOptions" style="display:none"> 
            <span class="hidden forPlanningCycle forCycle"></span>
            <span class="hidden forReportingCycle forCycle"></span>
            <span class="hidden fileTypes excelType">leadProjectInstitutionsSummary-leadProjectInstitutionsSummary</span>
            <div class="pull-right">
              <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
            </div>
            </div>
          </div>
          [#-- Partners and projects they relate --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="projectPartnersSummary" value="projectPartnersSummary"/>
              <label for="">[@s.text name="summaries.board.report.partnersWorkingWithProjects" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.partnersWorkingWithProjects.description" /]</span>
            <div class="extraOptions" style="display:none">
            <span class="hidden forPlanningCycle forCycle"></span>
            <span class="hidden forReportingCycle forCycle"></span>
            <span class="hidden fileTypes excelType">projectPartnersSummary-projectPartnersSummary</span>
            <div class="pull-right">
              <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
            </div>
            </div>
          </div>
        </div>
        
        
        [#-- Deliverables reports --]
        <div id="deliverables-contentOptions" style="display:none">
          [#-- Expected deliverables --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="expectedDeliverables" value="expectedDeliverablesSummary"/>
              <label for="">[@s.text name="summaries.board.report.expectedDeliverables" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.expectedDeliverables.description" /]</span>
            <div class="extraOptions" style="display:none">
            <span class="hidden forPlanningCycle forCycle"></span>
            <span class="hidden forReportingCycle forCycle"></span>
            <span class="hidden fileTypes excelType">expectedDeliverablesSummary-DeliverablesReportingSummary</span> 
            <div class="pull-right">
              <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
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
        [#-- -- -- Budget reports -- -- --]
        <div id="budget-contentOptions" style="display:none">
          [#-- Budget Summary per Partners --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="budgetPerPartnersSummary" value="budgetPerPartnersSummary"/>
              <label for="budgetPerPartnersSummary">[@s.text name="summaries.board.report.powb" /]</label>
            </div>
            <span class="description col-md-12">[@s.text name="summaries.board.report.powb.description" /]</span>
            <div class="extraOptions" style="display:none"> 
            <span class="hidden forPlanningCycle forCycle"></span>
            <span class="hidden fileTypes excelType">budgetPerPartnersSummary-budgetPerPartnersSummary</span> 
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
          [#-- Budget Summary by CoA --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="budgetByCoAsSummary" value="budgetByCoAsSummary"/>
              <label for="budgetByMOGsSummary">[@s.text name="summaries.board.report.powbMOG" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="This report summarizes the project budget that is allocated to each CoA by source of funding (e.g. W1/W2, W3 and Bilateral)." /]</span>
            <div class="extraOptions" style="display:none"> 
            <span class="hidden forPlanningCycle forCycle"></span>
            <span class="hidden fileTypes excelType">budgetByCoAsSummary-budgetByCoAsSummary</span> 
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
              </div>
            </div>
          </div>
          
          [#-- Funding Sources Summary --]
          <div class="summariesFiles borderBox col-md-12">
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="budgetByCoAsSummary" value="FundingSourcesSummary"/>
              <label for="budgetByMOGsSummary">[@s.text name="Funding Sources Summary" /] </label>
            </div>
            <span class="description col-md-12">[@s.text name="This report provides a summary of the funding sources. The first tab lists each funding source, lead partner(s), project(s) to which it contributes, its annual budget, and allows you to see how much of the funding source budget has been allocated to projects. The second tab provides funding source information by managing partner. So in cases when more than one managing partner leads a funding source, this tab will show you how the funding source budget is allocated to the different managing partners." /]</span>
            <div class="extraOptions" style="display:none">
            <span class="hidden forPlanningCycle forCycle"></span>
            <span class="hidden fileTypes excelType">FundingSourcesSummary-FundingSourcesSummary</span> 
              <div class="pull-right">
                <a style="display:none;" target="_blank" class="generateReport addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
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

[#include "/WEB-INF/global/pages/footer.ftl"]