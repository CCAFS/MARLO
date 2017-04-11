[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro deliverablesList deliverables={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction=""]
  <table class="deliverableList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
        <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>
        <th id="deliverableEDY" title="[@s.text name="project.deliverableList.expectedYear" /]">Year</th>
        <th id="deliverableFC">[@s.text name="project.deliverableList.fairCompliance" /]</th>
        <th id="deliverableStatus">[@s.text name="project.deliverableList.status" /]</th>
        <th id="deliverableRF">[@s.text name="project.deliverableList.requiredFields" /] </th>
        <th id="deliverableDelete">[@s.text name="projectsList.delete" /]</th>  
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        [#assign isDeliverableNew = action.isDeliverableNew(deliverable.id) /]
        
        [#-- isDeliverableComplete --]
        [#if action.getDeliverableStatus(deliverable.id)??]
          [#if !((action.getDeliverableStatus(deliverable.id)).missingFields)?has_content]
            [#assign isDeliverableComplete = true /]
          [#else]
            [#assign isDeliverableComplete = false /]
          [/#if]
        [#else]
            [#assign isDeliverableComplete = false /]
        [/#if]
        
        <tr>
        [#-- ID --]
        <td class="deliverableId">
          <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">
            D${deliverable.id}
          </a>
        </td>
          [#-- Deliverable Title --]
          <td class="left">
            [#if isDeliverableNew]<span class="label label-info">New</span>[/#if]

            [#if deliverable.isRequieriedReporting(currentCycleYear) && reportingActive && !isDeliverableComplete]
              <span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>
            [/#if]
            [#if deliverable.title?has_content]
                <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]" title="${deliverable.title}">
                [#if deliverable.title?length < 120] 
                  ${deliverable.title}
                [#else] 
                  [@utilities.wordCutter string=deliverable.title maxPos=120 /]
                [/#if]
                </a> 
            [#else]
              [#if action.canEdit(deliverable.id)]
                <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url] ">
                  [@s.text name="projectsList.title.none" /]
                </a>
              [#else]
              [@s.text name="projectsList.title.none" /]
              [/#if]
            [/#if]
          </td>
          [#-- Deliverable Type --]
          <td >
            ${(deliverable.deliverableType.name?capitalize)!'none'}
          </td>
          [#-- Deliverable Year --]
          <td class="text-center">
          [#if deliverable.year== -1]
          none
          [#else]
          ${(deliverable.year)!'none'}
          [/#if]
            
          </td>
          [#-- Deliverable FAIR compliance --]
          <td class="fair"> 
          [#if deliverable.requeriedFair()]
            <span class="[#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">F</span>
            <span class="[#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">A</span>
            <span class="[#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">I</span>
            <span class="[#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">R</span>
          [#else]
            <p class="message">Not applicable</p>
          [/#if]
          </td>
          [#-- Deliverable Status --]
          <td>
            [#attempt]
              ${(deliverable.statusName)!'none'}
              [#if deliverable.status?? && deliverable.status==4 && deliverable.newExpectedYear??]
               to ${deliverable.newExpectedYear}
              [/#if]
            [#recover]
              none
            [/#attempt]
          </td>
          [#-- Deliverable required fields --]
          <td class="text-center">
            [#if isDeliverableComplete]
              <span class="icon-20 icon-check" title="Complete"></span>
            [#else]
              <span class="icon-20 icon-uncheck" title=""></span> 
            [/#if]
          </td>
          [#-- Delete Deliverable--]
          <td class="text-center">
            [#if isDeliverableNew]
              <a id="removeDeliverable-${deliverable.id}" class="removeDeliverable" href="${baseUrl}/projects/${crpSession}/deleteDeliverable.do?deliverableID=${deliverable.id}" title="">
                <img src="${baseUrl}/images/global/trash.png" title="[@s.text name="project.deliverable.removeDeliverable" /]" /> 
              </a>
            [#else]
              <img src="${baseUrl}/images/global/trash_disable.png" title="[@s.text name="project.deliverable.cannotDelete" /]" />
            [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro deliverablePartner dp={} dp_name="" dp_index="" isResponsable=false template=false editable=true]
  <div id="deliverablePartner-${template?string('template', dp_index)}" class="${isResponsable?string('responsiblePartner','deliverablePartner')} ${isResponsable?string('','borderBox')} projectPartnerPerson row" style="display:${template?string('none','')}">
    [#if editable && !isResponsable]
      <div class="removeElement removeLink" title="[@s.text name="project.deliverable.removePartnerContribution" /]"></div> 
    [/#if]
    [#if !isResponsable]
    <div class="leftHead">
      <span class="index">${dp_index+1}</span>
    </div> 
    [/#if]
    [#assign customName]${dp_name}[#if !isResponsable][${dp_index}][/#if][/#assign]
    <input class="type" type="hidden" name="${customName}.type" value="${isResponsable?string('Resp','Other')}">
    [#if !isResponsable]
    <input class="element" type="hidden" name="${dp_name}[${dp_index}].id" value="${(dp.id)!-1}">
    [/#if]
    [#if template]
      [#-- Partner Name --]
      <div class="fullPartBlock partnerName chosen"> 
        [@customForm.select name="" value="-1"  i18nkey="" showTitle=false listName="partnerPersons" keyFieldName="id"  displayFieldName="composedName"   className="${isResponsable?string('responsible','partner')}  id" editable=editable required=isResponsable /]
      </div>
    [#else]
      [#-- Partner Name --]
      <div class="form-group partnerName chosen"> 
      [#if editable]
        [@customForm.select name="${customName}.projectPartnerPerson.id" value="${(dp.projectPartnerPerson.id)!-1}"  label="" i18nkey="" showTitle=false listName="partnerPersons" keyFieldName="id"  displayFieldName="composedName"     className="${isResponsable?string('responsible','partner')} id " editable=editable required=isResponsable/]
      [#else]
        <div class="personRead-content"><span class="glyphicon glyphicon-user" ></span> <span>${((dp.projectPartnerPerson.composedName)!'Contact Person')?html}</span></div>
      [/#if]
      </div>
    [/#if]
    
    
    [#-- Division --]
    [#if action.hasSpecificities('crp_division_fs')]
      [#local ifpriDivision = false /]
      [#if (dp.projectPartnerPerson.institution.acronym == "IFPRI")!false ][#local ifpriDivision = true /][/#if]
      <div class="form-group row divisionBlock division-IFPRI"  style="display:${ifpriDivision?string('block','none')}">
        <div class="col-md-6">
          [@customForm.input name="${customName}.division" i18nkey="projectCofunded.division" className="divisionField" editable=editable /]
        </div>
      </div>
    [/#if]
    
  </div> 
  <div class="clearfix"></div>
[/#macro] 