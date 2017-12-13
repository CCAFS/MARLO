[#ftl]
[#macro relationsMacro element ]
  [#local className = (element.class.name)?split('.')?last/]
  [#local composedID = "${className}-${element.id}"]
  [#local deliverables = (action.getDeliverableRelationsImpact(element.id, element.class.name))![] /]
  [#local projects = (action.getProjectRelationsImpact(element.id, element.class.name))![] /]
  [#if (deliverables?has_content) ||  (projects?has_content)]
  <div id="${composedID}" class="form-group elementRelations ${className}">
    [#if deliverables?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-deliverables-${composedID}">
        <span class="icon-20 deliverable"></span> <strong>${deliverables?size}</strong> Deliverable(s)
      </button>
      
      [#-- Modal --]
      <div class="modal fade" id="modal-deliverables-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">Deliverables that are contributing</h4>
            </div>
            <div class="modal-body">
              Deliverable(s) ${composedID}
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]
    
    [#if projects?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-projects-${composedID}">
        <span class="icon-20 project"></span> <strong>${projects?size}</strong> Project(s)
      </button>
      
      [#-- Modal --]
      <div class="modal fade" id="modal-projects-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">Projects that are contributing</h4>
            </div>
            <div class="modal-body">
              Project(s) ${composedID}
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]
  </div>
  [/#if]
[/#macro]