[#ftl]
[#assign items= [
  { 'slug': 'marloBoard',   'name': 'menu.superadmin.board', 'action': 'marloBoard',   'active': true },
  { 'slug': 'slos',   'name': 'menu.superadmin.slos', 'action': 'slos',   'active': false },
  { 'slug': 'idos',   'name': 'menu.superadmin.idos', 'action': 'idos',   'active': false }
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>[@s.text name="menu.superadmin.title"/]</p>
      <ul>
        [#list items as item]
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