[#ftl] 
<div id="discardChanges" class="modal fade">
  <div class="modal-dialog modal-sm" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title">Discard Changes ?</h4>
      </div>
      <div class="modal-body">
        <p>You have unsaved changes which will be lost if you exit this page.</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-success" onclick="acceptChanges()">Accept Changes</button>
        <button type="button" class="btn btn-danger" onclick="cancel()">Cancel</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->