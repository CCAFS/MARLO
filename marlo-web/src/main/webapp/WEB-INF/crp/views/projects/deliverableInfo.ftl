[#ftl]
[#import "/WEB-INF/global/macros/deliverableMacros.ftl" as deliverableMacros /]
[#--
  General information tab (A2-2607 redesign). The section cards are new; every field inside them is still drawn by
  the customForm and deliverableMacros macros, so the names, ids, classes and listname attributes that
  deliverableInfo.js, deliverableDissemination.js, fieldsValidation.js, the feedback module and DeliverableAction
  rely on are unchanged. Only the presentation moves to deliverableForm.css.

  Layout per global unit (deliverable_participants_general_info_active, set in projectDeliverable.ftl):
  off -> shared clusters get their own card after Partners, and Participants/Trainees stays in Dissemination;
  on  -> shared teams sit inside Contribution, and Participants/Trainees is a card of this tab.
--]

<input type="hidden" id="traineesIndicator" name="traineesIndicator" value="${(action.getTraineesIndicatorDB())!''}"/>
<input type="hidden" name="deliverable.deliverableInfo.remainingPending" value="${(deliverable.deliverableInfo.remainingPending!'false')?c}"/>
<input type="hidden" id="adminRole" name="adminRole" value="${((action.canEditCrpAdmin())!'false')?c}"/>
<input type="hidden" id="clusterLeaderRole" name="clusterLeaderRole" value="${((action.isProjectLeader(projectID))!'false')?c}"/>

<div class="dlf-sections">
  [#-- Texts deliverableFormRedesign.js writes into the list controls --]
  <span hidden data-dlf-strings
    data-add-indicator="[@s.text name="project.deliverable.redesign.add.indicator" /]"
    data-add-priority-action="[@s.text name="project.deliverable.redesign.add.priorityAction" /]"
    data-add-sub-action="[@s.text name="project.deliverable.redesign.add.subAction" /]"
    data-add-activity="[@s.text name="project.deliverable.redesign.add.activity" /]"
    data-add-funding-source="[@s.text name="project.deliverable.redesign.add.fundingSource" /]"
    data-add-shared="[@s.text name="project.deliverable.redesign.add.shared"][@s.param]${sectionNoun}[/@s.param][/@s.text]"
    data-add-region="[@s.text name="project.deliverable.redesign.add.region" /]"
    data-add-country="[@s.text name="project.deliverable.redesign.add.country" /]"
    data-sub-actions="[@s.text name="project.deliverable.redesign.subActions" /]"
    data-this-section="[@s.text name="project.deliverable.redesign.thisSection"][@s.param]${sectionNoun}[/@s.param][/@s.text]"
    data-select-all="[@s.text name="project.deliverable.redesign.geo.selectAll" /]"
    data-remove="[@s.text name="project.deliverable.redesign.remove" /]"
    data-project-code="C${project.id?c}"></span>

  [#-- ─── Basic information ─────────────────────────────────────────────── --]
  <section class="dlf-card" id="dlf-basics">
    <h2 class="dlf-card__title">[@s.text name="project.deliverable.redesign.section.basics" /]</h2>

    [#-- Title input --]
    <div class="dlf-field">
      [@customForm.input name="deliverable.deliverableInfo.title" value="${(deliverable.deliverableInfo.title)!}" type="text" i18nkey="project.deliverable.generalInformation.title"  placeholder="" className="limitWords-25" required=true editable=editable /]
    </div>

    [#-- Description input --]
    <div class="dlf-field">
      [@customForm.textArea name="deliverable.deliverableInfo.description" value="${(deliverable.deliverableInfo.description)!}" i18nkey="project.deliverable.generalInformation.description"  placeholder="" className="limitWords-100" required=true editable=editable /]
    </div>

    [#-- Type and subtype inputs --]
    <div class="dlf-grid dlf-grid--2">
      <div class="dlf-field">
        [@customForm.select name="deliverable.deliverableInfo.deliverableType.deliverableCategory.id" label=""  i18nkey="project.deliverable.generalInformation.type" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm typeSelect" editable=editable/]
      </div>
      <div class="dlf-field subType-select">
        [@customForm.select name="deliverable.deliverableInfo.deliverableType.id" label=""  i18nkey="project.deliverable.generalInformation.subType" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm subTypeSelect" editable=editable/]
      </div>
    </div>

    [#-- Deliverable table with categories and sub categories (opened by deliverableInfo.js through .helpMessage3) --]
    <div class="deliverableTypeMessage">
      <div id="dialog" title="Deliverable types" style="display: none">
        <table id="deliverableTypes">
          <th> [@s.text name="project.deliverables.dialogMessage.part1" /] </th>
          <th> [@s.text name="project.deliverables.dialogMessage.part2" /] / [@s.text name="project.deliverables.dialogMessage.part3" /] </th>
          [#if deliverableTypeParent?has_content]
          [#list deliverableTypeParent as mt]
            [#list action.getDeliverablesSubTypes(mt.id) as st]
              <tr>
                [#if st_index == 0]<th rowspan="${action.getDeliverablesSubTypes(mt.id).size()}" class="text-center"> ${mt.name} </th>[/#if]
                <td>
                  ${st.name}
                  [#if ((st.description?has_content)!false) && (st.description != st.name)]<br /> (<i><small>${st.description}</small></i>)[/#if]
                  [#if mt.fair || st.fair]<span class="label label-info pull-right">FAIR</span>[/#if]
                </td>
              </tr>
            [/#list]
          [/#list]
          [/#if]
        </table>
      </div> <!-- End dialog-->

      <div id="popup" class="helpMessage3">
        <button type="button" id="opener" class="dlf-link">
          [@dlfIcon "info" /][@s.text name="project.deliverable.generalInformation.deliverableType" /]
        </button>
      </div>
    </div>
  </section>

  [#-- ─── Status and timeline ───────────────────────────────────────────── --]
  <section class="dlf-card" id="dlf-status">
    <h2 class="dlf-card__title">[@s.text name="project.deliverable.redesign.section.status" /]</h2>

    [#assign hasExpectedYear = ((deliverable.deliverableInfo.newExpectedYear != -1))!false /]
    [#assign isStatusExtended = (deliverable.deliverableInfo.status == 4)!false]
    <div class="dlf-grid dlf-grid--3">
      [#-- Deliverable Status --]
      <div class="dlf-field dlf-field--status">
        [@customForm.select name="deliverable.deliverableInfo.status" label=""   i18nkey="project.deliverable.generalInformation.status" listName="status"  multiple=false required=true header=false className="status isNew-${isDeliverableNew?string}" editable=editable || editStatus/]
        [#-- A new deliverable at reporting is reported as complete --]
        [#if isDeliverableNew && editable && (reportingActive)]
          <p class="dlf-hint">[@s.text name="project.deliverable.redesign.newShouldBeComplete" /]</p>
        [/#if]
      </div>

      [#-- Deliverable Year --]
      <div id="deliverableYear" class="dlf-field">
         [#assign dbExpectedYear = ((deliverable.deliverableInfo.year)!currentCycleYear)  ]
         [#if isDeliverableNew]
         	[#if reportingActive]
          	[#assign projectExpectedYear = "project.projectInfo.getYearActualPhase(${currentCycleYear})"]
          [#else]
          	[#assign projectExpectedYear = "project.projectInfo.getAllYearsPhase(${currentCycleYear})"]
          [/#if]
         [#else]
          [#assign projectExpectedYear = "project.projectInfo.getAllYearsPhase(${dbExpectedYear})"]
         [/#if]
        [#if editable ]
          <div class="overlay" style="display:${((!isDeliverableNew || isStatusExtended || hasExpectedYear))?string('block', 'none')}"></div>
          [@customForm.select name="deliverable.deliverableInfo.year" label=""  i18nkey="project.deliverable.generalInformation.year" listName=projectExpectedYear header=false required=true className="yearExpected"  /]
        [#else]
           <div class="select">
            <label for="">[@s.text name="project.deliverable.generalInformation.year" /]:</label>
            <p>${(deliverable.deliverableInfo.year)!}</p>
            <input type="hidden" name="deliverable.deliverableInfo.year" value="${(deliverable.deliverableInfo.year)!}"/>
          </div>
        [/#if]
      </div>

      [#-- New Expected Year - Extended = 4 or exist--]
      <div id="newExpectedYear" class="dlf-field" style="display:${(isStatusExtended)?string('block','none')}">
        [#assign startExpectedYear = ((deliverable.deliverableInfo.year)!currentCycleYear)  ]
        [#if editable || editStatus]
          <div class="overlay expectedDisabled" style="display:${(!isStatusExtended)?string('block', 'none')}"></div>
          [@customForm.select name="deliverable.deliverableInfo.newExpectedYear"  i18nkey="deliverable.newExpectedYear"  listName="project.projectInfo.getYears(${startExpectedYear})" header=true  multiple=false required=true  className="yearNewExpected" editable=editable || editStatus/]
        [#else]
          <div class="select">
            <label for="">[@s.text name="deliverable.newExpectedYear" /]:</label>
            <p>${(deliverable.deliverableInfo.newExpectedYear)!}</p>
            <input type="hidden" class="yearNewExpected" name="deliverable.deliverableInfo.newExpectedYear" value="${(deliverable.deliverableInfo.newExpectedYear)!}" />
          </div>
        [/#if]
      </div>
    </div>

    [#-- Status justification textArea --]
    [#if !isDeliverableNew]
      [#assign justificationRequired = ((deliverable.deliverableInfo.status == 4)  || (deliverable.deliverableInfo.status == 5))!false ]
      <div id="statusDescription" class="dlf-field" style="display:${justificationRequired?string('block','none')}">
        [@customForm.textArea name="deliverable.deliverableInfo.statusDescription" className="statusDescription limitWords-150" i18nkey="deliverable.statusJustification.status${(deliverable.deliverableInfo.status)!'NotSelected'}" editable=editable || editStatus/]
        <div id="statusesLabels" style="display:none">
          <div id="status-2">[@s.text name="deliverable.statusJustification.status2" /]:<span class="red">*</span></div>[#-- Ongoing("2", "On-going") --]
          <div id="status-3">[@s.text name="deliverable.statusJustification.status3" /]:<span class="red">*</span></div>[#-- Complete("3", "Complete") --]
          <div id="status-4">[@s.text name="deliverable.statusJustification.status4" /]:<span class="red">*</span></div>[#-- Extended("4", "Extended") --]
          <div id="status-5">[@s.text name="deliverable.statusJustification.status5" /]:<span class="red">*</span></div>[#-- Cancelled("5", "Cancelled") --]
        </div>
      </div>
    [/#if]

    [#if action.hasSpecificities('shfrm_contribution_active') ]
      [#-- SHRFM contribution --]
      <div class="dlf-question">
        [@deliverableMacros.shfmrContributionMacro /]
      </div>

      [#-- Contact person TEMPLATE from partnersTemplate.ftl --]
      [@deliverableMacros.contactPersonMacro element={} allSubActions=[] name="deliverable.shfrmPriorityActions[-1].shfrmSubActions[-1]" isTemplate=true /]

      [@deliverableMacros.subActionItemMacro subActionItem={} name="deliverable.shfrmPriorityActions[-1].shfrmSubActions" index=-1 isTemplate=true /]
    [/#if]
  </section>

  [#-- ─── Contribution ──────────────────────────────────────────────────── --]
  <section class="dlf-card" id="dlf-contribution">
    <h2 class="dlf-card__title">[@s.text name="project.deliverable.redesign.section.contribution" /]</h2>

    [#-- Performance indicators. The help asks for an SHFRM indicator, so it only shows where SHFRM is asked. --]
    [#if !project.projectInfo.administrative && !phaseOne && !isCenterProject ]
      [#assign indicatorsHelp = action.hasSpecificities('shfrm_contribution_active')?then("project.deliverable.redesign.indicators.help", "project.deliverable.redesign.indicators.helpNoShfrm") /]
      <div class="dlf-field listindicators">
        [@customForm.elementsListComponent name="deliverable.crpOutcomes" elementType="crpProgramOutcome" elementList=(deliverable.crpOutcomes)![] label="project.deliverable.generalInformation.keyOutput" listName="programOutcomes" keyFieldName="id" help=indicatorsHelp helpIcon=false paramText=soilIndicatorsText displayFieldName="composedName" required=true maxLimit=3/]
        <a class="dlf-link dlf-link--end" href="[@s.url namespace=namespace action="${crpSession}/contributionsCrpList"][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          [@s.text name="project.deliverable.redesign.indicators.notListed"][@s.param]${sectionNoun}[/@s.param][/@s.text]
        </a>
      </div>
    [/#if]

    [#-- MELIA --]
    [#if action.hasSpecificities('deliverable_melia_module_active') ]
      <div class="dlf-question dlf-divided">
        [@deliverableMacros.meliaStudyMacro /]
      </div>
    [/#if]

    [#-- Contribution to LP6 --]
    [#if action.hasSpecificities('crp_lp6_active') && reportingActive && ((action.getProjectLp6ContributionValue(project.id, actualPhase.id))!false) ]
      <div class="dlf-field dlf-divided dlf-lp6">
        <label>[@s.text name="deliverable.LP6Contribution.contribution"/][@customForm.req required=true /]</label>
        [@customForm.radioFlat id="lp6Contribution-yes" name="deliverable.contribution" label="Yes" value="true"  checked=(action.getHasLp6ContributionDeliverable(deliverable.id,actualPhase.id))!false cssClassLabel="radio-label-yes" editable=editable /]
        [@customForm.radioFlat id="lp6Contribution-no" name="deliverable.contribution" label="No" value="false" checked=!((action.getHasLp6ContributionDeliverable(deliverable.id,actualPhase.id))!true) cssClassLabel="radio-label-no" editable=editable /]
      </div>
    [/#if]

    [#-- Deliverable Activities --]
    <div class="dlf-field dlf-divided dlf-list">
      <div class="dlf-list__head">
        <label for="">[@customForm.text name="project.deliverable.activities" readText=!editable /][@customForm.req required=editable /]</label>
      </div>
      <div id="activityList" class="dlf-list__body" listname="deliverable.activities">
        <ul class="list">
        [#if deliverable.activities?has_content]
          [#list deliverable.activities as element]
            <li id="activities" class="activities clearfix">
              [#if editable]<div class="removeActivity removeIcon" title="Remove activity"></div>[/#if]
              <input class="id" type="hidden" name="deliverable.activities[${element_index}].id" value="${(element.id)!}" />
              <input class="aId" type="hidden" name="deliverable.activities[${element_index}].activity.id" value="${(element.activity.id)!}" />
              <span class="name">
              [#if element.activity.displayTitle?has_content]
                ${(element.activity.displayTitle)!}<br />
              [#else]
                No Title selected
              [/#if]
              </span>
              <div class="clearfix"></div>
            </li>
          [/#list]
          <p style="display:none;" class="emptyText"> [@s.text name="project.deliverable.activities.empty" /]</p>
        [#else]
          <p class="emptyText"> [@s.text name="project.deliverable.activities.empty" /]</p>
        [/#if]
        </ul>
        [#if editable ]
          [@customForm.select name="deliverable.deliverableActivity.id" label="" showTitle=false  i18nkey="" listName="activities" keyFieldName="id"  displayFieldName="title"  header=true required=false  className="activity" editable=true/]

          [#if !activities?has_content]
            <div class="note"> [@s.text name="project.deliverable.activities.empty" /]  </div>
          [/#if]
        [/#if]
      </div>
    </div>

    [#-- Activities List --]
    <div style="display:none">
      [#if activities?has_content]
        [#list activities as element]
          <span id="activity-${(element.id)!}">
            ${(element.displayTitle)!} <br />
          </span>
        [/#list]
      [/#if]
    </div>

    [#-- Funding Source --]
    [#if !phaseOne]
    <div class="dlf-field dlf-divided dlf-list">
      <div class="dlf-list__head">
        <label for="">[@customForm.text name="project.deliverable.fundingSource" readText=!editable /][@customForm.req required=false /]</label>
      </div>
      <div id="fundingSourceList" class="dlf-list__body" listname="deliverable.fundingSources">
        <ul class="list">
        [#if deliverable.fundingSources?has_content]
          [#list deliverable.fundingSources as element]
            <li class="fundingSources clearfix">
              [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if]
              <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
              <input class="fId" type="hidden" name="deliverable.fundingSources[${element_index}].fundingSource.id" value="${(element.fundingSource.id)!}" />
              <span class="name">
                <strong>FS${(element.fundingSource.id)!} - ${(element.fundingSource.fundingSourceInfo.budgetType.name)!} [#if (element.fundingSource.fundingSourceInfo.w1w2)!false] (Co-Financing)[/#if]</strong> <br />
                <span class="description">${(element.fundingSource.fundingSourceInfo.title)!}</span><br />
              </span>
              <div class="clearfix"></div>
            </li>
          [/#list]
          <p style="display:none;" class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p>
        [#else]
          <p class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p>
        [/#if]
        </ul>
        [#if editable ]
          [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]

          [#if !fundingSources?has_content]
            <div class="note"> [@s.text name="deliverable.fundingSourceListEmpty" /]  </div>
          [/#if]
        [/#if]
      </div>
    </div>

    [#-- Funding source List --]
    <div style="display:none">
      [#if fundingSources?has_content]
        [#list fundingSources as element]
          <span id="fundingSource-${(element.id)!}">
            <strong>FS${(element.id)!} - ${(element.fundingSourceInfo.budgetType.name)!} [#if (element.w1w2)!false] (Co-Financing) [/#if]</strong> <br />
            <span class="description">${(element.fundingSourceInfo.title)!}</span><br />
          </span>
        [/#list]
      [/#if]
    </div>
    [/#if]

    [#-- AICCRA III layout: the shared teams close the Contribution card --]
    [#if participantsInGeneralInfo]
      <div class="dlf-field dlf-divided">
        <h3 class="dlf-card__subtitle">[@s.text name="deliverable.sharedProjects.title" /]</h3>
        [@dlfSharedProjects /]
      </div>
    [/#if]
  </section>

  [#-- ─── Participants/Trainees (AICCRA III layout; the general layout asks it in Dissemination) ───── --]
  [#if participantsInGeneralInfo && showDisseminationTabs]
    <section class="dlf-card" id="dlf-trainees">
      <h2 class="dlf-card__title">[@s.text name="project.deliverable.redesign.section.trainees" /]</h2>
      <div class="dlf-question">
        [@deliverableMacros.deliverableParticipantsMacro /]
      </div>
    </section>
  [/#if]

  [#-- ─── Geographic scope ──────────────────────────────────────────────── --]
  <section class="dlf-card" id="dlf-geographicScope">
    <h2 class="dlf-card__title">[@s.text name="project.deliverable.redesign.section.geo" /]</h2>
    [@deliverableMacros.deliverableGeographicScope /]
  </section>

  [#-- ─── Cross-cutting dimensions ──────────────────────────────────────── --]
  <section class="dlf-card" id="dlf-crossCutting">
    <h2 class="dlf-card__title">[@s.text name="project.deliverable.redesign.section.cross" /]</h2>
    [@deliverableMacros.deliverableCrossCuttingMacro /]
  </section>

  [#-- ─── Partners ──────────────────────────────────────────────────────── --]
  <section class="dlf-card" id="dlf-partners">
    <h2 class="dlf-card__title">[@s.text name="project.deliverable.redesign.section.partners" /]</h2>
    <div id="deliverable-partnerships-new">
      [#-- Partner who is responsible --]
      <div class="dlf-field">
        <label for="">[@customForm.text name="project.deliverable.indicateResponsablePartner" readText=!editable/][@customForm.req required=editable /]</label>
        <div class="dlf-partnerBox">
          [@deliverableMacros.deliverablePartnerMacro element=(deliverable.responsiblePartnership[0])!{} name="deliverable.responsiblePartnership" index=0 defaultType=1 /]
        </div>
      </div>

      [#-- Other contact person that will contribute --]
      [#assign displayOtherPerson = (!deliverable.otherPartnerships?has_content && !editable)?string('none','block') /]
      <div class="dlf-field dlf-divided">
        <div class="dlf-list__head">
          <label for="" style="display:${displayOtherPerson}">[@customForm.text name="projectDeliverable.otherContactContributing" readText=!editable/]</label>
          [#if editable && canEdit]
            <button type="button" class="dlf-btn dlf-btn--soft dlf-btn--sm addPartnerItem">
              [@dlfIcon "plus" /][@s.text name="form.buttons.addPartner" /]
            </button>
          [/#if]
        </div>
        <div class="otherDeliverablePartners">
          [#list (deliverable.otherPartnerships)![] as otherPartnership]
            [@deliverableMacros.deliverablePartnerMacro element=(otherPartnership)!{} name="deliverable.otherPartnerships" index=otherPartnership_index defaultType=2/]
          [#else]
            <p class="simpleBox emptyText center"> [@s.text name="project.deliverable.partnership.emptyText" /] </p>
          [/#list]
        </div>

        [#if editable]
          <p class="partnerListMsj dlf-hint">
            [@s.text name="project.deliverable.generalInformation.partnerNotList" /]
            <a href="[@s.url namespace="/${currentSection}" action='${(crpSession)!}/partners'] [@s.param name="projectID"]${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              [@s.text name="project.deliverable.generalInformation.partnersLink" /]
            </a>
          </p>
        [/#if]
      </div>
    </div>
  </section>

  [#-- ─── Shared clusters (general layout) ──────────────────────────────── --]
  [#if !participantsInGeneralInfo]
    <section class="dlf-card" id="dlf-shared">
      <h2 class="dlf-card__title">[@s.text name="deliverable.sharedProjects.title" /]</h2>
      [@dlfSharedProjects /]
    </section>
  [/#if]

</div>

[#-- Projects shared: .listClusters is what deliverableDissemination.js watches to keep the trainees split per cluster --]
[#macro dlfSharedProjects]
  <div class="listClusters">
    [@customForm.elementsListComponent name="deliverable.sharedDeliverables" elementType="project" elementList=(deliverable.sharedDeliverables)![] label="deliverable.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
  </div>
[/#macro]

[#macro dlfIcon name]
  [#switch name]
    [#case "ok"]<svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M5.2 8.2 7.1 10l3.7-4" stroke="#fff" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>[#break]
    [#case "warn"]<svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M8 4.6v4.2" stroke="#fff" stroke-width="1.7" stroke-linecap="round"></path><circle cx="8" cy="11.1" r="1" fill="#fff"></circle></svg>[#break]
    [#case "info"]<svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" stroke="currentColor" stroke-width="1.4"></circle><path d="M8 7.3v3.6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path><circle cx="8" cy="5.1" r=".95" fill="currentColor"></circle></svg>[#break]
    [#case "plus"]<svg class="dlf-icon" width="11" height="11" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>[#break]
  [/#switch]
[/#macro]
