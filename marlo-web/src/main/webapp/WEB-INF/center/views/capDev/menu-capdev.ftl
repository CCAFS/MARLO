[#ftl]
[#assign objs= [
  { 'slug': 'New Capdev Intervention',      'name': 'capdev.menu.hrefCapdev',           'action': 'detailCapdev',           'active': true  },
  { 'slug': 'Capdev Description',           'name': 'capdev.menu.hrefDescription',      'action': 'descriptionCapdev',           'active': true  },
  { 'slug': 'Supporting Documents',         'name': 'capdev.menu.hrefSuppotingDocs',    'action': 'supportingDocs',           'active': true  }
  
]/]





<nav id="secondaryMenu" class="">
  <p>Capdev Menu</p>
   <ul>
    [#list objs as item]
      <li>
        <ul>
              <li id="menu-${item.action}" ">
                <a href="[@s.url action="${centerSession}/${item.action}"][@s.param name='capdevID']${capdevID}[/@s.param][/@s.url]"  class="action-${centerSession}/${item.action}">
                  [@s.text name=item.name/]
                </a>
              </li>
        </ul>
      </li>
    [/#list]
  </ul> 
</nav>