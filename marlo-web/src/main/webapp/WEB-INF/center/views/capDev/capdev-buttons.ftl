[#ftl]

[#assign recordsList = (action.getListLog(capdev))!{} /]

[#if editable]


	<div class="col-md-12">
			<div class="buttons">

	        	<div class="buttons-content">  
	        		[#if recordsList?has_content]
				      [#import "/WEB-INF/center//global/macros/logHistory.ftl" as logHistory /]
				      [@logHistory.logList list=recordsList itemName="capdevID" itemId=capdev.id /]
				      <a href="" onclick="return false" class="form-button button-history"><span class="glyphicon glyphicon-glyphicon glyphicon-list-alt" aria-hidden="true"></span> [@s.text name="form.buttons.history" /]</a>
				    [/#if] 

		          	[@s.submit type="button" name="save" cssClass="button-save" ]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /] [/@s.submit]
		        	<div class="clearfix"></div>
		        	
	        	</div>

	        	
	        	
	        	
			</div>
	</div>
[/#if]

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