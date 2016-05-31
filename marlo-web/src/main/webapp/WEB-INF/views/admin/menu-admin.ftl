[#ftl]
[#assign items= [
  { 'slug': 'management',       'name': 'Program Management', 'action': 'management.do' },
  { 'slug': 'leaders',          'name': 'Leaders',            'action': 'leaders.do' },
  { 'slug': 'siteIntegration',  'name': 'Site Integration' },
  { 'slug': 'subIdos',          'name': 'Sub IDOs' }
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>CRP Admin</p>
      <ul>
        [#list items as item]
          <li class="[#if item.slug == currentStage]currentSection[/#if] ${(item.action??)?string('enabled','disabled')}">
            <a href="${(item.action)!'#'}">${item.name}</a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>