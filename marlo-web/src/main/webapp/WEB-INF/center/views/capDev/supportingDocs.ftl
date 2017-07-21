[#ftl]

[#assign customCSS = ["${baseUrlMedia}/css/global/customDataTable.css"] /]
[#assign customCSS = ["${baseUrlMedia}/css/capDev/capacityDevelopment.css"] /]
[#assign customJS = ["${baseUrlMedia}/js/capDev/capacityDevelopment.js"] /]

[#include "/WEB-INF/center/global/pages/header.ftl" /]
[#include "/WEB-INF/center/global/pages/main-menu.ftl" /]





<script src="${baseUrl}/bower_components/jquery/dist/jquery.min.js"></script>
<script src="${baseUrlMedia}/js/capDev/capacityDevelopment.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.7.7/xlsx.core.min.js"></script>


<div class="container"> 
	<div class="col-md-3 capDevMenu">
		[#include "/WEB-INF/center/views/capDev/menu-capdev.ftl" /]
	</div>
	
	
	
	
	
	<div class="col-md-9 ">
	
	<div class="col-md-12">
			Supporting Documents		
	</div>
	
	<div class="col-md-12 form-group newCapdevForm"> 

		[@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
		

		<div  class="fullForm" >
			
			<!-- supporting documents -->
			<div class="row">
				<div class="col-md-12 newCapdevField deliverablesTitle">
					Supporting Documents
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 newCapdevField deliverablesContainer">
					[#if deliverables?has_content]
					[#list deliverables as deliverable]

					[/#list]
					[/#if]

					<p class="text-center inf" style="display:${(deliverables?has_content)?string('none','block')}">[@s.text name="capdev.notDeliverables" /]</p>

				</div>
				<div class="col-md-12 newCapdevField">
					<div class="pull-right">
						<div class="buttons-content">        
							<a class="addButton" href="[@s.url action='${centerSession}/addDeliverable' ][/@s.url]">[@s.text name="capdev.addSupportingDoc" /]</a>
							<div class="clearfix"></div>
						</div>
					</div>
				</div>
			</div>



			




			<!-- buttons -->
			<div class="col-md-12">
				<div class="col-md-6">
					<div class="pull-right">
						[@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] [/@s.submit]

					</div>
				</div>

				

			</div>

		</div>

	</div>
	
	[/@s.form]

	</div>
	

</div>


[#include "/WEB-INF/center/global/pages/footer.ftl"]















