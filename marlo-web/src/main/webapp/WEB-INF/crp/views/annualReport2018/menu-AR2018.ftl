[#ftl]

[#assign menus= [
  { 'title': '1. Key Results', 'show': true,
    'items': [
    { 'slug': 'crpProgress',          'name': 'annualReport2018.menu.crpProgress',          'action': 'crpProgress',        'active': true },
    { 'slug': 'flagshipProgress',     'name': 'annualReport2018.menu.flagshipProgress',     'action': 'flagshipProgress',   'active': true },
    { 'slug': 'ccDimensions',         'name': 'annualReport2018.menu.ccDimensions',         'action': 'ccDimensions',       'active': true }
    ]
  },
  { 'title': '2. Effectiveness and Efficiency', 'show': true,
    'items': [
    [#-- { 'slug': 'plannedVariance',        'name': 'annualReport.menu.plannedVariance',      'action': 'plannedVariance',        'active': true, 'onlyPMU': !PMU }, --]
    { 'slug': 'governance',             'name': 'annualReport2018.menu.governance',             'action': 'governance',             'active': true, 'onlyPMU': !PMU }
    { 'slug': 'externalPartnerships',   'name': 'annualReport2018.menu.externalPartnerships',   'action': 'externalPartnerships',   'active': true },
    { 'slug': 'intellectualAssets',     'name': 'annualReport2018.menu.intellectualAssets',     'action': 'intellectualAssets',             'active': true, 'onlyPMU': !PMU },
    [#-- { 'slug': 'crossPartnerships',      'name': 'annualReport.menu.crossPartnerships',    'action': 'crossPartnerships',      'active': true }, --]
    { 'slug': 'melia',                  'name': 'annualReport2018.menu.melia',                  'action': 'melia',                  'active': true },
    { 'slug': 'efficiency',             'name': 'annualReport2018.menu.efficiency',             'action': 'efficiency',             'active': true, 'onlyPMU': !PMU },
    { 'slug': 'risks',                  'name': 'annualReport2018.menu.risks',                  'action': 'risks',                  'active': true, 'onlyPMU': !PMU },
    { 'slug': 'fundingUse',             'name': 'annualReport2018.menu.fundingUse',             'action': 'fundingUse',             'active': true, 'onlyPMU': !PMU }
    ]
  },
  { 'title': '3. Financial Summary', 'show': true,
    'items': [
    { 'slug': 'financial',    'name': 'annualReport2018.menu.financial',    'action': 'financial',    'active': true, 'onlyPMU': !PMU }
    ]
  },
  { 'title': '4. Reporting Indicators', 'show': true,
    'items': [
    { 'slug': 'influence',    'name': 'annualReport2018.menu.influence',    'action': 'influence',  'active': true, 'onlyPMU': !PMU },
    { 'slug': 'control',      'name': 'annualReport2018.menu.control',      'action': 'control',    'active': true, 'onlyPMU': !PMU }
    ]
  },
  { 'title': '0. Narrative section', 'show': true,
    'items': [
    { 'slug': 'narrative',    'name': 'annualReport2018.menu.executiveSummary',    'action': 'narrative',  'active': true, 'onlyPMU': !PMU }
    ]
  }
]/]


[#assign submission = false /]
[#assign canSubmit = false /]
[#attempt]
  [#assign completed = (action.isCompleteReportSynthesis2018(synthesisID))!false /]
[#recover]
  [#assign completed = false /]
[/#attempt]

[#assign canUnSubmit = false /]

[#assign sectionsForChecking = [] /]
[#assign currentMenuItem = {} /]

[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>Annual Report Menu <br /><small> </small> </p> 
  <ul>
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [#assign itemRequired = (((item.onlyPMU)!false) && PMU) || (((item.onlyFlagship)!false) && flagship) || (!((item.onlyFlagship)!false) && !((item.onlyPMU)!false)) /]
            [#assign submitStatus = (action.getReportSynthesisSectionStatus(item.action, synthesisID))!false /]
            [#assign hasDraft = (action.getAutoSaveFilePath(reportSynthesis.class.simpleName, item.action, reportSynthesis.id))!false /]
            [#if (item.show)!true ]
              <li id="menu-${item.action}" class="${hasDraft?string('draft', '')} [#if item.slug == currentStage]currentSection[/#if] [#if item.active && itemRequired]${submitStatus?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
                  [#-- Name --]
                  [@s.text name=item.name/]
                </a>
              </li>
              [#-- Set current Item --]
              [#if item.slug == currentStage][#assign currentMenuItem = item /][/#if]
              [#-- Set sections for checking --]
              [#if item.active && itemRequired][#assign sectionsForChecking = sectionsForChecking + ["${item.action}"] /][/#if]
            [/#if]
          [/#list] 
        </ul>
      </li>
      [/#if]
    [/#list]
  </ul> 
</nav>

<div class="clearfix"></div>

[#-- Sections for checking (Using by JS) --]
<span id="sectionsForChecking" style="display:none">[#list sectionsForChecking as item]${item}[#if item_has_next],[/#if][/#list]</span>

[#-- Submition message --]
[#if !submission && completed && !canSubmit]
  <p class="text-center" style="display:block">
    [#if (PMU)!false ]
      [@s.text name="powb.messages.synthesisCanBeSubmitted"][@s.param][@s.text name="global.pmu"/][/@s.param][/@s.text]
    [/#if]
    
    [#if (flagship)!false ]
      [@s.text name="powb.messages.synthesisCanBeSubmitted"][@s.param][@s.text name="CrpProgram.leaders"/][/@s.param][/@s.text]
    [/#if]
    </p>
[/#if]

[#-- Check button --] 
[#if canEdit && !completed && !submission]
  <p class="projectValidateButton-message text-center">Check for missing fields.<br /></p>
  <div id="validateProject-${liaisonInstitutionID}" class="projectValidateButton ${(project.type)!''}">[@s.text name="form.buttons.check" /]</div>
  <div id="progressbar-${liaisonInstitutionID}" class="progressbar" style="display:none"></div>
[/#if]

 
[#-- Submit button --]
[#if false && canEdit && canSubmit]
  [#assign showSubmit=(canSubmit && !submission && completed)]
  <a id="submitProject-${synthesisID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${crpSession}/submitAnnualReport"][@s.param name='synthesisID']${synthesisID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.submit" /]
  </a>
[#else]
  <div></div>
[/#if]

[#-- Unsubmit button --]
[#if false && (canUnSubmit && submission) && !crpClosed && !reportingActive]
  <a id="submitProject-${liaisonInstitutionID}" class="projectUnSubmitButton" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='liaisonInstitutionID']${liaisonInstitutionID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.unsubmit" /]
  </a>
[/#if]

[#-- Generate WORD Document --]
<br />
<div class="text-center">
  [#assign documentLink][@s.url namespace="/summaries" action="${crpSession}/AnnualReportSummary"][@s.param name='phaseID']${actualPhase.id}[/@s.param][/@s.url][/#assign]
  <a class="btn btn-default" href="${documentLink}" target="_blank">
   <img  src="${baseUrl}/global/images/icons/file-doc.png" alt="" /> Generate DOC file
  </a>
</div>

[#-- Justification --]
<div id="unSubmit-justification" title="Unsubmit justification" style="display:none"> 
  <div class="dialog-content"> 
      [@customForm.textArea name="justification-unSubmit" i18nkey="saving.justification" required=true className="justification"/]
  </div>  
</div>

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]

[#-- Project Submit JS --]
[#--  HERMES TO ENABLE THE AUTOSAVE FUNCTION PLASE ADD THIS: "${baseUrl}/global/js/autoSave.js" --]
[#assign customJS = customJS  + [  "${baseUrlMedia}/js/annualReport/annualReportSubmit.js", "${baseUrl}/global/js/fieldsValidation.js","${baseUrl}/global/js/autoSave.js"]
/]
