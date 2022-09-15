[#ftl]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

[#assign reportingActiveMenu = (reportingActive)!false ]

[#if annualReport2018]
  [#assign reportingDefaultAction = '${((PMU)!false)?string("narrative","flagshipProgress")}' ]
  [#assign reportingDefaultNamespace = 'annualReport2018' ]
[#else]
  [#assign reportingDefaultAction = 'flagshipProgress' ]
  [#assign reportingDefaultNamespace = 'annualReport' ]
[/#if]

[#attempt]
  [#assign canAcessPublications = (action.canAcessPublications())!false ]
  [#assign canAcessCrp = (action.canAcessCrp())!false ]
  [#assign canAcessBI = (action.canAccessSuperAdmin())!false ]
  [#assign canAcessCCAFS = (action.crpID == 1)!false ]
  [#assign canAcessWLE = (action.crpID == 4)!false ]
  [#assign biModuleActive = false ]
[#recover]
  [#assign canAcessPublications = false ]
  [#assign canAcessCrp = false ]
  [#assign canAcessCCAFS = false ]
  [#assign canAcessWLE = false ]
[/#attempt]

[#if action.hasSpecificities('crp_bi_module_active')]
  [#assign biModuleActive = true ]
[/#if]

[#assign mainMenu= [
  [#-- HOME - Not Logged --]
  { 'slug': 'home',           'name': 'menu.home',          'namespace': '/',               'action': 'login',                            'visible': !logged, 'active': true },
  [#-- HOME - Logged --]
  { 'slug': 'home',           'name': 'menu.home',          'namespace': '/',               'action': '${(crpSession)!}/crpDashboard',    'icon': 'home',     'visible': logged, 'active': true },
  [#-- IMPACT PATHWAY - CRP --]
  { 'slug': 'impactPathway',  'name': 'menu.impactPathway', 'namespace': '/impactPathway',  'action': '${(crpSession)!}/outcomes',        'visible': logged && !centerGlobalUnit, 'active': true},
  [#-- IMPACT PATHWAY - CENTER --]
  { 'slug': 'impactPathway',  'name': 'menu.impactPathway', 'namespace': '/impactPathway',  'action': '${(crpSession)!}/programimpacts',  'visible': logged && centerGlobalUnit, 'active': true},
  [#-- MONITORING OUTCOMES - CENTER --]
  { 'slug': 'outcomes',       'name': 'menu.outcomes',      'namespace': '/monitoring',       'action': '${(crpSession)!}/monitoringOutcomesList',                      'visible': logged && centerGlobalUnit, 'active': true },
  [#-- PROJECTS - ALL --]
  { 'slug': 'projects',       'name': 'menu.projects',      'namespace': '/projects',       'action': '${(crpSession)!}/projectsList',                      'visible': logged && !aiccra, 'active': true },
  { 'slug': 'projects',       'name': 'menu.projects',      'namespace': '/clusters',       'action': '${(crpSession)!}/projectsList',                      'visible': logged && aiccra, 'active': true },
  [#-- FUNDING SOURCES - ALL --]
  { 'slug': 'fundingSources', 'name': 'menu.fundingSources',      'namespace': '/fundingSources',       'action': '${(crpSession)!}/fundingSourcesList',    'visible': logged, 'active': true },
  [#-- ADDITIONAL REPORTING - CRP --]
  { 'slug': 'additionalReporting', 'name': 'menu.additionalReporting',      'namespace': '/publications',       'action': '${(crpSession)!}/publicationsList',  'visible': false && logged && reportingActive && !centerGlobalUnit, 'active': true,  'help': true,
    'subItems' : [
      { 'slug': 'publications', 'name': 'menu.publications', 'namespace': '/publications',  'action': '${(crpSession)!}/publicationsList',  'visible': logged, 'active':  canAcessPublications, "development": false },
      { 'slug': 'studies', 'name': 'menu.studies', 'namespace': '/studies',  'action': '${(crpSession)!}/studiesList',  'visible': logged, 'active':  true, "development": false }
    ]
  },
  [#-- SYNTHESIS PLANNING - CRP --]
  { 'slug': 'synthesis', 'name': 'menu.synthesis',      'namespace': '/powb${powb2019?string("2019", "")}',   'action': '${(crpSession)!}/adjustmentsChanges',  'visible': false && logged && !reportingActive && !centerGlobalUnit && !upKeepActive, 'active': true,
    'subItems' : [
      { 'slug': 'powbReport', 'name': 'menu.synthesis.powbReport', 'namespace': '/powb${powb2019?string("2019", "")}',  'action': '${(crpSession)!}/adjustmentsChanges',  'visible': logged, 'active': true }
    ]
  },
  [#-- SYNTHESIS REPORTING - CRP --]
  { 'slug': 'synthesis', 'name': 'menu.synthesis',      'namespace': '/${reportingDefaultNamespace}',       'action': '${(crpSession)!}/${reportingDefaultAction}','development':false,     'visible': false && logged && reportingActive && !centerGlobalUnit && !upKeepActive, 'active': true,
    'subItems' : [
      { 'slug': 'annualReport', 'name': 'menu.synthesis.annualReport', 'namespace': '/${reportingDefaultNamespace}',  'action': '${(crpSession)!}/${reportingDefaultAction}',  'visible': logged, 'active': true },
      [#-- { 'slug': 'projectsEvaluation', 'name': 'menu.synthesis.projectsEvaluation', 'namespace': '/synthesis',  'action': '${(crpSession)!}/projectsEvaluation',  'visible': logged, 'active': false, "development": true }--]
      { 'slug': 'qualityAssessment', 'name': 'menu.synthesis.qualityAssessment', 'namespace': '/qa',  'action': '${(crpSession)!}/qa',  'visible': logged, 'active': (action.canAccessSuperAdmin())!false }
    ]
  },
  [#-- Cap Dev - CENTER --]
  { 'slug': 'capdev', 'name': 'menu.capdev',      'namespace': '/capdev',       'action': '${(centerSession)!}/capdev',    'visible': logged && centerGlobalUnit, 'active': action.centerCapDevActive()},
  [#-- SUMMARIES - ALL --]
  { 'slug': 'summaries', 'name': 'menu.summaries',      'namespace': '/summaries',       'action': '${(crpSession)!}/summaries',    'visible': logged, 'active': true }
  [#-- BI Module --]
  { 'slug': 'bi', 'name': 'menu.bi',      'namespace': '/bi',       'action': '${(crpSession)!}/bi',    'visible': logged && biModuleActive, 'active': true }
]/]


[#macro mainMenuList]
  [#list mainMenu as item]
   [#if item.visible]
    <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
      [#if item.active]
        [#assign url][@s.url namespace=item.namespace action='${item.action}'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
      [#else]
        [#assign url]#[/#assign]
      [/#if]
      <a href="${url}" onclick="return ${item.active?string}" class="action-${item.action}" title="[#if item.help?? && item.help][@s.text name="${item.name}.help" /][/#if]">
        [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if]
        [@s.text name=item.name ][@s.param]${(crpSession?upper_case)!'CRP'}[/@s.param] [/@s.text]
        [#if (item.development)!false][@utils.underConstruction title="global.underConstruction" width="18px" height="18px" /][/#if]
      </a>
      [#if item.subItems?has_content]
        <ul class="subMenuItems subMenu">
          [#list item.subItems as subItem]
            [#if subItem.visible]
            <li id="${subItem.slug}" class="[#if currentStage?? && currentStage == subItem.slug ]currentSection[/#if] ${(subItem.active)?string('enabled','disabled')}">
              [#if subItem.active]
                [#assign url][@s.url namespace=subItem.namespace action='${subItem.action}'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#assign]
              [#else]
                [#assign url]#[/#assign]
              [/#if]
              <a href="${url}" onclick="return ${subItem.active?string};" class="action-${subItem.action}">
                [#if subItem.icon?has_content]<span class="glyphicon glyphicon-${subItem.icon}"></span> [/#if]
                [@s.text name=subItem.name ][/@s.text]
                [#if (subItem.development)!false][@utils.underConstruction title="global.underConstruction" width="18px" height="18px" /][/#if]
              </a>
            </li>
            [/#if]
          [/#list]
        </ul>
      [/#if]
    </li>
    [/#if]
  [/#list]
[/#macro]

<nav id="mainMenu">
<div class="menuContent">
	<div class="container">
	  <ul class="hidden-md hidden-lg">
	   <li> <span class="glyphicon glyphicon-menu-hamburger"></span> Main menu
	     <ul class="subMenu">
	       [@mainMenuList /]
	     </ul>
	   </li>
	  </ul>
	  <ul class="visible-md-block visible-lg-block visible-ul">
	    [@mainMenuList /]
	  </ul>

	  [#if logged?? && logged]
      <div id="userInfo">
        <a id="userLogOut" href="[@s.url action="logout" namespace="/" /]">[@s.text name="header.logout" /]</a>
        <p class="userId" style="display:none">${(currentUser.id)!}</p>
        <p class="name"><span class="glyphicon glyphicon-user"></span> ${(currentUser.composedCompleteName)!}</p>
        <p class="institution">${(currentUser.email)!}</p>
        <p class="roles"> [${(roles)!}${(roles?has_content && liasons?has_content)?string(',','')}${(liasons)!}]</p>
      </div>
  	[/#if]
  </div>
</div>
</nav>




<div class="subMainMenu">
  <div class="container">
    [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
  </div>
</div>

[#if logged?? && logged]
<div class="form-group timeline-phases">
  [#attempt]
    [#include "/WEB-INF/global/pages/timeline-phases.ftl" /]
  [#recover]
  [/#attempt]
  <div class="clearFix"></div>
</div>
[/#if]

[#include "/WEB-INF/global/pages/generalMessages.ftl" /]
