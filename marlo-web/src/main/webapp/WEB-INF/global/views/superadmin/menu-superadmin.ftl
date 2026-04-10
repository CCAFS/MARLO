[#ftl]
[#assign srfItems= [
  { 'slug': 'slos',   'name': 'menu.superadmin.slos', 'action': 'marloSLOs',   'active': true, 'visible': (action.specificityValue('slos')?has_content)?then(action.hasSpecificities('slos'), true) },
  { 'slug': 'crossCutting',   'name': 'menu.superadmin.crossCutting', 'action': 'marloCrossCutting',   'active': true, 'visible': (action.specificityValue('crossCutting')?has_content)?then(action.hasSpecificities('crossCutting'), true) },
  { 'slug': 'idos',   'name': 'menu.superadmin.idos', 'action': 'marloIDOs',   'active': true, 'visible': (action.specificityValue('idos')?has_content)?then(action.hasSpecificities('idos'), true) },
  { 'slug': 'siteIntegration',   'name': 'menu.superadmin.siteIntegration', 'action': 'marloSiteIntegration',   'active': true, 'visible': (action.specificityValue('siteIntegration')?has_content)?then(action.hasSpecificities('siteIntegration'), true) }
]/]

[#assign standards= [
  { 'slug': 'marloBoard',   'name': 'menu.superadmin.board', 'action': 'marloBoard',   'active': true, 'visible': (action.specificityValue('marloBoard')?has_content)?then(action.hasSpecificities('marloBoard'), true) },
  { 'slug': 'users.types',            'name': 'Partner Types', 'action': 'marloUsers',   'active': false, 'visible': (action.specificityValue('users.types')?has_content)?then(action.hasSpecificities('users.types'), false) },
  { 'slug': 'customLocations',    'name': 'menu.superadmin.customLocations', 'action': 'customLocations',   'active': true, 'visible': (action.specificityValue('customLocations')?has_content)?then(action.hasSpecificities('customLocations'), true) }
]/]

[#if !action.isAiccra()]
  [#assign toolItems= [
    { 'slug': 'notifications',    'name': 'menu.superadmin.notifications', 'action': 'notifications',   'active': true, 'visible': (action.specificityValue('notifications')?has_content)?then(action.hasSpecificities('notifications'), true) },
    { 'slug': 'emails',    'name': 'menu.superadmin.emails', 'action': 'emails',   'active': true, 'visible': (action.specificityValue('emails')?has_content)?then(action.hasSpecificities('emails'), true) },
    { 'slug': 'permissions',    'name': 'menu.superadmin.permissions', 'action': 'marloPermissions',   'active': false, 'visible': (action.specificityValue('permissions')?has_content)?then(action.hasSpecificities('permissions'), false) },
    { 'slug': 'parameters',    'name': 'menu.superadmin.parameters', 'action': 'marloParameters',   'active': true, 'visible': (action.specificityValue('parameters')?has_content)?then(action.hasSpecificities('parameters'), true) },
    { 'slug': 'institutions',    'name': 'menu.superadmin.institutions', 'action': 'marloInstitutions',   'active': true, 'visible': (action.specificityValue('institutions')?has_content)?then(action.hasSpecificities('institutions'), true) },
    { 'slug': 'bulkReplication',    'name': 'menu.superadmin.bulkReplication', 'action': 'deliverablesReplication',   'active': true, 'visible': (action.specificityValue('bulkReplication')?has_content)?then(action.hasSpecificities('bulkReplication'), true) }
  ]/]

  [#assign managers= [
    { 'slug': 'globalUnitManagement',    'name': 'menu.superadmin.globalUnitManagement', 'action': 'globalUnitManagement',   'active': true, 'visible': true }
  ]/]
[#else]
  [#assign toolItems= [
    { 'slug': 'notifications',    'name': 'menu.superadmin.notifications', 'action': 'notifications',   'active': true, 'visible': (action.specificityValue('notifications')?has_content)?then(action.hasSpecificities('notifications'), true) },
    { 'slug': 'emails',    'name': 'menu.superadmin.emails', 'action': 'emails',   'active': true, 'visible': (action.specificityValue('emails')?has_content)?then(action.hasSpecificities('emails'), true) },
    { 'slug': 'permissions',    'name': 'menu.superadmin.permissions', 'action': 'marloPermissions',   'active': false, 'visible': (action.specificityValue('permissions')?has_content)?then(action.hasSpecificities('permissions'), false) },
    { 'slug': 'parameters',    'name': 'menu.superadmin.parameters', 'action': 'marloParameters',   'active': true, 'visible': (action.specificityValue('parameters')?has_content)?then(action.hasSpecificities('parameters'), true) },
    <#--  { 'slug': 'institutions',    'name': 'menu.superadmin.institutions', 'action': 'marloInstitutions',   'active': true, 'visible': (action.specificityValue('institutions')?has_content)?then(action.hasSpecificities('institutions'), true) }  -->
    { 'slug': 'bulkReplication',    'name': 'menu.superadmin.bulkReplication', 'action': 'deliverablesReplication',   'active': true, 'visible': (action.specificityValue('bulkReplication')?has_content)?then(action.hasSpecificities('bulkReplication'), true) }
  ]/]
  
  [#assign managers= [
    { 'slug': 'globalUnitManagement',    'name': 'menu.superadmin.globalUnitManagement', 'action': 'globalUnitManagement',   'active': true, 'visible': true },
    { 'slug': 'marloMessages',    'name': 'menu.superadmin.marloMessages', 'action': 'marloMessages',   'active': true, 'visible': (action.specificityValue('marloMessages')?has_content)?then(action.hasSpecificities('marloMessages'), true) },
    { 'slug': 'tipManagement',    'name': 'menu.superadmin.tipManagement', 'action': 'tipManagement',   'active': true, 'visible': (action.specificityValue('tip_section_active')?has_content)?then(action.hasSpecificities('tip_section_active'), true) },
    { 'slug': 'reportsManagement',    'name': 'menu.superadmin.reportsManagement', 'action': 'reportsManagement',   'active': true, 'visible': (action.specificityValue('reportsManagement')?has_content)?then(action.hasSpecificities('reportsManagement'), true) },
    { 'slug': 'buttonGuideManagement',    'name': 'menu.superadmin.buttonGuideManagement', 'action': 'buttonGuideManagement',   'active': true, 'visible': (action.specificityValue('button_guide_active')?has_content)?then(action.hasSpecificities('button_guide_active'), true) }
  ]/]
[/#if]

<nav id="secondaryMenu">
  <p>[@s.text name="menu.superadmin.srfMenuTitle"/]</p>
  <ul>
    <li>
      <ul>
        [#list srfItems as item]
          [#if (item.visible)!true]
            <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
              <a href="[@s.url action=item.action ][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
                [@s.text name=item.name/]
              </a>
            </li>
          [/#if]
        [/#list] 
      </ul>
    </li>
  </ul>
  
  <p>Standards</p>
  <ul>
    <li>
      <ul>
        [#list standards as item]
          [#if (item.visible)!true]
            <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
              <a href="[@s.url action=item.action ][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
                [@s.text name=item.name/]
              </a>
            </li>
          [/#if]
        [/#list] 
      </ul>
    </li>
  </ul> 
  
  <p>[@s.text name="menu.superadmin.toolsMenuTitle"/]</p>
  <ul>
    <li>
      <ul>
        [#list toolItems as item]
          [#if (item.visible)!true]
            <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
              <a href="[@s.url action=item.action ][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
                [@s.text name=item.name/]
              </a>
            </li>
          [/#if]
        [/#list] 
      </ul>
    </li>
  </ul> 
  
  <p>Section Managers</p>
  <ul>
    <li>
      <ul>
        [#if managers?has_content]
          [#list managers as item]
            [#if (item.visible)!true]
              <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action=item.action ][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
                  [@s.text name=item.name/]
                </a>
              </li>
            [/#if]
          [/#list] 
        [/#if]
      </ul>
    </li>
  </ul> 
</nav>



<div class="clearfix"></div>