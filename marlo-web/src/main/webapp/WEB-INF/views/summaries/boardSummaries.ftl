[#ftl]
[#assign title = "Summaries Section" /]
[#assign pageLibs = ["select2","font-awesome"] /]
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
      <div class="headerBlock">
        <h5 class="col-md-3">[@s.text name="summaries.board.projectResearchCycle" /]</h5>
        <div class="summariesOption col-md-3">
          <input type="radio" name="cycle" id="planning" value="planning"/>
          <label for="planning">[@s.text name="summaries.board.projectResearchCycle.planning" /]</label>
        </div>
        <div class="summariesOption col-md-3">
          <input type="radio" name="cycle" id="reporting" value="Reporting"  checked="checked" />
          <label for="reporting">[@s.text name="summaries.board.projectResearchCycle.reporting" /]</label>
        </div>
        <div class="clearfix"></div>
        <hr />
      </div>
      
      <h4 class="col-md-12 selectReport-title">[@s.text name="summaries.board.selectReportType" /]</h4>
      <div class="summariesOptions col-md-12">
        [#-- -- -- Projects reports -- -- --]
        <div id="projects-contentOptions">
          
          [#-- Gender Contribution Project Level Summary --]
          <div class="summariesFiles borderBox col-md-3">
          <span title="[@s.text name="summaries.board.report.genderContributionSummary.description" /]" class="info-file fa fa-info-circle"></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="searchTermsSummary" value="searchTermsSummary"/>
              <label for="searchTermsSummary">[@s.text name="summaries.board.report.genderContributionSummary" /] </label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
          </div>
          
          [#-- List of all projects and their leading institution --]
          <div class="summariesFiles borderBox col-md-3">
            <span title="[@s.text name="summaries.board.report.projectPartnersSummary.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectPartnersSummary" value="leadProjectPartnersSummary"/>
              <label for="leadProjectPartnersSummary">[@s.text name="summaries.board.report.projectPartnersSummary" /]  </label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
          </div>
          
          [#-- Impact Pathways Contributions --] 
          <div class="summariesFiles borderBox col-md-3">
            <span title="[@s.text name="summaries.board.report.impactPathwayContributionsSummary.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="impactPathwayContributionsSummary" value="impactPathwayContributionsSummary"/>
              <label for="impactPathwayContributionsSummary">[@s.text name="summaries.board.report.impactPathwayContributionsSummary" /]</label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
              <div class="extraOptions" style="display:none"> 
              <input type="hidden" name="year" value="${currentCycleYear}" />
            </div>
          </div>
          
          [#-- List of all Submmited Projects --] 
          <div class="summariesFiles borderBox col-md-3">
            <span title="[@s.text name="summaries.board.report.submmitedProjects.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="submmitedProjects" value="submmitedProjects"/>
              <label for="submmitedProjects">[@s.text name="summaries.board.report.submmitedProjects" /]</label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
            <div class="extraOptions" style="display:none"> 
              <input type="hidden" name="year" value="${currentCycleYear}" />
            </div>
          </div>
          
          [#-- Full Project Report (PDF) --]
          <div class="summariesFiles borderBox col-md-3">
            <span title="[@s.text name="summaries.board.report.projectPortfolio.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input type="radio" name="formOptions" id="projectPortfolio" value="project" class="hidden"/>
              <label for="projectPortfolio">[@s.text name="summaries.board.report.projectPortfolio" /] </label>
            </div>
            <span class="fa fa-file-pdf-o col-md-12 pdfIcon"></span>
            <div class="extraOptions" style="display:none"> 
              [@customForm.select name="projectID" label="" i18nkey="" listName="allProjects" keyFieldName="id" displayFieldName="composedName" className="" disabled=true/]
            </div>
          </div>
          
          
          
        </div>
        [#-- -- -- Partners reports -- -- --]
        <div id="partners-contentOptions" style="display:none">
          [#-- Partners and lead projects --]
          <div class="summariesFiles borderBox col-md-3">
            <span title="[@s.text name="summaries.board.report.leadProjectInstitutionsSummary.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="leadProjectInstitutionsSummary" value="leadProjectInstitutionsSummary"/>
              <label for="leadProjectInstitutionsSummary">[@s.text name="summaries.board.report.leadProjectInstitutionsSummary" /] </label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
          </div>
          [#-- Partners and projects they relate --]
          <div class="summariesFiles borderBox col-md-3">
            <span title="[@s.text name="summaries.board.report.partnersWorkingWithProjects.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="projectPartnersSummary" value="projectPartnersSummary"/>
              <label for="projectPartnersSummary">[@s.text name="summaries.board.report.partnersWorkingWithProjects" /] </label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
          </div>
        </div>
        
        
        [#-- Deliverables reports --]
        <div id="deliverables-contentOptions" style="display:none">
          [#-- Expected deliverables --]
          <div class="summariesFiles borderBox col-md-3">
            <span title="[@s.text name="summaries.board.report.expectedDeliverables.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="expectedDeliverables" value="expectedDeliverables"/>
              <label for="expectedDeliverables">[@s.text name="summaries.board.report.expectedDeliverables" /] </label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
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
          <div class="summariesFiles borderBox col-md-3">
          <span title="[@s.text name="summaries.board.report.powb.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="budgetPerPartnersSummary" value="budgetPerPartnersSummary"/>
              <label for="budgetPerPartnersSummary">[@s.text name="summaries.board.report.powb" /] <span>XLSx</span></label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
            <div class="extraOptions" style="display:none"> 
              <input type="hidden" name="year" value="${currentCycleYear}" />
            </div>
          </div>
          [#-- Budget Summary by MOGs --]
          <div class="summariesFiles borderBox col-md-3">
            <span title="[@s.text name="summaries.board.report.powbMOG.description" /]" class="info-file fa fa-info-circle "></span>
            <div class="col-md-12 title-file">
              <input class="hidden" type="radio" name="formOptions" id="budgetByMOGsSummary" value="budgetByMOGsSummary"/>
              <label for="budgetByMOGsSummary">[@s.text name="summaries.board.report.powbMOG" /] <span>XLSx</span></label>
            </div>
            <span class="fa fa-file-excel-o col-md-12 excelIcon"></span>
            <div class="extraOptions" style="display:none"> 
              <input type="hidden" name="year" value="${currentCycleYear}" />
            </div>
          </div>
          
          
        </div>
      </div>
      <br />
      <a id="generateReport" target="_blank" class="addButton pull-right" style="" href="#">[@s.text name="form.buttons.generate" /]</a>
      </form>
    </div> 
  </article>
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]