[#ftl]
[#assign title = "Summaries Section" /]
[#assign currentSectionString = "${actionName?replace('/','-')}" /]
[#assign pageLibs = ["select2","font-awesome","jsUri", "caret", "jquery-tag-editor"] /]
[#assign customJS = [
  "${baseUrl}/global/js/utils.js", 
  "${baseUrlMedia}/js/summaries/boardSummaries_v2.js"
  ] 
/]

[#assign customCSS = ["${baseUrlMedia}/css/summaries/summaries.css"] /]
[#assign currentSection = "summaries" /]

[#assign breadCrumb = [
  {"label":"summaries", "nameSpace":"summaries", "action":"summaries"}
]/]

[#assign reportsTypes = [
  [#-- PROJECT REPORTS --]
  { "slug": "projects", "title":"summaries.board.options.projects", "reportsList": [
    { "title": "summaries.board.report.projectPortfolio", 
      "description": "summaries.board.report.projectPortfolio.description",
      "namespace": "/projects",
      "action": "${crpSession}/reportingSummary",
      "formats": [ "PDF" ],
      "cycles": [ "Planning", "Reporting" ],
      "allowProjectID": true
    },
    { "title": "summaries.board.report.genderContributionSummary", 
      "description": "summaries.board.report.genderContributionSummary.description",
      "namespace": "/projects",
      "action": "${crpSession}/searchTermsSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning", "Reporting" ],
      "allowKeyWords": true
    },
    { "title": "summaries.board.report.impactPathwayContributionsSummary", 
      "description": "summaries.board.report.impactPathwayContributionsSummary.description",
      "namespace": "/projects",
      "action": "${crpSession}/OutcomesContributionsSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    },
    { "title": "summaries.board.report.outcomeCaseStudies", 
      "description": "summaries.board.report.outcomeCaseStudies.description",
      "namespace": "/projects",
      "action": "${crpSession}/caseStudySummary",
      "formats": [ "PDF", "Excel" ],
      "cycles": [ "Reporting" ]
    },
    { "title": "summaries.board.report.projectHighlights", 
      "description": "summaries.board.report.projectHighlights.description",
      "namespace": "/projects",
      "action": "${crpSession}/projectHighlightsSummary",
      "formats": [ "PDF", "Excel" ],
      "cycles": [ "Reporting" ]
    },
    { "title": "summaries.board.report.leverages", 
      "description": "summaries.board.report.leverages.description",
      "namespace": "/projects",
      "action": "${crpSession}/LeveragesReportingSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Reporting" ]
    }
  ]},
  [#-- PARTNERS REPORTS --]
  { "slug": "partners", "title":"summaries.board.options.partners", "reportsList": [
    { "title": "summaries.board.report.leadProjectInstitutionsSummary", 
      "description": "summaries.board.report.leadProjectInstitutionsSummary.description",
      "namespace": "/projects",
      "action": "${crpSession}/leadProjectInstitutionsSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning", "Reporting" ]
    },
    { "title": "summaries.board.report.partnersWorkingWithProjects", 
      "description": "summaries.board.report.partnersWorkingWithProjects.description",
      "namespace": "/projects",
      "action": "${crpSession}/projectPartnersSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning", "Reporting" ]
    }
  ]},
  [#-- DELIVERABLES REPORTS --]
  { "slug": "deliverables", "title":"summaries.board.options.deliverables", "reportsList": [
    { "title": "summaries.board.report.expectedDeliverables", 
      "description": "summaries.board.report.expectedDeliverables.description",
      "namespace": "/projects",
      "action": "${crpSession}/expectedDeliverablesSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    },
    { "title": "summaries.board.report.reportedDeliverables", 
      "description": "summaries.board.report.reportedDeliverables.description",
      "namespace": "/projects",
      "action": "${crpSession}/DeliverablesReportingSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Reporting" ]
    }
  ]},
  [#-- BUDGET REPORTS --]
  { "slug": "budget", "title":"summaries.board.options.budget", "reportsList": [
    { "title": "summaries.board.report.powb", 
      "description": "summaries.board.report.powb.description",
      "namespace": "/projects",
      "action": "${crpSession}/budgetPerPartnersSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    },
    { "title": "summaries.board.report.powbMOG", 
      "description": "summaries.board.report.powbMOG.description",
      "namespace": "/projects",
      "action": "${crpSession}/budgetByCoAsSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    },
    { "title": "summaries.board.report.fundingSourceSummary", 
      "description": "summaries.board.report.fundingSourceSummary.description",
      "namespace": "/projects",
      "action": "${crpSession}/FundingSourcesSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    }
  ]}
]/]


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#assign years = [ "2016","2017", "2018" ]/]

<span class="hidden planningYear">${(action.getPlanningYear())!}</span>
<span class="hidden reportingYear">${(action.getReportingYear())!}</span>
    
<section class="container">
  <article id="" class="" > 
    [#--  Reports Tabs --]
    <div class="summariesButtons col-md-3">
      [#list reportsTypes as reportType]
        <div id="${reportType.slug}" class="summariesSection [#if reportType_index == 0]current[/#if]">
          <span>[#-- Icon --]</span><a href="">[@s.text name=reportType.title /]</a>
        </div>
      [/#list]
    </div>
    [#--  Reports Content --]
    <div class="summariesContent col-md-9" style="min-height:550px;">
      <h3 class="headTitle text-center">Summaries</h3>
      <div class="loading" style="display:none"></div>
<<<<<<< HEAD
      <div class="summariesOptions">
        [#list reportsTypes as reportType]
        <div id="${reportType.slug}-contentOptions" class="" style="display: [#if reportType_index != 0]none[/#if];">
          [#list reportType.reportsList as report]
            [@reportMacro report /]
          [/#list]
=======
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
            <span class="description col-md-12">[@s.text name="summaries.board.report.genderContributionSummary.description" /] </span>
            <div class="extraOptions col-md-12" style="display:none">
            <span class="hidden fileTypes excelType">searchTermsSummary-searchTermsSummary</span>
            <span class="hidden forPlanningCycle forCycle"></span>
            <span class="hidden forReportingCycle forCycle"></span>
            [#-- predefined terms --]
            [#if action.hasSpecificities('show_gender_keywords_summaries')]
            <label for="">Predefined terms:</label>
            <br />
            <div class="col-md-12">
              <label for="gender">Gender <input id="gender" type="checkbox" class="notview" /></label>
            </div>
            <div class="clearfix"></div>
            <hr />
            [/#if]
            [#-- content --]
            <div class="simpleBox wordContent">
              
            </div>
            [#-- input --]
            <label for="">[@s.text name="summaries.board.report.searchTermsSummary" /]</label>
            <div class="row">
              <div class="col-md-9">
                <input class="form-control inputTerm" type="text" />
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
          
        
        
>>>>>>> refs/remotes/origin/staging
        </div>
        [/#list] 
      </div>
    </div> 
  </article>
</section>

[#include "/WEB-INF/crp/pages/footer.ftl"]


[#macro reportMacro report]

<div class="summariesFiles simpleBox ${(report.allowProjectID??)?string('allowProjectID','')}">
  <div class="loading" style="display:none"></div>
  <div class="form-group">
    [#-- Tags --]
    <div class="tags pull-right">
      [#list report.cycles as tag ]<span class="label label-default type-${tag?lower_case}">${tag}</span>[/#list]
      [#list report.formats as icon ]
      <span class="label label-default type-${icon?lower_case}"><span class="fa fa-file-${icon?lower_case}-o ${icon?lower_case}Icon file"></span> ${icon}</span>
      [/#list] 
    </div>
    [#-- Title --]
    <h4 class="title-file">[@s.text name=report.title /]</h4>
    [#-- Description --]
    <p class="description">[@s.text name=report.description /]</p>
  </div>
  
  [#-- Options --]
  <div class="extraOptions" style="display: none;">
    <hr />
    [@s.form  target="_blank" action=report.action  method="GET" namespace=report.namespace cssClass=""]
      [#-- Parameters --]
      <div class="form-group row">
        [#-- Cycles (Planning/Reporting) --]
        [#if report.cycles??]
          [#if report.cycles?size > 1]
          <div class="col-md-4">
            <label for="">Cycle:</label>
            <select name="cycle" id="">
              [#list report.cycles as cycle ]
              <option value="${cycle}" [#if (actualPhase.description == cycle)!false]selected[/#if]>${cycle}</option>
              [/#list]  
            </select>
          </div>
          [#else]
            <input type="hidden" name="cycle" value="${report.cycles[0]}" />
          [/#if]
        [/#if]
        [#-- Years --]
        <div class="col-md-4">
          <label for="">Year:</label>
          <select name="year" id="">
            [#list years as year ]
            <option value="${year}" [#if (actualPhase.year == year?number)!false]selected[/#if]>${year}</option>
            [/#list]  
          </select>
        </div>
        [#-- Formats (PDF/Excel) --]
        [#if report.formats??]
          [#if report.formats?size > 1]
          <div class="col-md-4">
            <label for="">Format:</label>
            <select name="format" id="">
              [#list report.formats as format ]
              <option value="${format}">${format}</option>
              [/#list]
            </select>
          </div>
          [#else]
            <input type="hidden" name="format" value="${report.formats[0]}" />
          [/#if]
        [/#if]
      </div>
      
      [#-- Projetc ID --]
      [#if report.allowProjectID??]
      <div class="form-group row">
        <div class="col-md-8">
          [@customForm.select name="projectID"   label=""  i18nkey="Select a project"  listName=""  keyFieldName="id"  displayFieldName="composedName" className="allProjectsSelect" /]
        </div>
      </div>
      [/#if]
      
      [#-- KeyWords--]
      [#if report.allowKeyWords??]
      <div class="form-group">
        <div class="form-group">
          <label for="">Keywords: </label> <i>(Separated by commas)</i>
          <textarea name="keys" class="keywords" cols="30" rows="40"></textarea>
        </div>
        <div class="btn btn-default btn-xs addGenderKeys" role="button">Add predefined <strong>gender</strong> keywords</div>
        <div class="btn btn-danger btn-xs removeAllTags" role="button">Remove all keywords</div>
      </div>
      [/#if]
      
      [#-- Generate Button--]
      <button type="submit" class="btn btn-info pull-right"><span class="glyphicon glyphicon-download-alt"></span> Generate</button> <div class="clearfix"></div>
    [/@s.form]
  </div>
</div>
[/#macro]