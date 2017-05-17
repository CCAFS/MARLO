[#ftl]
[#assign items= [
  { 'slug': 'outcomes',           'name': 'impactPathway.menu.hrefOutcomes',  'action': 'outcomes',           'active': true  },
  { 'slug': 'clusterActivities',  'name': 'impactPathway.menu.hrefCOA',       'action': 'clusterActivities',  'active': true }
]/]


[#assign submission = (action.submission)! /]
[#assign canSubmit = (action.hasPersmissionSubmitImpact())!false /]
[#assign completed = action.isCompleteImpact(crpProgramID) /]
[#assign canUnSubmit = (action.hasPersmissionUnSubmitImpact(crpProgramID))!false /]


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
          [#assign hasDraft = (action.getAutoSaveFilePath(selectedProgram.class.simpleName, item.action, selectedProgram.id))!false /]
          <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] ${action.getImpactSectionStatus(item.action, crpProgramID)?string('submitted','toSubmit')} ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
              [#-- Draft Tag --]
              [#if hasDraft]<strong class="text-danger" title="[@s.text name="message.fieldsCheck.draft" /]"> <span class="glyphicon glyphicon-bell"></span> </strong>[/#if]
              [#-- Name --]
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

[#-- Unsubmit button --]
[#if canUnSubmit && submission?has_content]
  <a id="submitProject-${crpProgramID}" class="impactUnSubmitButton projectUnSubmitButton" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='projectID']${crpProgramID}[/@s.param][/@s.url]" >
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

[#-- Mini-graph --]
<div id="graphicWrapper" style="">
<p class="text-center"><b>Impact Pathway Graph</b></p>
  <div id="mini-graphic">
    <div id="overlay" >
      <button class="btn btn-primary btn-xs"><strong>Show graph</strong></button>
    </div>
  </div>
  <div class="clearfix"></div>
</div>

[#-- PopUp Graph --]
<div id="impactGraphic-content"  style="display:none;" >
  
  <div id="loader" style="display:none;" ></div>
  
  [#-- Information panel --]
  <div id="infoRelations" class="panel panel-default">
    <div class="panel-heading"><strong>Relations</strong></div>
    <div id="infoContent" class="panel-body">
     <ul></ul>
    </div>
  </div>
  
  [#-- Controls --]
  <div id="controls" class="">
    <span id="zoomIn" class="glyphicon glyphicon-zoom-in tool"></span>
    <span id="zoomOut" class="glyphicon glyphicon-zoom-out tool "></span>
    <span id="panRight" class="glyphicon glyphicon-arrow-right tool "></span>
    <span id="panDown" class="glyphicon glyphicon-arrow-down tool "></span>
    <span id="panLeft" class="glyphicon glyphicon-arrow-left tool "></span>
    <span id="panUp" class="glyphicon glyphicon-arrow-up tool "></span>
    <span id="resize" class="glyphicon glyphicon-resize-full  tool"></span>
  </div>
  
  [#-- Change to full or current graph --]
  <div id="changeGraph" style="display:none;">
  <span class="btn btn-primary btn-md currentGraph">Show full graph</span>
  </div>
  
  [#-- Download button--]
  <a class="download" href=""><span title="download" id="buttonDownload"><span class="glyphicon glyphicon-download-alt"></span></span></a>
  
  <div id="impactGraphic"></div>
</div>
