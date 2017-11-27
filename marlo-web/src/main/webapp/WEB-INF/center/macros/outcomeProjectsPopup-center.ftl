[#ftl]

<link rel="stylesheet" href="${baseUrl}/global/css/outcomeProjectsPopup.css">

<!-- Modal -->
<div class="modal fade" id="outcomeProjectsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Modal title</h4>
      </div>
      <div class="modal-body">
        [#-- Loading --]
        <div class="loading"></div>
        <ul class="projectsList">
          
        </ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button> 
      </div>
    </div>
  </div>
</div>

[#--  Outcome Projects Popup JS --]
[#assign customJS =  [ "${baseUrl}/global/js/outcomeProjectsPopup.js" ]  + customJS/]
  
