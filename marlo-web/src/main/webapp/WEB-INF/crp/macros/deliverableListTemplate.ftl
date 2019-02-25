[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro deliverablesList deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/" defaultAction="" currentTable=true]
  <table class="deliverableList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
        <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>
        <th id="deliverableEDY">[@s.text name="project.deliverableList.deliveryYear" /]</th>
        [#if isReportingActive]
          <th id="deliverableFC">[@s.text name="project.deliverableList.fairCompliance" /]</th>
        [/#if]
        <th id="deliverableStatus">[@s.text name="project.deliverableList.status" /]</th>
        [#if currentTable]
        <th id="deliverableRF"></th>
        [/#if]
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        [#-- Is New --]
        [#assign isDeliverableNew = action.isDeliverableNew(deliverable.id) /]
        [#-- Has draft version (Auto-save) --]
        [#assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /]
        [#-- Is Complete --]
        [#assign isDeliverableComplete = action.isDeliverableComplete(deliverable.id, actualPhase.id) /]
        [#-- To Report --]
        [#local toReport = reportingActive && !isDeliverableComplete ]
        
        <tr>
          [#-- ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              D${deliverable.id}
            </a>
          </td>
          [#-- Deliverable Title --]
          <td class="left">
            [#-- Hidden title to sort correctly by title --]
            <span class="hidden">${deliverable.deliverableInfo.title!''}</span>
            [#-- Draft Tag --]
            [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]
            [#-- Report --]
            [#if toReport]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/#if]
            [#-- New Tag --]
            [#if isDeliverableNew]<span class="label label-info">New</span>[/#if]
            
            [#if deliverable.deliverableInfo.title?has_content]
              <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
                [@utilities.wordCutter string=deliverable.deliverableInfo.title maxPos=120 /]
              </a> 
            [#else]
              [#if action.canEdit(deliverable.id)]
                <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                  [@s.text name="projectsList.title.none" /]
                </a>
              [#else]
                [@s.text name="projectsList.title.none" /]
              [/#if]
            [/#if]
          </td>
          [#-- Deliverable Type --]
          <td >
            ${(deliverable.deliverableInfo.deliverableType.name?capitalize)!'None'}
          </td>
          [#-- Deliverable Year --]
          <td class="text-center">
            [#if deliverable.deliverableInfo.year== -1]
              None
            [#else]
              ${(deliverable.deliverableInfo.year)!'None'}
              [#if  
                    ((deliverable.deliverableInfo.status == 4 || deliverable.deliverableInfo.status==3)!false )
                    && ((deliverable.deliverableInfo.newExpectedYear != -1)!false) 
                    ]
                Extended to ${deliverable.deliverableInfo.newExpectedYear}
              [/#if]
            [/#if]
            
          </td>
          [#if isReportingActive]
            [#-- Deliverable FAIR compliance --]
            <td class="fair text-center"> 
            [#if deliverable.deliverableInfo.requeriedFair()]
              <span class="[#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">F</span>
              <span class="[#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">A</span>
              <span class="[#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">I</span>
              <span class="[#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">R</span>
            [#else]
              <p class="message">Not applicable</p>
            [/#if]
            </td>
          [/#if]
          [#-- Deliverable Status --]
          <td class="text-center">
            [#attempt]
              <div class="status-container">
                <div class="status-indicator ${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}" title="${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}"></div>
                <span class="hidden">${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}</span>
              </div>
            [#recover]
              None
            [/#attempt]
          </td>
          [#-- Deliverable required fields --]
          [#if currentTable]
            <td class="text-center">
              [#if isDeliverableComplete]
                <span class="icon-20 icon-check" title="Complete"></span>
              [#else]
                <span class="icon-20 icon-uncheck" title="[@s.text name="project.deliverableList.requiredStatus.incomplete" /]"></span> 
              [/#if]
              [#-- Remove icon --]
              [#if isDeliverableNew]
                <a id="removeDeliverable-${deliverable.id}" class="removeDeliverable" href="${baseUrl}/projects/${crpSession}/deleteDeliverable.do?deliverableID=${deliverable.id}&phaseID=${(actualPhase.id)!}" title="Remove deliverable">
                  <div class="icon-container"><span class="trash-icon glyphicon glyphicon-trash"></span><div>
                </a>
              [/#if]
            </td>
          [/#if]
        </tr>
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro deliverablesListExtended deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false FAIRColumn=true namespace="/" defaultAction=""]
  <table class="deliverableList" id="deliverables" width="100%">
    <thead>
      <tr class="subHeader">
        <th id="ids" width="0%">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" width="30%">[@s.text name="project.deliverableList.deliverableName" /]</th>
        <th id="deliverableType" width="0%">[@s.text name="project.deliverableList.subtype" /]</th>
        <th id="deliverableEDY" width="0%">[@s.text name="project.deliverableList.deliveryYear" /]</th>
        [#if isReportingActive || FAIRColumn]
          <th id="deliverableFC" width="0%">[@s.text name="project.deliverableList.fairCompliance" /]</th>
        [/#if]
        <th id="deliverableStatus" width="0%">[@s.text name="project.deliverableList.status" /]</th>
        <th id="deliverableRF" width="0%"></th>
        <th id="deliverableRP" width="0%">Responsible partner</th>
        <th id="deliverableFS" width="70%">Funding source(s)</th>
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        [#assign isDeliverableNew = action.isDeliverableNew(deliverable.id) /]
        [#assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /]
        
        [#-- isDeliverableComplete --]
        [#assign isDeliverableComplete = action.isDeliverableComplete(deliverable.id, actualPhase.id) /]
        [#-- To Report --]
        [#local toReport = reportingActive && !isDeliverableComplete ]
        
        <tr>
          [#-- ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              D${deliverable.id}
            </a>
          </td>
          [#-- Deliverable Title --]
          <td class="left">
            [#-- New Tag --]
            [#if isDeliverableNew]<span class="label label-info">New</span>[/#if]
            [#-- Draft Tag --]
            [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]
            [#-- To report --]
            [#if toReport]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/#if]
            
            [#if deliverable.deliverableInfo.title?has_content]
                <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                  [@utilities.wordCutter string=deliverable.deliverableInfo.title maxPos=120 /]
                </a> 
            [#else]
              [#if action.canEdit(deliverable.id)]
                <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                  [@s.text name="projectsList.title.none" /]
                </a>
              [#else]
              [@s.text name="projectsList.title.none" /]
              [/#if]
            [/#if]
          </td>
          [#-- Deliverable Type --]
          <td>
            ${(deliverable.deliverableInfo.deliverableType.name?capitalize)!'None'}
          </td>
          [#-- Deliverable Year --]
          <td class="text-center">
          [#if deliverable.deliverableInfo.year== -1]
          None
          [#else]
          ${(deliverable.deliverableInfo.year)!'None'}
            [#if deliverable.deliverableInfo.status?? && (deliverable.deliverableInfo.status==4 || deliverable.deliverableInfo.status==3)  && deliverable.deliverableInfo.newExpectedYear?? && (deliverable.deliverableInfo.newExpectedYear != -1)]
              Extended to ${deliverable.deliverableInfo.newExpectedYear}
            [/#if]
          [/#if]
            
          </td>
          [#-- Deliverable FAIR compliance --]
          [#if isReportingActive || FAIRColumn]
            <td class="fair text-center"> 
            [#if deliverable.deliverableInfo.requeriedFair()]
              <span class="[#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">F</span>
              <span class="[#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">A</span>
              <span class="[#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">I</span>
              <span class="[#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">R</span>
            [#else]
              <p class="message">Not applicable</p>
            [/#if]
            </td>
          [/#if]
          [#-- Deliverable Status --]
          <td class="text-center">
            [#attempt]
              <div class="status-container">
                <div class="status-indicator ${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}" title="${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}"></div>
                <span class="hidden">${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}</span>
              </div>
              [#-- ${deliverable.deliverableInfo.getStatusName(action.getActualPhase())!'none'} --]
            [#recover]
              None
            [/#attempt]
          </td>
          [#-- Deliverable required fields --]
          <td class="text-center">
            [#if isDeliverableComplete]
              <span class="icon-20 icon-check" title="Complete"></span>
            [#else]
              <span class="icon-20 icon-uncheck" title="[@s.text name="project.deliverableList.requiredStatus.incomplete" /]"></span> 
            [/#if]
          </td>
          [#-- Deliverable Responsible Partner --]
          <td class="text-center">
            [#attempt]
              ${(deliverable.responsiblePartner.projectPartnerPerson.projectPartner.institution.acronym)!'None'}
            [#recover]
              None
            [/#attempt]
          </td>
          [#-- Deliverable Funding source(s) --]
          <td>
            [#if deliverable.fundingSources?? && deliverable.fundingSources?size > 0]
              [#list deliverable.fundingSources as deliverableFundingSource]
                <div class="fundingSource-container" title="${(deliverableFundingSource.fundingSource.fundingSourceInfo.title)!'None'}">
                 <div class="fundingSource-id-window label label-default">FS${(deliverableFundingSource.fundingSource.id)!'None'}-${(deliverableFundingSource.fundingSource.fundingSourceInfo.budgetType.name)!'None'}</div>
                 [#-- Could be necessary add a ->deliverable.title?? that check if exists --]
                 [#if deliverableFundingSource.fundingSource.fundingSourceInfo?has_content]
                  [#if deliverableFundingSource.fundingSource.fundingSourceInfo.title?length < 13] 
                    <span>${(deliverableFundingSource.fundingSource.fundingSourceInfo.title)!'None'}</span>
                  [#else] 
                    <span>[@utilities.letterCutter string=deliverableFundingSource.fundingSource.fundingSourceInfo.title maxPos=13 /]<span>
                  [/#if]
                 [#else]
                  <span>${(deliverableFundingSource.fundingSource.fundingSourceInfo.title)!'None'}</span>
                 [/#if] 
                </div>
              [/#list]
            [#else]
              <span>None<span>
            [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro deliverablePartner dp={} dp_name="" dp_index="" isResponsable=true template=false editable=true]
  <div id="deliverablePartner-${template?string('template', dp_index)}" class="responsiblePartner projectPartnerPerson row" style="display:${template?string('none','')}">
    [#-- Remove --]
    [#if editable && !isResponsable]<div class="removeElement removeLink" title="[@s.text name="project.deliverable.removePartnerContribution" /]"></div> [/#if]
    [#-- Hidden inputs --]
    <input class="element" type="hidden" name="${dp_name}.id" value="${(dp.id)!}">
    [#if template]
      [#-- Partner Name --]
      <div class="form-group partnerName chosen"> 
        [@customForm.select name="${dp_name}.projectPartner.id" value="-1"  i18nkey="" showTitle=false listName="partners" keyFieldName="id"  displayFieldName="composedName"   className="responsible id" editable=editable required=isResponsable /]
        <div class="partnerPersons">
        </div>
      </div>
    [#else]
      [#-- Partner Name --]
      <div class="form-group partnerName chosen"> 
      [#if editable]
        [#local projectPartnerObj = ((dp.projectPartnerPerson.projectPartner)!dp.projectPartner)!{} /]
        [@customForm.select name="${dp_name}.projectPartner.id" value="${(projectPartnerObj.id)!-1}"  label="" i18nkey="" showTitle=false listName="partners" keyFieldName="id"  displayFieldName="composedName" className="responsible id " editable=editable required=isResponsable/]
        <div class="partnerPersons" listname="deliverable.responsiblePartner.projectPartnerPerson.id">
          [#if (projectPartnerObj.id??)!false]
            [#attempt]
              [#local personsList = (action.getPersons(projectPartnerObj.id))![] ]
            [#recover]
              [#local personsList = [] ]
            [/#attempt]
            [#list personsList as person]
              [@deliverablePerson element=person name="${dp_name}" projectPartner=(projectPartnerObj) index=person_index checked=(dp.projectPartnerPerson.id == person.id)!false isResponsable=true /]
            [/#list]
          [/#if]
        </div>
        
        [#-- Division --]
        [#if action.hasSpecificities('crp_division_fs')]
          [#local ifpriDivision = false /]
          [#if (projectPartnerObj.institution.acronym == "IFPRI")!false ][#local ifpriDivision = true /][/#if]
          <div class="form-group row divisionBlock division-IFPRI"  style="display:${ifpriDivision?string('block','none')}">
            <div class="col-md-7">
              [@customForm.select name="${dp_name}.partnerDivision.id" i18nkey="projectCofunded.division" className="divisionField" listName="divisions" keyFieldName="id" displayFieldName="composedName" required=true editable=editable /]
            </div>
          </div>
        [/#if]
      [#else]
        <div class="form-group partnerName chosen">
          <strong class="text-muted">${(dp.projectPartnerPerson.projectPartner.composedName)!}</strong>
          <input class="element" type="hidden" name="${dp_name}.projectPartner.id" value="${(dp.projectPartnerPerson.projectPartner.id)!}">
          <div class="partnerPersons">
          [#if (dp.projectPartnerPerson.projectPartner.id??)!false]
            [#attempt]
              [#local personsList = (action.getPersons(dp.projectPartnerPerson.projectPartner.id))![] ]
            [#recover]
              [#local personsList = [] ]
            [/#attempt]
            [#list personsList as person]
              [#if dp.projectPartnerPerson.id == person.id]
                <input class="element" type="hidden" name="${dp_name}.projectPartnerPerson.id" value="${(person.id)!}">
                <p class="checked">${person.composedCompleteName}</p>
                [#if (dp.partnerDivision??) && (dp.partnerDivision.id??) &&(dp.partnerDivision.id != -1)]
                 <input class="element" type="hidden" name="${dp_name}.partnerDivision.id" value="${(dp.partnerDivision.id)!}">
                 <p><strong>[@s.text name="projectCofunded.division" /]:</strong> ${(dp.partnerDivision.name)!}</p>
                [/#if]
              [/#if]
            [/#list]
          [#else]
            <strong>${(dp.projectPartner.composedName)!}</strong>
          [/#if]
          </div>
        </div>
      [/#if]
      </div>
    [/#if]
  </div> 
  <div class="clearfix"></div>
[/#macro]


[#macro deliverablePartnerOther dp=[] dp_name="" dp_index="" isResponsable=false template=false editable=true]
  [#assign personsIndex =  0 /]
  [#list dp as projectPartner]
    <div id="deliverablePartner-${template?string('template', projectPartner_index)}" class="deliverablePartner borderBox projectPartnerPerson row" style="display:${template?string('none','')}">
      [#-- Remove --]
      [#if editable && !isResponsable]<div class="removeElement removeLink" title="[@s.text name="project.deliverable.removePartnerContribution" /]"></div> [/#if]
      [#-- Index --]
      <div class="leftHead"><span class="index">${projectPartner_index+1}</span></div>
  
      [#if template]
        [#-- Partner Name --]
        <div class="fullPartBlock partnerName chosen"> 
          [@customForm.select name="" value="-1"  i18nkey="" showTitle=false listName="partners" keyFieldName="id"  displayFieldName="composedName"   className="partner id" editable=editable required=isResponsable /]
          <div class="partnerPersons">
          </div>
        </div>
      [#else]
        [#-- Partner Name --]
        <div class="form-group partnerName chosen"> 
        [#if editable] 
          [@customForm.select name="" value="${(projectPartner.id)!-1}"  label="" i18nkey="" showTitle=false listName="partners" keyFieldName="id"  displayFieldName="composedName" className="partner id " editable=editable required=isResponsable/]
          <div class="partnerPersons">
            [#if (projectPartner.id??)!false]
              [#assign selectedPersons =  action.getSelectedPersons(projectPartner.id) /]
              [#attempt]
                [#local personsList = (action.getPersons(projectPartner.id))![] ]
              [#recover]
                [#local personsList = [] ]
              [/#attempt]
              [#if personsList?has_content]
                [#list personsList as person]
                  [@deliverablePerson element=person name="${dp_name}" projectPartner=projectPartner index=personsIndex checked=(action.isSelectedPerson(person.id,projectPartner.id)) isResponsable=false /]
                  [#assign personsIndex =  personsIndex + 1 /]
                [/#list]
              [#else]
                [#-- Person Fake --]
                <div style="display:none">[@deliverablePerson element={} name="${dp_name}" projectPartner=projectPartner index=personsIndex checked=true isResponsable=false /]</div>
                [#assign personsIndex =  personsIndex + 1 /]
              [/#if]
              
            [/#if]
            <div class="clearfix"></div>
          </div>
        [#else]
          <div class="form-group partnerName chosen">
            <strong class="text-muted">${(projectPartner.composedName)!}</strong>
            <div class="partnerPersons">
            [#if (projectPartner.id??)!false]
              [#assign selectedPersons =  action.getSelectedPersons(projectPartner.id) /]
              [#attempt]
                [#local personsList = (action.getPersons(projectPartner.id))![] ]
              [#recover]
                [#local personsList = [] ]
              [/#attempt]
              [#list personsList as person]
                [#if selectedPersons?seq_contains(person.id)]
                  [#local deliverablePartnerShip =(action.getDeliverablePartnership((person.id)!-1))!{} /]
                  [#local partnerShipIndex = "${dp_name}[${personsIndex}]"/]
                  <input class="element" type="hidden" name="${partnerShipIndex}.id" value="${(deliverablePartnerShip.id)!}">
                  <input class="element" type="hidden" name="${partnerShipIndex}.projectPartner.id" value="${(projectPartner.id)!}">
                  <input class="element" type="hidden" name="${partnerShipIndex}.projectPartnerPerson.id" value="${(person.id)!}">
                  <p class="checked">${person.composedCompleteName}</p>
                  [#if (deliverablePartnerShip.partnerDivision??) && (deliverablePartnerShip.partnerDivision.id??) && (deliverablePartnerShip.partnerDivision.id != -1)]
                    <input class="element" type="hidden" name="${partnerShipIndex}.partnerDivision.id" value="${(deliverablePartnerShip.partnerDivision.id)!}">
                    <p><strong>[@s.text name="projectCofunded.division" /]:</strong> ${(deliverablePartnerShip.partnerDivision.name)!}</p>
                  [/#if]
                  [#assign personsIndex =  personsIndex + 1 /]
                [/#if]
              [/#list]
            [/#if]
            </div>
          </div>
        [/#if]
        </div>
      [/#if]
    </div> 
    <div class="clearfix"></div>
  [/#list]
[/#macro]


[#macro deliverablePerson element projectPartner name index checked isResponsable=false isTemplate=false]
  [#local customName]${name}[#if !isResponsable][${index}][/#if][/#local] 
  [#local type][#if isResponsable]radio[#else]checkbox[/#if][/#local]
  [#local deliverablePartnerShip =(action.getDeliverablePartnership((element.id)!-1))!{} /]
  
  <div id="deliverablePerson-${isTemplate?string('template', index)}" class="${type} deliverablePerson ${isResponsable?string('resp','other')} inputsFlat" style="display:${isTemplate?string('none','')}">
    [#if !isResponsable]
      <input type="hidden" class="projectPartnerID" name="${customName}.projectPartner.id" value="${(projectPartner.id)!}" />
      <input class="element" type="hidden" name="${customName}.id" value="${(deliverablePartnerShip.id)!}">
    [/#if]
    <input id="${type}-${index}-${(element.id)!}" type="${type}" name="${customName}.projectPartnerPerson.id" value="${(element.id)!}" [#if checked]checked[/#if]/>
    <label for="${type}-${index}-${(element.id)!}" class="${type}-label [#if isResponsable]radio-label-yes[/#if]" >${(element.composedCompleteName)!}</label>

    [#-- Division --]
    [#if action.hasSpecificities('crp_division_fs') && !isResponsable]
      [#local ifpriDivision = false /]
      [#if (element.projectPartner.institution.acronym == "IFPRI")!false ][#local ifpriDivision = true /][/#if]
      <div class="form-group row divisionBlock division-IFPRI"  style="display:${ifpriDivision?string('block','none')}">
        <div class="col-md-7">
          [@customForm.select name="${customName}.partnerDivision.id" value="${(deliverablePartnerShip.partnerDivision.id)!-1}" i18nkey="projectCofunded.division" className="divisionField" listName="divisions" keyFieldName="id" displayFieldName="composedName" required=true editable=editable /]
        </div>
      </div>
    [/#if]
  </div>
[/#macro]