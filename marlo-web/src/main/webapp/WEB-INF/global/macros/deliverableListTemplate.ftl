[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro deliverablesList deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/" defaultAction="" currentTable=true projectID=0]
  <table class="deliverableList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
        <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>
        <th id="deliverableEDY">[@s.text name="project.deliverableList.deliveryYear" /]</th>
        <th id="deliverableEDY">[@s.text name="project.deliverableList.owner" /]</th>
        <th id="deliverableEDY">[@s.text name="project.deliverableList.sharedWith" /]</th>
        [#if isReportingActive]
          <th id="deliverableFC">[@s.text name="project.deliverableList.fairCompliance" /]</th>
        [/#if]
        [#if action.hasSpecificities('feedback_active') ]
          <th id="feedbackStatus" width="0%">[@s.text name="Feedback Status" /]</th>
        [/#if]

        <th id="deliverableStatus">[@s.text name="project.deliverableList.status" /]</th>
        
        [#if action.hasSpecificities('duplicated_deliverables_functionality_active') ]
          <th id="deliverableDuplicated">[@s.text name="project.deliverable.isDuplicated" /]</th>
        [/#if]
        
        <th id="deliverableResponsible">[@s.text name="project.deliverable.responsible.person" /]</th>
        
        [#if currentTable]
        <th id="deliverableRF"></th>
        <th id="deliverableRemove"></th>
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
        [#local toReport = reportingActive && (deliverable.deliverableInfo.isRequiredToBeReported())]

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
            [#-- Owner --]
            [#local isOwner = (deliverable.project.id == projectID)!false]

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
              [#if
                [#-- ((deliverable.deliverableInfo.status == 4 || deliverable.deliverableInfo.status==3)!false ) --]
                      ((deliverable.deliverableInfo.status == 4 || deliverable.deliverableInfo.status==3 || deliverable.deliverableInfo.status==5)!false )
                      && ((deliverable.deliverableInfo.newExpectedYear != -1)!false)
                    ]
                ${deliverable.deliverableInfo.newExpectedYear} (Extended from 
                ${(deliverable.deliverableInfo.year)!'None'})
              [#else]
                ${(deliverable.deliverableInfo.year)!'None'}
              [/#if]
              
              
            [/#if]

          </td>
          [#-- Deliverable owner --]
          <td class="owner text-center">
            [#if isOwner] <nobr>This Cluster</nobr>  [#else][#if deliverable.owner?has_content]${deliverable.owner}[#else]Not defined[/#if][/#if]
          </td>
          [#-- Deliverable shared with --]
          <td class="owner text-center">
            [#if deliverable.sharedWithProjects?has_content]${deliverable.sharedWithProjects}[#else]Not shared[/#if]
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
          [#-- Feedback status --]
          [#if action.hasSpecificities('feedback_active') ]
            <td class="text-center">
              ${(deliverable.commentStatus)!}
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
          
          [#-- Deliverable Duplicated --]
          [#if action.hasSpecificities('duplicated_deliverables_functionality_active') ]
          
           <td class="text-center">
            [#if (deliverable.deliverableInfo.duplicated)?has_content ]
              [#if (deliverable.deliverableInfo.duplicated)]
                <span class="icon-20 icon-duplicated" title="Duplicated"></span>
              [/#if]
            [/#if]
           </td>
          [/#if]

          [#-- Deliverable Responsible --]
          <td class="responsible text-center">
            ${deliverable.responsible!''}
          </td>
          
          [#-- Deliverable required fields --]
          [#if currentTable]
            <td class="text-center">
              [#if isDeliverableComplete]
                <span class="icon-20 icon-check" title="Complete"></span>
              [#else]
                <span class="icon-20 icon-uncheck" title="[@s.text name="project.deliverableList.requiredStatus.incomplete" /]"></span>
              [/#if]
            </td>
            <td class="text-center">
              [#-- Remove icon --]
              [#if isDeliverableNew && isOwner]
                <a id="removeDeliverable-${deliverable.id}" class="removeDeliverable" href="${baseUrl}/projects/${crpSession}/deleteDeliverable.do?deliverableID=${deliverable.id}&phaseID=${(actualPhase.id)!}" title="Remove deliverable">
                  <div class="icon-container"><span class="trash-icon glyphicon glyphicon-trash"></span><div>
                </a>
              [#else]
                 <div class="icon-container remove-disabled"><span class="trash-icon glyphicon glyphicon-trash" title="This deliverable cannot be deleted"></span><div>
              [/#if]
              </td>
          [/#if]
        </tr>
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro deliverablesSummaryList deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" defaultAction="deliverableList" currentTable=true]
  <table class="projectsList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="ids">[@s.text name="projectsList.projectids" /]</th>
        <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableSummaryName" /]</th>
        <th id="deliverableEDY">[@s.text name="project.deliverableList.deliverySummaryYear" /]</th>
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
        [#local toReport = reportingActive && (deliverable.deliverableInfo.isRequiredToBeReported()) ]

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

            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [@utilities.wordCutter string=(deliverable.deliverableInfo.title)! maxPos=160 /]
            </a>
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
        [#if action.hasSpecificities('feedback_active') ]
          <th id="feedbackStatus" width="0%">[@s.text name="Feedback Status" /]</th>
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
        [#local toReport = reportingActive && (deliverable.deliverableInfo.isRequiredToBeReported()) ]

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
