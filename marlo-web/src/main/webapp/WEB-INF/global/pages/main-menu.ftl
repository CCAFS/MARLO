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
        
        [#-- Admin --]
        <li [#if currentSection?? && currentSection == "admin"] class="currentSection" [/#if]>
          <a href="[@s.url namespace="/${crpSession}" action='adminManagement'/]">[@s.text name="menu.admin" /]
          </a>
        </li>
        
        [#-- PRE-Planning section 
        <li [#if currentSection?? && currentSection == "preplanning"] class="currentSection" [/#if]>
          <a href="[@s.url namespace="/pre-planning" action='intro'/]">[@s.text name="menu.preplanning" /]
          </a>
        </li>
        --]
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
        <p class="userId" style="display:none">${(currentUser.id)!}</p> 
        <p class="name">${(currentUser.firstName)!} ${(currentUser.lastName)!}</p>  
        <p class="institution">${(currentUser.email)!}</p>
        <p class="roles">${(securityContext.roles)!}
         [#if currentUser.liaisonInstitution??][#list currentUser.liaisonInstitution as liaison]${(liaison.acronym)!}[#if liaison_has_next], [/#if][/#list] [/#if]
        </p>
      </div>
  	[/#if]
  </div>
</nav> 

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
