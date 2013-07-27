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
  <#assign total = 0.0>
  <#assign fromDateAccounts = fromDateAccountBalances.get(type)/>
  <#assign thruDateAccounts = thruDateAccountBalances.get(type)/>
    <#list accounts as account>
    <#if fromDateAccounts.get(account)?exists>
    	<#assign montoIni = fromDateAccounts.get(account)>
    <#else>
    	<#assign montoIni = 0.0>
    </#if>
    <#if cuentasCredito.get(account.glAccountId)?exists>
    	<#assign montoAbono = cuentasCredito.get(account.glAccountId)>
    <#else>
    	<#assign montoAbono = 0.0>
    </#if>   
    <#if cuentasDebito.get(account.glAccountId)?exists>
    	<#assign montoCargo = cuentasDebito.get(account.glAccountId)>
    <#else>
    	<#assign montoCargo = 0.0>
    </#if>
    <#assign montoFin = montoIni + montoCargo - montoAbono>
	<#assign montoFlujo = montoIni - montoFin>    
     <#if account?has_content>
      <tr>
        <td class="tabletext">${account.accountCode?if_exists}: ${account.accountName?if_exists} (<a href="<@ofbizUrl>AccountActivitiesDetail?glAccountId=${account.glAccountId?if_exists}&organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="buttontext">${account.glAccountId?if_exists}</a>) </td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=fromDateAccounts.get(account) isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=cuentasDebito.get(account.glAccountId) isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=cuentasCredito.get(account.glAccountId) isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=montoFin isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=montoFlujo isoCode=currencyUomId/></td>
      </tr>
      <#assign total = total + balances.get(account)>
     </#if>
    </#list>
      <tr><td colspan="6"><hr/></td></tr>
      <tr>
        <td colspan="6" class="tableheadtext" align="right"><@ofbizCurrency amount=total isoCode=currencyUomId/></td>
      </tr>
      <tr><td colspan="6">&nbsp;</td></tr>
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
     <td class="tableheadtext" align="left">${uiLabelMap.AccountingAccount}</td>
     <td class="tableheadtext" align="right">
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
   </tr>
   <tr><td class="tableheadtext" align="left">${uiLabelMap.AccountingAssets}</td></tr>
   <@listBalances type="assetAccountBalances" accounts=assetAccounts balances=assetAccountBalances/>
   
</table>
</#if>
