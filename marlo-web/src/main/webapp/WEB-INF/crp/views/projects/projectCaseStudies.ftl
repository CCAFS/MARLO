[#ftl]
[#assign title = "Project Outcome Case Studies" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectCaseStudiesList.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectCaseStudies.css?20230106"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "caseStudies" /]
[#assign hideJustification = true /]


[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
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
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectStudies.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

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
        
        [@s.form action="caseStudies" cssClass="pure-form" enctype="multipart/form-data" ]  
          
          [#-- Studies list --]
          <h3 class="headTitle">[@s.text name="projectStudies.caseStudiestitle" /]</h3>
          <div id="caseStudiesBlock" class="simpleBox">
            [@tableList list=(project.caseStudies)![]  /]
          </div>
          [#-- Add a new --]
          [#if action.canEdit()] 
          <div class="text-right">
            <a class="button-blue" href="[@s.url action='${crpSession}/addNewCaseStudy'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>  [@s.text name="form.buttons.addOutcomeCaseStudy" /]
            </a>
            <a class="button-blue" href="[@s.url action='${crpSession}/addNewCaseStudy'] [@s.param name="projectID"]${projectID}[/@s.param][/@s.url]">
              <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>  [@s.text name="form.buttons.addStudy" /]
            </a>
          </div>
          [/#if]
          
          [#-- Previous Studies List --]
          <h3 class="headTitle">Previous Studies</h3>
          <div id="caseStudiesBlock" class="simpleBox">
            [@tableList list=(project.caseStudies)![] previousTable=true /]
          </div>
         
        [/@s.form]
  
      </div>
      
      
    </div>  
</section>

[#-- Internal parameters --]
[#list params?keys as prop]<input id="${params[prop].id}" type="hidden" value="${params[prop].name}" />[/#list]


[@customForm.confirmJustification action="${crpSession}/deleteCaseStudy.do" namespace="/projects" nameId="caseStudyID" projectID="${projectID}" title="Remove outcomes case study" /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- -- MACROS -- --]

[#macro tableList list previousTable=false]
  [@s.set var="counter" value=0/]
  <table id="projectHighlights" class="table table-striped table-hover ">
    <thead>
      <tr>
        <th class="id" >ID</th> 
        <th class="name">Study Title</th>
        <th class="type">Type</th>
        <th class="owner">Owner</th>
        <th class="year">Year</th>
        [#if !previousTable]
        <th class="removeHighlight">Remove</th> 
        [/#if]
      </tr>
    </thead>
    <tbody>
    [#if list?has_content]
        [#list list as item]
          [#assign dlurl][@s.url namespace=namespace action='${crpSession}/caseStudy' ][@s.param name='caseStudyID']${item.id}[/@s.param][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
          <tr>
            <td class="id" ><a href="${dlurl}">${item.id}</a></td> 
            <td class="name"><a href="${dlurl}">[#if item.title?trim?has_content]${item.title}[#else]Untitled[/#if]</a></td>
            <td class="type">Outcome Case Study</td>
            <td class="owner">[#if item.owner?trim?has_content]P${item.owner.id}[#else]Not defined[/#if]</td>
            <td class="year">[#if item.year?trim?has_content]${item.year}[#else]Not defined[/#if]</td>
            [#if !previousTable]
            <td class="removeHighlight-row text-center">
              [#if canEdit && action.canDelete(item.owner.id) && (item.year gte  currentCycleYear) && action.canEdit() ]
                <a id="removeElement-${item.id}" class="removeElementList" href="#" title="" >
                  <img src="${baseUrlCdn}/global/images/trash.png" title="[@s.text name="projectStudies.removeCaseStudy" /]" /> 
                </a>
              [#else]
                <img src="${baseUrlCdn}/global/images/trash_disable.png" title="[@s.text name="projectStudies.cantDeleteCaseStudy" /]" />
              [/#if]
            </td>
            [/#if] 
          </tr> 
        [/#list]
    [/#if]  
    </tbody> 
  </table>
  <div class="clearfix"></div>
[/#macro] 
