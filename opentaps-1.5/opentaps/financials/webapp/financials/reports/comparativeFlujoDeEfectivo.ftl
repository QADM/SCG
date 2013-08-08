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

<#macro listBalances type accounts balances1 balances2 balances3>
	<#assign totIni = 0.0>
	<#assign totFin = 0.0>
	<#assign totDif = 0.0>   
   <#list accounts as account>   
     <#if account?has_content && (balances1.get(account)?has_content || balances2.get(account)?has_content)>
       <tr>
         <td class="tabletext">${account.accountCode?if_exists}: ${account.accountName?if_exists} (<a href="<@ofbizUrl>AccountActivitiesDetail?glAccountId=${account.glAccountId?if_exists}&organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="buttontext">${account.glAccountId?if_exists}</a>)</td>
         <td class="tabletext" align="right"><@ofbizCurrency amount=balances1.get(account) isoCode=currencyUomId/></td>
         <td class="tabletext" align="right"><@ofbizCurrency amount=balances2.get(account) isoCode=currencyUomId/></td>
         <td class="tabletext" align="right"><@ofbizCurrency amount=balances3.get(account) isoCode=currencyUomId/></td>
       </tr>
		<#if balances1.get(account)?exists>
			<#assign totIni = totIni + balances1.get(account)>
		</#if>
		<#if balances2.get(account)?exists>
			<#assign totFin = totFin + balances2.get(account)>
		</#if>   
		<#if balances3.get(account)?exists>
			<#assign totDif = totDif + balances3.get(account)>
		</#if>         
     </#if>
   </#list>
   <tr><td colspan="4"><hr/></td></tr>
</#macro>

<#macro displaySummary summary >
      <tr>
        <td class="tableheadtext" align="left">${summary?if_exists}</td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=totIni isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=totFin isoCode=currencyUomId/></td>
        <td class="tabletext" align="right"><@ofbizCurrency amount=totDif isoCode=currencyUomId/></td>        
      </tr>
      <tr><td>&nbsp;</td></tr>
</#macro>
    
<#if set1FlujoDeEfectivo?has_content || set2FlujoDeEfectivo?has_content>
  <#assign currencyUomId = parameters.orgCurrencyUomId>  <!-- for some reason, putting this in context in main-decorator.bsh does not work -->
  <div style="border: 1px solid #999999; margin-top: 20px; margin-bottom: 20px;"></div>
  <table>
    <tr>
       <td colspan="2" class="tableheadtext" align="right">${uiLabelMap.EdoFlujoEfect} for ${parameters.organizationName?if_exists} (${organizationPartyId})</td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr>
      <td class="tableheadtext" align="left">${uiLabelMap.AccountingAccount}</td>
      <td class="tableheadtext" align="right" style="white-space:nowrap">
        ${getLocalizedDate(fromDate1, "DATE")} - ${getLocalizedDate(thruDate1, "DATE")}<br/>
        (${glFiscalType1.description})
      </td>
      <td class="tableheadtext" align="right" style="white-space:nowrap">
        ${getLocalizedDate(fromDate2, "DATE")} - ${getLocalizedDate(thruDate2, "DATE")}<br/>
        (${glFiscalType2.description})
      <td class="tableheadtext" align="right">${uiLabelMap.OpentapsDifference}</td>
    </tr>

   <@listBalances type="cuentasImpuestoList" accounts=cuentasImpuestoList balances1=cuentasImpuestoFr balances2=cuentasImpuestoTr balances3=difCuentasImpuesto/>
   <@displaySummary summary=uiLabelMap.Impuestos/>
 
   <@listBalances type="cuentasContribuList" accounts=cuentasContribuList balances1=cuentasContribuFr balances2=cuentasContribuTr balances3=difCuentasImpuesto/>
   <@displaySummary summary=uiLabelMap.ContribucionesMejoras/>

   <@listBalances type="cuentasParticipaList" accounts=cuentasParticipaList balances1=cuentasContribuFr balances2=cuentasContribuTr balances3=difCuentasImpuesto/>
   <@displaySummary summary=uiLabelMap.ParticipacionesAportaciones/>
   
   <@listBalances type="cuentasTransfeList" accounts=cuentasTransfeList balances1=cuentasTransfeFr balances2=cuentasTransfeTr balances3=difCuentasTransfe/>
   <@displaySummary summary=uiLabelMap.TransferenciasAsignacionesSubsidiosOtras/>   
   
   <@listBalances type="cuentasOtrosIngList" accounts=cuentasOtrosIngList balances1=cuentasOtrosIngFr balances2=cuentasOtrosIngTr balances3=difCuentasOtrosIng/>
   <@displaySummary summary=uiLabelMap.OtrosIngresosBeneficios/>    
   
   <@listBalances type="cuentasIcreVariaList" accounts=cuentasIcreVariaList balances1=cuentasIcreVariaFr balances2=cuentasIcreVariaTr balances3=difCuentasIcreVaria/>
   <@displaySummary summary=uiLabelMap.IncrementoVariacionInventarios/>       
   
   <@listBalances type="cuentasGastosFunList" accounts=cuentasGastosFunList balances1=cuentasGastosFunFr balances2=cuentasGastosFunTr balances3=difCuentasGastosFun/>
   <@displaySummary summary=uiLabelMap.GastosFuncionamiento/> 
   
   <@listBalances type="cuentaPartiAportaList" accounts=cuentaPartiAportaList balances1=cuentaPartiAportaFr balances2=cuentaPartiAportaTr balances3=difCuentaPartiAporta/>
   <@displaySummary summary=uiLabelMap.ParticipacionesAportacionesGastos/>    
   
   <@listBalances type="cuentaInteComisList" accounts=cuentaInteComisList balances1=cuentaInteComisFr balances2=cuentaInteComisTr balances3=difCuentaInteComis/>
   <@displaySummary summary=uiLabelMap.InteresesComisionesDeudaPublica/>    
      
   <@listBalances type="cuentasOtrosGastosList" accounts=cuentasOtrosGastosList balances1=cuentasOtrosGastosFr balances2=cuentasOtrosGastosTr balances3=difCuentasOtrosGastos/>
   <@displaySummary summary=uiLabelMap.OtrosGastosYPerdidasExtra/>           

	</table>
</#if>
