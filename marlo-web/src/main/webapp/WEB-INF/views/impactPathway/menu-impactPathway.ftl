[#ftl]
[#assign items= [
  { 'slug': 'outcomes',           'name': 'Outcomes',               'action': 'outcomes',         'active': true },
  { 'slug': 'clusterActivities',  'name': 'Cluster Of Activities',  'action': 'clusterActivities',  'active': true }
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>Impact Pathway</p>
      <ul>
        [#list items as item]
          <li class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              ${item.name}
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>