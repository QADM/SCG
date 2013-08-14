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

<script type="text/javascript">
	function borrarArbol(nombre){
	
		
		
	}
</script>

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>
<form method="POST" name="createOperacionDiariaIngresos" action=""> <#-- action set by the screen -->
  <input type="hidden" name="organizationPartyId" value="${organizationPartyId}"/>
  <input type="hidden" name="glFiscalTypeId" value="ACTUAL"/>
  <div class="form" style="border:0">
    <table class="fourColumnForm" style="border:0">
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialDocumentType titleClass="requiredField"/>
        <@inputSelectCell list=listDocumentos?if_exists displayField="descripcion" name="idTipoDoc" default=idTipoDoc?if_exists />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsGlFiscalType titleClass="requiredField"/>
        <@inputSelectCell list=listFiscalTypes?if_exists displayField="description" name="glFiscalTypeId" default=glFiscalTypeId?if_exists />
      </tr>      
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsTransactionDate />
        <@inputDateTimeCell name="transactionDate" default=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsAccountigDate titleClass="requiredField"/>
        <@inputDateTimeCell name="accountingDate" default=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
      </tr>      
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsReferenceDocument />
        <@inputTextCell name="refDocument" maxlength=60  />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsSequence />
        <@inputTextCell name="sequence" maxlength=60  />
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsCatalogType />
        <@inputTextCell name="catalogType" maxlength=60  />
      </tr>  
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsProduct />
        <@inputSelectCell list=listProducts?if_exists displayField="description" name="productId" default=productId?if_exists key="productId"/>
      </tr>
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsCaption />
        <@inputSelectCell list=listRubros?if_exists displayField="description" name="productCategoryIdRu" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>         
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsType />
        <@inputSelectCell list=listTipos?if_exists displayField="description" name="productCategoryIdTi" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr> 
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsClass />
        <@inputSelectCell list=listClases?if_exists displayField="description" name="productCategoryIdCl" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>    
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsConcept />
        <@inputSelectCell list=listConceptos?if_exists displayField="description" name="productCategoryIdCon" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>    
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsN5 />
        <@inputSelectCell list=listN5?if_exists displayField="description" name="productCategoryIdN5" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsFederalEntity />
      	<@padresGeo name="EntidadFederativa" geoCode="MEX" hijoName="Region" />
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsRegion />
      	<@padresGeo name="Region" hijoName="Municipio"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsTown />
      	<@padresGeo name="Municipio" hijoName="Localidad"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsLocality />
      	<@padresGeo name="Localidad" />
      </tr>                         
      <tr>
        <@displayTitleCell title=uiLabelMap.CommonAmount titleClass="requiredField" />
        <@inputTextCell size="10" name="amount" />
      </tr>
      <@inputSubmitRow title=uiLabelMap.CommonCreate />
    </table>
  </div>
</form>
