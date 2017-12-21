[#ftl]
[#macro relationsMacro element ]
  [#local className = (element.class.name)?split('.')?last/]
  [#local composedID = "${className}-${element.id}"]
  [#local deliverablesProject = (action.getDeliverableRelationsProject(element.id, element.class.name,(element.project.id)!-1))! /]
  [#local deliverables = (action.getDeliverableRelationsImpact(element.id, element.class.name))!deliverablesProject /]
  [#local projects = (action.getProjectRelationsImpact(element.id, element.class.name))! /]
  
  [#local elementTitle = (element.keyOutput)!((element.title)!((element.description)!'')) /]
  [#if (deliverables?has_content) ||  (projects?has_content)]
  <div id="${composedID}" class="form-group elementRelations ${className}">
    [#if projects?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-projects-${composedID}">
        <span class="icon-20 project"></span> <strong>${projects?size}</strong> Project(s)
      </button>
      
      [#-- Modal --]
      <div class="modal fade" id="modal-projects-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">
              
                Projects that are contributing to this [@s.text name="global.${className}" /] 
                <br />
                <small>${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- Projects table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
                    <th id="projectLeader" >[@s.text name="projectsList.projectLeader" /]</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list projects as p]
                    [#local projectUrl][@s.url namespace="/projects" action="${(crpSession)!}/description"][@s.param name='projectID']${p.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url][/#local]
                    <tr>
                      <th scope="row">P${p.id}</th>
                      <td>${(p.projectInfo.title)!'Untitled'}</td>
                      <td class="">[#if p.getLeader(action.getActualPhase())?has_content]${(p.getLeader(action.getActualPhase()).institution.acronym)!p.getLeader(action.getActualPhase()).institution.name}[#else][@s.text name="projectsList.title.none" /][/#if]</td>
                      <td> <a href="${projectUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                    </tr>
                  [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]
    
    [#if deliverables?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-deliverables-${composedID}">
        <span class="icon-20 deliverable"></span> <strong>${deliverables?size}</strong> Deliverable(s)
      </button>
      
      [#-- Modal --]
      <div class="modal fade" id="modal-deliverables-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">
               
                 [#if className=="ProjectBudget"]
                Deliverables funded by this funding source in this project
                [#else]
                  Deliverables that are contributing to this [@s.text name="global.${className}" /] 
                [/#if]
              
                <br />
                <small>${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body"> 
              [#-- Deliverables table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
                    <th>Project ID</th>
                    <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list deliverables as d]
                    [#local deliverableUrl][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${d.id?c}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url][/#local]
                    <tr>
                      <th scope="row">D${d.id}</th>
                      <td class="col-md-6">${(d.deliverableInfo.title)!'Untitled'}</td>
                      <td>P${(d.project.id)!'none'}</td>
                      <td>${(d.deliverableInfo.deliverableType.name?capitalize)!'none'}</td>
                      <td> <a href="${deliverableUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                    </tr>
                    [/#list]
                </tbody>
              </table>
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