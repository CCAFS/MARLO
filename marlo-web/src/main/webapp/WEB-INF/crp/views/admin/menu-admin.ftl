[#ftl]
[#if !action.isAiccra()]
  [#assign items= [
    { 'slug': 'management',       'name': 'CRPAdmin.menu.hrefProgramManagement',  'action': 'management',         'active': true, 'visible': true },
    { 'slug': 'regionalMapping',  'name': 'CRPAdmin.menu.hrefRegionalMapping',    'action': 'regionalMapping',    'active': action.hasProgramnsRegions(), 'visible': true },
    { 'slug': 'siteIntegration',  'name': 'CRPAdmin.menu.hrefSiteIntegration',    'action': 'siteIntegration',    'active': true, 'visible': true },
    { 'slug': 'ppaPartners',      'name': 'CRPAdmin.menu.hrefPPAPartners',        'action': 'ppaPartners',        'active': true, 'visible': true },  
    { 'slug': 'activities',       'name': 'CRPAdmin.menu.activities',             'action': 'activityManager',    'active': true, 'visible': true },  
    { 'slug': 'locations',        'name': 'CRPAdmin.menu.hrefLocations',          'action': 'locations',          'active': true, 'visible': true },
    [#--  --{ 'slug': 'projectPhases',    'name': 'CRPAdmin.menu.hrefProjectPhases',      'action': 'projectPhases',      'active': false },--]
    { 'slug': 'users',            'name': 'CRPAdmin.menu.users',                  'action': 'crpUsers',           'active': true, 'visible': true },
    [#-- { 'slug': 'deliverables',     'name': 'CRPAdmin.menu.deliverables',           'action': 'crpDeliverables',    'active': true }, --]
    [#-- { 'slug': 'guestUsers',       'name': 'CRPAdmin.menu.guestUsers',             'action': 'guestUser',          'active': !config.production }, --]
    { 'slug': 'targetUnits',      'name': 'CRPAdmin.menu.hrefTargetUnits',        'action': 'targetUnits',        'active': true, 'visible': true },
    { 'slug': 'institutions',     'name': 'menu.superadmin.institutions',         'action': 'marloInstitutions',  'active': true, 'visible': true },
    { 'slug': 'crpPhases',        'name': 'CRPAdmin.menu.crpPhases',              'action': 'crpPhases',          'active': true, 'visible': true },
    { 'slug': 'homepageBannerManagement', 'name': 'menu.superadmin.homepageBannerManagement', 'action': 'homepageBannerManagement',   'active': true, 'visible': true }
  ]/]
[#else]
  [#assign items= [
    { 'slug': 'management',       'name': 'CRPAdmin.menu.hrefProgramManagement',  'action': 'management',         'active': true, 'visible': true },
    { 'slug': 'regionalMapping',  'name': 'CRPAdmin.menu.hrefRegionalMapping',    'action': 'regionalMapping',    'active': action.hasProgramnsRegions(), 'visible': true },
    { 'slug': 'siteIntegration',  'name': 'CRPAdmin.menu.hrefSiteIntegration',    'action': 'siteIntegration',    'active': true, 'visible': true },
    { 'slug': 'ppaPartners',      'name': 'CRPAdmin.menu.hrefPPAPartners',        'action': 'ppaPartners',        'active': true, 'visible': true },  
    { 'slug': 'activities',       'name': 'CRPAdmin.menu.activities',             'action': 'activityManager',    'active': true, 'visible': true },  
    { 'slug': 'locations',        'name': 'CRPAdmin.menu.hrefLocations',          'action': 'locations',          'active': true, 'visible': true },
    [#--  --{ 'slug': 'projectPhases',    'name': 'CRPAdmin.menu.hrefProjectPhases',      'action': 'projectPhases',      'active': false },--]
    { 'slug': 'users',            'name': 'CRPAdmin.menu.users',                  'action': 'crpUsers',           'active': true, 'visible': true },
    [#-- { 'slug': 'crpDeliverables',     'name': 'CRPAdmin.menu.deliverables',           'action': 'crpDeliverables',    'active': true }, --]
    [#-- { 'slug': 'guestUsers',       'name': 'CRPAdmin.menu.guestUsers',             'action': 'guestUser',          'active': !config.production }, --]
    { 'slug': 'targetUnits',      'name': 'CRPAdmin.menu.hrefTargetUnits',        'action': 'targetUnits',        'active': true, 'visible': true },
    <#--  { 'slug': 'institutions',     'name': 'menu.superadmin.institutions',         'action': 'marloInstitutions',  'active': true },  -->
    { 'slug': 'crpPhases',        'name': 'CRPAdmin.menu.crpPhases',              'action': 'crpPhases',          'active': true, 'visible': true },
    { 'slug': 'portfolioManagement',    'name': 'CRPAdmin.menu.portfolioManagement', 'action': 'portfolioManagement',   'active': true, 'visible': (action.specificityValue('portfolio_feature_active')?has_content)?then(action.hasSpecificities('portfolio_feature_active'), true) },
    { 'slug': 'timelineManagement',    'name': 'menu.superadmin.timelineManagement', 'action': 'timelineManagement',   'active': true, 'visible': true },
    { 'slug': 'homepageBannerManagement', 'name': 'menu.superadmin.homepageBannerManagement', 'action': 'homepageBannerManagement',   'active': true, 'visible': true },
    { 'slug': 'feedbackManagement',    'name': 'CRPAdmin.menu.feedbackManagement', 'action': 'feedbackManagement',   'active': true, 'visible': true },
    { 'slug': 'feedbackRolesPermissionsManagement',    'name': 'CRPAdmin.menu.feedbackPermissionsManagement', 'action': 'feedbackRolesPermissionsManagement',   'active': true, 'visible': true }
  ]/]
[/#if]



<nav id="secondaryMenu">
  <p>[@s.text name="CRPAdmin.menu.title"/]</p>
  <ul>
    <li>
      <ul>
        [#list items as item]
          [#if (item.visible)!true]
            <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
              <a href="[@s.url action="${crpSession}/${item.action}"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
                [@s.text name=item.name/]
              </a>
            </li>
          [/#if]
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>

<div class="clearfix"></div>