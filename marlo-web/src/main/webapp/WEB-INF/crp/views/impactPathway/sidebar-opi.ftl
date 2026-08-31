[#ftl]
[#--
  OPI sidebar — the design's "Components" card.

  Used by outcomes.ftl only. clusterActivities.ftl keeps including the shared
  menu-impactPathway.ftl, so its dropdown-style menu is untouched.

  Differences from the shared menu, all required by the design:
    - no dropdown: the component list is always visible, so there is no
      .selectedProgram trigger for global.js to bind its slideToggle to
    - no <ul> section list ("Indicators"): the page already is that section
    - the check message and button live inside the card, not after it
--]

[#assign submission = (action.submission)! /]
[#assign canSubmit = (action.hasPersmissionSubmitImpact())!false /]
[#assign completed = action.isCompleteImpact(crpProgramID) /]
[#assign canUnSubmit = (action.hasPersmissionUnSubmitImpact(crpProgramID))!false /]
[#assign outcomeCounts = (action.outcomeCountByProgram)!{} /]

[#-- Kept for fieldsValidation.js, which reads the sections to validate from here. --]
<span id="sectionsForChecking" style="display:none">outcomes</span>

<nav id="secondaryMenu" class="opi-menu hidden-print">

  <p class="opi-menu__heading">[@s.text name="outcomes.sidebar.title"/]</p>

  <div class="menuList opi-menu__list">
    [#list programs as program]
      [#assign isActive = (program.id == crpProgramID)/]
      [#assign programCount = (outcomeCounts[program.id?c])!0 /]
      <p class="${isActive?string('active','')}">
        <a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"
          [#if isActive]aria-current="page"[/#if]>
          <span class="opi-menu__body">
            <span class="opi-menu__title">[#if centerGlobalUnit]${(program.composedName)!}[#else][@s.text name="flagShip.menu"/] ${(program.acronym)!}[/#if]</span>
            <span class="opi-menu__sub">${programCount} [#if programCount == 1][@s.text name="outcomes.status.count.one"/][#else][@s.text name="outcomes.status.count.many"/][/#if]</span>
          </span>
          [#-- Only the active component's data is on the page, so only it can carry
               a live missing-fields badge; outcomes.js fills this in. --]
          [#if isActive]<span class="opi-menu__badge" data-opi-menu-badge></span>[/#if]
        </a>
      </p>
    [/#list]
  </div>

  [#-- Check for missing fields --]
  [#if canEdit && !completed && !submission?has_content]
    <div class="opi-menu__foot">
      <p class="projectValidateButton-message">[@s.text name="outcomes.sidebar.checkHint"/]</p>
      <div id="validateProject-${crpProgramID}" class="projectValidateButton">[@s.text name="outcomes.sidebar.checkButton"/]</div>
      <div id="progressbar-${crpProgramID}" class="progressbar" style="display:none"></div>
    </div>
  [/#if]

  [#-- Submission message --]
  [#if !submission?has_content && completed && !canSubmit]
    <div class="opi-menu__foot">
      <p class="opi-menu__note">The Impact Pathway can be submitted now by the [@s.text name="global.managementLiaison" /].</p>
    </div>
  [/#if]

  [#-- Submit button --]
  [#if canEdit]
    [#assign showSubmit=(canSubmit && (!submission?has_content) && completed)]
    <a id="submitProject-${crpProgramID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}"
      href="[@s.url action="${crpSession}/submit"][@s.param name='crpProgramID']${crpProgramID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
      [@s.text name="form.buttons.submit" /]
    </a>
  [/#if]

  [#-- Unsubmit button --]
  [#if canUnSubmit && canEditPhase && submission?has_content]
    <a id="submitProject-${crpProgramID}" class="impactUnSubmitButton projectUnSubmitButton"
      href="[@s.url action="${crpSession}/unsubmit"][@s.param name='projectID']${crpProgramID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
      [@s.text name="form.buttons.unsubmit" /]
    </a>
  [/#if]

</nav>

[#-- Impact Pathway mini-graph: not part of the OPI design, but AICCRA already
     hides it and the other global units still rely on it. impactGraphic.js
     renders into #mini-graphic unconditionally, so the container must exist. --]
[#if !centerGlobalUnit && !action.isAiccra()]
  [#-- Mini-graph --]
  <div id="graphicWrapper" class="hidden-print" style="">
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

[#-- Unsubmit justification dialog --]
<div id="unSubmit-justification" title="Unsubmit justification" style="display:none">
  <div class="dialog-content">
    [@customForm.textArea name="justification-unSubmit" i18nkey="saving.justification" required=true className="justification"/]
  </div>
</div>

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]
