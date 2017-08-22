[#ftl]
[#if action.canAccessSuperAdmin() || action.canAcessCrpAdmin()]
  [#assign superAdminMenu =[
     { 'slug': 'superadmin',     'name': 'menu.superadmin',    'namespace': '/superadmin',     'action': 'marloSLOs', 'visible': action.canAccessSuperAdmin(), 'active': true },
     { 'slug': 'admin',          'name': 'menu.admin',         'namespace': '/admin',          'action': '${(crpSession)!}/management',    'icon': 'cog',      'visible': action.canAcessCrpAdmin(), 'active': true }
  ]/]
  <div id="superadminBlock">
    <div class="container">
      <ul>
        [#list superAdminMenu as item]
          [#if item.visible]
          <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url namespace=item.namespace action=item.action ][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if][@s.text name=item.name ][@s.param]${(crpSession)!'CRP'}[/@s.param] [/@s.text]
            </a>
          </li>
          [/#if]
        [/#list]
        [#-- CRPs List --]
        [#if action.canAccessSuperAdmin()]
        <li class="[#if currentSection?? && currentSection != 'superadmin' ]currentSection[/#if]">
          <a href="[@s.url namespace="/" action="${(crpSession?lower_case)!}/crpDashboard" ][@s.param name="edit" value="true"/][/@s.url]">
            <span class="glyphicon glyphicon-chevron-down"></span> CRP (${(currentCrp.acronym)!})
          </a>
          <ul class="subMenu">
          [#if crpList?has_content]
            [#list crpList as crp]
              [#if crp.login]
              <li class="[#if crpSession?? && crpSession == crp.acronym?lower_case ]currentSection[/#if]" >
                <a href="[@s.url namespace="/" action="${crp.acronym?lower_case}/crpDashboard" ][@s.param name="edit" value="true"/][/@s.url]" title="">${crp.acronym}</a>
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