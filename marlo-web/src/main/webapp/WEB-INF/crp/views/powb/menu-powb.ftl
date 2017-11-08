[#ftl]

[#assign menus= [
  { 'title': 'A. CRP Level', 'show': true,
    'items': [
    { 'slug': 'delivery',  'name': 'powb.menu.delivery',  'action': 'delivery',  'active': true  },
    { 'slug': 'budget',  'name': 'powb.menu.budget',  'action': 'budget',  'active': PMU, 'show': PMU  },
    { 'slug': 'collIntegration',  'name': 'powb.menu.collIntegration',  'action': 'collIntegration',  'active': true  },
    { 'slug': 'manGovMonEvaLea',  'name': 'powb.menu.manGovMonEvaLea',  'action': 'manGovMonEvaLea',  'active': PMU, 'show': PMU }
    ]
  },
  { 'title': 'B. Flagship Level', 'show': flagship,
    'items': [
    { 'slug': 'expectedMilestones',  'name': 'powb.menu.expectedMilestones',  'action': 'expectedMilestones',  'active': true  },
    { 'slug': 'outputs',  'name': 'powb.menu.outputs',  'action': 'outputs',  'active': true  },
    { 'slug': 'contFunds',  'name': 'powb.menu.contFunds',  'action': 'contFunds',  'active': true  }
    ]
  },
  { 'title': 'Tables', 'show': false,
    'items': [
    { 'slug': 'plannedBudget',  'name': 'powb.menu.plannedBudget',  'action': 'plannedBudget',  'active': true  },
    { 'slug': 'contSubIDOs',  'name': 'powb.menu.contSubIDOs',  'action': 'contSubIDOs',  'active': true  },
    { 'slug': 'expectedMilestonestable',  'name': 'powb.menu.expectedMilestonestable',  'action': 'expectedMilestonestable',  'active': true  },
    { 'slug': 'expectedKeyOutputstable',  'name': 'powb.menu.expectedKeyOutputstable',  'action': 'expectedKeyOutputstable',  'active': true  }
    ]
  },
  [#-- ---------------------------- --]
  { 'title': 'Executive Summary', 'show': true,
    'items': [
    { 'slug': 'summaryHighlight',  'name': 'powb.menu.summaryHighlight',  'action': 'summaryHighlight',  'active': true  }
    ]
  },
  { 'title': 'Planned Results', 'show': flagship,
    'items': [
    { 'slug': 'plannedOutcomesOutputs',  'name': 'powb.menu.plannedOutcomesOutputs',  'action': 'plannedOutcomesOutputs',  'active': true  },
    { 'slug': 'keyOutputsAchieved',  'name': 'powb.menu.keyOutputsAchieved',  'action': 'keyOutputsAchieved',  'active': true  }
    ]
  },
  { 'title': 'CRP Management', 'show': true,
    'items': [
    { 'slug': 'platformCollaboration',  'name': 'powb.menu.platformCollaboration',  'action': 'platformCollaboration',  'active': true  },
    { 'slug': 'interactions',  'name': 'powb.menu.interactions',  'action': 'interactions',  'active': true  },
    { 'slug': 'adaptativeManagement',  'name': 'powb.menu.adaptativeManagement',  'action': 'adaptativeManagement',  'active': PMU, 'show': PMU  }
    ]
  },
  { 'title': 'Financial Plan', 'show': PMU,
    'items': [
    { 'slug': 'plannedBudget',  'name': 'powb.menu.plannedBudget',  'action': 'plannedBudget',  'active': true  }
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
                <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="liaisonInstitutionID" value=liaisonInstitutionID /][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
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
