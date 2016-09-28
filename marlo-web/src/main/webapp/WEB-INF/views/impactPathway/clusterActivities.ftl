[#ftl]
[#assign title = "Impact Pathway - Cluster Of Activities" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${crpProgramID}" /]
[#assign pageLibs = ["cytoscape","cytoscape-panzoom","select2"] /]
[#assign customJS = ["${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/impactPathway/programSubmit.js", "${baseUrl}/js/impactPathway/clusterActivities.js", "${baseUrl}/js/global/autoSave.js", "${baseUrl}/js/global/impactGraphic.js"] /]
[#assign customCSS = [ "${baseUrl}/css/impactPathway/clusterActivities.css","${baseUrl}/css/global/impactGraphic.css" ] /]
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

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

[#--  marlo cluster of activities--]
<section class="marlo-content">
  
  <div class="container"> 
    [#if programs?has_content]
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/views/impactPathway/messages-impactPathway.ftl" /]
        
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

        <h4 class="sectionTitle"> [@s.text name="clusterOfActivities.title"] [@s.param]${(selectedProgram.acronym)!}[/@s.param] [/@s.text]</h4>
          [#-- Cluster of Activities List --]
          <div class="clusterList ">
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
          <div class="buttons">
            <div class="buttons-content">
              [#-- History Log --]
              [#if action.getListLog(selectedProgram)?has_content]
                [#import "/WEB-INF/global/macros/logHistory.ftl" as logHistory /]
                [@logHistory.logList list=action.getListLog(selectedProgram) itemName="crpProgramID" itemId=crpProgramID /]
                <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
              [/#if]
              [#if editable]
                [#-- Back Button --]
                <a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
                [#-- Cancel Button --]                
                [@s.submit type="button" cssStyle="display:${draft?string('inline-block','none')}"   name="cancel" cssClass="button-cancel"]<span class="glyphicon glyphicon-trash" aria-hidden="true"></span> [@s.text name="form.buttons.discard" /] [/@s.submit]
                [#-- Save Button --]
                [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] <span class="draft">${draft?string('(Draft Version)','')}</span>[/@s.submit]
              [#elseif canEdit]
                [#-- Back Button --]
                <a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
              [/#if]
            </div>
          </div>
          
          [#-- Last update message --]
          [#if selectedProgram?has_content]
          <span id="lastUpdateMessage" class="pull-right"> 
            Last edit was made on <span class="datetime">${(selectedProgram.activeSince)?datetime}</span> by <span class="modifiedBy">${selectedProgram.modifiedBy.composedCompleteName}</span>  
          </span>
          [/#if]
          
          [#-- Hidden Parameters --]
          <input type="hidden" id="crpProgramID"  name="crpProgramID" value="${(crpProgramID)!}"/>
          <input type="hidden"  name="className" value="${(selectedProgram.class.name)!}"/>
          <input type="hidden"  name="id" value="${(selectedProgram.id)!}"/>
          <input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
          <input type="hidden"  name="actionName" value="${(actionName)!}"/>       
        [/@s.form]
      </div>
    </div>
    [#else]
      <p class="text-center borderBox inf">There is not flagships added</p>
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

[#-- Key output Template --]
[@keyOutputItem element={} index=0 name="${keyOutputsName}"  isTemplate=true /]

[#-- Outcome by CoA Template --]
[@outcomeByCluster element={} index=0 name="${outcomesName}"  isTemplate=true /]

<input type="hidden" id="clusterName" value="clusterofActivities" />
<input type="hidden" id="leaderName" value="${leadersName}" />
<input type="hidden" id="keyOutputName" value="${keyOutputsName}" />
<input type="hidden" id="outcomesName" value="${outcomesName}" />

[#include "/WEB-INF/global/pages/footer.ftl" /]


[#macro clusterMacro cluster name index isTemplate=false]
  [#assign clusterCustomName= "${name}[${index}]" /]
  
  <div id="cluster-${isTemplate?string('template', index)}" class="cluster form-group borderBox" style="display:${isTemplate?string('none','block')}">
      
      <div class="form-group">
      <div class="leftHead">
        <span class="index">${index+1}</span>
        <span class="elementId">${(selectedProgram.acronym)!} - [@s.text name="cluster.index.title"/]</span>
      </div>
      [#-- Remove Button --]
      [#if editable]
        <div class=" removeElement removeCluster" title="Remove Cluster"></div>
      [/#if]
      [#-- Cluster Activity Name --]
      <div class=" form-group cluster-title">
        [@customForm.textArea name="${clusterCustomName}.description" i18nkey="cluster.title" required=true className="outcome-statement limitWords-100" editable=editable /]
      </div>
      [#-- Cluster Activity Leaders --]
      <span class="subtitle cold-md-12"><label>[@s.text name="cluster.leaders.title" /]</label></span>
      <div class="items-list form-group col-md-12 simpleBox">
        <ul class="leaders">
        [#if cluster.leaders?has_content]
          [#list cluster.leaders as leaderItem]
            [@userItem element=leaderItem index=leaderItem_index name='leaders'  userRole=roleCl.id  /]
          [/#list]
        [/#if]
        </ul>
        <p class="text-center inf" style="display:${(cluster.leaders?has_content)?string('none','block')}">[@s.text name="siteIntegration.notUsers" /]</p>
      </div>
      [#-- Add CoA Leader --]
      [#if editable]
      <div class="addPerson text-center">
        <div class="button-green searchUser"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="form.buttons.addPerson" /]</div>
      </div>
      [/#if]
      
      [#-- Key outputs --]
      <span class="subtitle cold-md-12"><label>Key Outputs: </label></span>
      <div class="keyOutputsItems-list form-group col-md-12">
        [#if cluster.keyOutputs?has_content]
          [#list cluster.keyOutputs as keyOutputItems]
            [@keyOutputItem element=keyOutputItems name='${clusterCustomName}.${keyOutputsName}' index=keyOutputItems_index /]
          [/#list]
        [/#if]
        <p class="text-center " style="display:${(cluster.leaders?has_content)?string('none','block')}">There are not key outputs added yet.</p>
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
  [#assign customName = "${name}[${index}]" /]
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
  [#assign customName = "${name}[${index}]" /]
  <div id="keyOutput-${isTemplate?string('template',(element.id)!)}" class="keyOutputItem expandableBlock borderBox"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div id="removeActivity" class="removeKeyOutput removeElement removeLink" title="[@s.text name='cluster.removeKeyOutput' /]"></div>
      </div>
    [/#if]
    [#-- Partner Title --]
    <div class="blockTitle closed">
       <span title="${(element.keyOutput)!}" class="koTitle col-md-9">[#if element.keyOutput?has_content][@utils.wordCutter string=(element.keyOutput) maxPos=70 substr=" "/][#else]New Key output[/#if]</span>
    
      
      <span class="pull-right koContribution-title"><span><b>Contribution:</b></span> <span class="koContribution-percentage">${(element.contribution)!}%</span></span> 
    <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- Statement --]
      <div class="form-group col-md-9">
        [@customForm.textArea  name="${customName}.keyOutput" i18nkey="Key Output statement" value="${(element.keyOutput)!}" required=false className="limitWords-50 keyOutputInput" editable=editable /]
        <input class="id" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
      </div>
      [#-- Contribution --]
      <div class="form-group col-md-3">
          [@customForm.input name="${customName}.contribution" i18nkey="Contribution" className="keyOutputContribution" type="text" disabled=!editable  required=true editable=editable /]
      </div>
            
      [#-- Outcomes list --]
      <div class="col-md-12">
        <label for="" class="${editable?string('editable', 'readOnly')}">Outcomes:</label>
        <div class="outcomesWrapper simpleBox form-group">
        [#if element.keyOutputOutcomes?has_content]
          [#list element.keyOutputOutcomes as keyOutputOutcome]
            [@outcomeByCluster element=keyOutputOutcome index=keyOutputOutcome_index name='${customName}.${outcomesName}'  /]
          [/#list]
        [/#if]
        </div>
      </div>
      <div class="form-group col-md-12">
        [@customForm.select name="" label=""  i18nkey="Select to add a outcome" listName="outcomes" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className=" outcomeList" disabled=!editable/]
      </div>
    </div>
  
  </div>

[/#macro]

[#macro outcomeByCluster element index name  isTemplate=false]
  [#assign customName = "${name}[${index}]" /]
  <div id="outcomeByCluster-${isTemplate?string('template',(element.id)!)}" class="outcomeByClusterItem  borderBox"  style="display:${isTemplate?string('none','block')}">
    [#if editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div id="removeActivity" class="removeOutcome removeElement removeLink" title="[@s.text name='cluster.removeOutcome' /]"></div>
      </div>
    [/#if]    
      [#-- Statement --]
      <div class="form-group col-md-9">
        <label style="display:block;" for="">Outcome statement</label>
        <span title="${(element.crpProgramOutcome.description)!}" class="outcomeStatement">[@utils.wordCutter string=(element.crpProgramOutcome.description)!"undefined" maxPos=100 substr=" "/]</span>
        <input class="elementId" type="hidden" name="${customName}.id" value="${(element.id)!}"/>
        <input class="outcomeId" type="hidden" name="${customName}.crpProgramOutcome.id" value="${(element.crpProgramOutcome.id)!}"/>
        
      </div>
      [#-- Contribution --]
      <div class="form-group col-md-3">
          [@customForm.input name="${customName}.contribution" i18nkey="Contribution" value="${(element.contribution)!}" className="outcomeContribution" type="text" disabled=!editable  required=true editable=editable /]
      </div>
  
  </div>

[/#macro]


