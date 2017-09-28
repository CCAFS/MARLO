[#ftl]
[#assign objs= [
  { 'slug': 'capdevIntervention',      'name': 'capdev.menu.hrefCapdev',           'action': 'detailCapdev',           'active': true  },
  { 'slug': 'capdevDescription',           'name': 'capdev.menu.hrefDescription',      'action': 'descriptionCapdev',           'active': true  },
  { 'slug': 'supportingDocuments',         'name': 'capdev.menu.hrefSupportingDocs',    'action': 'supportingDocs',           'active': true  }
  
]/]





<nav id="secondaryMenu" class="">
  <p>Capdev Menu</p>
   <ul>
    [#list objs as item]
      <li>
        <ul> 
              <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] capdevmenu-links">
                <a href="[@s.url action="${centerSession}/${item.action}"][@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='edit' value="true" /][/@s.url]"  class="action-${centerSession}/${item.action}">
                  [@s.text name=item.name/]
                </a>
              </li>
        </ul>
      </li>
    [/#list]
  </ul> 
</nav>