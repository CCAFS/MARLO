[#ftl]
[#assign srfItems= [
  { 'slug': 'slos',   'name': 'menu.superadmin.slos', 'action': 'marloSLOs',   'active': true },
  { 'slug': 'crossCutting',   'name': 'menu.superadmin.crossCutting', 'action': 'marloCrossCutting',   'active': true },
  { 'slug': 'idos',   'name': 'menu.superadmin.idos', 'action': 'marloIDOs',   'active': true },
  { 'slug': 'siteIntegration',   'name': 'menu.superadmin.siteIntegration', 'action': 'marloSiteIntegration',   'active': true }
]/]

[#assign standards= [
  { 'slug': 'marloBoard',   'name': 'menu.superadmin.board', 'action': 'marloBoard',   'active': true },
  { 'slug': 'users',            'name': 'Partner Types', 'action': 'marloUsers',   'active': false },
  { 'slug': 'permissions',    'name': 'Locations', 'action': 'marloPermissions',   'active': false }
]/]

[#assign toolItems= [
  { 'slug': 'notifications',    'name': 'menu.superadmin.notifications', 'action': 'notifications',   'active': true },
  { 'slug': 'users',            'name': 'menu.superadmin.users', 'action': 'marloUsers',   'active': false },
  { 'slug': 'permissions',    'name': 'menu.superadmin.permissions', 'action': 'marloPermissions',   'active': false }
]/]

<nav id="secondaryMenu">
  <p>[@s.text name="menu.superadmin.srfMenuTitle"/]</p>
  <ul>
    <li>
      <ul>
        [#list srfItems as item]
          <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action=item.action ][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul>
  
  <p>Standards</p>
  <ul>
    <li>
      <ul>
        [#list standards as item]
          <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action=item.action ][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
  
  <p>[@s.text name="menu.superadmin.toolsMenuTitle"/]</p>
  <ul>
    <li>
      <ul>
        [#list toolItems as item]
          <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action=item.action ][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>



<div class="clearfix"></div>