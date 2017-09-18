[#ftl]
[#if action.canAccessSuperAdmin()]
  [#assign superAdminMenu =[
     { 'slug': 'superadmin',     'name': 'menu.superadmin.admin',    'namespace': '/centerAdmin',     'action': 'coordination', 'visible': action.canAccessSuperAdmin(), 'active': true }
  ]/]
  <div id="superadminBlock">
    <div class="container">
      <ul>
        [#list superAdminMenu as item]
          [#if item.visible]
          <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url namespace=item.namespace action=centerSession+"/"+item.action ][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if][@s.text name=item.name ][@s.param]${(centerSession?upper_case)!'CRP'}[/@s.param] [/@s.text]
            </a>
          </li>
          [/#if]
        [/#list]
         <li class="pull-left"><span class="glyphicon glyphicon-th-list"></span> MARLO Admin Menu</li>
        <div class="clearfix"></div>
      </ul>
    </div>
  </div>
[/#if]