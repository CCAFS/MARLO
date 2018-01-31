[#ftl]

  [#assign menus= [
    { 'title': 'General Information', 'show': true,
      'items': [
      { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'projectDescription',  'active': true  },
      { 'slug': 'projectPartners',  'name': 'projects.menu.partners',  'action': 'projectPartners',  'active': true  },
      { 'slug': 'projectCapdev',  'name': 'projects.menu.capdev',  'action': 'projectCapdev',  'active': true,  'visible' : action.centerCapDevActive() },
      { 'slug': 'deliverables',  'name': 'projects.menu.deliverables',  'action': 'deliverableList',  'active': false }
      ]
    }
    
  ]/]
  
[#attempt]
  [#assign submission = (action.isSubmitCenterProject(projectID))! /]
  [#assign canSubmit = (action. hasPersmissionSubmitProject(projectID))!false /]
  [#assign completed = (action.isCompleteCenterProject(projectID))!false /]
[#recover]
error
  [#assign submission = false /]
  [#assign canSubmit = false /]
  [#assign completed = false /]
[/#attempt]

[#assign sectionsForChecking = [] /]

[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>Project Menu </p> 
  <ul>
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [#if (item.show)!true ]
            [#assign submitStatus = (action.getCenterSectionStatusProject(item.action, projectID))!false /]
              <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] [#if canEdit]${submitStatus?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${centerSession}/${item.action}"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${centerSession}/${item.action}">
                  [@s.text name=item.name/]
                </a>
              </li>
            [/#if]
             [#if item.active]
              [#assign sectionsForChecking = sectionsForChecking + ["${item.action}"] /]
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
  <p class="text-center" style="display:block">The Program can be submitted now.</p>
[/#if]

[#-- Check button --]
[#if canEdit && !completed && !submission?has_content]
  <p class="programValidateButton-message text-center">Check for missing fields.<br /></p>
  <div id="validateProgram-${projectID}" class="projectValidateButton">[@s.text name="form.buttons.check" /]</div>
  <div id="progressbar-${projectID}" class="progressbar" style="display:none"></div>
[/#if]

[#-- Submit button --]
[#if canEdit]
  [#assign showSubmit=(canSubmit && !submission?has_content && completed)]
  <a id="submitProgram-${programID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${centerSession}/submitProject"][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
    [@s.text name="form.buttons.submit" /]
  </a>
[/#if]

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]

[#-- program Submit JS --]
[#assign customJS = [ "${baseUrlMedia}/js/monitoring/projects/projectSubmit.js" ] + customJS  /]
