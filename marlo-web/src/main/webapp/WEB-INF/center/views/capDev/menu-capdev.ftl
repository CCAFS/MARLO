[#ftl]


[#assign menus= [
  { 'title': 'Information', 'show': true,
    'items': [
    { 'slug': 'capdevIntervention',       'name': 'capdev.menu.hrefCapdev',           'action': 'detailCapdev',       'active': true  },
    { 'slug': 'capdevDescription',        'name': 'capdev.menu.hrefDescription',      'action': 'descriptionCapdev',  'active': true  },
    { 'slug': 'supportingDocuments',      'name': 'capdev.menu.hrefSupportingDocs',   'action': 'supportingDocs',     'active': true  }
    ]
  }
]/]

[#attempt]
  [#assign submission = (action.isSubmitCapDev(capdevID))!false /]
  [#assign canSubmit = (action. hasPersmissionSubmitCapDev(capdevID))!false /]
  [#assign completed = (action.isCompleteCapDev(capdevID))!false /]
[#recover]
  [#assign submission = false /]
  [#assign canSubmit = false /]
  [#assign completed = false /]
[/#attempt]

[#assign sectionsForChecking = [] /]

[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>Capdev Menu</p> 
  <ul>
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [#assign submitStatus = (action.getCenterSectionStatusCapDev(item.action, capdevID))!false /]
            [#assign hasDraft = (action.getAutoSaveFilePath(project.class.simpleName, item.action, project.id))!false /]
            [#if (item.show)!true ]
              <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] ${submitStatus?string('submitted','toSubmit')} ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${centerSession}/${item.action}"][@s.param name="capdevID" value=capdevID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${centerSession}/${item.action}">
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


  [#-- Check button --]
  [#if canEdit && !completed && !submission]
    <p class="projectValidateButton-message text-center">Check for missing fields.<br /></p>
    <div id="validateProject-${capdevID}" class="projectValidateButton">[@s.text name="form.buttons.check" /]</div>
    <div id="progressbar-${capdevID}" class="progressbar" style="display:none"></div>
  [/#if] 
  
  [#-- Submit button --]
  [#if canEdit]
    [#assign showSubmit=(canSubmit && !submission && completed)]
    <a id="submitProject-${capdevID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${centerSession}/submitCapDev"][@s.param name='capdevID']${capdevID}[/@s.param][/@s.url]" >
      [@s.text name="form.buttons.submit" /]
    </a>
  [/#if]

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]

[#-- Project Submit JS --]
[#assign customJS = [ "${baseUrlMedia}/js/capDev/capDevSubmit.js" ] + customJS  /]