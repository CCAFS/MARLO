[#ftl]
[#assign items= [
  { 'slug': 'outcomes',           'name': 'impactPathway.menu.hrefOutcomes',  'action': 'outcomes',           'active': true  },
  { 'slug': 'clusterActivities',  'name': 'impactPathway.menu.hrefCOA',       'action': 'clusterActivities',  'active': true }
]/]


[#assign currentCycleYear= ((reportingCycle?string(currentReportingYear,currentPlanningYear))?number)!2016 /]
[#assign submission = (project.isSubmitted(currentCycleYear, cycleName))! /]
[#assign canSubmit = (action.hasProjectPermission("submitProject", project.id, "manage"))!true /]
[#assign completed = action.isCompleteImpact(crpProgramID) /]

[#-- Menu--]
<nav id="secondaryMenu">
  <ul>
    <li><p>[@s.text name="impactPathway.menu.title"/]</p>
      <ul>
        [#list items as item]
          <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] [#if canEdit]${action.getImpactSectionStatus(item.action, crpProgramID)?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>

<div class="clearfix"></div>

[#-- Submition message --]
[#if !submission?has_content && completed && !canSubmit]
  <p class="text-center" style="display:block">The Impact Pathway can be submitted now by Flagship leaders.</p>
[/#if]

[#-- Check button --]
[#if canEdit && !completed && !submission?has_content]
  <p class="projectValidateButton-message text-center">Check for missing fields.<br /></p>
  <div id="validateProject-${crpProgramID}" class="projectValidateButton ${(project.type)!''}">[@s.text name="form.buttons.check" /]</div>
  <div id="progressbar-${crpProgramID}" class="progressbar" style="display:none"></div>
[/#if]

[#-- Submit button --]
[#if canEdit]
  [#assign showSubmit=(canSubmit && !submission?has_content && completed)]
  <a id="submitProject-${crpProgramID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${crpSession}/submit"][@s.param name='crpProgramID']${crpProgramID}[/@s.param][/@s.url]" >
    [@s.text name="form.buttons.submit" /]
  </a>
[/#if]