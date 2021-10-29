[#ftl]

[#assign menus= [
  { 'title': '0. Narrative section', 'show': true,
    'items': [
    { 'slug': 'narrative',    'name': 'annualReport2018.menu.executiveSummary',    'action': 'narrative',  'active': true, 'onlyPMU': !PMU }
    ]
  },
  { 'title': '1. Key Results', 'show': true,
    'items': [
    { 'slug': 'crpProgress',          'name': 'annualReport2018.menu.crpProgress',          'action': 'crpProgress',        'active': true, 'subName': 'annualReport2018.menu.crpProgress.subName' },
    { 'slug': 'flagshipProgress',     'name': 'annualReport2018.menu.flagshipProgress',     'action': 'flagshipProgress',   'active': true,
      'items': [
        { 'slug': 'policies',             'name': 'annualReport2018.menu.policies',           'action': 'policies',             'active': true, 'onlyPMU': !PMU     },
        { 'slug': 'oicr',                 'name': 'annualReport2018.menu.oicr',               'action': 'oicr',                 'active': true, 'onlyPMU': !PMU     },
        { 'slug': 'innovations',          'name': 'annualReport2018.menu.innovations',        'action': 'innovations',          'active': true, 'onlyPMU': !PMU     },
        { 'slug': 'outomesMilestones',    'name': 'annualReport2018.menu.outomesMilestones',  'action': 'outomesMilestones',    'active': false, 'development': true     },
        { 'slug': 'publications',         'name': 'annualReport2018.menu.publications',       'action': 'publications',         'active': true, 'onlyPMU': !PMU }  
      ]
    },
    { 'slug': 'ccDimensions',         'name': 'annualReport2018.menu.ccDimensions',         'action': 'ccDimensions',       'active': true, 'subName': '(Table 7: CapDev)'}
    ]
  },
  { 'title': '2. Effectiveness and Efficiency', 'show': true,
    'items': [
    { 'slug': 'governance',             'name': 'annualReport2018.menu.governance',             'action': 'governance',             'active': true, 'onlyPMU': !PMU }
    { 'slug': 'externalPartnerships',   'name': 'annualReport2018.menu.externalPartnerships',   'action': 'externalPartnerships',   'active': true, 'subName': '(Table 8 and Table 9)' },
    { 'slug': 'intellectualAssets',     'name': 'annualReport2018.menu.intellectualAssets',     'action': 'intellectualAssets',     'active': true, 'onlyPMU': !PMU },
    { 'slug': 'melia',                  'name': 'annualReport2018.menu.melia',                  'action': 'melia',                  'active': true, 'subName': '(Table 10 and Table 11)' },
    { 'slug': 'efficiency',             'name': 'annualReport2018.menu.efficiency',             'action': 'efficiency',             'active': true, 'onlyPMU': !PMU },
    { 'slug': 'risks',                  'name': 'annualReport2018.menu.risks',                  'action': 'risks',                  'active': true, 'onlyPMU': !PMU },
    { 'slug': 'fundingUse',             'name': 'annualReport2018.menu.fundingUse',             'action': 'fundingUse',             'active': true, 'subName': '(Table 12)', 'onlyPMU': !PMU }
    ]
  },
  { 'title': '3. Financial Summary', 'show': true,
    'items': [
    { 'slug': 'financial',    'name': 'annualReport2018.menu.financial',    'action': 'financial',    'active': true, 'subName': '(Table 13)', 'onlyPMU': !PMU }
    ]
  }
  
]/]


[#assign submission = (action.isAr2018Submitted(synthesisID))!false /]
[#assign canSubmit = (action.hasPersmissionSubmitAR2018(synthesisID))!false /]
[#assign canUnSubmit = false /]
[#assign sectionsForChecking = [] /]
[#assign currentMenuItem = {} /]
[#assign sectionsChecked = 0 /]


[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>Annual Report Menu <br /><small> </small> </p> 
  <ul>
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [@menuItem item=item /]
          [/#list] 
        </ul>
      </li>
      [/#if]
    [/#list]
  </ul> 
</nav>

[#assign completed = (sectionsChecked == sectionsForChecking?size) /]

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
[#if canEdit]
  <p class="projectValidateButton-message text-center">Check for missing fields.<br /></p>
  <div id="validateProject-${liaisonInstitutionID}" class="projectValidateButton ${(project.type)!''}">[@s.text name="form.buttons.check" /]</div>
  <div id="progressbar-${liaisonInstitutionID}" class="progressbar" style="display:none"></div>
  <br>
[/#if]


[#-- Submit button --] 
[#if canEdit && canSubmit && PMU]
  [#assign showSubmit=(canSubmit && !submission )]
  <center><small style="display:${showSubmit?string('block','none')}"><i>By clicking in this button, you allow the SMO to access all the data contained in the required tables in the template.</i></small></center>
  <a id="submitProject-${synthesisID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${crpSession}/submitAnnualReport"][@s.param name='synthesisID']${synthesisID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.submitSynthesisAR" /]
  </a>
[#else]
  <div></div>
[/#if]

[#--
[#-- Unsubmit button --][#--
[#if (canUnSubmit && submission) && !crpClosed && !reportingActive]
  <a id="submitProject-${liaisonInstitutionID}" class="projectUnSubmitButton" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='liaisonInstitutionID']${liaisonInstitutionID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.unsubmit" /]
  </a>
[/#if]
--]
[#-- Generate WORD Document --]
[#if true]
<br />
<div class="text-center">
  [#assign documentLink][@s.url namespace="/summaries" action="${crpSession}/AnnualReportSummary2018"][@s.param name='phaseID']${actualPhase.id}[/@s.param][/@s.url][/#assign]
  <a class="btn btn-default" href="${documentLink}" target="_blank">
   <img  src="${baseUrlCdn}/global/images/icons/file-doc.png" alt="" /> Generate DOC file <br /> <small></small>
  </a>
</div>
[/#if]

[#-- Justification --]
<div id="unSubmit-justification" title="Unsubmit justification" style="display:none"> 
  <div class="dialog-content"> 
      [@customForm.textArea name="justification-unSubmit" i18nkey="saving.justification" required=true className="justification"/]
  </div>  
</div>

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]

[#-- Project Submit JS --]
[#assign customJS = customJS  + [  "${baseUrlMedia}/js/annualReport/annualReportSubmit.js", "${baseUrlCdn}/global/js/fieldsValidation.js"]
/]

[#macro menuItem item]
  [#local itemRequired = (((item.onlyPMU)!false) && PMU) || (((item.onlyFlagship)!false) && flagship) || (!((item.onlyFlagship)!false) && !((item.onlyPMU)!false)) /]
  [#local submitStatus = (action.getReportSynthesis2018SectionStatus(item.action, synthesisID))!false /]
  [#local hasDraft = (action.getAutoSaveFilePath(reportSynthesis.class.simpleName, item.action, reportSynthesis.id))!false /]
  [#if (item.show)!true ]
    <li id="menu-${item.action}" class="${hasDraft?string('draft', '')} [#if item.slug == currentStage]currentSection[/#if] [#if item.active && itemRequired]${submitStatus?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
      <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
        [#-- Name --]
        [@s.text name=item.name/]
        [#-- Sub Name --]
        [#if item.subName?has_content]<br /><small>[@s.text name=item.subName/]</small>[/#if]
        [#-- Development --]
        [#if (item.development)!false][@utils.underConstruction title="global.underConstruction" width="18px" height="18px" /][/#if]
      </a>
      
      [#if item.items?has_content]
        <ul>
          [#list item.items as subItem]
            [@menuItem item=subItem /]
          [/#list]
        </ul>
      [/#if]
    </li>
    [#-- Set current Item --]
    [#if item.slug == currentStage][#assign currentMenuItem = item /][/#if]
    [#-- Set sections for checking --]
    [#if item.active && itemRequired]
      [#if submitStatus][#assign sectionsChecked = sectionsChecked + 1 /][/#if]
      [#assign sectionsForChecking = sectionsForChecking + ["${item.action}"] /]
    [/#if]
  [/#if]
[/#macro]
