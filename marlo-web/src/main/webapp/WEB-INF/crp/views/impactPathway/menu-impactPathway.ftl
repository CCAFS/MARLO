[#ftl]
[#if centerGlobalUnit]
  [#assign items= [
    { 'slug': 'programImpacts',   'name': 'impactPathway.menu.hrefProgramImpacts',  'action': 'programimpacts',   'active': true  },
    { 'slug': 'researchTopics',   'name': 'impactPathway.menu.hrefResearchTopics',  'action': 'researchTopics',   'active': true },
    { 'slug': 'outcomes',         'name': 'impactPathway.menu.hrefOutcomes',        'action': 'centerOutcomesList',     'active': true  },
    { 'slug': 'outputs',          'name': 'impactPathway.menu.hrefOutputs',         'action': 'outputsList',      'active': true }
  ]/]
[#else]
  [#assign items= [
    { 'slug': 'outcomes',           'name': 'impactPathway.menu.hrefOutcomes',          'action': 'outcomes',           'active': true  },
    { 'slug': 'clusterActivities',  'name': 'impactPathway.menu.hrefCOA',               'action': 'clusterActivities',  'active': true  }
  ]/]
[/#if]

[#assign menus= [
  { 'title': '', 'show': true,
    'items': items
  }
]/]


[#assign sectionsForChecking = [] /]
[#assign currentMenuItem = {} /]
[#assign programID = crpProgramID /]

[#if centerGlobalUnit]
  [#assign submission = (action.isSubmitIP(crpProgramID))! /]
  [#assign canSubmit = (action. hasPersmissionSubmitIP(crpProgramID))!false /]
  [#assign completed = (action.isCompleteIP(crpProgramID))!false /]
  [#assign canUnSubmit = false /]
[#else]
  [#assign submission = (action.submission)! /]
  [#assign canSubmit = (action.hasPersmissionSubmitImpact())!false /]
  [#assign completed = action.isCompleteImpact(crpProgramID) /]
  [#assign canUnSubmit = (action.hasPersmissionUnSubmitImpact(crpProgramID))!false /]
[/#if]

[#-- Impact Pathway Menu--]
<nav id="secondaryMenu" class="">
  <p>[@s.text name="impactPathway.menu.title"/] <span class="selectedProgram">(${(selectedProgram.acronym)!}) <span class="glyphicon glyphicon-chevron-down"></span></span></p>
  <div class="menuList">
  [#if centerGlobalUnit]
    [#if researchAreas??]
      [#list researchAreas as area ]
        [#if area.researchPrograms?has_content]
          [#list area.researchPrograms as program]
            [#assign isActive = (program.id == crpProgramID)/]
            <p class="${isActive?string('active','')}"><a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              ${(program.composedName)!}</a>
            </p>
          [/#list]
        [/#if]
      [/#list]
    [/#if]
  [#else]
    [#if programs??]
      [#list programs as program]
        [#assign isActive = (program.id == crpProgramID)/]
        <p class="${isActive?string('active','')}"><a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          [@s.text name="flagShip.menu"/] ${program.acronym}</a>
        </p>
      [/#list]
    [/#if]
  [/#if]
  
  </div>
  <ul>
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul>[#if menu.title?has_content]<p class="menuTitle">${menu.title}</p>[/#if]
          [#list menu.items as item]
            [#if centerGlobalUnit]
              [#assign submitStatus = (action.getCenterSectionStatusIP(item.action, crpProgramID))!false /]
            [#else]
              [#assign submitStatus = (action.getImpactSectionStatus(item.action, crpProgramID))!false /]
            [/#if]
            [#assign hasDraft = (action.getAutoSaveFilePath(selectedProgram.class.simpleName, item.action, selectedProgram.id))!false /]
            [#if (item.show)!true ]
              <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] [#if item.active]${submitStatus?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="crpProgramID" value=crpProgramID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
                  [#-- Name --]
                  [#if item.action == "outcomesList"]
                      <span class="glyphicon glyphicon-chevron-right"></span>
                  [#elseif item.action == "outputsList"]
                      &nbsp; <span class="glyphicon glyphicon-chevron-right"></span>
                  [/#if]
                  [@s.text name=item.name/]
                  [#-- Draft Tag --]
                  [#if hasDraft][@s.text name="message.fieldsCheck.draft" ][@s.param]section[/@s.param][/@s.text][/#if]
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
[#if !submission?has_content && completed && !canSubmit]
  <p class="text-center" style="display:block">The Impact Pathway can be submitted now by the flagship leader.</p>
[/#if]

[#-- Check button --]
[#if canEdit && !completed && !submission?has_content]
  <p class="projectValidateButton-message text-center">Check for missing fields.<br /></p>
  <div id="validateProject-${crpProgramID}" class="projectValidateButton ${(project.type)!''}">[@s.text name="form.buttons.check" /]</div>
  <div id="progressbar-${crpProgramID}" class="progressbar" style="display:none"></div>
[/#if]

[#-- Submit button --]
[#if canEdit]
  [#assign showSubmit=(canSubmit && (!submission?has_content) && completed)]
  [#if centerGlobalUnit]
    [#assign submitAction= "${crpSession}/centerSubmit" ]
  [#else]
    [#assign submitAction= "${crpSession}/submit" ]
  [/#if]
  <a id="submitProject-${crpProgramID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action=submitAction][@s.param name='crpProgramID']${crpProgramID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
    [@s.text name="form.buttons.submit" /]
  </a>
[/#if]

[#-- Unsubmit button --]
[#if canUnSubmit && canEditPhase && submission?has_content]
  <a id="submitProject-${crpProgramID}" class="impactUnSubmitButton projectUnSubmitButton" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='projectID']${crpProgramID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
    [@s.text name="form.buttons.unsubmit" /]
  </a>
[/#if]
 [#-- Justification --]
<div id="unSubmit-justification" title="Unsubmit justification" style="display:none"> 
  <div class="dialog-content"> 
      [@customForm.textArea name="justification-unSubmit" i18nkey="saving.justification" required=true className="justification"/]
  </div>  
</div>


[#if centerGlobalUnit]

[#-- Impact Pathway PDF --]
<div class="col-md-12">
<p class="title">Download Impact Pathway</p>
<a class="col-md-12" href="${baseUrl}/summaries/impactPathwaySubmissions.do?crpProgramID=${crpProgramID}" target="__BLANK" style="text-align:center;">
  <img src="${baseUrl}/global/images/download-summary.png" width="40" height="50" title="Download" />
</a>
</div>


[#else]
[#-- Mini-graph --]
<div id="graphicWrapper" style="">
  <p class="text-center"><b>Impact Pathway Graph</b></p>
  <div id="mini-graphic">
    <div id="overlay" ><button class="btn btn-primary btn-xs"><strong>Show graph</strong></button></div>
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
[/#if]  


[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]

[#-- program Submit JS --]
[#if !centerGlobalUnit]
  [#assign customJS = [ "${baseUrlMedia}/js/impactPathway/impactGraphic.js" ] + customJS  /]
[/#if]
[#assign customJS = [ "${baseUrlMedia}/js/impactPathway/programSubmit.js" ] + customJS  /]