[#ftl]
[#if action.canAccessSuperAdmin()]
  [#assign superAdminMenu =[
     { 'slug': 'superadmin',     'name': 'menu.superadmin',    'namespace': '/superadmin',     'action': 'marloSLOs', 'visible': action.canAccessSuperAdmin(), 'active': true }
  ]/]
  <div id="superadminBlock">
    <div class="container">
      <ul>
        [#list superAdminMenu as item]
          [#if item.visible]
          <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url namespace=item.namespace action=item.action ][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if][@s.text name=item.name ][@s.param]${(crpSession?upper_case)!'CRP'}[/@s.param] [/@s.text]
            </a>
          </li>
          [/#if]
        [/#list]
        <li class="[#if currentSection?? && currentSection != 'superadmin' ]currentSection[/#if]">
          <a href="[@s.url namespace="/" action="${(crpSession?lower_case)!}/dashboard" ][@s.param name="edit" value="true"/][/@s.url]">
            <span class="glyphicon glyphicon-chevron-down"></span> CRP (${(currentCrp.name)!})
          </a>
          <ul class="subMenu">
          [#if crpList?has_content]
            [#list crpList as crp]
              <li class="[#if crpSession?? && crpSession == crp.acronym?lower_case ]currentSection[/#if]">
                <a href="[@s.url namespace="/" action="${crp.acronym?lower_case}/dashboard" ][@s.param name="edit" value="true"/][/@s.url]">${crp.name}</a>
              </li>
            [/#list]
           [/#if]
          </ul>
         </li>
         <li class="pull-left"><span class="glyphicon glyphicon-th-list"></span> MARLO Admin Menu</li>
        <div class="clearfix"></div>
      </ul>
    </div>
  </div>
[/#if]