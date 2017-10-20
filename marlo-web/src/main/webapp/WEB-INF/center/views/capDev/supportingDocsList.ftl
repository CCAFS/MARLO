[#ftl]



[#assign customCSS = ["${baseUrl}/global/css/customDataTable.css",
					  "${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]



[#assign pageLibs = ["datatables.net", "datatables.net-bs","select2","flat-flags"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/supportingDocuments.js"] /]


[#assign currentStage = "supportingDocuments" /]

[#assign breadCrumb = [
  {"label":"capdevList", "nameSpace":"/capdev", "action":"${(centerSession)!}/capdev"},
  {"label":"capdevSupportingDocs", "nameSpace":"/capdev", "action":""}
]/]

[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/center/views/capDev/supportingDocsListTemplate.ftl" as deliverableList /]





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

		<div class="col-md-12">
			<div class="pull-right">
				<a class="" href="[@s.url action='${centerSession}/capdev' /] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBack" /]</a> 
			</div>
		</div>
		
		<div class="col-md-12">
				Supporting Documents		
		</div>
	
		<div class="col-md-12 form-group "> 

			[@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
		

			<div  class="fullForm borderBox" >
				
				<!-- supporting documents -->
				
				<div>[@deliverableList.deliverableList deliverables=deliverables canValidate=true canEdit=true namespace="/capdev" defaultAction="${(centerSession)!}/detailSupportingDoc" /]</div>
					

				<p class="text-center inf" style="display:${(deliverables?has_content)?string('none','block')}">[@s.text name="capdev.notSupportDocuments" /]</p>	

					
					[#if editable]
						<div class="col-md-12 newCapdevField">
							<div class="pull-right">
								<div class="buttons-content">        
									<a class="addButton" href="[@s.url action='${centerSession}/addSupportingDoc'][@s.param name='supportingDocID']${supportingDocID}[/@s.param] [@s.param name='capdevID']${capdevID}[/@s.param] [@s.param name='edit' value="true" /][/@s.url]">[@s.text name="capdev.addSupportingDoc" /]</a>
									<div class="clearfix"></div>
								</div>
							</div>
						</div>
					[/#if]
				</div>


			<div class="modal fade" id="confirm-delete" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
				    <div class="modal-content">
				    
				        <div class="modal-header">
				            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				            <h4 class="modal-title" id="myModalLabel">Confirm Delete</h4>
				        </div>
				    
				        <div class="modal-body">
				            <p>You are about to delete one track, this procedure is irreversible.</p>
				            <p>Do you want to proceed?</p>
				            <p class="debug-url"></p>
				        </div>
				        
				        <div class="modal-footer">
				            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				            <a class="btn btn-danger btn-ok">Delete</a>
				        </div>
				    </div>
				</div>
			</div>
				

				




				

			</div>

		</div>
	
		[/@s.form]

	</div>
	

</div>


[#include "/WEB-INF/center/pages/footer.ftl"]














