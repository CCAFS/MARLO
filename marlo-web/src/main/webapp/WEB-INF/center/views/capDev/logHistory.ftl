[#ftl] 
[#macro logList list itemName1 itemName2 itemName3 itemId1 itemId2 itemId3] 
[#if list?has_content]
  <div id="log-history" class="" title="${(title)!} History" style="display:none">
    <h3 class="simpleTitle"> [@s.text name="logHistory.title" /] <span>[@s.text name="logHistory.subTitle" /]</span></h3> 
    <table class="table table-striped">
      <thead>
        <tr>
          <th class="type">&nbsp;</th>
          <th class="date">[@s.text name="logHistory.date" /]</th>
          <th class="person">[@s.text name="logHistory.person" /]</th>
          <th class="justification">[@s.text name="logHistory.justification" /]</th>
          <th class="view text-center">[@s.text name="logHistory.viewHistory" /]</th>
        </tr>
      </thead>
      <tbody>
        [#list list as log]
        <tr>
          <td class="type"><span class="logType ${log.action?lower_case}" title="${log.action?capitalize}">&nbsp;</span></td>
          <td class="date"> 
            ${log.createdDate?datetime} 
            [#if log_index == 0]<!--span class="label label-primary">Current</span--> 
              [#if canEdit]<a id="cancelButton" class="btn btn-danger btn-xs" href="#" role="button">[@s.text name="form.buttons.recover" /] this version</a>[/#if]
            [/#if]
          </td>
          <td class="person">${log.user.composedName?html}</td>
          <td class="justification">${(log.modificationJustification)!'Prefilled if available'}</td>
          <td class="view text-center">
            <a href="[@s.url][@s.param name=itemName1 value=itemId1 /][@s.param name=itemName2 value=itemId2 /][@s.param name=itemName3 value=itemId3 /][@s.param name="transactionId"]${log.transactionId}[/@s.param][/@s.url]">
             <span class="glyphicon glyphicon-eye-open"></span> View 
            </a>
          </td>
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