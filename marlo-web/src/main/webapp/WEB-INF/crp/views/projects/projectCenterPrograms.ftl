[#ftl]
[#assign title = "Project Programs" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/centerProjectDescription.js",  
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "centerProgram" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"centerProgram", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Center Programs Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          [#-- Select Project Program--]
          <h3 class="headTitle">[@customForm.text name="projectPrograms.title" param="${currentCrp.acronym}" /]</h3>
          <div class="borderBox">
          
            [#-- Project Title --]
            <div class="form-group">
              <div class="input">
                <label for="">[@customForm.text name="project.title" /]:</label>
                <p>${(project.composedName)!}</p>
              </div>
            </div>
            
            <hr />
            
            <div class="form-group row">
              [#-- CENTER Research program --]
              <div class="col-md-6 researchProgram ">
                [@customForm.select name="project.projectInfo.liaisonInstitutionCenter.id" listName="liaisonInstitutions" paramText="${currentCrp.acronym}" keyFieldName="id" displayFieldName="composedName" i18nkey="CIAT Program" className="liaisonInstitutionSelect" help="project.researchProgram.help" required=true editable=editable /]
              </div>
            </div>

            <div class="form-group row simpleBox additionalPrograms">
              <div class="col-md-6 ">
                <h4> Additional Program(s)</h4>
                <div id="projectFlagshipsBlock" class="${customForm.changedField('project.flagshipValue')}">
                [#list (programFlagships)![] as element]                                  
                  [@customForm.checkmark id="program-${element.id}" name="project.flagshipValue" label="${element.centerComposedName}" value="${element.id}" editable=editable checked=(flagshipIds?seq_contains(element.id))!false cssClass="fpInput" cssClassLabel="font-normal" /]
                  <br />
                [/#list]
                </div>
              </div>
              <div class="col-md-6 ">
                <h4> Regional Offices</h4>
                [#list (regionFlagships)![] as element]
                  [@customForm.checkmark id="region-${element.id}" name="project.regionsValue" label="${element.name}" value="${element.id}" editable=editable  checked=((regionsIds?seq_contains(element.id))!false) cssClass="rpInput" cssClassLabel="font-normal" /]
                  <br />
                [/#list]
              </div>
            </div>
            [#-- Cluster of Activities --]
            
            <div class="panel tertiary">
              <div class="panel-head ${customForm.changedField('project.centerOutcomes')}"> 
                <label for="">[@s.text name="projectDescription.clusterActivities"][@s.param][@s.text name="global.clusterOfActivities" /][/@s.param] [/@s.text]:[@customForm.req required=editable /]</label>
              </div>
              <div id="projectsList" class="panel-body" listname="project.centerOutcomes">
                [#-- Loading --]
                <div class="loading clustersBlock" style="display:none"></div>
                <ul class="list">
                [#if project.centerOutcomes?has_content]
                  [#list project.centerOutcomes as element]
                    <li class="clusterActivity clearfix [#if !element_has_next]last[/#if]">
                      <input class="id" type="hidden" name="project.centerOutcomes[${element_index}].centerOutcome.id" value="${element.centerOutcome.id}" />
                      <input class="cid" type="hidden" name="project.centerOutcomes[${element_index}].id" value="${(element.id)!}" />
                      [#if editable]<span class="listButton remove popUpValidation pull-right">[@s.text name="form.buttons.remove" /]</span>[/#if] 
                      <span class="name">${(element.centerOutcome.listComposedName)!'null'}</span>
                      <div class="clearfix"></div>                      
                    </li>
                  [/#list]               
                [/#if]  
                </ul>
                [#if editable]
                  [#assign multipleCoA = action.hasSpecificities('crp_multiple_coa')]
                  <span id="coaSelectedIds" style="display:none">[#if project.centerOutcomes?has_content][#list project.centerOutcomes as e]${e.centerOutcome.id}[#if e_has_next],[/#if][/#list][/#if]</span>  
                  [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="centerOutcomes" keyFieldName="id" displayFieldName="listComposedName" className="CoASelect multipleCoA-${multipleCoA?string}" value="" /]
                [#else]
                  [#if !project.centerOutcomes?has_content]
                    <p>[@s.text name="form.values.fieldEmpty" /]</p>
                  [/#if]
                [/#if] 
              </div>
            </div>
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          <input type="hidden"  name="sharedPhaseID" value="${sharedPhaseID}"/>
          [/@s.form] 
      </div> 
      
    </div> 
</section>
[/#if]
 
<span id="liaisonInstitutionsPrograms" style="display:none">{[#list liaisonInstitutions as li]"${li.id}" : ${(li.crpProgram.id)!-1}[#if li_has_next], [/#if][/#list]}</span>
<ul style="display:none">
  <li id="cpListTemplate" class="clusterActivity clearfix">
    <span class="listButton remove pull-right">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.clusterActivities[-1].crpClusterOfActivity.id" value="" />
    <input class="cid" type="hidden" name="project.clusterActivities[-1].id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>


[#include "/WEB-INF/global/pages/footer.ftl"]



