[#ftl]
[#assign title = "Project Highlights" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectHighlightsList.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectHighlights.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "highlights" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"projectHighlights", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectHighlights.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        <h3 class="headTitle">[@s.text name="projectHighlights.title" /]</h3> 
        <div id="" class="clearfix">
           
          <div class="highlights-block simpleBox">
            [#-- Project Highlights list --]
            <div class="highlights-list">
              [@highlightsList highlights=(project.highligths)![]  /]
            </div>
          </div>
          
          [#-- Add a new highlight --]
          [#if canEdit] 
          <div class="text-right"> 
            <a class="button-blue" href="[@s.url action='${crpSession}/addNewHighlight'] [@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>  [@s.text name="form.buttons.addHighlight" /]
            </a>
          </div>
          [/#if]
           
        </div>     
      </div>
      
      
    </div>  
</section>
[/#if]

[@customForm.confirmJustification action="${crpSession}/deleteHighLight.do" namespace="/projects" nameId="higlightID" projectID="${projectID}" title="Remove project highlights" /]

[#include "/WEB-INF/global/pages/footer.ftl"]

 
[#macro highlightsList highlights ]
  [@s.set var="counter" value=0/]
  <table id="projectHighlights" class="table table-striped table-hover ">
    <thead>
      <tr>
        <th class="id" >ID</th> 
        <th class="name">Highlight Name</th>
        <th class="type">Author</th>
        <th class="year">Year</th>
        <th id="projectDownload">[@s.text name="projectsList.download" /]</th>
        <th class="removeHighlight">Remove</th> 
      </tr>
    </thead>
    <tbody>
    [#if highlights?has_content]
        [#list highlights as hl]
          [#assign dlurl][@s.url namespace=namespace action='${crpSession}/highlight' ][@s.param name='highlightID']${hl.id}[/@s.param][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
          <tr>
            <td class="id" ><a href="${dlurl}">${hl.id}</a></td> 
            <td class="name">
              [#if reportingActive && ((hl.projectHighlightInfo.year == currentCycleYear)!false)]
                <span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>
              [/#if]
              <a href="${dlurl}">[#if hl.projectHighlightInfo.title?trim?has_content]${hl.projectHighlightInfo.title}[#else]Untitled[/#if]</a>
            </td>
            <td class="type">[#if hl.projectHighlightInfo.title?trim?has_content]${hl.projectHighlightInfo.author}[#else]Not defined[/#if]</td>
            <td class="year">[#if hl.projectHighlightInfo.title?trim?has_content]${hl.projectHighlightInfo.year}[#else]Not defined[/#if]</td>
            [#-- Summary PDF download --]
          <td>
            <a href="[@s.url namespace="/summaries" action='${(crpSession)!}/projectHighlightSummary'][@s.param name='highlightID']${hl.id?c}[/@s.param][@s.param name='cycle']${action.getCurrentCycle()}[/@s.param][@s.param name='year']${action.getCurrentCycleYear()}[/@s.param][/@s.url]" target="__BLANK">
              <img src="${baseUrl}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" />
            </a>            
          </td>
            <td class="removeHighlight-row text-center">
              [#if canEdit  && (hl.projectHighlightInfo.year gte  currentCycleYear) ]
                <a id="removeHighlight-${hl.id}" class="removeHighlight" href="#" title="" >
                  <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="projectHighlights.removeHighlight" /]" /> 
                </a>
              [#else]
                <img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="projectHighlights.cantDeleteHighlight" /]" />
              [/#if]
            </td> 
          </tr> 
        [/#list]
    [/#if]  
    </tbody> 
  </table>
  <div class="clearfix"></div>
[/#macro] 