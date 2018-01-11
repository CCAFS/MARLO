[#ftl]
[#if action.canAccessSuperAdmin() || action.canAcessCrpAdmin()]
  [#assign superAdminMenu =[
     { 'slug': 'superadmin',     'name': 'menu.superadmin',    'namespace': '/superadmin',     'action': 'marloSLOs', 'visible': action.canAccessSuperAdmin(), 'active': true }
     
  ]/]
  
  [#if ((globalUnitType == 2)!false)]
    [#assign superAdminMenu = superAdminMenu + [
      { 'slug': 'admin',     'name': 'menu.admin',    'namespace': '/centerAdmin',     'action': '${(centerSession)!}/coordination', 'icon': 'cog',   'visible': action.canAccessSuperAdmin(),  'active': true }
    ]/]
  [#else]
    [#assign superAdminMenu = superAdminMenu + [
      { 'slug': 'admin',     'name': 'menu.admin',    'namespace': '/admin',            'action': '${(crpSession)!}/management',      'icon': 'cog',  'visible': action.canAcessCrpAdmin(),     'active': true }
    ]/]
  [/#if]
  <div id="superadminBlock">
    <div class="container">
      <ul>
        [#list superAdminMenu as item]
          [#if item.visible]
          <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url namespace=item.namespace action=item.action ][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
              [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if][@s.text name=item.name ][@s.param]${(crpSession)!'CRP'}[/@s.param] [/@s.text]
            </a>
          </li>
          [/#if]
        [/#list]
        [#-- Global units --]
        [#if action.canAccessSuperAdmin()]
        <li class="[#if currentSection?? && currentSection != 'superadmin' ]currentSection[/#if]">
          <a href="[@s.url namespace="/" action="${(crpSession)!}/crpDashboard" ][@s.param name="edit" value="true"/][/@s.url]">
            <span class="glyphicon glyphicon-chevron-down"></span> [#if centerSession??]${centerSession}[#else]${(currentCrp.acronym)!}[/#if]
          </a>
          <ul class="subMenu">
          [#-- CRPs --]
          [#if crpList?has_content]
            <li text-align:center>  CRPs  </li>
            [#list crpList as globalUnit]
              [#if globalUnit.login]
              <li class="[#if crpSession?? && crpSession == globalUnit.acronym ]currentSection[/#if]" >
                <a href="[@s.url namespace="/" action="${globalUnit.acronym}/crpDashboard" ][@s.param name="edit" value="true"/][/@s.url]" title="">${globalUnit.acronym}</a>
              </li>
              [/#if]
            [/#list]              
          [/#if]
          [#-- Centers --]
          [#if centersList?has_content]
            <li text-align:center>   Centers  </li>
            [#list centersList as globalUnit]
              [#if globalUnit.login]
              <li class="[#if centerSession?? && centerSession == globalUnit.acronym ]currentSection[/#if]">
                <a href="[@s.url namespace="/" action="${globalUnit.acronym}/centerDashboard" ][@s.param name="edit" value="true"/][/@s.url]">${globalUnit.acronym}</a>
              </li>
              [/#if]
            [/#list]
          [/#if]
          [#-- Platforms --]
          [#if platformsList?has_content]
            <li text-align:center>   Platforms   </li>
            [#list platformsList as globalUnit]
              [#if globalUnit.login]
              <li class="[#if centerSession?? && centerSession == globalUnit.acronym ]currentSection[/#if]">
                <a href="[@s.url namespace="/" action="${globalUnit.acronym}/crpDashboard" ][@s.param name="edit" value="true"/][/@s.url]">${globalUnit.acronym}</a>
              </li>
              [/#if]
            [/#list]
          [/#if]
          </ul>
        </li>
         [/#if]
         <li class="pull-left"><span class="glyphicon glyphicon-th-list"></span> MARLO Admin Menu</li>
        <div class="clearfix"></div>
      </ul>
    </div>
  </div>
[/#if]