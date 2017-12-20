[#ftl]
[#assign objs= [
  { 'slug': 'capdevIntervention',      'name': 'capdev.menu.hrefCapdev',           'action': 'detailCapdev',           'active': true  },
  { 'slug': 'capdevDescription',           'name': 'capdev.menu.hrefDescription',      'action': 'descriptionCapdev',           'active': true  },
  { 'slug': 'supportingDocuments',         'name': 'capdev.menu.hrefSupportingDocs',    'action': 'supportingDocs',           'active': true  }
  
]/]

[#attempt]
  [#assign submission = (action.isSubmitCapDev(capdevID))! /]
  [#assign canSubmit = (action. hasPersmissionSubmitCapDev(capdevID))!false /]
  [#assign completed = (action.isCompleteCapDev(capdevID))!false /]
[#recover]
  [#assign submission = false /]
  [#assign canSubmit = false /]
  [#assign completed = false /]
[/#attempt]

[#assign sectionsForChecking = [] /]

<nav id="secondaryMenu" class="">
  <p>Capdev Menu</p>
   <ul>
    [#list objs as item]
    [#assign submitStatus = (action.getCenterSectionStatusCapDev(item.action, capdevID))!false /]
      <li>
        <ul> 
               <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] [#if canEdit]${submitStatus?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${centerSession}/${item.action}"][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='projectID']${projectID}[/@s.param][@s.param name='edit' value="true" /][/@s.url]"  class="action-${centerSession}/${item.action}">
                  [@s.text name=item.name/]
                </a>
              </li>
        </ul>
      </li>
       [#if item.active]
              [#assign sectionsForChecking = sectionsForChecking + ["${item.action}"] /]
            [/#if]
    [/#list]
  </ul> 
</nav>

<div class="clearfix"></div>

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]