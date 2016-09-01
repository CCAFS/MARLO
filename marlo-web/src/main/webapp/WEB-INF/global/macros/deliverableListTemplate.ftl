[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro deliverablesList deliverables={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction=""]
  <table class="deliverableList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >[@s.text name="projectDeliverableList.deliverableName" /]</th>
        <th id="deliverableType">[@s.text name="projectDeliverableList.type" /]</th>
        <th id="deliverableEDY">[@s.text name="projectDeliverableList.expectedYear" /]</th>
        <th id="deliverableFC">[@s.text name="projectDeliverableList.fairCompliance" /]</th>
        <th id="deliverableStatus">[@s.text name="projectDeliverableList.status" /]</th>
        <th id="deliverableRF">[@s.text name="projectDeliverableList.requiredFields" /]</th>
        <th id="deliverableDelete">[@s.text name="projectsList.delete" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        <tr>
        [#-- ID --]
        <td class="projectId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${deliverable.id}</a>
        </td>
          [#-- Deliverable Title --]
          <td class="left"> 
            [#if deliverable.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="${deliverable.title}">
              [#if deliverable.title?length < 120] ${deliverable.title}</a> [#else] [@utilities.wordCutter string=deliverable.title maxPos=120 /]...</a> [/#if]
            [#else]
              <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url] ">
                [@s.text name="projectsList.title.none" /]
              </a>
            [/#if]
          </td>
          [#-- Deliverable Type --]
          <td>
            ${(deliverable.deliverableType.name?lower_case)!'none'}
          </td>
          [#-- Deliverable Year --]
          <td>
            ${(deliverable.year)!'none'}
          </td>
          [#-- Deliverable FAIR compliance --]
          <td class="fair">
            <span class=" active">F</span>
            <span class=" ">A</span>
            <span class="active ">I</span>
            <span class=" ">R</span>
          </td>
          [#-- Deliverable Status --]
          <td>
            ${(deliverable.statusName)!'none'}
          </td>
          [#-- Deliverable required fields --]
          <td>
            {TODO}
          </td>
          [#-- Delete Deliverable--]
          <td class="text-center">
            [#--if (action.hasProjectPermission("deleteProject", project.id, "manage") && project.isNew(currentPlanningStartDate)) --]
            [#if true]
              <a id="removeDeliverable-${deliverable.id}" class="removeDeliverable" href="#" title="">
                <img src="${baseUrl}/images/global/trash.png" title="[@s.text name="projectDeliverable.removeDeliverable" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/images/global/trash_disable.png" title="[@s.text name="projectDeliverable.cannotDelete" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro deliverablePartner dp={} dp_name="" dp_index="" isResponsable=false template=false editable=true]
  <div id="deliverablePartner-${template?string('template', dp_index)}" class="${isResponsable?string('responsiblePartner','deliverablePartner')} ${isResponsable?string('simpleBox','borderBox')} row" style="display:${template?string('none','')}">
    [#if editable && !isResponsable]
      <div class="removeElement removeLink" title="[@s.text name="planning.deliverables.removePartnerContribution" /]"></div> 
    [/#if]
    [#if !isResponsable]
    <div class="leftHead">
      <span class="index">${dp_index+1}</span>
    </div> 
    [/#if]
    [#assign customName]${dp_name}[#if !isResponsable][${dp_index}].projectPartnerPerson[/#if][/#assign]
    <input class="id" type="hidden" name="${customName}.id" value="${(dp.projectPartnerPerson.id)!'-1'}">
    <input class="type" type="hidden" name="${customName}.type" value="${isResponsable?string('Resp','Other')}">
    [#if template]
      [#-- Partner Name --]
      <div class="fullPartBlock partnerName chosen col-md-12"> 
        [@customForm.select name="" value="-1"  i18nkey="${isResponsable?string('Partner who is responsible for the delivery of this deliverable','Partner')}" listName="partnerPersons" keyFieldName="id"  displayFieldName="composedName"  multiple=false className="${isResponsable?string('responsible','partner')} form-control input-sm " disabled=!editable required=isResponsable /]
      </div>
    [#else]
      [#-- Partner Name --]
      <div class="fullPartBlock partnerName chosen col-md-12"> 
        [@customForm.select name="" value="${(dp.projectPartnerPerson.id)!-1}"  label="" i18nkey="${isResponsable?string('Partner who is responsible for the delivery of this deliverable','Partner')}" listName="partnerPersons" keyFieldName="id"  displayFieldName="composedName"  multiple=false   className="${isResponsable?string('responsible','partner')} form-control input-sm " disabled=!editable required=isResponsable/]
      </div>
    [/#if] 
  </div> 
[/#macro] 