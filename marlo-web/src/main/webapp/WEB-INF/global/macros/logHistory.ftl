[#ftl] 
[#macro logList list ] 
[#if list?has_content]
  <div id="log-history" class="borderBox">
    <h1 class="simpleTitle"> [@s.text name="logHistory.title" /] <span>[@s.text name="logHistory.subTitle" /]</span></h1> 
    <table class="log-table">
      <thead>
        <tr>
          <th class="type">&nbsp;</th>
          <th class="date">[@s.text name="logHistory.date" /]</th>
          <th class="person">[@s.text name="logHistory.person" /]</th>
          <th class="justification">[@s.text name="logHistory.justification" /]</th>
        </tr>
      </thead>
      <tbody>
        [#list list as log]
        <tr>
          <td class="type"><span class="logType ${log.action}" title="${log.action?capitalize}">&nbsp;</span></td>
          <td class="date">${log.date?datetime}</td>
          <td class="person">${log.user.composedName}</td>
          <td class="justification">${log.justification}</td>
        </tr>
        [/#list]
      </tbody>
    </table>
    <br />
    <div class="legend">
      <div class="action"><span class="logType insert">&nbsp;</span> [@s.text name="logHistory.action.insert" /]</div>
      <div class="action"><span class="logType update">&nbsp;</span> [@s.text name="logHistory.action.update" /]</div>
      <div class="action"><span class="logType delete">&nbsp;</span> [@s.text name="logHistory.action.delete" /]</div>
    </div>
  </div>
[/#if]
[/#macro]