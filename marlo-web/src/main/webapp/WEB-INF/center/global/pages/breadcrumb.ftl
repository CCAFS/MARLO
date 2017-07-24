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
    
    [#-- Users Online --]
    <span id="usersOnline" class="""> <span>0</span> users online on this section </span>
    
    [#-- Channel name --]
    <span id="currentSectionString" style="display:none">${(currentSectionString)!'none'}</span>

    [#-- Mouse Pointer Template --]
    <span id="mouse-template" style="display:none;position:absolute;top:0;left:0;">
     | <small style="vertical-align: top;">{sessionID}</small> 
    </span>
    
    [#-- User Badge Template --]
    <span id="user-badge-template" class="user-badge" style="display:none">{}</span>
    
  </div>
  [/#if]
</ol>
[/#if]