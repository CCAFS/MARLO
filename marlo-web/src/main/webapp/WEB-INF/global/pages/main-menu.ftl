[#ftl]
[#assign mainMenu= [
  { 'slug': 'home',           'name': 'menu.login',         'namespace': '/',               'action': '',            'icon': 'log-in',  'visible': (!logged)!false, 'active': true },
  { 'slug': 'home',           'name': 'menu.home',          'namespace': '/',               'action': '',            'icon': 'home',   'visible': (logged)!false, 'active': true },
  { 'slug': 'impactPathway',  'name': 'menu.impactPathway', 'namespace': '/impactPathway',  'action': 'outcomes',                       'visible': (logged)!false, 'active': true },
  { 'slug': 'admin',          'name': 'menu.admin',         'namespace': '/admin',          'action': 'management',  'icon': 'cog',    'visible': action.canAcessCrpAdmin(), 'active': true }
]/]

<nav id="mainMenu"> 
<div class="menuContent">
	<div class="container">
	  <ul class="hidden-md hidden-lg">
	   <li> <span class="glyphicon glyphicon-menu-hamburger"></span> Menu
	     <ul class="subMenu">
	      [#list mainMenu as item]
	       [#if item.visible]
          <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url namespace=item.namespace action='${(crpSession)!}/${item.action}'][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if]
              [@s.text name=item.name ][@s.param]${(crpSession?upper_case)!'CRP'}[/@s.param] [/@s.text]
            </a>
          </li>
          [/#if]
        [/#list]
	     </ul>
	   </li>
	  </ul>
	  <ul class="visible-md-block visible-lg-block">
	    [#list mainMenu as item]
       [#if item.visible]
        <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
          <a href="[@s.url namespace=item.namespace action='${(crpSession)!}/${item.action}'][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
            [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if]
            [@s.text name=item.name ][@s.param]${(crpSession?upper_case)!'CRP'}[/@s.param] [/@s.text]
          </a>
        </li>
        [/#if]
      [/#list]
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

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>

<section id="generalMessages" class="container">
  [#-- Messages are going to show using notify plugin (see global.js) --]
  <ul class="messages" style="display: none;">
  [@s.iterator value="actionMessages"]    
    <li class="success">[@s.property escape="false" /]</li>    
  [/@s.iterator]
  [@s.iterator value="errorMessages"]    
    <li class="error">[@s.property escape="false" /]</li>    
  [/@s.iterator]
  </ul>
</section>
