[#ftl] 
[#-- All institutions MODAL --]
<div class="modal fade" id="allInstitutions" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">All Institutions & location offices</h4>
      </div>
      <div class="modal-body">
        [#-- All institutions table --]
        <table id="allInstitutionsTable" class="display table table-striped table-hover" width="100%">
          <thead>
            <tr>
                <th>ID</th>
                <th>Acronym</th>
                <th>Name</th>
                <th>Type</th> 
            </tr>
          </thead> 
        </table>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
[#--  Funding Source Popup JS --]
[#assign customJS =  [ "${baseUrl}/global/js/allInstitutionsPopup.js" ]  + customJS/]
  
