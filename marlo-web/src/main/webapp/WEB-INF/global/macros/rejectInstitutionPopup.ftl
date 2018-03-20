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
          [@customForm.checkBoxFlat id="sendNotification" name="" label="Tick to send a justification email" value="true" checked=true cssClass="sendEmailInput"/]
        </div>
        <div class="form-group">
          [@customForm.textArea name="marloRequestInstitution.justification" required=true className="limitWords-30" /]
        </div>
        <hr />
        <ul>
          <li><b>Already exists: </b><i>"InstitutionRequested"</i> already exists in MARLO as <i>"MARLO Institution"</i></li>
          <li><b>Already exists as PPA: </b><i>"InstitutionRequested"</i> is available in MARLO as Managing/PPA Partner. Please contact your PMU or <i>"ManagementLiaison Role"</i> in order to add it to the list of project partners</li>
          <li><b>Not legal: </b>This is a program/project, not an institution. We suggest to use <i>"MARLOInstitution"</i> instead.</li>
          <li><b>Sub-departmnet: </b>The <i>"InstitutionRequested"</i> is a part of the <i>"MARLOInstitution"</i>. Therefore, we kindly suggest you to include <i>"MARLOInstitution"</i> as sub-departmnet of <i>"MARLOInstitution"</i>.</li>
        </ul> 
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
        [#-- Request Info is filled by marloInstitutions.js --]
        <div class="requestInfo"></div>
        <hr />
        <div class="form-group">
          [@customForm.checkBoxFlat id="sendNotification-offices" name="" label="Tick to send the justification email" value="true" checked=true cssClass="sendEmailInput"/]
        </div>
        <div class="form-group">
          [@customForm.textArea name="marloRequestInstitution.justification" required=true className="modificationJustification limitWords-30" /]
        </div>
        
        <hr />
        <ul>
          <li><b>Message 1: </b><i>"CountryRequested"</i> is already part of <i>"InstitutionRequested"</i></li>
        </ul> 
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