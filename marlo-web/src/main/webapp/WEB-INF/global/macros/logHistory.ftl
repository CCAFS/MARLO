[#ftl] 
[#macro logList list itemId] 
[#if list?has_content]
  <div id="log-history" class="" style="display:none">
    <h3 class="simpleTitle"> [@s.text name="logHistory.title" /] <span>[@s.text name="logHistory.subTitle" /]</span></h3> 
    <table class="log-table">
      <thead>
        <tr>
          <th class="type">&nbsp;</th>
          <th class="date">[@s.text name="logHistory.date" /]</th>
          <th class="person">[@s.text name="logHistory.person" /]</th>
          <th class="justification">[@s.text name="logHistory.justification" /]</th>
          <th class="view">[@s.text name="logHistory.viewHistory" /]</th>
        </tr>
      </thead>
      <tbody>
        [#list list as log]
        <tr>
          <td class="type"><span class="logType ${log.action}" title="${log.action?capitalize}">&nbsp;</span></td>
          <td class="date">${log.createdDate?datetime}</td>
          <td class="person">${log.userId}</td>
          <td class="justification">${(log.justification)!'Empty'}</td>
          <td class="view"><a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="transactionId" value=log.transactionId/][@s.param name="edit" value="true"/][/@s.url]">View</a></td>
        </tr>
        [/#list]
      </tbody>
    </table>
    <br />
    [#-- 
    <div class="legend">
      <div class="action"><span class="logType insert">&nbsp;</span> [@s.text name="logHistory.action.insert" /]</div>
      <div class="action"><span class="logType update">&nbsp;</span> [@s.text name="logHistory.action.update" /]</div>
      <div class="action"><span class="logType delete">&nbsp;</span> [@s.text name="logHistory.action.delete" /]</div>
    </div>
    --]
  </div>
[/#if]
[/#macro]