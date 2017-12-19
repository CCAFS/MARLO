[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro deliverablesList deliverables={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction=""]
  <table class="deliverableList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
        <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>
        <th id="deliverableEDY">[@s.text name="project.deliverableList.deliveryYear" /]</th>
        <th id="deliverableFC">[@s.text name="project.deliverableList.fairCompliance" /]</th>
        <th id="deliverableStatus">[@s.text name="project.deliverableList.status" /]</th>
        <th id="deliverableRF"></th>
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        [#assign isDeliverableNew = action.isDeliverableNew(deliverable.id) /]
        [#assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /]
        
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
            [#-- New Tag --]
            [#if isDeliverableNew]<span class="label label-info">New</span>[/#if]
            
            [#-- Draft Tag --]
            [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]

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
            [#if deliverable.status?? && deliverable.status==4 && deliverable.newExpectedYear??]
              to ${deliverable.newExpectedYear}
            [/#if]
          [/#if]
            
          </td>
          [#-- Deliverable FAIR compliance --]
          <td class="fair text-center"> 
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
          <td class="text-center">
            [#attempt]
              <div class="status-container">
                <div class="status-indicator ${(deliverable.statusName)!'none'}" title="${(deliverable.statusName)!'none'}"></div>
              </div>
            [#recover]
              none
            [/#attempt]
          </td>
          [#-- Deliverable required fields --]
          <td class="text-center">
            [#if isDeliverableComplete]
              <span class="icon-20 icon-check" title="Complete"></span>
            [#else]
              <span class="icon-20 icon-uncheck" title="Required fields still incompleted"></span> 
            [/#if]
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro deliverablesListExtended deliverables={} owned=true canValidate=false canEdit=false isPlanning=false namespace="/" defaultAction=""]
  <table class="deliverableList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
        <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>
        <th id="deliverableEDY">[@s.text name="project.deliverableList.deliveryYear" /]</th>
        <th id="deliverableFC">[@s.text name="project.deliverableList.fairCompliance" /]</th>
        <th id="deliverableStatus">[@s.text name="project.deliverableList.status" /]</th>
        <th id="deliverableRF"></th>
        <th id="deliverableRP">Responsible partner</th>
        <th id="deliverableFS">Funding source(s)</th>
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        [#assign isDeliverableNew = action.isDeliverableNew(deliverable.id) /]
        [#assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /]
        
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
          <td class="deliverableId" width="">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">
              D${deliverable.id}
            </a>
          </td>
          [#-- Deliverable Title --]
          <td class="left">
            [#-- New Tag --]
            [#if isDeliverableNew]<span class="label label-info">New</span>[/#if]
            
            [#-- Draft Tag --]
            [#if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if]

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
            [#if deliverable.status?? && deliverable.status==4 && deliverable.newExpectedYear??]
              to ${deliverable.newExpectedYear}
            [/#if]
          [/#if]
            
          </td>
          [#-- Deliverable FAIR compliance --]
          <td class="fair text-center"> 
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
          <td class="text-center">
            [#attempt]
              <div class="status-container">
                <div class="status-indicator ${(deliverable.statusName)!'none'}" title="${(deliverable.statusName)!'none'}"></div>
              </div>
            [#recover]
              none
            [/#attempt]
          </td>
          [#-- Deliverable required fields --]
          <td class="text-center">
            [#if isDeliverableComplete]
              <span class="icon-20 icon-check" title="Complete"></span>
            [#else]
              <span class="icon-20 icon-uncheck" title="Required fields still incompleted"></span> 
            [/#if]
          </td>
          [#-- Deliverable Responsible Partner --]
          <td class="text-center">
            [#attempt]
              ${(deliverable.responsiblePartner.projectPartnerPerson.projectPartner.institution.acronym)!'none'}
            [#recover]
              none
            [/#attempt]
          </td>
          [#-- Deliverable Funding source(s) --]
          <td width="">
          [#-- 
            [#if deliverable.fundingSources??]
              [#list deliverable.fundingSources as fundingSource]
                ${(fundingSource.directDonor.acronym)!'none'}
              [/#list]
            [/#if]
             --]
             <div class="fundingSource-container">
              <div class="fundingSource-id-window">FS174-Bilateral</div>
              <span>Scalable straasdfsdfkn</span>
             </div>
          </td>
        </tr>  
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro deliverablePartner dp={} dp_name="" dp_index="" isResponsable=false template=false editable=true]
  <div id="deliverablePartner-${template?string('template', dp_index)}" class="responsiblePartner projectPartnerPerson row" style="display:${template?string('none','')}">
    [#-- Remove --]
    [#if editable && !isResponsable]<div class="removeElement removeLink" title="[@s.text name="project.deliverable.removePartnerContribution" /]"></div> [/#if]
    [#-- Hidden inputs --]
    <input class="element" type="hidden" name="${dp_name}.id" value="${(dp.id)!}">
    [#if template]
      [#-- Partner Name --]
      <div class="fullPartBlock partnerName chosen"> 
        [@customForm.select name="" value="-1"  i18nkey="" showTitle=false listName="partners" keyFieldName="id"  displayFieldName="composedName"   className="responsible id" editable=editable required=isResponsable /]
        <div class="partnerPersons">
        </div>
      </div>
    [#else]
      [#-- Partner Name --]
      <div class="form-group partnerName chosen"> 
      [#if editable]
        [@customForm.select name="" value="${(dp.projectPartnerPerson.projectPartner.id)!-1}"  label="" i18nkey="" showTitle=false listName="partners" keyFieldName="id"  displayFieldName="composedName" className="responsible id " editable=editable required=isResponsable/]
        <div class="partnerPersons">
          [#if (dp.projectPartnerPerson.projectPartner.id??)!false]
            [#list action.getPersons(dp.projectPartnerPerson.projectPartner.id) as person]
              [@deliverablePerson element=person name="${dp_name}" index=person_index checked=(dp.projectPartnerPerson.id == person.id)!false isResponsable=true /]
            [/#list]
          [/#if]
        </div>
        
        [#-- Division --]
        [#if action.hasSpecificities('crp_division_fs')]
          [#local ifpriDivision = false /]
          [#if (dp.projectPartnerPerson.projectPartner.institution.acronym == "IFPRI")!false ][#local ifpriDivision = true /][/#if]
          <div class="form-group row divisionBlock division-IFPRI"  style="display:${ifpriDivision?string('block','none')}">
            <div class="col-md-7">
              [@customForm.select name="${dp_name}.partnerDivision.id" i18nkey="projectCofunded.division" className="divisionField" listName="divisions" keyFieldName="id" displayFieldName="composedName" required=true editable=editable /]
            </div>
          </div>
        [/#if]
      [#else]
        <div class="form-group partnerName chosen">
          <strong class="text-muted">${(dp.projectPartnerPerson.projectPartner.composedName)!}</strong>
          <div class="partnerPersons">
          [#if (dp.projectPartnerPerson.projectPartner.id??)!false]
            [#list action.getPersons(dp.projectPartnerPerson.projectPartner.id) as person]
              [#if dp.projectPartnerPerson.id == person.id]
                <p class="checked">${person.composedCompleteName}</p>
                [#if (dp.partnerDivision??) && (dp.partnerDivision.id??) &&(dp.partnerDivision.id != -1)]
                 <p><strong>[@s.text name="projectCofunded.division" /]:</strong> ${(dp.partnerDivision.name)!}</p>
                [/#if]
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
[/#macro]


[#macro deliverablePartnerOther dp=[] dp_name="" dp_index="" isResponsable=false template=false editable=true]
  [#assign personsIndex =  0 /]
  [#list dp as projectPartner]
    <div id="deliverablePartner-${template?string('template', projectPartner_index)}" class="deliverablePartner borderBox projectPartnerPerson row" style="display:${template?string('none','')}">
      [#-- Remove --]
      [#if editable && !isResponsable]<div class="removeElement removeLink" title="[@s.text name="project.deliverable.removePartnerContribution" /]"></div> [/#if]
      [#-- Index --]
      <div class="leftHead"><span class="index">${dp_index+1}</span></div>
  
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
              [#list action.getPersons(projectPartner.id) as person]
           
                [@deliverablePerson element=person name="${dp_name}" index=personsIndex checked=(action.isSelectedPerson(person.id,projectPartner.id)) isResponsable=false /]
                [#assign personsIndex =  personsIndex + 1 /]
              [/#list]
            [/#if]
            <div class="clearfix"></div>
          </div>
        [#else]
          <div class="form-group partnerName chosen">
            <strong class="text-muted">${(projectPartner.composedName)!}</strong>
            <div class="partnerPersons">
            [#if (projectPartner.id??)!false]
              [#assign selectedPersons =  action.getSelectedPersons(projectPartner.id) /]
              [#list action.getPersons(projectPartner.id) as person]
                [#if selectedPersons?seq_contains("${person.id}")]
                  [#local deliverablePartnerShip =(action.getDeliverablePartnership((person.id)!-1))!{} /]
                  <p class="checked">${person.composedCompleteName}</p>
                  [#if (deliverablePartnerShip.partnerDivision??) && (deliverablePartnerShip.partnerDivision.id??) &&(deliverablePartnerShip.partnerDivision.id != -1)]
                   <p><strong>[@s.text name="projectCofunded.division" /]:</strong> ${(deliverablePartnerShip.partnerDivision.name)!}</p>
                  [/#if]
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


[#macro deliverablePerson element name index checked isResponsable=false isTemplate=false]
  [#local customName]${name}[#if !isResponsable][${index}][/#if][/#local] 
  [#local type][#if isResponsable]radio[#else]checkbox[/#if][/#local]
  [#local deliverablePartnerShip =(action.getDeliverablePartnership((element.id)!-1))!{} /]
  
  <div id="deliverablePerson-${isTemplate?string('template', index)}" class="${type} deliverablePerson ${isResponsable?string('resp','other')} inputsFlat" style="display:${isTemplate?string('none','')}">
    [#if !isResponsable]<input class="element" type="hidden" name="${customName}.id" value="${(deliverablePartnerShip.id)!}">[/#if]
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