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





<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<!-- <script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>
<script src="${baseUrlMedia}/js/capDev/supportingDocuments.js"></script> -->



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
				[#if projectID > 0]
		          <a class="" href="[@s.url namespace='/monitoring' action='${centerSession}/projectCapdev'] [@s.param name='projectID']${projectID?c}[/@s.param][@s.param name='edit' value="true" /] [/@s.url] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBackProjects" /]</a>
		        [#else]
		          <a class="" href="[@s.url action='${centerSession}/capdev' /] "><span class="glyphicon glyphicon-circle-arrow-left"></span>[@s.text name="capdev.gotoBack" /]</a>
		        [/#if]
			</div>
		</div>
		
		<div class="col-md-12">
				Supporting Documents		
		</div>
	
		<div class="col-md-12 form-group "> 

			[@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
		

			<div  class="fullForm borderBox" >
				
				<!-- supporting documents -->
				
				
					

					
					[#if capdev.capdevSupportingDocs?has_content]
						<table class="supportDocsList" id="supportingDocs">
							<thead>
								<tr class="header">
									<th>ID</th>
									<th>Title</th>
									<th>Type</th>
									<th>Publication date</th>
									<th>Remove</th>
								</tr>
							</thead>
							<tbody>
								[#list capdev.capdevSupportingDocs as supportDocs]
									[#if supportDocs.active]
										<tr>
											<td>
												<a href="[@s.url action='${centerSession}/detailSupportingDoc'][@s.param name='capdevID']${supportDocs.capacityDevelopment.id?c}[/@s.param][@s.param name='projectID']${projectID}[/@s.param][@s.param name='supportingDocID']${supportDocs.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]">S${supportDocs.id} </a>
											</td>
											<td>
											    [#if supportDocs.title?has_content]
											    	<a href="[@s.url action='${centerSession}/detailSupportingDoc'][@s.param name='capdevID']${supportDocs.capacityDevelopment.id?c}[/@s.param][@s.param name='projectID']${projectID}[/@s.param][@s.param name='supportingDocID']${supportDocs.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]">${supportDocs.title}</a>
											    [#else]
											    	<a href="[@s.url action='${centerSession}/detailSupportingDoc'][@s.param name='capdevID']${supportDocs.capacityDevelopment.id?c}[/@s.param][@s.param name='projectID']${projectID}[/@s.param][@s.param name='supportingDocID']${supportDocs.id?c}[/@s.param][@s.param name='edit' value="true" /][/@s.url]">Not defined</a>
											    [/#if]
										    </td>
										    <td>
											    [#if supportDocs.centerDeliverableType??]
											    	${supportDocs.centerDeliverableType.name}
											    [#else]
											    	Not defined
											    [/#if]
										    </td>
										    <td>
											    [#if supportDocs.publicationDate??]
											    	${supportDocs.publicationDate}
											    [#else]
											    	Not defined
											    [/#if]
										    </td>
											<td class="removeCol">
												[#if editable]
										            <a  class="deleteDoc" href="#" data-href="[@s.url action='${centerSession}/deleteSupportingDoc'][@s.param name='capdevID']${capdev.id}[/@s.param][@s.param name='projectID']${projectID}[/@s.param] [@s.param name='supportingDocID']${supportDocs.id?c}[/@s.param] [@s.param name='edit' value="true" /][/@s.url]" data-toggle="modal" data-target="#confirm-delete">
										               <img src="${baseUrl}/global/images/trash.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
										            </a>
									             [#else]
									             	<img src="${baseUrl}/global/images/trash_disable.png" title="[@s.text name="capdev.removeCapdev" /]" /> 
									             [/#if]
								            </td>


								            
										</tr>
									[/#if]

								[/#list]

								
							</tbody>
						</table>
					[/#if]
						<p class="text-center inf" style="display:${(capdev.capdevSupportingDocs?has_content)?string('none','block')}">[@s.text name="capdev.notSupportDocuments" /]</p>	

					
					[#if editable]
						<div class="col-md-12 newCapdevField">
							<div class="pull-right">
								<div class="buttons-content">        
									<a class="addButton" href="[@s.url action='${centerSession}/addSupportingDoc'][@s.param name='supportingDocID']${supportingDocID}[/@s.param] [@s.param name='capdevID']${capdevID}[/@s.param][@s.param name='projectID']${projectID}[/@s.param] [@s.param name='edit' value="true" /][/@s.url]">[@s.text name="capdev.addSupportingDoc" /]</a>
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














