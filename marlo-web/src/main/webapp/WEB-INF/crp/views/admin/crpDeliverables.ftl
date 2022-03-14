[#ftl]
[#assign title = "MARLO Deliverables" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/marloDeliverables.js?20201910" ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "crpDeliverables" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"deliverables", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#include "/WEB-INF/global/pages/generalMessages.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form id="bulkReplicationForm" action=actionName enctype="multipart/form-data" ]
        
        <h4 class="sectionTitle">[@s.text name="ManageDeliverables.title" /] </h4>
        
        
        <div class=" row borderBox">
          <div class="row form-group">
        
            <!--    
            <div class="col-md-4">
              [@customForm.select name="a" value="${(milestone.year)!-1}"  i18nkey="outcome.milestone.inputTargetYear" listName="milestoneYears"  required=true  className=" targetYear milestoneYear" editable=editableMilestone /]
            </div>
             -->
        
            <div class="col-md-12 " style="margin-bottom: 20px;">
              <!-- [@customForm.select name="Deliverable" value="name" label="Deliverable" listName="deliverables" header=true multiple=false
              required=true className="form-control" editable=editable || editStatus/] -->
              <label for="deliverableID">Deliverable:</label>
               <div class="form-group">
                <div class="selectList">   
                  <select name="deliverableID" id="deliverableID">
                    [#list deliverables as deliverable ]
                      <option value="${(deliverable.id)!}">D${(deliverable.id)!} (C${(deliverable.project.id)!}) - ${(deliverable.deliverableInfo.title)!}</option>
                    [/#list]  
                  </select>
                </div>
               </div>
              </div>
            </div>
        
           
            [#if action.canAccessSuperAdmin()]
          
            <div class="col-md-12 ma-bot">
              [@customForm.radioFlat id="anotherPhase" name="moveToSelection" label="Move to another phase" value="phase"
              checked=false cssClassLabel="radio-label-yes"/]
            </div>
            [/#if]
            <div id="anotherPhaseContent" class="row col-md-12 ma-bot">
        
              <div class="col-md-6">
                <label for="" class="title">
                  Current phase:
                </label>
                <p >${(actualPhase.name)!} ${(actualPhase.year)!}</p>
              </div>
        
              <div class="col-md-6 ">
              <!--  [@customForm.select name="phase" value="name" label="Phase" listName="phases" header=true  multiple=false required=true className="form-control" editable=editable || editStatus/] -->
              
               <label for="phaseID">Select phases:</label>
               <div class="form-group">
                <div class="selectList">   
                  <select name="phaseID" id="phaseID">
                    [#list phases as phase ]
                      <option value="${phase.id}" >${phase.name} - ${phase.year} </option>
                    [/#list]  
                  </select>
                </div>
               </div>
              </div>
        
            </div>
        
            <div class="col-md-12 ma-bot">
              [@customForm.radioFlat id="anotherProject" name="moveToSelection" label="Move to another cluster" value="project"
              checked=false cssClass="editable-no" cssClassLabel="radio-label-no"/]
            </div>
        
            <div id="anotherProjectContent" class="row col-md-12 ma-bot">
        
              <div class="col-md-6">
                <label for="" class="title">
                  Current cluster:
                </label>
                <p id="currentProject">P${(deliverables[0].project.id)!} - ${(deliverables[0].project.projectInfo.title)!}</p>
              </div>
        
              <div class="col-md-6">
                <!-- [@customForm.select name="Select cluster" value="name" label="Cluster" listName="projects" header=true multiple=false
                required=false className="form-control" editable=editable || editStatus/] -->
                <label for="projectID">Select cluster:</label>
                 <div class="form-group">
                  <div class="selectList">   
                    <select name="projectID" id="projectID">
                      [#list projects as project ]
                        <option value="${project.id}" >C${project.id} - ${(project.projectInfo.title)!}</option>
                      [/#list]  
                    </select>
                  </div>
                 </div>
                
              </div>
        
            </div>
          </div>
        </div>
        

     


        
        <div class="modal fade" id="modalConfirm" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title" style="font-size: 20px; font-weight: 700;">Warning</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>
              <div class="modal-body">
                <p style="font-size: 17px; font-weight: 500;">
                  Are you sure you want to perform this action?.</p>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> Confirm[/@s.submit]                
              </div>
            </div>
          </div>
        </div>
        
        [#-- Section Buttons--]


        <div class="buttons">
          <div class="buttons-content">
           <button type="button" class="button-save" data-toggle="modal" data-target="#modalConfirm">
            <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>Save
          </button>          
         </div>
        </div>
        

        <!-- <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          </div>
        </div> -->
        
        [/@s.form]
      	
      </div>
    </div>
  </div>
</section>



[#-- Check Template --]
<table class="check-template" style="display:none">
  <tr display="font-size: 0.9em;">
    <td>[@customForm.checkmark id="" label="{{ labelText }}" name="entityByPhaseList" cssClass="deliverableCheck" cssClassLabel="font-normal" /]</td>
  </tr>
</table>


[#include "/WEB-INF/global/pages/footer.ftl" /]

