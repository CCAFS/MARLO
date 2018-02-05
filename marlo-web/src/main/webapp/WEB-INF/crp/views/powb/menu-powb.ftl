[#ftl]

[#assign menus= [
  { 'title': '1.  Expected Key Results', 'show': true,
    'items': [
    { 'slug': 'adjustmentsChanges',       'name': 'powb.menu.adjustmentsChanges',     'action': 'adjustmentsChanges',     'active': flagship || PMU  },
    { 'slug': 'expectedProgress',         'name': 'powb.menu.expectedProgress',       'action': 'expectedProgress',       'active': flagship || PMU  },
    { 'slug': 'evidenceRelevant',         'name': 'powb.menu.evidenceRelevant',       'action': 'evidenceRelevant',       'active': flagship || PMU  },
    { 'slug': 'plansByFlagship',          'name': 'powb.menu.plansByFlagship',        'action': 'plansByFlagship',        'active': flagship  },
    { 'slug': 'crossCuttingDimensions',   'name': 'powb.menu.crossCuttingDimensions', 'action': 'crossCuttingDimensions', 'active': PMU  }
    ]
  },
  { 'title': '2.  Planning for CRP Effectiveness and Efficiency', 'show': true,
    'items': [
    { 'slug': 'crpStaffing',              'name': 'powb.menu.crpStaffing',                'action': 'crpStaffing',                'active': false  },
    { 'slug': 'financialPlan',            'name': 'powb.menu.financialPlan',              'action': 'financialPlan',              'active': false  },
    { 'slug': 'collaborationIntegration', 'name': 'powb.menu.collaborationIntegration',   'action': 'collaborationIntegration',   'active': false  },
    { 'slug': 'monitoringLearning',       'name': 'powb.menu.monitoringLearning',         'action': 'monitoringLearning',         'active': false  }
    ]
  },
  { 'title': '3.  CRP Management', 'show': true,
    'items': [
    { 'slug': 'managementRisks',        'name': 'powb.menu.managementRisks',        'action': 'managementRisks',        'active': false  },
    { 'slug': 'managementGovernance',   'name': 'powb.menu.managementGovernance',   'action': 'managementGovernance',   'active': false  }
    ]
  }
]/]


[#assign submission = false /]
[#assign canSubmit = true /]
[#assign completed = false /]
[#assign canUnSubmit = true /]

[#assign sectionsForChecking = [] /]

[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>POWB Menu <br /><small> </small> </p> 
  <ul>
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [#assign submitStatus = false /]
            [#assign hasDraft = false /]
            [#if (item.show)!true ]
              <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] ${submitStatus?string('submitted','toSubmit')} ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
                  [#-- Name --]
                  [@s.text name=item.name/]
                  [#-- Draft Tag 
                  [#if hasDraft][@s.text name="message.fieldsCheck.draft" ][@s.param]section[/@s.param][/@s.text][/#if]
                  --]
                </a>
              </li>
              [#if item.active]
                [#assign sectionsForChecking = sectionsForChecking + ["${item.action}"] /]
              [/#if]
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
  <p class="text-center" style="display:block">The Project can be submitted now by the project leader.</p>
[/#if]

[#-- Check button --]
[#if canEdit && !completed && !submission]
  <p class="projectValidateButton-message text-center">Check for missing fields.<br /></p>
  <div id="validateProject-${liaisonInstitutionID}" class="projectValidateButton ${(project.type)!''}">[@s.text name="form.buttons.check" /]</div>
  <div id="progressbar-${liaisonInstitutionID}" class="progressbar" style="display:none"></div>
[/#if]

[#-- Submit button --]
[#if canEdit]
  [#assign showSubmit=(canSubmit && !submission && completed)]
  <a id="submitProject-${liaisonInstitutionID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${crpSession}/submit"][@s.param name='liaisonInstitutionID']${liaisonInstitutionID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.submit" /]
  </a>
[/#if]

[#-- Unsubmit button --]
[#if (canUnSubmit && submission) && !crpClosed && !reportingActive]
  <a id="submitProject-${liaisonInstitutionID}" class="projectUnSubmitButton" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='liaisonInstitutionID']${liaisonInstitutionID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.unsubmit" /]
  </a>
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
[#assign customJS = [ "${baseUrlMedia}/js/powb/powbSubmit.js" ] + customJS  /]
