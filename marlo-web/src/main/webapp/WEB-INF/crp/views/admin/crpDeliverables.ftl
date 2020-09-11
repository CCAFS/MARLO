[#ftl]
[#assign title = "MARLO Deliverables" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/marloDeliverables.js?20200909" ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "crpDeliverables" /]

[#assign breadCrumb = [
  {"label":"admin", "nameSpace":"", "action":"adminManagement"},
  {"label":"crpDeliverables", "nameSpace":"", "action":""}
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
                      <option value="${deliverable.id}" selected>D${deliverable.id}</option>
                    [/#list]  
                  </select>
                </div>
               </div>
              </div>
            </div>
        
           
        
            <div class="col-md-12 ma-bot">
              [@customForm.radioFlat id="anotherPhase" name="MoveTo" label="Move to another phase" value="anotherPhase"
              checked=false cssClassLabel="radio-label-yes"/]
            </div>
        
            <div id="anotherPhaseContent" class="row col-md-12 ma-bot">
        
              <div class="col-md-6">
                <label for="" class="title">
                  Current phase:
                </label>
                <p >AR2019</p>
              </div>
        
              <div class="col-md-6 ">
              <!--  [@customForm.select name="phase" value="name" label="Phase" listName="phases" header=true  multiple=false required=true className="form-control" editable=editable || editStatus/] -->
              
               <label for="phaseID">Select phases:</label>
               <div class="form-group">
                <div class="selectList">   
                  <select name="phaseID" id="phaseID">
                    [#list phases as phase ]
                      <option value="${phase.id}" selected>${phase.name} - ${phase.year} </option>
                    [/#list]  
                  </select>
                </div>
               </div>
              </div>
        
            </div>
        
            <div class="col-md-12 ma-bot">
              [@customForm.radioFlat id="anotherProject" name="MoveTo" label="Move to another project" value="anotherProject"
              checked=false cssClass="editable-no" cssClassLabel="radio-label-no"/]
            </div>
        
            <div id="anotherProjectContent" class="row col-md-12 ma-bot">
        
              <div class="col-md-6">
                <label for="" class="title">
                  Current project:
                </label>
                <!-- <h1>Current project:</h1> -->
                <p >P250 - Bringing CSA practices to scale: assessing their contributions to narrow nutrient and yield gaps</p>
              </div>
        
              <div class="col-md-6">
                <!-- [@customForm.select name="Select project" value="name" label="Project" listName="projects" header=true multiple=false
                required=false className="form-control" editable=editable || editStatus/] -->
                <label for="projectID">Select project:</label>
                 <div class="form-group">
                  <div class="selectList">   
                    <select name="projectID" id="projectID">
                      [#list projects as project ]
                        <option value="${project.id}" selected>P${project.id}</option>
                      [/#list]  
                    </select>
                  </div>
                 </div>
                
              </div>
        
            </div>
          </div>
        </div>
        
        
   <!-- <div class="borderBox">
          <div class="loading" style="display:none"></div>
          <div class="row grayBox">
            [#--  Entity  --]
            <div class="col-md-4">
              <div class="form-group">
                [#assign entitiesList = [
                  { "actionName": "deliverablesReplication",
                    "i18nkey": "marloBulkReplication.deliverablesReplication",
                    "service": "getDeliverablesByPhase"
                  }
                ] /]
                <label for="entityID">Entity:</label>
                <select name="selectedEntityID" id="entityID" class="form-control">
                  [#list entitiesList as entity ]
                    <option value="${entity.service}" class="action-${entity.actionName}" [#if actionName == entity.actionName]selected[/#if]>[@s.text name=entity.i18nkey /]</option>
                  [/#list]
                </select>
              </div>
            </div>
            [#-- Global Unit --]
            <div class="col-md-4">
              <div class="form-group">
                <label for="globalUnitID">Global Unit:</label>
                <select name="selectedGlobalUnitID" id="globalUnitID" class="form-control">
                  <option value="-1">Select an option...</option>
                  [#list (crps)![] as globalUnit]<option value="${globalUnit.id}">${globalUnit.acronym}</option>[/#list]
                </select>
              </div>
            </div>
            [#-- PHASE --]
            <div class="col-md-4">
              <div class="form-group">
                <label for="phaseID">Phase:</label>
                <select name="selectedPhaseID" id="phaseID" class="form-control">
                  <option value="">Select an option...</option>
                </select>
              </div>
            </div>
            
            [#-- PHASE2 --]
            <div class="col-md-4">
              <div class="form-group">
                  [@customForm.select name="phase" value="name" label="Phase" listName="phases" header=true  multiple=false required=true className="form-control" editable=editable || editStatus/]
              </div>
            </div>
          </div>
          <hr />
          <div class="form-group">
            [#-- Filter --]
            <div class="controls-block row form-group" style="">
              <div class="col-md-7">
                <div class="input-group">
                  <input type="text" id="filterText" class="form-control" placeholder="Filter by IDs...">
                  <span class="input-group-btn">
                    <button id="filterButton" class="btn btn-default" type="button">Filter</button>
                  </span>
                </div>
              </div>
              <div class="col-md-5">
                <label for="toggleSelectAll" class="pull-right"><input type="checkbox" name="" id="toggleSelectAll" checked="checked"/> Select/Unselect All</label>
              </div>
            </div>
            <small><i> Items Checked: <span class="count">0</span></i></small>
            <div id="deliverables-checkbox">
              <table class="table table-hover">
                <thead>
                  <tr>
                    <th class="entityName">[@s.text name="marloBulkReplication.${actionName}" /]</th>
                  </tr>
                </thead>
                <tbody>
                  [#-- Filled by Javascript --]
                </tbody>
              </table>
            </div>
          </div>
          
        </div> -->
        
        [#-- Section Buttons--]
        <div class="buttons">
          <div class="buttons-content">
            [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
          </div>
        </div>
        
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

