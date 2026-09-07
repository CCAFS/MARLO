[#ftl]
[#--
  Relations popup for a feedback commentable field, following the same shape as
  /WEB-INF/crp/macros/relationsPopupMacro.ftl: a counter button that opens a modal listing where the element is
  being used. Here "used" means comments left on that field, grouped by project and phase, and always scoped to
  the current global unit by the action's usage query.

  Button and modal are separate macros because the button rides inside .blockTitle -- the accordion header -- while
  the modal must stay outside it: a Bootstrap modal nested in the clickable header would open and close with the
  block. Call feedbackFieldRelations when both can live in the same place.
--]
[#macro feedbackFieldRelations element labelText=true]
  [@feedbackFieldRelationsButton element=element labelText=labelText /]
  [@feedbackFieldRelationsModal element=element /]
[/#macro]

[#-- The counter button. Placed on the title row, so it carries no vertical margin of its own. --]
[#macro feedbackFieldRelationsButton element labelText=true]
  [#local fieldId = (element.id)!-1 /]
  [#if fieldId != -1]
    [#local total = action.getFieldUsageTotal(fieldId) /]
    [#if total > 0]
      [#local composedID = "feedbackField-${fieldId}" /]
      <div class="elementRelations FeedbackQACommentableFields">
        <button type="button" class="btn btn-default btn-xs" data-toggle="modal"
                data-target="#modal-feedbackComments-${composedID}">
          <span class="glyphicon glyphicon-comment" aria-hidden="true"></span>
          <strong>${total}</strong> [#if labelText][@s.text name="feedbackManagement.usage.comments" /][/#if]
        </button>
      </div>
    [/#if]
  [/#if]
[/#macro]

[#-- The modal the button opens. --]
[#macro feedbackFieldRelationsModal element]
  [#local fieldId = (element.id)!-1 /]
  [#if fieldId != -1]
    [#local total = action.getFieldUsageTotal(fieldId) /]
    [#if total > 0]
      [#local usage = action.getFieldUsage(fieldId) /]
      [#local composedID = "feedbackField-${fieldId}" /]
      [#local elementTitle = (element.fieldName)!((element.fieldDescription)!'') /]
      [#-- The section belongs to the field, so it is the same on every row. It is shown as a column anyway so the
           DataTables search box can match on it, and so the table reads on its own once detached from the block. --]
      [#local sectionLabel = (element.sectionDescription)!((element.sectionName)!'-') /]

      [#-- feedbackUsageModal is the scope hook for the DataTables styling in css/admin/feedbackManagement.css --]
      <div class="modal fade feedbackUsageModal" id="modal-feedbackComments-${composedID}" tabindex="-1"
           role="dialog" aria-labelledby="label-${composedID}">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
              <h4 class="modal-title" id="label-${composedID}">
                [@s.text name="feedbackManagement.usage.title" /]
                <br />
                [#-- The section is an attribute of the field, so it belongs here and not as a column that would
                     repeat the same value on every row. --]
                <small><strong>${sectionLabel}</strong> &middot; ${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- feedbackUsageList is the hook feedbackManagement.js uses to turn this into a DataTable --]
              [#-- A unique id keeps DataTables from generating duplicate _filter/_length ids across the modals --]
              <table id="feedbackUsageList-${fieldId}" class="table table-striped table-hover feedbackUsageList"
                     width="100%">
                <thead>
                  <tr>
                    <th>[@s.text name="feedbackManagement.usage.project" /]</th>
                    <th>[@s.text name="feedbackManagement.usage.phase" /]</th>
                    <th class="text-center">[@s.text name="feedbackManagement.usage.comments" /]</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list usage as row]
                    <tr>
                      <td>${(row.projectLabel)!'-'}</td>
                      <td>${(row.phaseLabel)!'-'}</td>
                      <td class="text-center">${(row.total)!0}</td>
                      <td>
                        [#if (row.link)?has_content]
                          <a href="${row.link}" target="_blank" rel="noopener">
                            <span class="glyphicon glyphicon-new-window"></span>
                          </a>
                        [/#if]
                      </td>
                    </tr>
                  [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">
                [@s.text name="feedbackManagement.usage.close" /]
              </button>
            </div>
          </div>
        </div>
      </div>
    [/#if]
  [/#if]
[/#macro]
