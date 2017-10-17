[#ftl]
<!-- Modal -->
<div class="modal fade" id="addProjectsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      [@s.form action='${(centerSession)!}/addNewProject' method="GET"]
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Add New Project</h4>
      </div>
      <div class="modal-body">
        <div class="loading" style="display:none"></div>
        [#-- Hiden inputs --]
        <input type="hidden" name="programID" value="${selectedProgram.id?c}" />
       
        <h5 class="note">Do you want automatic setup the new project with external information? select what information you want to synchronize:</h5>
       
        <div class="form-group row">
          <div class="col-md-6">
          <p>[@customForm.radioFlat id="radio-manually" name="syncTypeID" label="Set-Up Information Manually" value="-1" checked=true cssClass="radioSyncType" /]</p>
          [#if syncTypes??]
            [#list syncTypes as syncType]
              <p>[@customForm.radioFlat id="radio-${syncType.id}" name="syncTypeID" label=(syncType.syncName) value=(syncType.id) checked=false cssClass="radioSyncType requiredCode" /]</p>
            [/#list]
          [/#if]
          </div>
          <div class="col-md-6 syncCodeBlock" style="display:none"> 
            [@customForm.input name="syncCode" i18nkey="Please enter an existing project/agreement code" required=true /]
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <span class="btn btn-success addProjectButton">Add Project </span>
        [@s.submit type="button" name="submit" cssClass="btn btn-success hide"] [/@s.submit]
      </div>
      [/@s.form] 
    </div>
  </div>
</div>

[#--  Outcome Projects Popup JS --]
[#assign customJS =  [ "${baseUrlMedia}/js/monitoring/projects/addProjectsPopup.js" ]  + customJS/]
  
