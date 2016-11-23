[#ftl]
[#assign title = "Summaries Section" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/global/utils.js", "${baseUrl}/js/summaries/boardSummaries.js"] /]
[#assign customCSS = ["${baseUrl}/css/summaries/summaries.css"] /]
[#assign currentSection = "summaries" /]

[#assign breadCrumb = [
  {"label":"summaries", "nameSpace":"summaries", "action":"board"}
]/]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
    
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
      <h6>[@s.text name="summaries.board.projectResearchCycle" /]</h6>
      <div class="summariesOption">
        <input type="radio" name="cycle" id="planning" value="planning"/>
        <label for="planning">[@s.text name="summaries.board.projectResearchCycle.planning" /]</label>
      </div>
      <div class="summariesOption">
        <input type="radio" name="cycle" id="reporting" value="Reporting"  checked="checked" />
        <label for="reporting">[@s.text name="summaries.board.projectResearchCycle.reporting" /]</label>
      </div>
      
      <h6>[@s.text name="summaries.board.selectReportType" /]</h6>
      <div class="summariesOptions">
        [#-- -- -- Projects reports -- -- --]
        <div id="projects-contentOptions">
          [#-- Full Project Report (PDF) --]
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="projectPortfolio" value="project"/>
            <label for="projectPortfolio">[@s.text name="summaries.board.report.projectPortfolio" /] <span>PDF</span></label>
            <p class="description">[@s.text name="summaries.board.report.projectPortfolio.description" /] </p>
            <div class="extraOptions" style="display:none"> 
              [@customForm.select name="projectID" label="" i18nkey="" listName="allProjects" keyFieldName="id" displayFieldName="composedName" className="" disabled=true/]
            </div>
          </div>
          [#-- List of all projects and their leading institution --]
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="leadProjectPartnersSummary" value="leadProjectPartnersSummary"/>
            <label for="leadProjectPartnersSummary">[@s.text name="summaries.board.report.projectPartnersSummary" /]  <span>XLSx</span></label>
            <p class="description">[@s.text name="summaries.board.report.projectPartnersSummary.description" /]</p>
          </div>
          [#-- List of all Submmited Projects --] 
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="submmitedProjects" value="submmitedProjects"/>
            <label for="submmitedProjects">[@s.text name="summaries.board.report.submmitedProjects" /]  <span>XLSx</span></label>
            <p class="description">[@s.text name="summaries.board.report.submmitedProjects.description" /]</p>
            <div class="extraOptions" style="display:none"> 
              <input type="hidden" name="year" value="${currentCycleYear}" />
            </div>
          </div>
          [#-- Gender Contribution Project Level Summary --]
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="searchTermsSummary" value="searchTermsSummary"/>
            <label for="searchTermsSummary">[@s.text name="summaries.board.report.genderContributionSummary" /] <span>XLSx</span></label>
            <p class="description">[@s.text name="summaries.board.report.genderContributionSummary.description" /] </p>
            [#--<div class="extraOptions" style="display:none"> 
              [@customForm.input name="q" className="" i18nkey="summaries.board.report.genderContributionSummary.keywords" /]
            </div> --]
          </div>
          
          
          
          
          [#-- Impact Pathways Contributions --] 
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="impactPathwayContributionsSummary"/>
            <label for="impactPathwayContributionsSummary">[@s.text name="summaries.board.report.impactPathwayContributionsSummary" /] <span>XLSx</span></label>
            <p class="description">[@s.text name="summaries.board.report.impactPathwayContributionsSummary.description" /] </p>
            <div class="extraOptions" style="display:none"> 
              <input type="hidden" name="year" value="${currentCycleYear}" />
            </div>
          </div>
          
        </div>
        [#-- -- -- Partners reports -- -- --]
        <div id="partners-contentOptions" style="display:none">
          [#-- Partners and lead projects --]
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="leadProjectInstitutionsSummary"/>
            <label for="leadProjectInstitutionsSummary">[@s.text name="summaries.board.report.leadProjectInstitutionsSummary" /]  <span>XLSx</span></label>
            <p class="description">[@s.text name="summaries.board.report.leadProjectInstitutionsSummary.description" /] </p>
          </div>
          [#-- Partners and projects they relate --]
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="projectPartnersSummary" value="projectPartnersSummary"/>
            <label for="projectPartnersSummary">[@s.text name="summaries.board.report.partnersWorkingWithProjects" /]  <span>XLSx</span></label>
            <p class="description">[@s.text name="summaries.board.report.partnersWorkingWithProjects.description" /] </p>
          </div>
        </div>
        [#-- Deliverables reports --]
        <div id="deliverables-contentOptions" style="display:none">
          [#-- Expected deliverables --]
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="expectedDeliverables" value="expectedDeliverables"/>
            <label for="expectedDeliverables">[@s.text name="summaries.board.report.expectedDeliverables" /] <span>XLSx</span></label>
            <p class="description">[@s.text name="summaries.board.report.expectedDeliverables.description" /] </p>
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
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="budgetPerPartnersSummary" value="budgetPerPartnersSummary"/>
            <label for="budgetPerPartnersSummary">[@s.text name="summaries.board.report.powb" /] <span>XLSx</span></label>
            <p class="description"> [@s.text name="summaries.board.report.powb.description" /]</p>
            <div class="extraOptions" style="display:none"> 
              <input type="hidden" name="year" value="${currentCycleYear}" />
            </div>
          </div>
          [#-- Budget Summary by MOGs --]
          <div class="summariesOption">
            <input type="radio" name="formOptions" id="budgetByMOGsSummary" value="budgetByMOGsSummary"/>
            <label for="budgetByMOGsSummary">[@s.text name="summaries.board.report.powbMOG" /] <span>XLSx</span></label>
            <p class="description">[@s.text name="summaries.board.report.powbMOG.description" /] </p>
            <div class="extraOptions" style="display:none"> 
              <input type="hidden" name="year" value="${currentCycleYear}" />
            </div>
          </div>
        </div>
      </div>
      <br />
      <a id="generateReport" target="_blank" class="addButton" style="display:none" href="#">[@s.text name="form.buttons.generate" /]</a>
      </form>
    </div> 
  </article>
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]