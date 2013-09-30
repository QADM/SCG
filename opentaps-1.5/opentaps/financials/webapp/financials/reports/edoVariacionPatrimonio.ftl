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
<#macro listBalances type accounts>
    <#list accounts as account>
		<#if account?has_content>
      <tr>
        <td class="tabletext">${account.accountName?if_exists}</td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=account.contribuido isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=account.anteriores isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=account.ejercicio isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=account.ajustes isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=account.total isoCode=currencyUomId/></td>
      </tr>
     </#if>
    </#list>
</#macro>

<#if listaReporte?has_content>
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
		${uiLabelMap.HaciendaContribuido}
     </td>
     <td class="tableheadtext" align="right">
		${uiLabelMap.HaciendaGeneradoAnterior}
     </td>
     <td class="tableheadtext" align="right">
		${uiLabelMap.HaciendaGenerado}
     </td>     
     <td class="tableheadtext" align="right">
		${uiLabelMap.AjustesPorValor}
     <td class="tableheadtext" align="right">
		${uiLabelMap.AccountingTotalCapital}
     </td>
     <tr><td colspan="6">&nbsp;</td></tr>
   </tr>
   <@listBalances type="listaReporte" accounts=listaReporte/>

   
</table>
</#if>
