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

<#assign currencyUomId = parameters.orgCurrencyUomId>  <!-- for some reason, putting this in context in main-decorator.bsh does not work -->
<#macro listBalances type accounts balances>
	<#assign totInicial = 0.0>
	<#assign totCargo = 0.0>
	<#assign totAbono = 0.0>   
	<#assign totalFinal = 0.0>
	<#assign totalFlujo = 0.0>
	<#assign total = 0.0>
	<#assign fromDateAccounts = fromDateAccountBalances.get(type)/>
	<#assign thruDateAccounts = thruDateAccountBalances.get(type)/>
    <#list accounts as account>
    	<#if account?has_content>
		    <#if fromDateAccounts.get(account)?exists>
		    	<#assign montoIni = fromDateAccounts.get(account)>
		    	<#assign totInicial = totInicial + montoIni>
		    <#else>
		    	<#assign montoIni = 0.0>
		    </#if>
		    <#if cuentasCredito.get(account.glAccountId)?exists>
		    	<#assign montoAbono = cuentasCredito.get(account.glAccountId)>
		    	<#assign totAbono = totAbono + montoAbono>
		    <#else>
		    	<#assign montoAbono = 0.0>
		    </#if>   
		    <#if cuentasDebito.get(account.glAccountId)?exists>
		    	<#assign montoCargo = cuentasDebito.get(account.glAccountId)>
		    	<#assign totCargo = totCargo + montoCargo>
		    <#else>
		    	<#assign montoCargo = 0.0>
		    </#if>
		    <#assign montoFin = montoIni + montoCargo - montoAbono>
			<#assign montoFlujo = montoIni - montoFin>
			<#assign totalFinal = totalFinal + montoFin>
			<#assign totalFlujo = totalFlujo + montoFlujo>    
      <tr>
        <td class="tabletext">${account.accountCode?if_exists}: ${account.accountName?if_exists} (<a href="<@ofbizUrl>AccountActivitiesDetail?glAccountId=${account.glAccountId?if_exists}&organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="buttontext">${account.glAccountId?if_exists}</a>) </td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=fromDateAccounts.get(account) isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=cuentasDebito.get(account.glAccountId) isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=cuentasCredito.get(account.glAccountId) isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=montoFin isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=montoFlujo isoCode=currencyUomId/></td>
      </tr>
      <#if balances.get(account)?exists>
      	<#assign total = total + balances.get(account)>
      </#if>
     </#if>
    </#list>
    <tr><td colspan="6"><hr/></td></tr>
</#macro>

<#macro displaySummary summary >
      <tr>
        <td class="tableheadtext" align="left">${summary?if_exists}</td>
        <td class="tabletext" align="right"  style="padding-right: 5px"><@ofbizCurrency amount=totInicial isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"  style="padding-right: 5px"><@ofbizCurrency amount=totCargo isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"  style="padding-right: 5px"><@ofbizCurrency amount=totAbono isoCode=currencyUomId/></td>       
        <td class="tabletext" align="right"  style="padding-right: 5px"><@ofbizCurrency amount=totalFinal isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"  style="padding-right: 5px"><@ofbizCurrency amount=totalFlujo isoCode=currencyUomId/></td>   
      </tr>
      <tr><td>&nbsp;</td></tr>
</#macro>

<#if assetAccounts?has_content>
<div style="border: 1px solid #999999; margin-top: 20px; margin-bottom: 20px;"></div>
<div class="tabletext">
<table>
   <tr>
      <td colspan="6" class="tableheadtext" align="center">${uiLabelMap.EdoAnaliticoActivo} de ${parameters.organizationName?if_exists} (${organizationPartyId}) (${fromGlFiscalType.description})</td>
   </tr>
   <tr><td colspan="6">&nbsp;</td></tr>
   <tr>
	 <td class="tableheadtext" align="left">
	 ${uiLabelMap.AccountingAccount}
	 </td>
     <td class="tableheadtext" align="right" >
       ${uiLabelMap.AccountingOpeningBalance}
     </td>
     <td class="tableheadtext" align="right">
       ${uiLabelMap.FormFieldTitle_debit}
     </td>
     <td class="tableheadtext" align="right">
       ${uiLabelMap.FormFieldTitle_credit}
     </td>     
     <td class="tableheadtext" align="right">
       ${uiLabelMap.AccountingFinalBalance}
     <td class="tableheadtext" align="right">
     ${uiLabelMap.AccountingCashFlow}
     </td>
     <tr><td colspan="6">&nbsp;</td></tr>
   </tr>
   <@listBalances type="assetAccountBalances" accounts=assetAccounts balances=assetAccountBalances/>
   <@displaySummary summary=uiLabelMap.AccountingAssets/>
   
</table>
</#if>
