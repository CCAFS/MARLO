[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]
[#macro publicationsList publications={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction="description"]
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
        [#-- Source --]
        <th>[@s.text name="publicationsList.column.source" /]</th>
        [#-- Delivery year --]
        <th>[@s.text name="publicationsList.column.year" /]</th>
        [#-- FAIR Compliance --]
        <th>[@s.text name="publicationsList.column.fair" /]</th>
        [#-- Lead partner(s) --]
        <th>[@s.text name="publicationsList.column.lead" /]</th>
        [#-- Flagship / Region --]
        <th>[@s.text name="publicationsList.column.flagshipRegion" /]</th>
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
            ${deliverable.id}
          </td>
          [#-- Title / Name --]
          <td class="">
            
          </td>
          [#-- Source --]
          <td class=""> 
           
          </td>
          [#-- Delivery year --]
          <td class=""> 
           
          </td>
          [#-- FAIR Compliance --]
          <td class=""> 
           
          </td>
          [#-- Lead partner(s) --]
          <td class=""> 
           
          </td>
          [#-- Flagship / Region --]
          <td class=""> 
           
          </td>
          [#-- Delete --]
          <td class=""> 
           
          </td>
        </tr>  
      [/#list]
    [/#if]
    </tbody>
  </table>
[/#macro]