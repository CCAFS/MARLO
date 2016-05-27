[#ftl]
[#assign items= [
  { 'slug': 'management',       'name': 'Program Management'},
  { 'slug': 'leaders',          'name': 'Leaders'},
  { 'slug': 'siteIntegration',  'name': 'Site Integration'},
  { 'slug': 'subIdos',          'name': 'Sub IDOs'}
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>CRP Admin</p>
      <ul>
        [#list items as item]
          <li class="[#if item.slug == currentStage]currentSection[/#if]"><a href="">${item.name}</a></li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>