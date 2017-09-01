[#ftl]

[#assign pageLibs = ["datatables.net", "datatables.net-bs"] /]
[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css", 
					  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]


[#assign customJS = ["${baseUrlMedia}/js/capDev/supportingDocuments.js",
					 "${baseUrlMedia}/js/global/fieldsValidation.js", 
					 "${baseUrlMedia}/js/capDev/capacityDevelopment.js"] /]

[#assign currentStage = "supportingDocuments" /] 

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevSupportingDocs", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]





<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<!-- <script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>
<script src="${baseUrlMedia}/js/capDev/supportingDocuments.js"></script> -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.7.7/xlsx.core.min.js"></script>


<div class="container"> 

	<div class="row">
		<div class="col-md-12 capdevinfo">
			help text
		</div>
	</div>
	
	<div class="col-md-3 capDevMenu">
		[#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
	</div>
	
	
	
	
	
	<div class="col-md-9 ">

		<div class="col-md-12">
			<div class="pull-right">
				<a class="" href="[@s.url action='${centerSession}/supportingDocs'] [@s.param name='capdevID']${capdevID}[/@s.param] [/@s.url]"><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.supportingDocs.goBack" /]</a> 
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
						[@customForm.input name="capdevSupportingDocs.title" i18nkey="capdev.supportingDocs.title" type="text" help="" editable=true   required=true /]
					</div>
					
				</div>
				<div class="form-group row ">
					<!-- supporting docs type -->
					<div class="col-md-6">
						 [@customForm.select name="capdevSupportingDocs.centerDeliverableTypes.id" listName="deliverablesList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.supportingDocs.type" className="capdevDeliverableType" placeholder="capdev.select" required=true editable=true/]
					</div>
					<!-- supporting docs subtypes -->
					<div class="col-md-6">
						[@customForm.select name="capdevSupportingDocs.deliverableSubtype.id" listName="deliverablesSubtypesList" keyFieldName="id" displayFieldName="name" help="" i18nkey="capdev.supportingDocs.subType" className="capdevDeliverableSubtype" placeholder="capdev.select" required=true editable=true/]
					</div>
				</div>

				<div class="row">
					<div class="col-md-12">
						[@customForm.input name="capdevSupportingDocs.publicationDate" i18nkey="capdev.supportingDocs.publicationdate" type="text" help="" editable=true  required=true /]
					</div>
				</div>
					
					

				<div class="form-group row">
					<div class="form-group col-md-12">
						<label for="">Document(s):</label>
						<div class=" borderBox documentList" listname="capdev.supportingDocs">
							
							[#if documents?has_content]
								[#list documents as document ]
									[#if document.active]
									
									<div class="col-md-12 documents">
								    	<input class="documentID" type="hidden" name="" value="${document.id}" />
								    	<div class="removeCapdevsupportDocument-action removeCapdevsupportDocument removeIcon" title="Remove document"></div>
										<div class="input input-" style="display:block;">
											
											[@customForm.input name="documents[${document_index}].link" i18nkey="capdev.supportingDocs.link" type="text" className="link"   /]
											
											
										</div>
								    </div>
								    [/#if]
							    [/#list]
							[#else]
								<p class="text-center inf" style="display:${(documents?has_content)?string('none','block')}">[@s.text name="There are not document(s) added yet." /]</p>

							[/#if]
							
						</div>
						<div class="col-md-12">
							<div class="pull-right">
								<div class="button-green addCapdevsupportDocument"><span class="glyphicon glyphicon-plus-sign"></span>[@s.text name="Add  document" /]</div>
							</div>
						</div>
						
					</div>
				</div>


				<input  type="hidden" name="capdevID" value="${capdevSupportingDocs.capacityDevelopment.id}" /> 
				<input  type="hidden" name="supportingDocID" value="${capdevSupportingDocs.id}" /> 

				<!-- buttons -->
				<div class="col-md-12">
						<div class="pull-right">
							[@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] [/@s.submit]
						</div>
				</div>

			</div>

		</div>
		
		[/@s.form]

	</div>
	

</div>


<div id="document-template" class="documents form-group "  style="display:none;">
    <div class="col-md-12">
    	<div class="removeCapdevsupportDocument removeIcon" title="Remove document"></div>
    	[@customForm.input name="documents[-1].link" i18nkey="capdev.supportingDocs.link" type="text" className="link"   /]
    </div>
</div>




[#include "/WEB-INF/center/global/pages/footer.ftl"]

















