[#ftl] 
<div id="discardChanges" class="modal fade">
  <div class="modal-dialog modal-sm" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title">Save Changes ?</h4>
      </div>
      <div class="modal-body">
        <p>MARLO saved a temporary version of your work. Please confirm your changes by clicking on the save button.</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-success" onclick="acceptChanges()">Save</button>
        <button type="button" class="btn btn-danger" onclick="cancel()">Continue</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->