[#ftl]
[#assign title = "Project Contributions to CRP" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "jsUri"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectContributionsCrpList.js","${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectContributionsCrpList.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "contributionsCrpList" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectContributionsCrpList", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectContributionsCrpList.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        <h3 class="headTitle">[@s.text name="projectHighlights.title" /]</h3> 
        <div id="" class="clearfix">
          [#if project.allYears?has_content]
            <div class="highlights-block">
              [#-- Project Highlights list --]
              <div class="highlights-list">
                [@highlightsList highlights=(project.highlights)![] canEdit=canEdit /]
              </div>
              [#-- Add a new highlight --]
              [#if canEdit && action.hasPermission("addHighlight", project.id)]
              <div class="buttons"> 
                <a class="addButton" href="[@s.url namespace="/reporting/projects" action='addNewhighlight'] [@s.param name="${projectRequestID}"]${projectID}[/@s.param][/@s.url]">
                  [@s.text name="reporting.projectHighlights.addNewhighlight" /]
                </a>
              </div>
              [/#if]
            </div>
          [#else]
            [#-- If the project has not an start date and/or end date defined --]
            <p class="simpleBox center">[@s.text name="reporting.projectHighlights.message.dateUndefined" /]</p>
          [/#if]
        </div>     
      </div>
      
      
    </div>  
</section>
  
[@customForm.confirmJustification action="deleteHighLight" namespace="/reporting/projects" nameId="deliverableID" projectID="${projectID}" title="Remove project highlights" /]

[#include "/WEB-INF/global/pages/footer.ftl"]

 
[#macro highlightsList highlights canEdit=true]
    [@s.set var="counter" value=0/] 
    <table id="projectHighlights">
      <thead>
        <tr>
          <th class="id" >ID</th> 
          <th class="name">Highlight Name</th>
          <th class="type">Author</th>
          <th class="year">Year</th>
          <th class="removeHighlight">Remove</th> 
        </tr>
      </thead>
      <tbody>
  [#if highlights?has_content]
      [#list highlights as hl]
        [#if editable]
          [#assign dlurl][@s.url namespace=namespace action='highlight' ][@s.param name='highlightID']${hl.id}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url][/#assign]
        [#else]
          [#assign dlurl][@s.url namespace=namespace action='highlight' ][@s.param name='highlightID']${hl.id}[/@s.param][/@s.url][/#assign]
        [/#if]
        <tr>
          <td class="id" ><a href="${dlurl}">${hl.id}</a></td> 
          <td class="name"><a href="${dlurl}">[#if hl.title?trim?has_content]${hl.title}[#else]Untitled[/#if]</a></td>
          <td class="type">[#if hl.title?trim?has_content]${hl.author}[#else]Not defined[/#if]</td>
          <td class="year">[#if hl.title?trim?has_content]${hl.year}[#else]Not defined[/#if]</td>
          <td class="removeHighlight-row">
            [#if canEdit && action.hasProjectPermission("removeHighlight", project.id) && hl.year gte  action.getCurrentReportingYear() ]
              <a id="removeHighlight-${hl.id}" class="removeHighlight" href="highlightID${hl.id}" title="" >
                <img src="${baseUrl}/images/global/trash.png" title="[@s.text name="reporting.projectHighlights.removeHighlight" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/images/global/trash_disable.png" title="[@s.text name="reporting.projectHighlights.cantDeleteHighlight" /]" />
            [/#if]
          </td> 
        </tr> 
      [/#list]
  [/#if]  
      </tbody> 
    </table>
    <div class="clearfix"></div>
[/#macro] 