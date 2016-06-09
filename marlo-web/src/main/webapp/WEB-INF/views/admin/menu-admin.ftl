[#ftl]
[#assign items= [
  { 'slug': 'management',       'name': 'Program Management', 'action': 'management',       'active': true },
  { 'slug': 'leaders',          'name': 'Leaders',            'action': 'leaders',          'active': true },
  { 'slug': 'siteIntegration',  'name': 'Site Integration',   'action': 'siteIntegration',  'active': true },
  { 'slug': 'subIdos',          'name': 'Sub IDOs',           'action': '#',                'active': false }
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>CRP Admin</p>
      <ul>
        [#list items as item]
          <li class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="${item.action}.do">${item.name}</a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>