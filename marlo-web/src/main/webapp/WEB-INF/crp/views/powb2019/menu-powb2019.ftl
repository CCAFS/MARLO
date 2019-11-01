[#ftl]

[#assign menus= [
  { 'title': '1.  Theories of Change',    'show': true,
    'items': [
    { 'slug': 'adjustmentsChanges',     'name': 'powb.menu.adjustmentsChanges',     'action': 'adjustmentsChanges',     'active': true }
    ]
  },
  { 'title': '2.  Expected Key Results',  'show': true,
    'items': [
    { 'slug': 'expectedProgress',       'name': 'powb.menu.expectedOutcomes',       'action': 'progressOutcomes',       'active': true, 'subName': '(Table 2A)' },
    { 'slug': 'plannedStudies',         'name': 'powb.menu.plannedStudies',         'action': 'plannedStudies',         'active': true, 'subName': '(Table 2B)' }
    { 'slug': 'plannedCollaborations',  'name': 'powb.menu.plannedCollaborations',  'action': 'plannedCollaborations',  'active': true, 'subName': '(Table 2C)' }
    ]
  },
  { 'title': '3.  Financial Plan',        'show': true,
    'items': [
    { 'slug': 'plannedBudget',          'name': 'powb.menu.plannedBudget',          'action': 'plannedBudget',          'active': true, 'subName': '(Table 3)' }
    ]
  },
  { 'title': '4. CCAFS Specific',        'show':  action.hasSpecificities(action.powbProgramChangeModule()),
    'items': [
    { 'slug': 'programChanges',          'name': 'powb.menu.programChanges',          'action': 'programChanges',       'active': true }
    ]
  }
]/]



[#attempt]
  [#assign submission = (action.isPowbSynthesisSubmitted(powbSynthesisID))!false /]
  [#assign canSubmit = (action.hasPersmissionSubmitPowb(powbSynthesisID))!false /]
  [#assign completed = (action.isCompletePowbSynthesis2019(powbSynthesisID))!false /]
[#recover]
  [#assign submission = false /]
  [#assign canSubmit = false /]
  [#assign completed = false /]
[/#attempt]

[#if config.debug]
<ul>
  <li>submission ${submission?string}</li>
  <li>canSubmit ${canSubmit?string}</li>
  <li>completed ${completed?string}</li>
</ul>
[/#if]


[#assign canUnSubmit = false /]
[#assign sectionsForChecking = [] /]
[#assign currentMenuItem = {} /]

[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>POWB Menu <br /><small> </small> </p> 
  <ul>
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [#assign submitStatus = (action.getPowbSynthesisSectionStatus2019(item.action, powbSynthesisID))!false /]
            [#assign hasDraft = (action.getAutoSaveFilePath(powbSynthesis.class.simpleName, item.action, powbSynthesis.id))!false /]
            [#if (item.show)!true ]
              <li id="menu-${item.action}" class="${hasDraft?string('draft', '')}  [#if item.slug == currentStage]currentSection[/#if] [#if item.active]${submitStatus?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
                  [#-- Name --]
                  [@s.text name=item.name/]
                  [#-- Sub Name --]
                  [#if item.subName?has_content]<br /><small>[@s.text name=item.subName/]</small>[/#if]
                  [#if (item.development)!false][@utils.underConstruction title="global.underConstruction" width="20px" height="20px" /][/#if]
                </a>
              </li>
              [#-- Set current Item --]
              [#if item.slug == currentStage][#assign currentMenuItem = item /][/#if]
              [#-- Set sections for checking --]
              [#if item.active][#assign sectionsForChecking = sectionsForChecking + ["${item.action}"] /][/#if]
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
[#if canEdit && canSubmit]
  [#assign showSubmit=(canSubmit && !submission && completed)]
  <a id="submitProject-${powbSynthesisID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${crpSession}/submitPowb"][@s.param name='powbSynthesisID']${powbSynthesisID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.submit" /]
  </a>
[#else]
  <div></div>
[/#if]


[#-- Unsubmit button --]
[#if (canUnSubmit && submission) && !crpClosed && !reportingActive]
  <a id="submitProject-${liaisonInstitutionID}" class="projectUnSubmitButton" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='liaisonInstitutionID']${liaisonInstitutionID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.unsubmit" /]
  </a>
[/#if]


[#-- Generate RTF --]
[#if true]
<br />
<div class="text-center">
  [#assign documentLink][@s.url namespace="/summaries" action="${crpSession}/POWBSummaryPLT2019"][@s.param name='phaseID']${actualPhase.id}[/@s.param][/@s.url][/#assign]
  <a class="btn btn-default" href="${documentLink}" target="_blank">
   <img  src="${baseUrlCdn}/global/images/icons/file-doc.png" alt="" /> Generate DOC file <small>(Beta version)</small>
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
[#assign customJS = customJS  + [  "${baseUrlMedia}/js/powb2019/powb2019Submit.js", "${baseUrlCdn}/global/js/autoSave.js", "${baseUrlCdn}/global/js/fieldsValidation.js" ]
/]

