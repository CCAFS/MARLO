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
        [#-- Request Info is filled by marloInstitutions.js --]
        <div class="requestInfo"></div>
        <div class="form-group">
          [@customForm.textArea name="marloRequestInstitution.justification" required=true className="limitWords-30" /]
        </div>
        <hr />
        <div class="form-group">
          [@customForm.checkBoxFlat id="sendEmail" name="marloRequestInstitution.sendEmail" label="Tick to send a justification email" value="true" checked=false cssClass="sendEmailInput"/]
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-danger rejectButton"> <span class="glyphicon glyphicon-remove"></span> Reject</button>
      </div>
    </div>
  </div>
</div>

[#-- Reject Country Office request MODAL --]
<div class="modal fade" id="rejectOfficeRequest" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="loading" style="display:none"></div>
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Reject selected Country office(s)</h4>
      </div>
      <div class="modal-body">
        <div class="requestInfo">
        </div>
        <div class="form-group">
          [@customForm.textArea name="marloRequestInstitution.justification" required=true className="modificationJustification limitWords-30" /]
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button> 
        <a class="btn btn-danger btn-sm rejectOfficesRequest" href="#">
          <span class="glyphicon glyphicon-remove"></span> Reject selected
        </a>
        
      </div>
    </div>
  </div>
</div>