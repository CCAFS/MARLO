[#ftl]
[#assign items= [
  { 'slug': 'management',       'name': 'CRPAdmin.menu.hrefProgramManagement',  'action': 'management',         'active': true },
  { 'slug': 'regionalMapping',  'name': 'CRPAdmin.menu.hrefRegionalMapping',    'action': 'regionalMapping',    'active': action.hasProgramnsRegions() },
  { 'slug': 'siteIntegration',  'name': 'CRPAdmin.menu.hrefSiteIntegration',    'action': 'siteIntegration',    'active': true },
  { 'slug': 'ppaPartners',      'name': 'CRPAdmin.menu.hrefPPAPartners',        'action': 'ppaPartners',        'active': true },  
  { 'slug': 'locations',        'name': 'CRPAdmin.menu.hrefLocations',          'action': 'locations',          'active': true },
  [#--  --{ 'slug': 'projectPhases',    'name': 'CRPAdmin.menu.hrefProjectPhases',      'action': 'projectPhases',      'active': false },--]
  { 'slug': 'users',            'name': 'CRPAdmin.menu.users',                  'action': 'crpUsers',           'active': true },
  { 'slug': 'targetUnits',      'name': 'CRPAdmin.menu.hrefTargetUnits',        'action': 'targetUnits',        'active': true },
  { 'slug': 'institutions',     'name': 'menu.superadmin.institutions',         'action': 'marloInstitutions',  'active': true },
  { 'slug': 'crpPhases',        'name': 'CRPAdmin.menu.crpPhases',              'action': 'crpPhases',          'active': true }
]/]



<nav id="secondaryMenu">
  <p>[@s.text name="CRPAdmin.menu.title"/]</p>
  <ul>
    <li>
      <ul>
        [#list items as item]
          <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>

<div class="clearfix"></div>