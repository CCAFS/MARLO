[#ftl]
<nav id="mainMenu">  
	<div class="container">
	  <ul>
	    [#if logged?? && logged]
	      [#-- Home element --]
        <li [#if currentSection?? && currentSection == "home"] class="currentSection" [/#if]>
  	      <a href="${baseUrl}/">
	         <span class="icon"><img class="icon-15" src="${baseUrl}/images/global/icon-home-menu-selected.png" /></span>
	         <span class="text">[@s.text name="menu.home" /]</span>
  	      </a>
        </li>
        
        [#-- PRE-Planning section --]
        [#if securityContext.FPL || securityContext.RPL || securityContext.ML || securityContext.CU || securityContext.admin ]
        <li [#if currentSection?? && currentSection == "preplanning"] class="currentSection" [/#if]>
          <a [#if preplanningActive ]href="[@s.url namespace="/pre-planning" action='intro'/]">[#else]href="javascript:void(0);" title="[@s.text name="menu.link.disabled" /]" class="disabled">[/#if]
            [@s.text name="menu.preplanning" /]
          </a>
        </li>
        [/#if]

	    [#else]
	      [#-- If the user is not logged show the login element in menu --]
	      <li [#if currentSection?? && currentSection == "home"] class="currentSection" [/#if]>
  	      <a href="${baseUrl}/">[@s.text name="menu.login" /]</a>
	      </li>
     
	    [/#if]
	  </ul>
	  
	  [#if logged?? && logged]
      <div id="userInfo">
        <a id="userLogOut" href="[@s.url action="logout" namespace="/" /]">[@s.text name="header.logout" /]</a>
        <p class="userId" style="display:none">${currentUser.id}</p> 
        <p class="name">${currentUser.firstName} ${currentUser.lastName}</p>  
        <p class="institution">${currentUser.email}</p>
        <p class="roles">${(securityContext.roles)!}
         [#if currentUser.liaisonInstitution??][#list currentUser.liaisonInstitution as liaison]${(liaison.acronym)!}[#sep], [/#list] [/#if]
        </p>
      </div>
  	[/#if]
  </div>
</nav> 

<div class="container">
 [#include "/WEB-INF/global/pages/breadcrumb.ftl"]
</div>

<section id="generalMessages">
  [#-- Messages are going to show using notify plugin (see global.js) --]
  <ul id="messages" style="display: none;">
  [@s.iterator value="actionMessages"]    
    <li class="success">[@s.property escape="false" /]</li>    
  [/@s.iterator]
  [@s.iterator value="errorMessages"]    
    <li class="error">[@s.property escape="false" /]</li>    
  [/@s.iterator]
  </ul>
</section>
