[#ftl]
[#assign title = "Project Policies" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectPoliciesList.js",
  "${baseUrl}/global/js/fieldsValidation.js"] 
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectPolicies.css" ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "projectPolicies" /]
[#assign hideJustification = true /]
[#assign isListSection = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"policies", "nameSpace":"/projects", "action":""}
] /]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectPolicies.help" /] </p>
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
        
      [#-- Policies list --]
      <h3 class="headTitle">[@s.text name="projectPolicies.title" /] <br /> <small>([@s.text name="projectPolicies.subTitle" /])</small></h3>
      <div id="" class="simpleBox">
        [@policiesTable list=(project.policies)![] /]
      </div>
      
      [#-- Add a new item --]
      [#if canEdit] 
      <div class="text-right">
        <a class="button-blue" href="[@s.url action='${crpSession}/addPolicy'][@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>  [@s.text name="form.buttons.addPolicy" /]
        </a>
      </div>
      [/#if]
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- -- MACROS -- --]

[#macro policiesTable list previousTable=false]
  [@s.set var="counter" value=0/]
  <table id="projectPolicies" class="table table-striped table-hover ">
    <thead>
      <tr>
        <th class="id" >ID</th> 
        <th class="name col-md-5">Title</th>
        <th class="type">Type</th>
        <th class="subIdos">Sub-IDOs</th>
        <th class="maturity">Level of Maturity</th>
        <th class="no-sort"></th>
        <th class="no-sort"></th>
      </tr>
    </thead>
    <tbody>
    [#if list?has_content]
        [#list list as item]
          [#-- URL --]
          [#local dlurl][@s.url namespace=namespace action='${crpSession}/policy' ][@s.param name='policyID']${(item.id)!}[/@s.param][@s.param name='projectID']${(item.project.id)!(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
          [#-- Is this complete --]
          [#local isThisComplete = false]
          <tr>
            <td class="" >
              <a href="${dlurl}">${(item.id)!'ID'}</a>
            </td> 
            <td class="">
              <a href="${dlurl}"}>[@utils.tableText value=(item.title)!"" /]</a>
            </td>
            <td class="">
              [@utils.tableText value=(item.typeName)!"" /]
            </td>
            <td class="">
              [@utils.tableList list=(item.subIdos)![] displayFieldName="name" /]
            </td>
            <td class="">
              [@utils.tableText value=(item.maturity)!"" /]
            </td>
            <td class="text-center">
              [@utils.tableCheckIcon state=(isThisComplete || ((item.year lt  currentCycleYear)!false)) /]
            </td>
            <td class="removeHighlight-row text-center">
              [#if canEdit && ((item.year gte  currentCycleYear)!true) ]
                <a id="removeElement-${(item.id)!}" class="removeElementList" href="#" title="" data-toggle="modal" data-target="#removeItem-${item_index}" >
                  <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="projectPolicies.removeItem" /]" /> 
                </a>
                
                <div id="removeItem-${item_index}" class="modal fade" tabindex="-1" role="dialog">
                  <div class="modal-dialog" role="document">
                    <div class="modal-content">
                      [@s.form action="deletePolicy"]
                        <div class="modal-header">
                          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                          <h4 class="modal-title">Remove this item <br /> <small>${(item.title)!}</small> </h4>
                        </div>
                        <div class="modal-body">
                          [@customForm.textArea name="justification" i18nkey="projectPolicies.removeJustification" required=true className="removeJustification"/]
                          <input type="hidden"  name="policyID" value="${(item.id)!}" />
                          <input type="hidden"  name="phaseID"  value="${(actualPhase.id)!}"/>
                        </div>
                        <div class="modal-footer">
                          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                          <button type="submit" class="btn btn-danger">Remove</button>
                        </div>
                      [/@s.form]
                    </div>
                  </div>
                </div>
                
              [#else]
                <img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="projectPolicies.cantDeleteItem" /]" />
              [/#if]
            </td>
          </tr> 
        [/#list]
    [/#if]  
    </tbody> 
  </table>
  <div class="clearfix"></div>
[/#macro] 
