[#ftl]
[#if logged && action.isVisibleTop()]
  [#assign superAdminMenu =[
     { 'slug': 'superadmin',     'name': 'menu.superadmin',    'namespace': '/superadmin',     'action': 'marloSLOs', 'visible': action.canAccessSuperAdmin(), 'active': true }
     
  ]/]
  
  [#if ((globalUnitType == 4)!false)]
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
        [#if action.isVisibleTopGUList()]
        
        [#-- Global units --]
        <li class="[#if currentSection?? && currentSection != 'superadmin' ]currentSection[/#if]">
          <a href="[@s.url namespace="/" action="${(crpSession)!}/crpDashboard" ][@s.param name="edit" value="true"/][/@s.url]">
            <span class="glyphicon glyphicon-chevron-down"></span> ${(currentGlobalUnit.acronym)!'null'}
          </a>
          <ul class="subMenu">
           
            [#if listGlobalUnitTypesUser??]
              [#list listGlobalUnitTypesUser as globalUnitType]
                <li text-align:center>  ${globalUnitType.name}  </li>
                [#list globalUnitType.globalUnitsList as globalUnit]
                  [#if globalUnit.login]
                    [#assign guAction = "crpDashboard" /]
                    [#if globalUnitType.id == 2][#assign guAction = "centerDashboard" /][/#if]
                    <li class="[#if crpSession?? && crpSession == globalUnit.acronym ]currentSection[/#if]" >
                      <a href="[@s.url namespace="/" action="${globalUnit.acronym}/${guAction}" ][@s.param name="edit" value="true"/][/@s.url]" title="">${globalUnit.acronym}</a>
                    </li>
                  [/#if]
                [/#list]
              [/#list]
            [/#if]
          
          </ul>
        </li>
        [/#if]
        <div class="clearfix"></div>
      </ul>
    </div>
  </div>
[/#if]