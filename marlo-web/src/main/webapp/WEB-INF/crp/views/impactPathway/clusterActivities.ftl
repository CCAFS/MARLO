[#ftl]
[#assign title = "Impact Pathway - Cluster Of Activities" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${crpProgramID}" /]
[#assign pageLibs = ["cytoscape","cytoscape-panzoom","select2"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/impactPathway/programSubmit.js", 
  "${baseUrl}/global/js/autoSave.js", 
  "${baseUrl}/global/js/impactGraphic.js",
  "${baseUrl}/global/js/fieldsValidation.js", 
  "${baseUrlMedia}/js/impactPathway/clusterActivities.js"
  ] 
/]
[#assign customCSS = [ 
  "${baseUrlMedia}/css/impactPathway/clusterActivities.css",
  "${baseUrlMedia}/css/impactPathway/impactGraphic.css" 
  ] 
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "clusterActivities" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"clusterActivities", "nameSpace":"", "action":""}
]/]
[#assign clustersName = "clusters"/]
[#assign leadersName = "leaders"/]
[#assign keyOutputsName = "keyOutputs"/]
[#assign outcomesName = "keyOutputOutcomes"/]

[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

<div class="container helpText viewMore-block">
  <div style="display:none;" class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10">[@s.text name="cluster.help"][@s.param][@s.text name="global.sClusterOfActivities" /][/@s.param] [/@s.text] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

[#--  marlo cluster of activities--]
<section class="marlo-content">
  
  <div class="container"> 
    [#if programs?has_content]
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/impactPathway/messages-impactPathway.ftl" /]
        
        [#-- Program (Flagships) --]
        <ul id="liaisonInstitutions" class="horizontalSubMenu">
          [#list programs as program]
            [#assign isActive = (program.id == crpProgramID)/]
            <li class="${isActive?string('active','')}">
              <a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">Flagship ${program.acronym}</a>
            </li>
          [/#list]
        </ul>
        
        [@s.form action=actionName enctype="multipart/form-data" ]  

        <h4 class="sectionTitle"> [@s.text name="clusterOfActivities.title"] [@s.param]${(selectedProgram.acronym)!}[/@s.param][@s.param][@s.text name="global.clusterOfActivities" /][/@s.param] [/@s.text]</h4>
          [#-- Cluster of Activities List --]
          <div class="clusterList " listname="clusterofActivities">
            [#if clusterofActivities?has_content]
              [#list clusterofActivities as cluster]
                [@clusterMacro cluster=cluster name="clusterofActivities" index=cluster_index /]
              [/#list]
            [#else]
              [@clusterMacro cluster={} name="clusterofActivities" index=0 /]
            [/#if]
          </div>
          [#-- Add CoA Button --]
          [#if editable]
            <div class="bigAddButton text-center addCluster"><span class="glyphicon glyphicon-plus"></span> Add a Cluster</div>
          [/#if]
  
          [#-- Section Buttons--]
          [#include "/WEB-INF/crp/views/impactPathway/buttons-impactPathway.ftl" /]
          
        [/@s.form]
      </div>
    </div>
    [#else]
      <p class="text-center borderBox inf">[@s.text name="impactPathway.noFlagshipsAdded" /]</p>
    [/#if]
  </div>
</section>
[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]

[#-- Cluster Template --]
[@clusterMacro cluster={} name="" index=0 isTemplate=true /]

<ul style="display:none">
  [#-- User template --]
  [@userItem element={} index=0 name="" userRole=roleCl.id template=true /]
</ul>

[#-- Template Outcome List --]
[#include "/WEB-INF/crp/macros/outcomesListSelectMacro.ftl"]

[#-- Key output Template --]
[@keyOutputItem element={} index=0 name="${keyOutputsName}"  isTemplate=true /]

[#-- Outcome by CoA Template --]
[@outcomeByCluster element={} index=0 name="clusterofActivities[-1].${keyOutputsName}[-1].${outcomesName}"  isTemplate=true /]

<input type="hidden" id="clusterName" value="clusterofActivities" />
<input type="hidden" id="leaderName" value="${leadersName}" />
<input type="hidden" id="keyOutputName" value="${keyOutputsName}" />
<input type="hidden" id="outcomesName" value="${outcomesName}" />

[#include "/WEB-INF/crp/pages/footer.ftl" /]


[#macro clusterMacro cluster name index isTemplate=false]
  [#local clusterCustomName= "${name}[${index}]" /]
  
  <div id="cluster-${isTemplate?string('template', index)}" class="cluster form-group borderBox" style="display:${isTemplate?string('none','block')}">
      
      <div class="form-group">
      <div class="leftHead">
        <span class="index">${index+1}</span>
        <span class="elementId">${(selectedProgram.acronym)!} - [@s.text name="global.sClusterOfActivities"/]</span>
      </div>
      [#-- Remove Button --]
      [#if editable]
        <div class=" removeElement removeCluster" title="Remove Cluster"></div>
      [/#if]
      [#-- Cluster Activity identifier --]
      <div class=" form-group cluster-identifier ">
        [@customForm.input name="${clusterCustomName}.identifier" i18nkey="cluster.identifier" required=true placeholder="e.g. Cluster 1.1"   className="clusterIdentifier" editable=editable /]
      </div>
      [#-- Cluster Activity Name --]
      <div class=" form-group cluster-title">
        [@customForm.textArea name="${clusterCustomName}.description" i18nkey="cluster.title" required=true className="outcome-statement limitWords-20" editable=editable /]
      </div>
      [#-- Cluster Activity Leaders --]
      <span class="subtitle cold-md-12"><label>[@s.text name="cluster.leaders.title"][@s.param][@s.text name="global.sClusterOfActivities" /][/@s.param] [/@s.text]
      [#if !action.hasSpecificities("crp_cluster_leader")]
         <span class="red">*</span>
      [/#if]
       </label></span>
      
      <div class="items-list form-group col-md-12 simpleBox" listname="${clusterCustomName}.leaders">
        <ul class="leaders">
        [#if cluster.leaders?has_content]
          [#list cluster.leaders as leaderItem]
            [@userItem element=leaderItem index=leaderItem_index name='${clusterCustomName}.leaders'  userRole=roleCl.id  /]
          [/#list]
        [/#if]
        </ul>
        <p class="text-center inf" style="display:${(cluster.leaders?has_content)?string('none','block')}">[@s.text name="cluster.notLeaders" /]</p>
      </div>
      [#-- Add CoA Leader --]
      [#if editable]
      <div class="addPerson text-center">
        <div class="button-green searchUser"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="form.buttons.addClusterLeader" /]</div>
      </div>
      [/#if]
      
      [#-- Key outputs --]
      <br>
      <h5 class="sectionSubTitle">Key Outputs: <small>[@customForm.req required=true /]</small> </h5>
      <div class="keyOutputsItems-list form-group col-md-12" listname="${clusterCustomName}.${keyOutputsName}">
        [#if cluster.keyOutputs?has_content]
          [#list cluster.keyOutputs as keyOutputItems]
            [@keyOutputItem element=keyOutputItems name='${clusterCustomName}.${keyOutputsName}' index=keyOutputItems_index /]
          [/#list]
        [/#if]
        <p class="text-center alertKeyoutput" style="display:${(cluster.keyOutputs?has_content)?string('none','block')}">No key output has been added yet.</p>
      </div>
      [#-- Add Key output --]
      [#if editable]
      <div class="addPerson text-center">
        <div class="button-green addKeyOutput"><span class="glyphicon glyphicon-plus-sign"></span>Add key output</div>
      </div>
      [/#if]
    </div>    
    <input class="cluterId" type="hidden" name="${clusterCustomName}.id" value="${(cluster.id)!}"/>        
           
  </div>
[/#macro]

[#macro userItem element index name userRole template=false]
  [#local customName = "${name}[${index}]" /]
  <li id="user-${template?string('template',index)}" class="user userItem"  style="list-style-type:none; display:${template?string('none','block')}">
    [#-- User Name --]
    <span class="glyphicon glyphicon-user" aria-hidden="true"></span><span class="name"> ${(element.user.getComposedName()?html)!'Unknown user'}</span>
    [#-- Hidden inputs --]
    <input class="user" type="hidden" name="${customName}.user.id" value="${(element.user.id)!}"/>
    <input class="role" type="hidden" name="${customName}.role.id" value="${userRole}"/>
    <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    [#-- Remove Button --]
    [#if editable]
      <span class="glyphicon glyphicon-remove pull-right remove-userItem" aria-hidden="true"></span>
    [/#if]
  </li>
[/#macro]

[#macro keyOutputItem element index name  isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="keyOutput-${isTemplate?string('template',(element.id)!)}" class="keyOutputItem expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
     
      <div class="removeLink">
        <div id="removeActivity" class="removeKeyOutput removeElement removeLink" title="[@s.text name='cluster.removeKeyOutput' /]"></div>
      </div>
     
    [/#if]
    [#-- Partner Title --]
    <div class="blockTitle closed">
       <span title="${(element.keyOutput)!}" class="koTitle col-md-9">[#if element.keyOutput?has_content][@utils.wordCutter string=(element.keyOutput) maxPos=70 substr=" "/][#else]New Key output[/#if]</span>
      <span class="pull-right koContribution-title"><span><b>Contribution:</b></span> <span class="koContribution-percentage">${(element.contribution?string["0.##"])!}%</span></span>
    <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      <div class="row">
        [#-- Statement --]
        <div class="form-group col-md-9">
          [@customForm.textArea  name="${customName}.keyOutput" i18nkey="cluster.keyOutput.statement" value="${(element.keyOutput)!}" required=true className="limitWords-50 keyOutputInput" editable=editable /]
          <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
        </div>
        [#-- Contribution --]
        <div class="form-group col-md-3">
            [@customForm.input name="${customName}.contribution" i18nkey="cluster.keyOutput.contribution" value="${(element.contribution?string['0.##'])!}" className="keyOutputContribution" type="text" disabled=!editable  required=true editable=editable /]
        </div>
      </div>
            
      [#-- Outcomes list --]
      <div class="form-group">
        <label for="" class="${editable?string('editable', 'readOnly')}">[@s.text name="keyOutput.outcomesContributions" /]:</label>
        <div class="outcomesWrapper simpleBox form-group" listname="${customName}.${outcomesName}">
        [#if element.keyOutputOutcomes?has_content]
          [#list element.keyOutputOutcomes as keyOutputOutcome]
            [@outcomeByCluster element=keyOutputOutcome index=keyOutputOutcome_index name='${customName}.${outcomesName}'  /]
          [/#list]
        [/#if]
        [#--  <p class="text-center alertOutcome" style="display:${(element.keyOutputOutcomes?has_content)?string('none','block')}">[@s.text name="keyOutput.outcomesEmpty" /]</p> --]
        </div>
      </div>
      <div class="form-group">
        [@customForm.select name="" label=""  i18nkey="keyOutput.selectOutcomes" listName="outcomes" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className=" outcomeList" disabled=!editable/]
      </div>
    </div>
  
  </div>

[/#macro]

[#macro outcomeByCluster element index name  isTemplate=false]
  [#local customName = "${name}[${index}]" /]
  <div id="outcomeByCluster-${isTemplate?string('template',(element.id)!)}" class="outcomeByClusterItem  borderBox ${customForm.changedField('${customName}.id')}"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div id="removeActivity" class="removeOutcome removeElement removeLink" title="[@s.text name='cluster.removeOutcome' /]"></div>
      </div>
    [/#if]    
      [#-- Statement --]
      <div class="form-group">
        <span title="${(element.crpProgramOutcome.composedName)!}" class="outcomeStatement">
          <p>
            <strong>${(element.crpProgramOutcome.crpProgram.acronym)!} Outcome:</strong> ${(element.crpProgramOutcome.description)!}
            [#if action.hasSpecificities('crp_ip_outcome_indicator')]
            <i class="indicatorText"><br /><strong>Indicator: </strong>${(element.crpProgramOutcome.indicator)!'No Indicator'}</i>
            [/#if]
          </p>
        </span>
        <input class="outcomeContributionId" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
        <input class="outcomeId" type="hidden" name="${customName}.crpProgramOutcome.id" value="${(element.crpProgramOutcome.id)!}"/>
        
      </div>
      [#-- Contribution --]
      <div class="hidden">
          [@customForm.input name="${customName}.contribution" i18nkey="Contribution" value="${(element.contribution)!}" className="outcomeContribution" type="text" disabled=!editable  required=true editable=editable /]
      </div>
  
  </div>

[/#macro]