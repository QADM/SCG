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
<form method="POST" name="createOperacionDiariaEgresos" action="${creaOpDiariaEgresos}"> <#-- action set by the screen -->
  <input type="hidden" name="organizationPartyId" value="${organizationPartyId}"/>
  <div class="form" style="border:0">
    <table class="fourColumnForm" style="border:0">
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialDocumentType />
        <@inputSelectCell list=listDocumentos?if_exists displayField="descripcion" name="Tipo_Documento" default=idTipoDoc?if_exists key="idTipoDoc" 
        			onChange="opentaps.getTipoFiscalByDoc(this,'Tipo_Fiscal');" />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsGlFiscalType />
        <@inputSelectCell list="" name="Tipo_Fiscal" />
      </tr>      
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsTransactionDate />
        <@inputDateTimeCell name="Fecha_Transaccion" default=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsAccountigDate />
        <@inputDateTimeCell name="Fecha_Contable" default=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
      </tr>      
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsReferenceDocument />
        <@inputTextCell name="Referencia_Documento" maxlength=60  />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsSequence />
        <@inputTextCell name="Secuencia" maxlength=20  />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsKeyBudget />
        <@inputTextCell name="Cve_Presupuestal" maxlength=100   />
      </tr>      
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsProductCredit />
        <@inputSelectCell list=listProducts?if_exists displayField="description" name="Id_Producto_Abono" default=productId?if_exists key="productId"/>
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsProductDebit />
        <@inputSelectCell list=listProducts?if_exists displayField="description" name="Id_Producto_Cargo" default=productId?if_exists key="productId"/>
      </tr>      
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsFederalEntity />
      	<@padresGeo name="EntidadFederativa" geoCode="MEX" hijoName="Region" nietos="Municipio,Localidad"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsRegion />
      	<@padresGeo name="Region" hijoName="Municipio" nietos="Localidad"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsTown />
      	<@padresGeo name="Municipio" hijoName="Localidad"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsLocality />
      	<@padresGeo name="Localidad" />
      </tr>  
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsUnderSpecificSource />
        <@inputSelectCell list=listaSubfuente?if_exists displayField="description" name="Sub_Fuente_Especifica" default=enumId?if_exists key="enumId"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsExecutingUnit />
        <@inputSelectCell list=listaUnidades?if_exists displayField="groupName" name="Unidad_Ejecutora" default=partyId?if_exists key="partyId"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsSubfunction />
        <@inputSelectCell list=listSubfunciones?if_exists displayField="description" name="Subfuncion" default=enumId?if_exists key="enumId"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsExpenseType />
        <@inputSelectCell list=listTipoGastos?if_exists displayField="description" name="Tipo_Gasto" default=enumId?if_exists key="enumId"/>
      </tr>      
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsSpecifiedItem />
        <@inputSelectCell list=listPartidasEsp?if_exists displayField="description" name="Partida_Especifica" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>      
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsActivity />
        <@inputSelectCell list=listaActividades?if_exists displayField="description" name="Actividad" default=workEffortTypeId?if_exists key="workEffortTypeId"/>
      </tr>      
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsArea />
        <@inputSelectCell list=listAreas?if_exists displayField="description" name="Area" default=enumId?if_exists key="enumId"/>
      </tr>      
     <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsIdLeviedH />
      	<@inputSelectCell list=listPayments?if_exists name="Id_RecaudadoH" displayField="description" default=paymentMethodId?if_exists key="paymentMethodId" />
      </tr>                                                                  
      <tr>                             
      <tr>
        <@displayTitleCell title=uiLabelMap.CommonAmount  />
        <@inputTextCell size="10" name="Monto" />
      </tr>
      <@inputSubmitRow title=uiLabelMap.CommonCreate />
    </table>
  </div>
</form>
