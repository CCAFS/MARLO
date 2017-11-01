[#ftl]

[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customCSS = ["${baseUrl}/global/css/customDataTable.css", 
					  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]

[#assign pageLibs = ["datatables.net", "datatables.net-bs","select2","flat-flags"] /]

[#assign customJS = [
	"${baseUrlMedia}/js/capDev/supportingDocuments.js",
	"${baseUrl}/global/js/fieldsValidation.js", 
	"${baseUrlMedia}/js/capDev/capacityDevelopment.js", 
	"${baseUrl}/global/js/autoSave.js"
	] 
/]

[#assign currentStage = "supportingDocuments" /] 

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevSupportingDocs", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]



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


		
        [#-- History Message --]
		[#if transaction??]
		  <div class="history-mode text-center animated flipInX">
		    [#if transaction == "-1"]
		      <p>[@s.text name="capdev.message.historyNotFound" /]</p>
		    [#else]
		      <p>[@s.text name="capdev.message.historyVersion" ]  
		          [@s.param]<span>${capdev.modifiedBy.composedName?html}</span>[/@s.param]
		          [@s.param]<span>${capdev.activeSince?datetime}</span>[/@s.param]
		          [@s.param]<a href="[@s.url][@s.param name="capdevID" value=capdevID /][@s.param name="projectID" value=projectID /][@s.param name="deliverableID" value=deliverableID /][@s.param name="edit" value="true"/][/@s.url]">here</a>[/@s.param]
		         [/@s.text]
		      </p>
		      [#-- Differences --]
		      [#include "/WEB-INF/global/macros/historyDiff.ftl" /]
		      [#-- Justification --]
		      <p><i>${(capdev.modificationJustification)!}</i></p>
		    [/#if]
		  </div>


		[/#if]
        <br />

		<div class="col-md-12">
			<div class="pull-right">
				<a class="" href="[@s.url action='${centerSession}/supportingDocs'] [@s.param name='capdevID']${capdevID}[/@s.param] [@s.param name='projectID']${projectID}[/@s.param] [@s.param name='edit' value="true" /][/@s.url]"><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.supportingDocs.goBack" /]</a> 
			</div>
			
		</div>
		
		<div class="col-md-12">
				Supporting Documents		
		</div>
		
		<div class="col-md-12 form-group "> 

			[@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
			

			<div  class="fullForm borderBox" >
				
				<!-- supporting documents -->
				<div class="form-group row ">
					<div class="col-md-12">
						[@customForm.input name="deliverable.name" i18nkey="capdev.supportingDocs.title" type="text" help="" editable=editable   required=true /]
					</div>
					
				</div>
				<div class="form-group row ">
					<!-- supporting docs type -->
					<div class="col-md-6">
						 [@customForm.select name="deliverable.deliverableType.deliverableType.id" listName="deliverablesList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.supportingDocs.type" className="capdevDeliverableType" placeholder="capdev.select" required=true editable=editable/]
					</div>
					<!-- supporting docs subtypes -->
					<div class="col-md-6">
						[@customForm.select name="deliverable.deliverableType.id"  listName="deliverablesSubtypesList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.supportingDocs.subType" className="capdevDeliverableSubtype" placeholder="capdev.select" required=true editable=editable/]
					</div>
				</div>

				<div class="form-group row">  
	              <div class="col-md-6">
	                [@customForm.input name="deliverable.startDate" i18nkey="Start date" type="text" disabled=!editable  required=true editable=editable /]
	              </div> 
	              <div class="col-md-6">
	                [@customForm.input name="deliverable.endDate" i18nkey="End date" type="text" disabled=!editable required=false editable=editable /]
	              </div>
	            </div>
					
					

				<div class="form-group row">
					<div class="form-group col-md-12">
						<label for="">Document(s):</label>
						<div class=" borderBox documentList" listname="capdev.supportingDocs">
							
							[#if deliverable.documents?has_content]
								[#list deliverable.documents as document ]
									[#if document.active]
									
									<div class="col-md-12 documents">
								    	<input class="documentID" type="hidden" name="" value="${document.id}" />
								    	[#if editable]
									    	<div class="removeCapdevsupportDocument-action removeCapdevsupportDocument removeIcon" title="Remove document"></div>
								    	[/#if]
										<div class="input input-" style="display:block;">
											<input class="id" type="hidden"  value="${(document.id)!-1}" name="deliverable.documents[${document_index}].id" />
											[@customForm.input name="deliverable.documents[${document_index}].link" i18nkey="capdev.supportingDocs.link" type="text" className="link"  editable=editable /]
										</div>
								    </div>
								    [/#if]
							    [/#list]
							[#else]
								<p class="text-center inf" style="display:${(deliverable.documents?has_content)?string('none','block')}">[@s.text name="There are not document(s) added yet." /]</p>

							[/#if]
							
						</div>
						[#if editable]
							<div class="col-md-12">
								<div class="pull-right">
									<div class="button-green addCapdevsupportDocument"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="Add  document" /]</div>
								</div>
							</div>
						[/#if]
						
					</div>
				</div>


				<input  type="hidden" name="capdevID" value="${capdev.id}" /> 
				<input  type="hidden" name="projectID" value="${projectID}" /> 
				<input  type="hidden" name="deliverableID" value="${deliverable.id}" /> 
				<input  type="hidden" name="id" value="${deliverable.id}" /> 
				<input type="hidden"  name="className" value="${(deliverable.class.name)!}"/>
				<input type="hidden"  name="modifiedBy.id" value="${(currentUser.id)!}"/>
				<input type="hidden"  name="actionName" value="${(actionName)!}"/>


				<!-- buttons -->
				[#assign recordsList = (action.getListLog(deliverable))!{} /]
					<div class="col-md-12">
							<div class="buttons">

					        	<div class="buttons-content">  
					        		[#if recordsList?has_content]
								      [#import "/WEB-INF/center//views/capDev/logHistory.ftl" as logHistory /]
								      [@logHistory.logList list=recordsList itemName1="capdevID" itemName2="deliverableID" itemName3="projectID" itemId1=capdev.id itemId2=deliverable.id  itemId3=projectID/]
								      <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
								    [/#if] 

								    [#if editable]
								    	<!-- Save Button -->
							          	[@s.submit type="button" name="save" cssClass="button-save" ]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] [/@s.submit]
						        	<div class="clearfix"></div>

						        	[#elseif canEdit]
							        	<!-- Edit Button -->
									    <a href="[@s.url][@s.param name="capdevID" value=capdevID /][@s.param name="deliverableID" value=deliverableID /][@s.param name="edit" value="true"/][/@s.url]" class="form-button button-edit"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> [@s.text name="form.buttons.edit" /]</a>
						        	
						        	[/#if]
					        	</div>
					        	
							</div>
					</div>
				

					<div class="col-md-12">
					[#-- Last update message --]
					[#if recordsList?has_content]
					[#assign lastRecord = recordsList[0] /]
						<div class="clearfix"></div>
						<span id="lastUpdateMessage" class="pull-right"> 
						  Last edit was made on <span class="datetime">${(lastRecord.createdDate)?datetime} ${(timeZone)!}</span> by <span class="modifiedBy">${lastRecord.user.composedCompleteName}</span>  
						</span>
					[/#if]
					</div>

			</div>

		</div>
		
		[/@s.form]

	</div>
	

</div>


<div id="document-template" class="documents form-group "  style="display:none;">
    <div class="col-md-12">
    	<div class="removeCapdevsupportDocument removeIcon" title="Remove document"></div>
    	[@customForm.input name="deliverable.documents[-1].link" i18nkey="capdev.supportingDocs.link" type="text" className="link"   /]
    </div>
</div>




[#include "/WEB-INF/center/pages/footer.ftl"]