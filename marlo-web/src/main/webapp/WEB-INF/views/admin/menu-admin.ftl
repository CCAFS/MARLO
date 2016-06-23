[#ftl]
[#assign items= [
  { 'slug': 'management',       'name': 'Program Management', 'action': 'management',       'active': true },
  { 'slug': 'regionalMapping',  'name': 'Regional Mapping',   'action': 'regionalMapping',  'active': action.hasProgramnsRegions() },
  { 'slug': 'ppaPartners',      'name': 'PPA Partners',       'action': 'ppaPartners',      'active': true },
  { 'slug': 'siteIntegration',  'name': 'Site Integration',   'action': 'siteIntegration',  'active': true },
  { 'slug': 'locations',        'name': 'Locations',          'action': 'locations',        'active': true }
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>CRP Admin</p>
      <ul>
        [#list items as item]
          <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              ${item.name}
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>