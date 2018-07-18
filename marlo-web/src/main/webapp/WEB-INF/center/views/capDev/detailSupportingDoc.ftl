[#ftl]
[#assign pageLibs = ["datatables.net", "datatables.net-bs", "select2", "flat-flags", "pickadate"] /]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css", 
  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"
  ] 
/]
[#assign customJS = [
  "${baseUrlMedia}/js/capDev/supportingDocuments.js",
  "${baseUrl}/global/js/fieldsValidation.js", 
  "${baseUrlMedia}/js/capDev/capacityDevelopment.js", 
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]

[#assign currentSection = "capdev" /]
[#assign currentStage = "supportingDocuments" /] 

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevSupportingDocs", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container"> 
  <div class="row">
    <div class="helpMessage infoText col-md-12 capdevinfo">
      <img class="col-md-2" src="${baseUrl}/global/images/icon-help.png" />
      [@s.text name="capdev.help.supportingDocs"][/@s.text]
    </div>
  </div>
  
  <div class="col-md-3 capDevMenu">
    [#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
  </div>

  <div class="col-md-9 ">
    [#-- Section Messages --]
    [#include "/WEB-INF/center/views/capDev/messages-capdev.ftl" /]
    <br />

    <div class="col-md-12">
      <div class="pull-right">
        <a class="" href="[@s.url action='${centerSession}/supportingDocs'] [@s.param name='capdevID']${capdevID}[/@s.param] [@s.param name='projectID']${projectID}[/@s.param] [@s.param name='edit' value="true" /][/@s.url]"><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.supportingDocs.goBack" /]</a> 
      </div>
    </div>
    
    <div class="form-group "> 
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
      
      <h4 class="form-group headTitle newCapdevField grupsParticipantsForm" >Supporting Documents</h4>
      <div  class="fullForm borderBox" >
        [#-- supporting documents --]
        <div class="form-group row ">
          <div class="col-md-12">
            [@customForm.input name="deliverable.name" i18nkey="capdev.supportingDocs.title" type="text" help="" editable=editable   required=true /]
          </div>
          
        </div>
        [#-- supporting documents --]
        <div class="form-group row ">
          <div class="col-md-12">
            [@customForm.textArea name="deliverable.description" i18nkey="capdev.supportingDocs.description"  help="" editable=editable   required=true /]
          </div>
          
        </div>
        <div class="form-group row ">
          [#-- supporting docs type --]
          <div class="col-md-6">
             [@customForm.select name="deliverable.deliverableType.deliverableType.id" listName="deliverablesList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.supportingDocs.type" className="capdevDeliverableType" placeholder="capdev.select" required=true editable=editable/]
          </div>
          [#-- supporting docs subtypes --]
          <div class="col-md-6">
            [@customForm.select name="deliverable.deliverableType.id"  listName="deliverablesSubtypesList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.supportingDocs.subType" className="capdevDeliverableSubtype" placeholder="capdev.select" required=true editable=editable/]
          </div>
        </div>
        
        [#-- Deliverable table with categories and sub categories --]
        <div class="form-group deliverableTypeMessage">
          <div class="note left">
            <button type="button" class="btn btn-link" data-toggle="modal" data-target=".deliverablesTypeModal">
              <span class="glyphicon glyphicon-info-sign"></span> [@s.text name="project.deliverable.generalInformation.deliverableType" /]
            </button>
          </div>
          <div class="modal fade deliverablesTypeModal" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
            <div class="modal-dialog modal-lg" role="document">
              <div class="modal-content">
                <table id="deliverableTypes" class="table" style="">
                  <thead>
                    <th> [@s.text name="project.deliverables.dialogMessage.part1" /] </th>
                    <th> [@s.text name="project.deliverables.dialogMessage.part2" /] </th>
                  </thead>
                  <tbody>
                    [#if deliverablesList?has_content]
                      [#list deliverablesList as mt]
                        [#list action.getDeliverablesSubTypes(mt.id) as st]
                          <tr>
                            [#if st_index == 0]
                            <th rowspan="${action.getDeliverablesSubTypes(mt.id).size()}" class="text-center"> ${mt.name} </th>
                            [/#if]
                            <td> ${st.name} [#if st.description?has_content]<br /><small><i>(${(st.description)!})</i></small>[/#if]</td>
                          </tr>
                        [/#list]
                      [/#list]
                    [/#if]  
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <div class="form-group row datePickersBlock">  
          [#--  
          <div class="col-md-6">
            [@customForm.input name="deliverable.startDate" value="${(deliverable.startDate?string.medium)!}" i18nkey="Start date" type="text" disabled=!editable required=true editable=editable className="startDate datePicker" /]
          </div> 
          <div class="col-md-6">
            [@customForm.input name="deliverable.endDate" value="${(deliverable.endDate?string.medium)!}" i18nkey="End date" type="text" disabled=!editable required=false editable=editable className="endDate datePicker" /]
          </div>
          --]
          <div class="col-md-3">
            [@customForm.input name="deliverable.year" i18nkey="Delivery Year" placeholder="e.g ${actualPhase.year +1}" disabled=!editable required=true editable=editable className="" /]
          </div>
        </div>
          
          
        <div class="form-group row">
          <div class="form-group col-md-12">
            <label for="">Document(s):</label>
            <div class=" borderBox documentList" listname="capdev.supportingDocs">
              [#if deliverable.documents?has_content]
                [#list deliverable.documents as document ]
                  [#if document.active]
                    <div class="documents grayBox">
                      [#if editable]
                        <a  class="removeCapdevsupportDocument removeIcon" title="Remove document" href="[@s.url action='${centerSession}/deleteLink'][@s.param name="capdevID" value=capdevID /][@s.param name="projectID" value=projectID /][@s.param name="deliverableID" value=deliverableID /][@s.param name="edit" value=true /][@s.param name="deliverableLink" value=document.id /][/@s.url]"></a>
                      [/#if]
                      <input class="id" type="hidden"  value="${(document.id)!}" name="deliverable.documents[${document_index}].id" />
                      [@customForm.input name="deliverable.documents[${document_index}].link" i18nkey="capdev.supportingDocs.link" type="text" className="link"  editable=editable /]
                    </div>
                  [/#if]
                [/#list]
              [#else]
                <p class="text-center inf" style="display:${(deliverable.documents?has_content)?string('none','block')}">[@s.text name="There are not document(s) added yet." /]</p>
              [/#if]
            </div>
            [#if editable]
              <div class="pull-right"><div class="button-green addCapdevsupportDocument"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="Add  document" /]</div></div>
            [/#if]
          </div>
        </div>
      </div>
    
    
        [#-- Buttons --]
        [#include "/WEB-INF/center/views/capDev/capdev-buttons.ftl" /]
    
      [/@s.form]
    </div>
  </div>  
</div>


<div id="document-template" class="documents form-group grayBox"  style="display:none;">
    <div class="removeCapdevsupportDocument removeIcon" title="Remove document"></div>
    <input class="id" type="hidden" name="deliverable.documents[-1].id" value=""  />
    [@customForm.input name="deliverable.documents[-1].link" i18nkey="capdev.supportingDocs.link" type="text" className="link"   /]
</div>

[#include "/WEB-INF/global/pages/footer.ftl"]