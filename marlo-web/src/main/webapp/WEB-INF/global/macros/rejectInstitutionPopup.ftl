[#ftl]
[#-- Reject the request MODAL --]
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="loading" style="display:none"></div>
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Reject the request</h4>
      </div>
      <div class="modal-body">
        <div class="requestInfo">
        </div>
        <div class="form-group">
          [@customForm.textArea name="marloRequestInstitution.justification" required=true className="limitWords-30" /]
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-danger rejectButton"> <span class="glyphicon glyphicon-remove"></span> Reject</button>
      </div>
    </div>
  </div>
</div>