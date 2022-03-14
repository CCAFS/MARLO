[#ftl]
[#assign title = "MARLO Bulk Replication" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [] /]
[#assign customJS = [ "${baseUrlCdn}/global/js/superadmin/marloBulk.js?20220309" ] /]
[#assign customCSS = [ "${baseUrlCdn}/global/css/superadmin/superadmin.css" ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "bulkReplication" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"bulkReplication", "nameSpace":"", "action":""}
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
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form id="bulkReplicationForm" action=actionName enctype="multipart/form-data" ]
        
        <h4 class="sectionTitle">[@s.text name="marloBulkReplication.title" /] </h4>
        <div class="borderBox">
          <div class="loading" style="display:none"></div>
          <div class="row grayBox">
            [#--  Entity  --]
            <div class="col-md-4">
              <div class="form-group">
                [#assign entitiesList = [
                  { "actionName": "deliverablesReplication",
                    "i18nkey": "marloBulkReplication.deliverablesReplication",
                    "service": "getDeliverablesByPhase"
                  },
                  { "actionName": "projectsReplication",
                    "i18nkey": "marloBulkReplication.projectsReplication",
                    "service": "getProjectsByPhase"
                  },
                  { "actionName": "projectsOutcomesReplication",
                    "i18nkey": "marloBulkReplication.projectsOutcomesReplication",
                    "service": "getProjectOutcomesByPhase"
                  },
                  { "actionName": "activitiesReplication",
                    "i18nkey": "marloBulkReplication.activitiesReplication",
                    "service": "getActivitiesByPhase"
                  },
                  { "actionName": "projectsPartnersReplication",
                    "i18nkey": "marloBulkReplication.projectsPartner",
                    "service": "getProjectsByPhase"
                  },
                  { "actionName": "fundingSourcesReplication",
                    "i18nkey": "Funding Sources",
                    "service": "getFundingSourcesByPhase"
                  },
                  { "actionName": "deliverableBulkSynchronization",
                    "i18nkey": "marloBulkReplication.deliverableSync",
                    "service": "getDeliverablesByPhase"
                  },
                  { "actionName": "projectPoliciesReplication",
                    "i18nkey": "marloBulkReplication.projectsPoliciesReplication",
                    "service": "getProjectPoliciesByPhase"
                  },
                  { "actionName": "projectExpectedStudiesReplication",
                    "i18nkey": "marloBulkReplication.projectsExpectedStudiesReplication",
                    "service": "getProjectExpectedStudiesByPhase"
                  },
                  { "actionName": "projectInnovationsReplication",
                    "i18nkey": "marloBulkReplication.projectsInnovationsReplication",
                    "service": "getProjectInnovationsByPhase"
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
            
          </div>
          <hr />
          <div class="form-group">
            [#-- Filter --]
            <div class="controls-block row form-group" style="">
              <div class="col-md-7">
                <div class="input-group">
                  <input type="text" id="filterText" class="form-control" placeholder="Filter by IDs...">
                  <span class="input-group-btn">
                    <button id="filterButton" class="btn btn-default" type="button">Go!</button>
                  </span>
                </div><!-- /input-group -->
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
          
          
        </div>
        
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

