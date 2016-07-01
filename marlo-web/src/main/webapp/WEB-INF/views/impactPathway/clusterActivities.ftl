[#ftl]
[#assign title = "Impact Pathway - Cluster Of Activities" /]
[#assign pageLibs = [] /]
[#assign customJS = ["${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/impactPathway/programSubmit.js", "${baseUrl}/js/impactPathway/clusterActivities.js"] /]
[#assign customCSS = [ "${baseUrl}/css/impactPathway/clusterActivities.css" ] /]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "clusterActivities" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"outcomes"},
  {"label":"clusterActivities", "nameSpace":"", "action":""}
]/]
[#assign clustersName = "clusters"/]
[#assign leadersName = "leaders"/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#--  marlo cluster of activities--]
<section class="marlo-content">
  [#-- History Message --]
  [#if transaction??]
    <div class="history-mode container text-center">
      <p>
      [#if transaction == "-1"]
        History not found
      [#else]
        This is a version history edited by ${selectedProgram.modifiedBy.composedName?html} on ${selectedProgram.activeSince?datetime}
      [/#if]
      </p>
    </div>
  [/#if]
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
      
        <div class="">
          [#-- Program (Flagships) --]
          <ul id="liaisonInstitutions" class="horizontalSubMenu">
            [#list programs as program]
              [#assign isActive = (program.id == crpProgramID)/]
              <li class="${isActive?string('active','')}">
                <a href="[@s.url][@s.param name ="crpProgramID"]${program.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">Flagship ${program.acronym}</a>
              </li>
            [/#list]
          </ul>
        </div>
        [@s.form action=actionName enctype="multipart/form-data" ]  
        
       
        <h4 class="sectionTitle"> [@s.text name="clusterOfActivities.title"] [@s.param]${(selectedProgram.acronym)!}[/@s.param] [/@s.text]</h4>
        [#-- Cluster of Activities List --]
        <div class="clusterList ">
          [#if clusterofActivities?has_content]
            [#list clusterofActivities as cluster]
              [@clusterMacro cluster=cluster name="clusterofActivities" index=cluster_index /]
            [/#list]
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
              [@logHistory.logList list=action.getListLog(selectedProgram) itemId=crpProgramID /]
              <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
            [/#if]
            [#if editable]
              <a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span> [@s.text name="form.buttons.back" /]</a>
              [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
            [#else]
              [#if canEdit]
                <a href="[@s.url][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
              [/#if]
            [/#if]
          </div>
        </div>
        
        [#-- Hidden Parameters --]
        <input type="hidden"  name="crpProgramID" value="${(crpProgramID)!}"/>
        [/@s.form]
      </div>
    </div>
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

<input type="hidden" id="clusterName" value="clusterofActivities" />
<input type="hidden" id="leaderName" value="${leadersName}" />

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
        <p class="text-center" style="display:${(cluster.leaders?has_content)?string('none','block')}">[@s.text name="siteIntegration.notUsers" /]</p>
      </div>
      [#-- Add CoA Leader --]
      [#if editable]
      <div class="addPerson text-center">
        <div class="button-green searchUser"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="form.buttons.addPerson" /]</div>
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


