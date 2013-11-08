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
<form method="POST" name="createOperacionDiariaIngresos" action="${creaOpDiariaIngresos}"> <#-- action set by the screen -->
  <input type="hidden" name="organizationPartyId" value="${organizationPartyId}"/>
  <div class="form" style="border:0">
    <table class="fourColumnForm" style="border:0">
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialDocumentType />
        <@inputSelectCell list=listDocumentos?if_exists displayField="descripcion" name="Tipo_Documento" default=idTipoDoc?if_exists key="idTipoDoc" 
        			onChange="opentaps.getTipoFiscalByDoc(this,'Tipo_Fiscal');" />
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
        <@displayTitleCell title=uiLabelMap.FinancialsReferenceDocument titleClass="requiredField"/>
        <@inputTextCell name="Referencia_Documento" maxlength=60  />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsSequence titleClass="requiredField"/>
        <@inputTextCell name="Secuencia" maxlength=20  />
      </tr>         
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsProductCredit />
        <@inputSelectCell list=listProducts?if_exists displayField="description" name="Id_Producto_Abono" default=productId?if_exists key="productId" required=false />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsProductDebit />
        <@inputSelectCell list=listProducts?if_exists displayField="description" name="Id_Producto_Cargo" default=productId?if_exists key="productId" required=false />
      </tr>      
      
      <#list tagTypes as tag>
        <#if tag.isRequired()>
          <#assign titleClass="requiredField" />
        <#else/>
          <#assign titleClass="tableheadtext" />
        </#if>        
        <tr>
          <@displayTitleCell title=tag.description titleClass=titleClass /> 
          <#if tag.description?contains("Administrativa")>         
	          <@inputSelectCell name="clasificacion${tag.index}" errorField="acctgTagEnumId${tag.index}" list=tag.activeTagValues key="externalId" required=false default=tag.defaultValue! ; tagValue>
	            ${tagValue.groupName}
	          </@inputSelectCell>            
           <#elseif tag.description?contains("geo")>         
	          <@inputSelectCell name="clasificacion${tag.index}" errorField="acctgTagEnumId${tag.index}" list=tag.activeTagValues key="geoId" required=false default=tag.defaultValue! ; tagValue>
	            ${tagValue.geoName}
	          </@inputSelectCell>  
          <#elseif tag.description?contains("CRI") || tag.description?contains("COG") >         
	          <@inputSelectCell name="clasificacion${tag.index}" errorField="acctgTagEnumId${tag.index}" list=tag.activeTagValues key="categoryName" required=false default=tag.defaultValue! ; tagValue>
	            ${tagValue.description}
	          </@inputSelectCell>  
	      <#elseif tag.description?contains("Programatica") || tag.description?contains("COG") >         
	          <@inputSelectCell name="clasificacion${tag.index}" errorField="acctgTagEnumId${tag.index}" list=tag.activeTagValues key="workEffortName" required=false default=tag.defaultValue! ; tagValue>
	            ${tagValue.description}
	          </@inputSelectCell>
	      <#elseif tag.description?contains("Ciclo")>         
	          <@inputSelectCell name="clasificacion${tag.index}" errorField="acctgTagEnumId${tag.index}" list=tag.activeTagValues key="nivelId" required=false default=tag.defaultValue! ; tagValue>
	            ${tagValue.nivelId}
	          </@inputSelectCell>
	      <#else>         
	          <@inputSelectCell name="clasificacion${tag.index}" errorField="acctgTagEnumId${tag.index}" list=tag.activeTagValues key="enumId" required=false default=tag.defaultValue! ; tagValue>
	            ${tagValue.enumCode}
	          </@inputSelectCell>
          </#if>     
        </tr>        
      </#list>   
     <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsIdLeviedD />
      	<@inputSelectCell list=listPayments?if_exists name="Id_RecaudadoH" displayField="description" default=paymentMethodId?if_exists key="paymentMethodId" required=false/>
      </tr>                                                                  
      <tr>                             
      <tr>
        <@displayTitleCell title=uiLabelMap.CommonAmount  titleClass="requiredField"/>
        <@inputTextCell size="10" name="Monto" />
      </tr>
      <@inputSubmitRow title=uiLabelMap.CommonCreate />
    </table>
  </div>
</form>
