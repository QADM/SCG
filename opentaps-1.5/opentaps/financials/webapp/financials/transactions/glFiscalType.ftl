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
-->
<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>
<#-- display the list of transactions -->
	
<#if listTransIds?has_content>
	<#assign acctTrans = listTransIds>
	   <#list acctTrans as mapAcctTrans>
        	<#if mapAcctTrans.acctgTransId?contains("P")>         	  
	        	<div class="screenlet-header"> 	
	 				<span class="boxhead">${uiLabelMap.FinancialsTransactionPresupuesto}</span>  
				</div>
			<#else>
				<div class="screenlet-header"> 	
	 				<span class="boxhead">${uiLabelMap.FinancialsTransactionContable}</span>  
				</div>
			</#if>
        	<table class="listTable" cellspacing="0" style="border:none;">          	  
		      <tr class="listTableHeader">
		        <td><span>${uiLabelMap.Transaction}</span></td>
		        <td><span>${uiLabelMap.Description}</span></td>       
		        <td></td>
		      </tr>
		      <tr class="${tableRowClass(entry_index)}">
		            <td><a class="linktext" href="<@ofbizUrl>viewAcctPresupuestal?acctgTransId=${mapAcctTrans.acctgTransId}</@ofbizUrl>">${mapAcctTrans.acctgTransId}</a></td>
		            <td>${mapAcctTrans.description}</td>			                                               
		      </tr> 
		      <tr class="${tableRowClass(entry_index)}">			                                 
		      </tr> 
     	  </table>
        </#list>  	       
  </#if>  

     
   

