[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro publicationsList publications={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="publication"]
  <table class="projectsList" id="projects">
    <thead>
      [#--  
      <tr class="header">
        <th colspan="4">General Information</th>
        <th colspan="3">[@s.text name="projectsList.projectBudget"] [@s.param]${(crpSession?upper_case)!}[/@s.param] [/@s.text] ${currentCycleYear}</th> 
        <th colspan="3">Actions</th> 
      </tr>
      --]
      <tr class="subHeader">
        [#-- ID --]
        <th>[@s.text name="publicationsList.column.id" /]</th>
        [#-- Title / Name --]
        <th>[@s.text name="publicationsList.column.title" /]</th>
        [#-- Added by --]
        <th>[@s.text name="publicationsList.column.addedBy" /]</th>
        [#-- Lead partner(s) --]
        <th>[@s.text name="publicationsList.column.lead" /]</th>
        [#-- Flagship / Region --]
        <th>[@s.text name="publicationsList.column.flagshipRegion" /]</th>
        [#-- Delivery year --]
        <th>[@s.text name="publicationsList.column.year" /]</th>
        [#-- FAIR Compliance --]
        <th>[@s.text name="publicationsList.column.fair" /]</th>
        [#-- Fields check --]
        <th>[@s.text name="publicationsList.column.fieldsCheck" /]</th>
        [#-- Delete --]
        <th></th>
      </tr>
    </thead>
    <tbody>
    [#if publications?has_content]
      [#list publications as deliverable]

        [#local projectUrl][@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
        <tr>
          [#-- ID --]
          <td class="">
            <a href="${projectUrl}">${(deliverable.id)!}</a>
          </td>
          [#-- Title / Name --]
          <td class="col-md-4 left">
            <a href="${projectUrl}">
              [#if deliverable.title?has_content]
                ${(deliverable.title)!}
              [#else]
                Not defined
              [/#if]
            </a>
          </td>
          [#-- Added by --]
          <td >
              [#if deliverable.createdBy?has_content]
                ${(deliverable.createdBy.composedName)!}
              [#else]
                Not defined
              [/#if]
          </td>
          [#-- Lead partner(s) --]
          <td class="">
            [#if deliverable.leaders?has_content]
              [#list deliverable.leaders as institutionLead]
                <p>${(institutionLead.institution.acronym)!((institutionLead.institution.name)!'undefined')}</p>
              [/#list]
            [/#if]
          </td>
          [#-- Flagship / Region --]
          <td class=""> 
            [#if deliverable.programs?has_content || deliverable.regions?has_content]
              [#if deliverable.programs?has_content][#list deliverable.programs as element]
              <span class="programTag" style="border-color:${(element.ipProgram.crpProgram.color)!'#fff'}">${element.ipProgram.acronym}</span>[/#list]
              [/#if][#if deliverable.regions?has_content][#list deliverable.regions as element]
              <span class="programTag" style="border-color:${(element.ipProgram.crpProgram.color)!'#fff'}">${element.ipProgram.acronym}</span>[/#list][/#if]
            [#else]
              [@s.text name="projectsList.none" /]
            [/#if]
          </td>
          [#-- Delivery year --]
          <td class=""> 
           ${(deliverable.year)!}
          </td>
          [#-- FAIR Compliance --]
          <td class="fair"> 
            <span class="[#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">F</span>
            <span class="[#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">A</span>
            <span class="[#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">I</span>
            <span class="[#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">R</span>
          </td>
          [#-- Fields check --]
          <td class=""> 
           [#if (action.getDeliverableStatus(deliverable.id)??)!false]
              [#if !((action.getDeliverableStatus(deliverable.id)).missingFields)?has_content]
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
              <a id="removeRow-${deliverable.id}" class="removeRow" href="${baseUrl}/publications/${crpSession}/deleteDeliverable.do?deliverableID=${deliverable.id}" title="">
                <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="project.deliverable.removeDeliverable" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="project.deliverable.cannotDelete" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]