[#ftl]
[#assign title = "Summaries Section" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
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
    { "active": true,
      "title": "summaries.board.report.projectPortfolio", 
      "description": "summaries.board.report.projectPortfolio.description",
      "namespace": "/projects",
      "action": "${crpSession}/reportingSummary",
      "formats": [ "PDF" ],
      "cycles": [ "Planning", "Reporting" ],
      "allowProjectID": true
    },
    { "active": true,
      "title": "summaries.board.report.genderContributionSummary", 
      "description": "summaries.board.report.genderContributionSummary.description",
      "namespace": "/projects",
      "action": "${crpSession}/searchTermsSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning", "Reporting" ],
      "allowKeyWords": true
    },
    { "active": true,
      "title": "summaries.board.report.impactPathwayContributionsSummary", 
      "description": "summaries.board.report.impactPathwayContributionsSummary.description",
      "namespace": "/projects",
      "action": "${crpSession}/OutcomesContributionsSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    },
    { "active": true,
      "title": "summaries.board.report.outcomeCaseStudies", 
      "description": "summaries.board.report.outcomeCaseStudies.description",
      "namespace": "/projects",
      "action": "${crpSession}/caseStudySummary",
      "formats": [ "PDF", "Excel" ],
      "cycles": [ "Reporting" ]
    },
    { "active": true,
      "title": "summaries.board.report.projectHighlights", 
      "description": "summaries.board.report.projectHighlights.description",
      "namespace": "/projects",
      "action": "${crpSession}/projectHighlightsSummary",
      "formats": [ "PDF", "Excel" ],
      "cycles": [ "Reporting" ]
    },
    { "active": action.hasSpecificities("crp_leverages_module"),
      "title": "summaries.board.report.leverages", 
      "description": "summaries.board.report.leverages.description",
      "namespace": "/projects",
      "action": "${crpSession}/LeveragesReportingSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Reporting" ]
    }
  ]},
  [#-- PARTNERS REPORTS --]
  { "slug": "partners", "title":"summaries.board.options.partners", "reportsList": [
    { "active": true,
      "title": "summaries.board.report.leadProjectInstitutionsSummary", 
      "description": "summaries.board.report.leadProjectInstitutionsSummary.description",
      "namespace": "/projects",
      "action": "${crpSession}/leadProjectInstitutionsSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning", "Reporting" ]
    },
    { "active": true,
      "title": "summaries.board.report.partnersWorkingWithProjects", 
      "description": "summaries.board.report.partnersWorkingWithProjects.description",
      "namespace": "/projects",
      "action": "${crpSession}/projectPartnersSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning", "Reporting" ]
    }
  ]},
  [#-- DELIVERABLES REPORTS --]
  { "slug": "deliverables", "title":"summaries.board.options.deliverables", "reportsList": [
    { "active": true,
      "title": "summaries.board.report.expectedDeliverables", 
      "description": "summaries.board.report.expectedDeliverables.description",
      "namespace": "/projects",
      "action": "${crpSession}/expectedDeliverablesSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    },
    { "active": true,
      "title": "summaries.board.report.reportedDeliverables", 
      "description": "summaries.board.report.reportedDeliverables.description",
      "namespace": "/projects",
      "action": "${crpSession}/DeliverablesReportingSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Reporting" ]
    }
  ]},
  [#-- BUDGET REPORTS --]
  { "slug": "budget", "title":"summaries.board.options.budget", "reportsList": [
    { "active": true,
      "title": "summaries.board.report.powb", 
      "description": "summaries.board.report.powb.description",
      "namespace": "/projects",
      "action": "${crpSession}/budgetPerPartnersSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    },
    { "active": true,
      "title": "summaries.board.report.powbMOG", 
      "description": "summaries.board.report.powbMOG.description",
      "namespace": "/projects",
      "action": "${crpSession}/budgetByCoAsSummary",
      "formats": [ "Excel" ],
      "cycles": [ "Planning" ]
    },
    { "active": true,
      "title": "summaries.board.report.fundingSourceSummary", 
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
      <div class="summariesOptions">
        [#list reportsTypes as reportType]
        <div id="${reportType.slug}-contentOptions" class="" style="display: [#if reportType_index != 0]none[/#if];">
          [#list reportType.reportsList as report]
            [#if report.active][@reportMacro report /][/#if]
          [/#list]
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
        [#if action.hasSpecificities('show_gender_keywords_summaries')]
        <div class="btn btn-default btn-xs addGenderKeys" role="button">Add predefined <strong>gender</strong> keywords</div>
        [/#if]
        <div class="btn btn-danger btn-xs removeAllTags" role="button">Remove all keywords</div>
      </div>
      [/#if]
      
      [#-- Generate Button--]
      <button type="submit" class="btn btn-info pull-right"><span class="glyphicon glyphicon-download-alt"></span> Generate</button> <div class="clearfix"></div>
    [/@s.form]
  </div>
</div>
[/#macro]