[#ftl]
[#assign title = "MARLO Deliverables" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/marloDeliverables.js?20181203" ] /]
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
                <div class="col-md-12" style="margin-bottom: 10px;">
                    [@customForm.select name="Deliverable" value="name" label="Phase" listName="phases" header=true  multiple=false required=true className="form-control" editable=editable || editStatus/]
                 </div>
                 <br>
                  <div class="col-md-12" >
                    [@customForm.radioFlat id="1" name="12" label="Move to another phase" value="true" checked=true cssClassLabel="radio-label-yes"/]
                  </div>
                  <div class="col-md-12" >
                    [@customForm.radioFlat id="2" name="13" label="Move to another project" value="false" checked=false cssClass="editable-no" cssClassLabel="radio-label-no"/]
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

