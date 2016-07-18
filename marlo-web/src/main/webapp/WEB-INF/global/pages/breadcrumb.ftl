[#ftl]
[#if breadCrumb??]
<ol class="breadcrumb">
  [#if breadCrumb?has_content] 
    [#list breadCrumb as item]
      <li class="[#if !item_has_next]active[/#if]">
        [#if item.action?has_content]
          [#if item.param?exists]
            <a href="${baseUrl}/${item.nameSpace}/${item.action}.do?${item.param}" >[@s.text name="breadCrumb.menu.${item.label}" /]</a>
          [#else]
            <a href="${baseUrl}/${item.nameSpace}/${item.action}.do" >[@s.text name="breadCrumb.menu.${item.label}" /]</a>
          [/#if]
        [#else]
          [@s.text name="breadCrumb.menu.${item.label}" /]</a>
        [/#if]
      </li> 
    [/#list]
  [/#if]
  [#if action.getUsersOnline()??]
  <div class="usersInfo">
    [#-- 
      [#assign users = action.getUsersOnline()]
      <button type="button" class="btn btn-xs btn-default" title="[#list users as us]${(us.user.firstName)!} ${(us.user.lastName)!} - ${(us.section)!} <br/> [/#list]">Users Online : ${online}</button>
    --]
    
    <p id="usersOnline"> Users Online: <span>0</span></p>
    
    <span id="currentSectionString" style="display:none">${(currentSectionString)!'none'}</span>

    <span id="mouse-template" style="display:none;position:absolute;top:0;left:0;">
     | <small style="font-size: 0.8em;vertical-align: top;">{sessionID}</small> 
    </span>
    
  </div>
  [/#if]
</ol>
[/#if]