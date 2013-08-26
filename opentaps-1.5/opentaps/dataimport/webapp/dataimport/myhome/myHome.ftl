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
-->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<#macro importForm importService label sectionLabel submitLabel processed notProcessed reportHref reportLabel>
    <form name="${importService}Form" method="post" action="setServiceParameters">
      <@inputHidden name="SERVICE_NAME" value="${importService}"/>
      <@inputHidden name="POOL_NAME" value="pool"/>
      <@inputHidden name="sectionHeaderUiLabel" value="${sectionLabel}"/>
      <@displayCell text="${label}:"/>
      <@displayCell text="${processed}"/>
      <@displayCell text="${notProcessed}"/>
      <#if hasDIAdminPermissions?default(false)>
        <@inputSubmitCell title="${submitLabel}"/>
      </#if>
      <@displayLinkCell href="${reportHref}" text="${reportLabel}" class="buttontext"/>
    </form>
    
</#macro>

<table  class="headedTable">
  <tr class="header">
    <@displayCell text=uiLabelMap.DataImportImporting/>
    <@displayCell text=uiLabelMap.DataImportNumberProcessed/>
    <@displayCell text=uiLabelMap.DataImportNumberNotProcessed/>
    <#if hasFullPermissions?default(false)><td>&nbsp;</td></#if>
  </tr>
  <tr>
    <@importForm importService="importCustomers"
                 sectionLabel="DataImportImportCustomers"
                 label=uiLabelMap.FinancialsCustomers
                 submitLabel=uiLabelMap.DataImportImport
                 processed=customersProcessed notProcessed=customersNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importSuppliers"
                 sectionLabel="DataImportImportSuppliers"
                 label=uiLabelMap.PurchSuppliers
                 submitLabel=uiLabelMap.DataImportImport
                 processed=suppliersProcessed notProcessed=suppliersNotProcessed
                 reportHref="setupReport?reportId=SUPPL_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importProducts"
                 sectionLabel="DataImportImportProducts"
                 label=uiLabelMap.ProductProducts
                 submitLabel=uiLabelMap.DataImportImport
                 processed=productsProcessed notProcessed=productsNotProcessed
                 reportHref="setupReport?reportId=PROD_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importProductInventory"
                 sectionLabel="DataImportImportInventory"
                 label=uiLabelMap.ProductInventoryItems
                 submitLabel=uiLabelMap.DataImportImport
                 processed=inventoryProcessed notProcessed=inventoryNotProcessed
                 reportHref="setupReport?reportId=INVENT_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importGlAccounts"
                 sectionLabel="DataImportImportGlAccounts"
                 label="Lista de Cuentas"
                 submitLabel=uiLabelMap.DataImportImport
                 processed=glAccountsProcessed notProcessed=glAccountsNotProcessed
                 reportHref="setupReport?reportId=GL_ACCTS_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
   <tr>
    <@importForm importService="importTag"
                 sectionLabel="DataImportImportTag"
                 label=uiLabelMap.DataImportImportTag
                 submitLabel=uiLabelMap.DataImportImport
                 processed=tagProcessed notProcessed=tagNotProcessed
                 reportHref="setupReport?reportId=Tag_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importParty"
                 sectionLabel="DataImportImportParty"
                 label="Clasificaci�n administrativa"
                 submitLabel=uiLabelMap.DataImportImport
                 processed=partyProcessed notProcessed=partyNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  
  <tr>
    <@importForm importService="importProject"
                 sectionLabel="DataImportImportProject"
                 label="Clasificaci�n program�tica"
                 submitLabel=uiLabelMap.DataImportImport
                 processed=projectProcessed notProcessed=projectNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importGeo"
                 sectionLabel="DataImportImportGeo"
                 label="Clasificaci�n geogr�fica"
                 submitLabel=uiLabelMap.DataImportImport
                 processed=geoProcessed notProcessed=geoNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importCategory"
                 sectionLabel="DataImportImportCategory"
                 label="CRI / COG / CA"
                 submitLabel=uiLabelMap.DataImportImport
                 processed=categoryProcessed notProcessed=categoryNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importPresupuestoIngreso"
                 sectionLabel="DataImportImportPresupuestoIngreso"
                 label=uiLabelMap.DataImportPresupuestoIngreso
                 submitLabel=uiLabelMap.DataImportImport
                 processed=presupuestoIngresoProcessed notProcessed=presupuestoIngresoNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
    <@importForm importService="importPresupuestoEgreso"
                 sectionLabel="DataImportImportPresupuestoEgreso"
                 label=uiLabelMap.DataImportPresupuestoEgreso
                 submitLabel=uiLabelMap.DataImportImport
                 processed=presupuestoEgresoProcessed notProcessed=presupuestoEgresoNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
    <tr>
    <@importForm importService="importIngresoDiario"
                 sectionLabel="DataImportImportIngresoDiario"
                 label=uiLabelMap.DataImportImportIngresoDiario
                 submitLabel=uiLabelMap.DataImportImport
                 processed=ingresoDiarioProcessed notProcessed=ingresoDiarioNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
    <tr>
    <@importForm importService="importEgresoDiario"
                 sectionLabel="DataImportImportEgresoDiario"
                 label=uiLabelMap.DataImportImportEgresoDiario
                 submitLabel=uiLabelMap.DataImportImport
                 processed=egresoDiarioProcessed notProcessed=egresoDiarioNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
    <tr>
    <@importForm importService="importOperacionDiaria"
                 sectionLabel="DataImportImportOperacionDiaria"
                 label=uiLabelMap.DataImportImportOperacionDiaria
                 submitLabel=uiLabelMap.DataImportImport
                 processed=operacionDiariaProcessed notProcessed=operacionDiariaNotProcessed
                 reportHref="setupReport?reportId=CUST_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  <tr>
      <@importForm importService="importMatrizEgr"
                 sectionLabel="DataImportMatrizEgr"
                 label=uiLabelMap.DataImportMatrizEgr
                 submitLabel=uiLabelMap.DataImportImport
                 processed=matrizEgrProcessed notProcessed=matrizEgrNotProcessed
                 reportHref="setupReport?reportId=MATRIZ_EGR&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  
  <tr>
    <@importForm importService="importMatrizIng"
                 sectionLabel="DataImportMatrizIng"
                 label=uiLabelMap.DataImportMatrizIng
                 submitLabel=uiLabelMap.DataImportImport
                 processed=matrizIngProcessed notProcessed=matrizIngNotProcessed
                 reportHref="setupReport?reportId=MATRIZ_ING&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>
  
  
  
  <tr>
    <@importForm importService="importOrders"
                 sectionLabel="DataImportImportOrders"
                 label=uiLabelMap.DataImportOrderLines
                 submitLabel=uiLabelMap.DataImportImport
                 processed=orderHeadersProcessed notProcessed=orderHeadersNotProcessed
                 reportHref="setupReport?reportId=ORDER_H_IMP&amp;sectionName=myHome"
                 reportLabel=uiLabelMap.OpentapsReport/>
  </tr>   
   
  <tr>
    <@displayCell text="${uiLabelMap.DataImportOrderItemLines}:"/>
    <@displayCell text="${orderItemsProcessed}"/>
    <@displayCell text="${orderItemsNotProcessed}"/>
    <td>&nbsp;</td>
    <@displayLinkCell href="setupReport?reportId=ORDER_I_IMP&amp;sectionName=myHome" text=uiLabelMap.OpentapsReport class="buttontext" />
  </tr>
</table>

<br/>

<#if hasDIAdminPermissions?default(false)>
  <@frameSection title=uiLabelMap.DataImportUploadFile>
    <form name="uploadFileAndImport" method="post" enctype="multipart/form-data" action="uploadFileForDataImport">
      <@inputHidden name="POOL_NAME" value="pool"/>
      <@inputHidden name="sectionHeaderUiLabel" value="DataImportImportFromFile"/>
      <table class="twoColumnForm">
        <@inputFileRow title=uiLabelMap.DataImportFileToImport name="uploadedFile" />
        <tr>
          <@displayTitleCell title=uiLabelMap.DataImportUploadFileFormat />
          <td>
            <select name="fileFormat" class="inputBox">
              <option value="EXCEL">${uiLabelMap.DataImportUploadFileFormatExcel}</option>
            </select>
          </td>
        </tr>
        <@inputSubmitRow title="${uiLabelMap.DataImportUpload}"/>
      </table>
    </form>
  </@frameSection>
</#if>

<#if hasOrgConfigPermissions?default(false)>
  <@frameSection title=uiLabelMap.DataImportCopyLedgerSetup>
    <form name="copyOrganizationLedgerSetupForm" method="post" action="scheduleService">
      <@inputHidden name="SERVICE_NAME" value="copyOrganizationLedgerSetup"/>
      <@inputHidden name="POOL_NAME" value="pool"/>
      <@inputHidden name="_RUN_SYNC_" value="Y"/>
      <@inputHidden name="sectionHeaderUiLabel" value="${uiLabelMap.DataImportCopyLedgerSetup}"/>

      <table class="twoColumnForm">
       <@inputSelectRow name="templateOrganizationPartyId" title=uiLabelMap.DataImportFromOrganizationTemplate list=fromOrganizationTemplates key="partyId" ; option>
         ${option.groupName} (${option.partyId})
       </@inputSelectRow>        
       <@inputSelectRow name="organizationPartyId" title=uiLabelMap.DataImportToOrganization list=toOrganizations key="partyId" ; option>
         ${option.groupName} (${option.partyId})
       </@inputSelectRow>        
       <@inputSubmitRow title="${uiLabelMap.CommonCopy}"/>
      </table>
    </form>
  </@frameSection>
</#if>