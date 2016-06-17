[#ftl]
[#assign items= [
  { 'slug': 'management',       'name': 'Program Management', 'action': 'management',       'active': true },
  { 'slug': 'regionalMapping',  'name': 'Regional Mapping',   'action': 'regionalMapping',  'active': true },
  { 'slug': 'ppaPartners',      'name': 'PPA Partners',       'action': 'ppaPartners',      'active': true },
  { 'slug': 'siteIntegration',  'name': 'Site Integration',   'action': 'siteIntegration',  'active': true },
  { 'slug': 'locations',        'name': 'Locations',          'action': 'locations',        'active': true }
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>CRP Admin</p>
      <ul>
        [#list items as item]
        
        [#if item.name =='Regional Mapping' ]
         [#if action.hasProgramnsRegions() ]
        <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="${item.action}.do">${item.name}</a>
        [/#if]
        [#else]
        <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="${item.action}.do">${item.name}</a>
        [/#if]
        
          
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>