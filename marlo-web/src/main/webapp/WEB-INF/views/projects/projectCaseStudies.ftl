[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]


[#assign title = "Project Outcome Case Studies" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = [ "select2" ] /]
[#assign customJS = ["${baseUrl}/js/projects/projectCaseStudies.js","${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectCaseStudies.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "caseStudies" /]


[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"caseStudies", "nameSpace":"/projects", "action":""}
] /]

[#assign params = {
  "caseStudies": {"id":"caseStudiesName", "name":"project.caseStudies"}
  }
/] 

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

        [@s.form action="caseStudies" cssClass="pure-form" enctype="multipart/form-data" ]  
         
          [#include "/WEB-INF/views/projects/dataInfo-projects.ftl" /] 
            
          <h3 class="headTitle">[@s.text name="projectCaseStudies.caseStudiestitle" /]</h3>
          [#assign projectCaseStudies = [
              { "id": "1",
                "title": "Creating Geographical Software and Building Capacity for its Use Strengthens Climate Change Analysis in Agriculture",
                "owner":"You",
                "year": 2015
              }  
            ] /]
          
          [#-- Outcome case studies list --]
          <div id="caseStudiesBlock" class="simpleBox">
            [@tableList list=(projectCaseStudies)![]  /]
          </div>
          
          [#-- Add a new highlight --]
          [#if canEdit] 
          <div class="text-right"> 
            <a class="button-blue" href="[@s.url action='${crpSession}/addNewCaseStudy'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>  [@s.text name="projectCaseStudies.addCaseStudy" /]
            </a>
          </div>
          [/#if]
         
        [/@s.form]
  
      </div>
      
      
    </div>  
</section>

[#-- Internal parameters --]
[#list params?keys as prop]<input id="${params[prop].id}" type="hidden" value="${params[prop].name}" />[/#list]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- -- MACROS -- --]

[#macro tableList list ]
  [@s.set var="counter" value=0/]
  <table id="projectHighlights" class="table table-striped table-hover ">
    <thead>
      <tr>
        <th class="id" >ID</th> 
        <th class="name">Case Study Title</th>
        <th class="type">Owner</th>
        <th class="year">Year</th>
        <th class="removeHighlight">Remove</th> 
      </tr>
    </thead>
    <tbody>
    [#if list?has_content]
        [#list list as item]
          [#assign dlurl][@s.url namespace=namespace action='${crpSession}/caseStudy' ][@s.param name='caseStudyID']${item.id}[/@s.param][@s.param name='projectID']${projectID}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url][/#assign]
          <tr>
            <td class="id" ><a href="${dlurl}">${item.id}</a></td> 
            <td class="name"><a href="${dlurl}">[#if item.title?trim?has_content]${item.title}[#else]Untitled[/#if]</a></td>
            <td class="owner">[#if item.owner?trim?has_content]${item.owner}[#else]Not defined[/#if]</td>
            <td class="year">[#if item.year?trim?has_content]${item.year}[#else]Not defined[/#if]</td>
            <td class="removeHighlight-row text-center">
              [#if canEdit && action.hasPermission("removeCaseStudy") && (item.year gte  currentCycleYear) ]
                <a id="removeHighlight-${item.id}" class="removeHighlight" href="highlightID${item.id}" title="" >
                  <img src="${baseUrl}/images/global/trash.png" title="[@s.text name="projectCaseStudies.removeCaseStudy" /]" /> 
                </a>
              [#else]
                <img src="${baseUrl}/images/global/trash_disable.png" title="[@s.text name="projectCaseStudies.cantDeleteCaseStudy" /]" />
              [/#if]
            </td> 
          </tr> 
        [/#list]
    [/#if]  
    </tbody> 
  </table>
  <div class="clearfix"></div>
[/#macro] 
