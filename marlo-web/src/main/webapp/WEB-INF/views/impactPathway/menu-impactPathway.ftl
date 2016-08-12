[#ftl]
[#assign items= [
  { 'slug': 'outcomes',           'name': 'impactPathway.menu.hrefOutcomes',  'action': 'outcomes',           'active': true  },
  { 'slug': 'clusterActivities',  'name': 'impactPathway.menu.hrefCOA',       'action': 'clusterActivities',  'active': true }
]/]


[#assign submission = (action.submission)! /]
[#assign canSubmit = (action.hasPersmissionSubmit())!false /]
[#assign completed = action.isCompleteImpact(crpProgramID) /]


[#-- Menu  ${action.getImpactSectionStatus(actionName, crpProgramID)?string("","hasMissingFields")} --]
<nav id="secondaryMenu" class="">
  <p>[@s.text name="impactPathway.menu.title"/] <span class="selectedProgram">(${(selectedProgram.acronym)!}) <span class="glyphicon glyphicon-chevron-down"></span></span></p>
  <div class="menuList">
    [#list programs as program]
      [#assign isActive = (program.id == crpProgramID)/]
      <p class="${isActive?string('active','')}"><a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">[@s.text name="flagShip.menu"/] ${program.acronym}</a></p>
    [/#list]
  </div>
  <ul>
    <li>
      
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

[#-- Mini-graph --]
<div id="graphicWrapper">
  <div id="mini-graphic">
    <div id="overlay" >
      <a><strong>Show Impact Graphic</strong></a>
    </div>
  </div>
  <div class="clearfix"></div>
</div>

[#-- PopUp Graph --]
<div id="impactGraphic-content"  style="display:none;" >
  <div id="infoRelations" class="panel panel-default">
    <div class="panel-heading"><strong>Relations</strong></div>
    <div id="infoContent" class="panel-body">
     <ul></ul>
    </div>
  </div>
  <div id="changeGraph">
        [@customForm.yesNoInput name="changeGraphic" label="" inverse=false value="" yesLabel="Current Impact Pathway" noLabel="Show full Impact Pathway"cssClass="" /]
  </div>
  
  <button id="buttonDownload"><a href=""><span class="glyphicon glyphicon-download-alt"></span> Download</a></button>
  
  <div id="impactGraphic"></div>
</div>
