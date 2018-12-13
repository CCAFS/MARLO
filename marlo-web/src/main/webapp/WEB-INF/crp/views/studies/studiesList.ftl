[#ftl]
[#assign title = "MARLO Studies" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customJS = ["${baseUrlMedia}/js/studies/studiesList.js?20180725" ] /]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css"
  ] 
/]
[#assign currentSection = "additionalReporting" /]
[#assign currentStage = (filterBy)!"all" /]


[#assign breadCrumb = [
  {"label":"studiesList", "nameSpace":"/studies", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="container">
  <article class="row" id="mainInformation">
    <div class="col-md-12">
    
      [#-- Studies not directly linked to a Project (My Studies) --]
      <h3 class="headTitle text-center">[@s.text name="studiesList.yourStudies"/] <br /> <small>([@s.text name="studiesList.yourStudies.help" /])</small></h3>
      <div class="loadingBlock"></div>
      <div style="display:none">[@studiesList elements=myNonProjectStudies canValidate=true canEdit=true namespace="/studies" defaultAction="${(crpSession)!}/study" /]</div>
  
      [#-- Section Buttons --]
      <div class="buttons">
        <div class="buttons-content">
          <a class="addButton" href="[@s.url action='${crpSession}/addNewStudy'][@s.param name="studyTypeID" value="1"/][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> <span class="saveText">[@s.text name="studiesList.addOutcomeCaseStudy" /] </span></a>
          <a class="addButton" href="[@s.url action='${crpSession}/addNewStudy'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"> <span class="saveText">[@s.text name="studiesList.addStudy" /] </span></a>
          <div class="clearfix"></div>
        </div>
      </div>
      
      
      <div class="clearfix"></div>
      <hr/>
      
      [#-- Studies List (Other Studies) --]
      <h3 class="headTitle text-center">[@s.text name="studiesList.otherStudies" /] </h3>
      <div class="loadingBlock"></div>
      <div style="display:none">[@studiesList elements=nonProjectStudies canValidate=true canEdit=false namespace="/studies" defaultAction="${(crpSession)!}/study"/]</div>
    </div>
    
  </article>
</section>
[@customForm.confirmJustification action="deleteStudy.do" namespace="/${currentSection}" nameId="expectedID" title="Remove" /]


[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- MACRO --]
[#macro studiesList elements=[] owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="study"]
  <table class="projectsList" id="projects">
    <thead>
      <tr class="subHeader">
        [#-- ID --]
        <th>[@s.text name="studiesList.column.id" /]</th>
        [#-- Title / Name --]
        <th>[@s.text name="studiesList.column.title" /]</th>
        [#-- Added by --]
        <th>[@s.text name="studiesList.column.addedBy" /]</th>
        [#-- Type --]
        <th>[@s.text name="studiesList.column.type" /]</th>
        [#-- Flagship / Region --]
        <th>[@s.text name="studiesList.column.flagshipRegion" /]</th>
        [#-- Delivery year --]
        <th>[@s.text name="studiesList.column.year" /]</th>
        [#-- Status --]
        <th>[@s.text name="studiesList.column.status" /]</th>
        [#if canEdit]
          [#-- Fields check --]
          <th>[@s.text name="studiesList.column.fieldsCheck" /]</th>
          [#-- Delete --]
          <th></th>
        [/#if]
      </tr>
    </thead>
    <tbody>
    [#if elements?has_content]
      [#list elements as element]

        [#local elementUrl][@s.url namespace=namespace action=defaultAction][@s.param name='expectedID']${element.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
        <tr>
          [#-- ID --]
          <td class="">
            <a href="${elementUrl}">${(element.id)!}</a>
          </td>
          [#-- Title / Name --]
          <td class="col-md-4 left">
            <a href="${elementUrl}">
              [#if (element.projectExpectedStudyInfo.title?has_content)!false]${(element.projectExpectedStudyInfo.title)!}[#else]Not defined[/#if]
            </a>
          </td>
          [#-- Added by --]
          <td >
            [#if element.createdBy?has_content]${(element.createdBy.composedName)!}[#else]Not defined[/#if]
          </td>
          [#-- Type--]
          <td class="">
            ${(element.projectExpectedStudyInfo.studyType.name)!'Not defined'}
          </td>
          [#-- Flagship / Region --]
          <td class=""> 
            [#if element.flagships?has_content || element.regions?has_content]
              [#if element.flagships?has_content][#list element.flagships as element]
              <span class="programTag" style="border-color:${(element.crpProgram.color)!'#fff'}">${(element.crpProgram.acronym)!}</span>[/#list]
              [/#if][#if element.regions?has_content][#list element.regions as element]
              <span class="programTag" style="border-color:${(element.crpProgram.color)!'#fff'}">${(element.crpProgram.acronym)!}</span>[/#list][/#if]
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [#-- Delivery year --]
          <td class=""> 
           ${(element.projectExpectedStudyInfo.year)!'Not defined'}
          </td>
          [#--Status --]
          <td class=""> 
           ${(element.projectExpectedStudyInfo.statusName)!'Not defined'}
          </td>
          [#if canEdit]
            [#-- Fields check --]
            <td class=""> 
              [#if (action.getStudyStatus(element.id)??)!false]
                [#if !((action.getStudyStatus(element.id)).missingFields)?has_content]
                  <span class="icon-20 icon-check" title="Complete"></span>
                [#else]
                  <span class="icon-20 icon-uncheck" title=""></span> 
                [/#if]
              [#else]
                <span class="icon-20 icon-uncheck" title=""></span>
              [/#if]
            </td>
            [#-- Delete --]
            <td class="">
              [#if canEdit]
                <a id="removeRow-${element.id}" class="removeRow" href="${baseUrl}/studies/${crpSession}/deleteStudy.do?expectedID=${element.id}" title="">
                  <img src="${baseUrl}/global/images/trash.png" title="Remove" /> 
                </a>
              [#else]
                <img src="${baseUrl}/global/images/trash_disable.png" title="" />
              [/#if]
          </td>
          [/#if]
        </tr>  
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]
