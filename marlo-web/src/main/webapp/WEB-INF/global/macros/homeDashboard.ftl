[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#macro deliverablesHomeList deliverables={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" defaultAction="deliverableList" currentTable=true]
  <table class="projectsList" id="deliverables">
    <thead>
      <tr class="subHeader">
        <th id="deliverableProject">[@s.text name="project" /]</th>
        <th id="ids">[@s.text name="project.deliverableList.deliverableId" /]</th>
        <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
        [#--  
        <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>--]
        <th id="deliverableEDY">[@s.text name="project.deliverableList.deliverySummaryYear" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if deliverables?has_content]
      [#list deliverables as deliverable]
        [#-- Is New --]
        [#assign isDeliverableNew = action.isDeliverableNew(deliverable.deliverableId) /]
        [#-- Has draft version (Auto-save) --]
        [#--assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /--]
        [#-- Is Complete --]
        [#assign isDeliverableComplete = action.isDeliverableComplete(deliverable.deliverableId, actualPhase.id) /]
        [#-- To Report --]
        [#local toReport = reportingActive && !isDeliverableComplete ]

        <tr>
          [#-- Project ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action='${(crpSession)!}/deliverableList'][@s.param name='projectID']${deliverable.projectId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [#if deliverable.projectAcronym?has_content]${deliverable.projectAcronym}
              [#else]
                C${deliverable.projectId}
              [/#if]
            </a>
          </td>
          [#-- Deliverable ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.deliverableId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              D${deliverable.deliverableId}
            </a>
          </td>
          [#-- Deliverable Title --]
          <td class="left">
            [#-- Hidden title to sort correctly by title --]
            <span class="hidden">${deliverable.deliverableTitle!''}</span>
            [#-- Draft Tag --]
            [#--if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if--]
            [#-- Report --]
            [#if toReport]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/#if]
            [#-- New Tag --]
            [#if isDeliverableNew]<span class="label label-info">New</span>[/#if]

            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.deliverableId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [@utilities.wordCutter string=(deliverable.deliverableTitle)! maxPos=100 /]
            </a>
          </td>
          [#-- Deliverable type 
          <td class="left">
            [@utilities.wordCutter string=(deliverable.deliverableType)! maxPos=100 /]
          </td>
          --]
          [#-- Deliverable Year --]
          <td class="text-center">

            [#if deliverable.expectedYear == -1]
              None
            [#else]
              ${(deliverable.expectedYear)!'None'}
              [#if ((deliverable.newExpectedYear != -1)!false)]
                Extended to ${deliverable.newExpectedYear}
              [/#if]
            [/#if]

          </td>
        </tr>
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro studiesHomeList studies={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" defaultAction="studyList" currentTable=true]
  <table class="projectsList" id="studies">
    <thead>
      <tr class="subHeader">
        <th id="studyProject">[@s.text name="project.id" /]</th>
        <th id="ids">[@s.text name="dashboard.studies.id" /]</th>
        <th id="studyTitles" >[@s.text name="dashboard.studies.title" /]</th>
        <th id="studyType">[@s.text name="dashboard.studies.type" /]</th>
        [#--<th id="studyEDY">[@s.text name="project.deliverableList.deliverySummaryYear" /]</th>--]
      </tr>
    </thead>
    <tbody>
    [#if studies?has_content]
      [#list studies as study]
        [#-- Is New --]
        [#assign isNew = (action.isEvidenceNew(study.studyId)) /]
        [#-- Has draft version (Auto-save) --]
        [#--assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /--]
        [#-- Is Complete --]
        [#assign isThisComplete = (action.hasStudiesMissingFields('',study.studyId))!false /]
        [#-- To Report --]
        [#local toReport = reportingActive && !isThisComplete ]

        <tr>
          [#-- Project ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action='${(crpSession)!}/studies'][@s.param name='projectID']${study.projectId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              C${study.projectId}
            </a>
          </td>
          [#-- Study ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='expectedID']${study.studyId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              ${study.studyId}
            </a>
          </td>
          [#-- Study Title --]
          <td class="left">
            [#-- Hidden title to sort correctly by title --]
            <span class="hidden">${study.studyTitle!''}</span>
            [#-- Draft Tag --]
            [#--if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if--]
            [#-- Report --]
            [#if toReport]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/#if]
            [#-- New Tag --]
            [#if isNew]<span class="label label-info">New</span>[/#if]

            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='expectedID']${study.studyId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [@utilities.wordCutter string=(study.studyTitle)! maxPos=160 /]
            </a>
          </td>
          [#-- Study type --]
          <td class="left">
            [@utilities.wordCutter string=(study.studyType)! maxPos=160 /]
          </td>
          [#-- Study Year --]
          [#--<td class="text-center">
            [#if study.expectedYear == -1]
              None
            [#else]
              ${(study.expectedYear)!'None'}
              Pending. I am not sure if this has an extended year
              [if ((deliverable.newExpectedYear != -1)!false)]
                Extended to ${deliverable.newExpectedYear}
              [/#if]
            [/#if]
          </td>--]
        </tr>
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro innovationsHomeList innovations={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" defaultAction="innovationList" currentTable=true]
  <table class="projectsList" id="innovations">
    <thead>
      <tr class="subHeader">
        <th id="innovationProject">[@s.text name="project.id" /]</th>
        <th id="ids">[@s.text name="dashboard.innovations.id" /]</th>
        <th id="innovationTitles" >[@s.text name="dashboard.innovations.title" /]</th>
        <th id="innovationType">[@s.text name="dashboard.innovations.type" /]</th>
        [#--<th id="innovationEDY">[@s.text name="project.deliverableList.deliverySummaryYear" /]</th>--]
      </tr>
    </thead>
    <tbody>
    [#if innovations?has_content]
      [#list innovations as innovation]
        [#-- Is New --]
        [#assign isNew = (action.isInnovationNew(innovation.innovationId))!false /]
        [#-- Has draft version (Auto-save) --]
        [#--assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /--]
        [#-- Is Complete --]
        [#assign isThisComplete = (action.hasInnovationMissingFields(innovation.innovationId))!false /]
        [#-- To Report --]
        [#local toReport = reportingActive && !isThisComplete ]

        <tr>
          [#-- Project ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action='${(crpSession)!}/innovationsList'][@s.param name='projectID']${innovation.projectId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              C${innovation.projectId}
            </a>
          </td>
          [#-- Innovation ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='innovationID']${innovation.innovationId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              ${innovation.innovationId}
            </a>
          </td>
          [#-- Innovation Title --]
          <td class="left">
            [#-- Hidden title to sort correctly by title --]
            <span class="hidden">${innovation.innovationTitle!''}</span>
            [#-- Draft Tag --]
            [#--if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if--]
            [#-- Report --]
            [#if toReport]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/#if]
            [#-- New Tag --]
            [#if isNew]<span class="label label-info">New</span>[/#if]

            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='innovationID']${innovation.innovationId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [@utilities.wordCutter string=(innovation.innovationTitle)! maxPos=160 /]
            </a>
          </td>
          [#-- Innovation type --]
          <td class="left">
            [@utilities.wordCutter string=(innovation.innovationType)! maxPos=160 /]
          </td>
          [#-- Innovation Year --]
          [#--<td class="text-center">
            [#if innovation.expectedYear == -1]
              None
            [#else]
              ${(innovation.expectedYear)!'None'}
              Pending. I am not sure if this has an extended year
              [if ((deliverable.newExpectedYear != -1)!false)]
                Extended to ${deliverable.newExpectedYear}
              [/#if]
            [/#if]
          </td>--]
        </tr>
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]

[#macro policiesHomeList policies={} owned=true canValidate=false canEdit=false isReportingActive=false namespace="/clusters" defaultAction="policyList" currentTable=true]
  <table class="projectsList" id="studies">
    <thead>
      <tr class="subHeader">
        <th id="policyProject">[@s.text name="project.id" /]</th>
        <th id="ids">[@s.text name="dashboard.policies.id" /]</th>
        <th id="policyTitles" >[@s.text name="dashboard.policies.title" /]</th>
        <th id="policyType">[@s.text name="dashboard.policies.type" /]</th>
        [#--<th id="policyEDY">[@s.text name="project.deliverableList.deliverySummaryYear" /]</th>--]
      </tr>
    </thead>
    <tbody>
    [#if policies?has_content]
      [#list policies as policy]
        [#-- Is New --]
        [#assign isNew = (action.isPolicyNew(policy.policyId)) /]
        [#-- Has draft version (Auto-save) --]
        [#--assign hasDraft = (action.getAutoSaveFilePath(deliverable.class.simpleName, "deliverable", deliverable.id))!false /--]
        [#-- Is Complete --]
        [#assign isThisComplete = (action.hasPoliciesMissingFields(policy.policyId))!false /]
        [#-- To Report --]
        [#local toReport = reportingActive && !isThisComplete ]

        <tr>
          [#-- Project ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action='${(crpSession)!}/policies'][@s.param name='projectID']${policy.projectId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              C${policy.projectId}
            </a>
          </td>
          [#-- Policy ID --]
          <td class="deliverableId">
            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='policyID']${policy.policyId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              ${policy.policyId}
            </a>
          </td>
          [#-- Policy Title --]
          <td class="left">
            [#-- Hidden title to sort correctly by title --]
            <span class="hidden">${policy.policyTitle!''}</span>
            [#-- Draft Tag --]
            [#--if hasDraft]<strong class="text-info">[DRAFT]</strong>[/#if--]
            [#-- Report --]
            [#if toReport]<span class="label label-primary" title="Required for this cycle"><span class="glyphicon glyphicon-flash" ></span> Report</span>[/#if]
            [#-- New Tag --]
            [#if isNew]<span class="label label-info">New</span>[/#if]

            <a href="[@s.url namespace=namespace action=defaultAction][@s.param name='policyID']${policy.policyId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [@utilities.wordCutter string=(policy.policyTitle)! maxPos=160 /]
            </a>
          </td>
          [#-- Policy type --]
          <td class="left">
            [@utilities.wordCutter string=(policy.policyType)! maxPos=160 /]
          </td>
          [#-- Policy Year --]
          [#--<td class="text-center">
            [#if policy.expectedYear == -1]
              None
            [#else]
              ${(policy.expectedYear)!'None'}
              Pending. I am not sure if this has an extended year
              [if ((deliverable.newExpectedYear != -1)!false)]
                Extended to ${deliverable.newExpectedYear}
              [/#if]
            [/#if]
          </td>--]
        </tr>
      [/#list]
      [/#if]
    </tbody>
  </table>
[/#macro]