[#ftl]
[#assign mainMenu= [
  { 'slug': 'home',           'name': 'menu.home',          'namespace': '/',               'action': 'login',                                              'visible': !logged, 'active': true },
  { 'slug': 'home',           'name': 'menu.home',          'namespace': '/',               'action': 'dashboard',                      'icon': 'home',     'visible': logged, 'active': true },
  { 'slug': 'impactPathway',  'name': 'menu.impactPathway', 'namespace': '/impactPathway',  'action': '${(crpSession)!}/outcomes',                          'visible': action.canAcessImpactPathway(), 'active': true },
  { 'slug': 'projects',       'name': 'menu.projects',      'namespace': '/projects',       'action': '${(crpSession)!}/projectsList',                      'visible': logged, 'active': true },
  { 'slug': 'fundingSources', 'name': 'menu.fundingSources',      'namespace': '/fundingSources',       'action': '${(crpSession)!}/fundingSourcesList',    'visible': logged, 'active': true },
  { 'slug': 'publications', 'name': 'menu.publications',      'namespace': '/publications',       'action': '${(crpSession)!}/publicationsList',    'visible': logged && reportingActive, 'active': action.canAcessPublications() , 'help': true },
  { 'slug': 'synthesis', 'name': 'menu.synthesis',      'namespace': '/synthesis',       'action': '${(crpSession)!}/crpIndicators',    'visible': logged && reportingActive, 'active': action.canAcessCrp() || action.canAcessSynthesisMog(),
    'subItems' : [
      { 'slug': 'crpIndicators', 'name': 'menu.synthesis.crpIndicators', 'namespace': '/synthesis',  'action': '${(crpSession)!}/crpIndicators',  'visible': logged, 'active': action.canAcessCrp()},
      [#-- PHASE 1 --]
      { 'slug': 'outcomeSynthesis', 'name': 'menu.synthesis.outcomeSynthesis', 'namespace': '/synthesis',  'action': '${(crpSession)!}/outcomeSynthesisPandR',  'visible': logged && phaseOne, 'active': action.canAcessSynthesisMog() },
      { 'slug': 'synthesisByMog', 'name': 'menu.synthesis.synthesisByMog', 'namespace': '/synthesis',  'action': '${(crpSession)!}/synthesisByMog',  'visible': logged && phaseOne, 'active': action.canAcessSynthesisMog() },
      [#-- PHASE 2 --]
      { 'slug': 'outcomeSynthesis', 'name': 'menu.synthesis.outcomeSynthesis', 'namespace': '/synthesis',  'action': '${(crpSession)!}/outcomeSynthesis',  'visible': logged && (!phaseOne), 'active': action.canAcessCrpAdmin() },
      { 'slug': 'coasSynthesis', 'name': 'menu.synthesis.coasSynthesis', 'namespace': '/synthesis',  'action': '${(crpSession)!}/coasSynthesis',  'visible': logged && (!phaseOne), 'active': action.canAcessCrpAdmin() },
      
      { 'slug': 'powbReport', 'name': 'menu.synthesis.powbReport', 'namespace': '/powb',  'action': '${(crpSession)!}/delivery',  'visible': logged, 'active': true },
      
      { 'slug': 'projectsEvaluation', 'name': 'menu.synthesis.projectsEvaluation', 'namespace': '/synthesis',  'action': '${(crpSession)!}/projectsEvaluation',  'visible': logged, 'active': false }
    ]
  },
  { 'slug': 'summaries', 'name': 'menu.summaries',      'namespace': '/summaries',       'action': '${(crpSession)!}/summaries',    'visible': logged, 'active': true }

]/]


[#macro mainMenuList]
  [#list mainMenu as item]
   [#if item.visible]
    <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
      [#if item.active]
        [#assign url][@s.url namespace=item.namespace action='${item.action}'][#if logged][@s.param name="edit" value="true"/][/#if][/@s.url][/#assign]
      [#else]
        [#assign url]#[/#assign]
      [/#if]
      <a href="${url}" onclick="return ${item.active?string}" class="action-${item.action}" title="[#if item.help?? && item.help][@s.text name="${item.name}.help" /][/#if]">
        [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if]
        [@s.text name=item.name ][@s.param]${(crpSession?upper_case)!'CRP'}[/@s.param] [/@s.text]
      </a>
      [#if item.subItems?has_content]
        <ul class="subMenu">
          [#list item.subItems as subItem]
            [#if subItem.visible]
            <li id="${subItem.slug}" class="[#if currentStage?? && currentStage == subItem.slug ]currentSection[/#if] ${(subItem.active)?string('enabled','disabled')}">
              [#if subItem.active]
                [#assign url][@s.url namespace=subItem.namespace action='${subItem.action}'][#if logged][@s.param name="edit" value="true"/][/#if][/@s.url][/#assign]
              [#else]
                [#assign url]#[/#assign]
              [/#if]
              <a href="${url}" onclick="return ${subItem.active?string}" class="action-${subItem.action}">
                [#if subItem.icon?has_content]<span class="glyphicon glyphicon-${subItem.icon}"></span> [/#if]
                [@s.text name=subItem.name ][/@s.text]
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
	  <ul class="visible-md-block visible-lg-block">
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

[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

</div>


