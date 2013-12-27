<#--
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  @author Leon Torres (leon@opensourcestrategies.com)
-->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<form method="POST" name="addPaymentApplicationGl" action="createPaymentApplication"> <#-- action set by the screen -->
  <input type="hidden" name="paymentId" value="${paymentId}"/>
  <div class="form" style="border:0">
    <table class="twoColumnForm" style="border:0">
      <tr>
        <@displayTitleCell title=uiLabelMap.AccountingGlAccount titleClass="requiredField" />
        <td><@inputAutoCompleteGlAccount name="overrideGlAccountId" id="overrideGlAccountId" /></td>
      </tr>
      <@inputTextRow title=uiLabelMap.CommonAmount size="10" name="amountApplied" titleClass="requiredField" />
	  <@inputTextRow title=uiLabelMap.CommonNote size="60" name="note" />      
	  <#if tagTypes?has_content && allocatePaymentTagsToApplications>
	  	<tr class="${tableRowClass(item_index)}">
            <td colspan="2">&nbsp;</td>
            <td colspan="<#if hasUpdatePermission>8<#else>6</#if>">
            	 <#list tagTypes as tag>
				    <#if tag.isRequired()>
				      <#assign titleClass="requiredField" />
				    <#else/>
				      <#assign titleClass="tableheadtext" />
				    </#if>        
				    <tr>
				      <@displayTitleCell title=tag.description titleClass=titleClass /> 
				      <#if tag.description?contains("geo")>         
				          <@inputSelectCell name="clasifTypeId${tag.index}" list=tag.activeTagValues key="geoId" required=false default=tag.defaultValue! ; tagValue>
				            ${tagValue.geoName}
				          </@inputSelectCell>             
				      <#elseif tag.description?contains("Programatica")>         
				          <@inputSelectCell name="clasifTypeId${tag.index}" list=tag.activeTagValues key="workEffortName" required=false default=tag.defaultValue! ; tagValue>
				            ${tagValue.description}
				          </@inputSelectCell>	     
				      <#else>         
				          <@inputSelectCell name="clasifTypeId${tag.index}" list=tag.activeTagValues key="enumId" required=false default=tag.defaultValue! ; tagValue>
				            ${tagValue.enumCode}
				          </@inputSelectCell>	  
				                
				      </#if>  
				    </tr>        
				  </#list>               
                  
                </td>
              </tr>
	    <#--<@accountingTagsSelectRows tags=tagTypes prefix="acctgTagEnumId" entity=paymentValue! />-->
	  </#if>
      <@inputSubmitRow title=uiLabelMap.CommonApply />
    </table>
  </div>
</form>
