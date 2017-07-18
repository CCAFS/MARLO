[#ftl]
[#assign mainMenu= [
  { 'slug': 'home',           'name': 'menu.home',          'namespace': '/',               'action': 'login',                                              'visible': !logged, 'active': true },
  { 'slug': 'home',           'name': 'menu.home',          'namespace': '/',               'action': 'centerDashboard',                      'icon': 'home',     'visible': logged, 'active': true },
  { 'slug': 'impactPathway',  'name': 'menu.impactPathway', 'namespace': '/centerImpactPathway',  'action': '${(centerSession)!}/programimpacts',                          'visible': logged, 'active': true },
  { 'slug': 'projects', 'name': 'menu.monitoring.projects',      'namespace': '/monitoring',       'action': '${(centerSession)!}/projectList',    'visible': (logged && action.canAccessSuperAdmin()), 'active': true },
  { 'slug': 'outcomes', 'name': 'menu.monitoring.outcomes',      'namespace': '/monitoring',       'action': '${(centerSession)!}/monitoringOutcomesList',    'visible': (logged && action.canAccessSuperAdmin()), 'active':action.canAccessSuperAdmin() },
  { 'slug': 'summaries', 'name': 'menu.monitoring.summaries',      'namespace': '/centerSummaries',       'action': '${(centerSession)!}/summaries',    'visible': (logged && action.canAccessSuperAdmin()), 'active': true }]
  /]

[#macro mainMenuList]
  [#list mainMenu as item]
   [#if item.visible]
    <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
      [#if item_index==3]
        <span class="tagMainMenu">Monitoring Programs</span>
      [/#if]
      <a href="[@s.url namespace=item.namespace action='${item.action}'][#if logged][@s.param name="edit" value="true"/][/#if][/@s.url]" onclick="return ${item.active?string}">
        [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if]
        [@s.text name=item.name ][@s.param]${(centerSession?upper_case)!'CENTER'}[/@s.param] [/@s.text]
      </a>
      [#if item.subItems?has_content]
        <ul class="subMenu">
          [#list item.subItems as subItem]
            [#if subItem.visible]
            <li id="${subItem.slug}" class="[#if currentStage?? && currentStage == subItem.slug ]currentSection[/#if] ${(subItem.active)?string('enabled','disabled')}">
              <a href="[@s.url namespace=subItem.namespace action='${subItem.action}'][#if logged][@s.param name="edit" value="true"/][/#if][/@s.url]" onclick="return ${subItem.active?string}" class="action-${subItem.action}">
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
	   <li> <span class="glyphicon glyphicon-menu-hamburger"></span> Menu
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
        <p class="name">${(currentUser.firstName)!} ${(currentUser.lastName)!}</p>  
        <p class="institution">${(currentUser.email)!}</p>
        <p class="roles">${(securityContext.roles)!}
         [#if currentUser.liaisonInstitution??][#list currentUser.liaisonInstitution as liaison]${(liaison.acronym)!}[#if liaison_has_next], [/#if][/#list] [/#if]
        </p>
      </div>
  	[/#if]
  </div>
</div> 
</nav> 

<div class="subMainMenu">
<div class="container">
  [#include "/WEB-INF/center/global/pages/breadcrumb.ftl" /]
</div>

[#include "/WEB-INF/center/global/pages/generalMessages.ftl" /]

</div>


