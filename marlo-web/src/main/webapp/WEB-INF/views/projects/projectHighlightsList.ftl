[#ftl]
[#assign title = "Project Highlights" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectHighlightsList.js","${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectHighlights.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "highlights" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectHighlights", "nameSpace":"/projects", "action":""}
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
           
          <div class="highlights-block simpleBox">
            [#-- Project Highlights list --]
            
            [#assign projectHighlights = [
              { "id": "1",
                "title": "Succesful communications on the Projected Shifts in Coffea arabica Suitability among Major Global Producing Regions Due to Climate Change",
                "author":"Ovalle-Rivera O, Läderach P, Bunn C, Obersteiner M, Schroth G",
                "year":"2015",
                "subject":"Coffea arabica, climate change, productivity",
                "":"",
                "":"",
                "":""
              }  
            ] /]
            
            <div class="highlights-list">
              [@highlightsList highlights=(projectHighlights)![]  /]
            </div>
            [#--  --if !project.highlights?has_content]
              <p class="textMessage text-center">[@s.text name="projectHighlights.empty" /]</p>
            [/#if--] 
            [#-- Add a new highlight --]
          </div>
          [#if true] 
          <div class="text-right"> 
            <a class="button-blue" href="[@s.url action='addNewhighlight'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>  [@s.text name="form.buttons.addHighlight" /]
            </a>
          </div>
          [/#if]
           
        </div>     
      </div>
      
      
    </div>  
</section>
  
[@customForm.confirmJustification action="deleteHighLight" namespace="/reporting/projects" nameId="deliverableID" projectID="${projectID}" title="Remove project highlights" /]

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
          <th class="removeHighlight">Remove</th> 
        </tr>
      </thead>
      <tbody>
  [#if highlights?has_content]
      [#list highlights as hl]
        [#assign dlurl][@s.url namespace=namespace action='${crpSession}/highlight' ][@s.param name='highlightID']${hl.id}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url][/#assign]
        <tr>
          <td class="id" ><a href="${dlurl}">${hl.id}</a></td> 
          <td class="name"><a href="${dlurl}">[#if hl.title?trim?has_content]${hl.title}[#else]Untitled[/#if]</a></td>
          <td class="type">[#if hl.title?trim?has_content]${hl.author}[#else]Not defined[/#if]</td>
          <td class="year">[#if hl.title?trim?has_content]${hl.year}[#else]Not defined[/#if]</td>
          <td class="removeHighlight-row text-center">
            [#if canEdit && action.hasPermission("removeHighlight", project.id) && (hl.year gte  action.getCurrentReportingYear()) ]
              <a id="removeHighlight-${hl.id}" class="removeHighlight" href="highlightID${hl.id}" title="" >
                <img src="${baseUrl}/images/global/trash.png" title="[@s.text name="projectHighlights.removeHighlight" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/images/global/trash_disable.png" title="[@s.text name="projectHighlights.cantDeleteHighlight" /]" />
            [/#if]
          </td> 
        </tr> 
      [/#list]
  [/#if]  
      </tbody> 
    </table>
    <div class="clearfix"></div>
[/#macro] 