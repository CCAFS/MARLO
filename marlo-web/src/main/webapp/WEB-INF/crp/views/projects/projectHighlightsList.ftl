[#ftl]
[#assign title = "Project Highlights" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
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
  {"label":"projectHighlights", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]


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
            <a class="button-blue" href="[@s.url action='${crpSession}/addNewHighlight'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]">
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

[#include "/WEB-INF/crp/pages/footer.ftl"]

 
[#macro highlightsList highlights ]
  [@s.set var="counter" value=0/]
  <table id="projectHighlights" class="table table-striped table-hover ">
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
          [#assign dlurl][@s.url namespace=namespace action='${crpSession}/highlight' ][@s.param name='highlightID']${hl.id}[/@s.param][@s.param name='projectID']${projectID}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url][/#assign]
          <tr>
            <td class="id" ><a href="${dlurl}">${hl.id}</a></td> 
            <td class="name"><a href="${dlurl}">[#if hl.title?trim?has_content]${hl.title}[#else]Untitled[/#if]</a></td>
            <td class="type">[#if hl.title?trim?has_content]${hl.author}[#else]Not defined[/#if]</td>
            <td class="year">[#if hl.title?trim?has_content]${hl.year}[#else]Not defined[/#if]</td>
            <td class="removeHighlight-row text-center">
              [#if canEdit  && (hl.year gte  currentCycleYear) ]
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