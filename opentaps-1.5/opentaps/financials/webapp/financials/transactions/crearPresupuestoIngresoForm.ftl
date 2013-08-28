<#--
 * Ftl que permite ingresar Presupuesto ingreso
 *
 *  
 *  
--><script type="text/javascript">
	function borrarArbol(nombre){
	
		
		
	}
</script>
<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<form method="POST" name="crearPresupuestoIngresoForm" action="${createAcctgPresupuestoIngresoFormTarget}">  
  <input type="hidden" name="organizationPartyId" value="${organizationPartyId}"/>
  <input type="hidden" name="glFiscalTypeId" value="BUDGET"/>
  <input type="hidden" name="acctgTransTypeId" value="TINGRESOESTIMADO"/>
  <input type="hidden" name="description" value="Carga Manual Presupuesto Ingreso"/>
  <div class="form" style="border:0">
    <@inputHidden name="performFind" value="Y"/>
  	<table class="twoColumnForm">
    <tbody>
      <tr>
	      <@displayTitleCell title=uiLabelMap.FinancialsTransactionType />
		  <@displayCell text=uiLabelMap.FinancialsBudgetTipoTransaccion />
	  </tr>
	  <tr>
		  <@displayTitleCell title=uiLabelMap.FinancialsBudgetTipoFiscalLabel />
		  <@displayCell text=uiLabelMap.FinancialsBudgetTipoFiscal />
	  </tr>
	  <tr>
	  		<@inputSelectRow title=uiLabelMap.FinancialsBudgetUnidadE required=false list=listUnidadE key="partyId"  displayField="groupName" name="unidadEjecutora" default=partyId?if_exists />
	  </tr>	
	  <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsCaption />
        <@inputSelectCell list=listRubros?if_exists displayField="description" name="idRubro" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>         
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsType />
        <@inputSelectCell list=listTipos?if_exists displayField="description" name="idTipo" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr> 
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsClass />
        <@inputSelectCell list=listClases?if_exists displayField="description" name="idClase" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>    
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsConcept />
        <@inputSelectCell list=listConceptos?if_exists displayField="description" name="idConcepto" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>    
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsN5 />
        <@inputSelectCell list=listN5?if_exists displayField="description" name="idN5" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsFederalEntity />
      	<@padresGeo name="EntidadFederativa" geoCode="MEX" hijoName="Region"  nietos="Municipio,Localidad"/>
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
      	<@padresGeo name="Localidad"/>
      </tr>                         
     </tr>
      <tr>
	      <@displayTitleCell title=uiLabelMap.FinancialsTransactionDate />
	      <@inputDateTimeCell name="fechaTransaccion" default=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
      </tr>
      <tr>
			<@displayTitleCell title=uiLabelMap.FinancialsTransactionDate />
			<@inputDateTimeCell name="fechaContable" default=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
      </tr>
      <tr>
      	    <@inputTextRow title=uiLabelMap.FinancialsBudgetClave name="clave" />
	  </tr>
	  <tr>
	  		<@inputTextRow title=uiLabelMap.FinancialsPostedAmount name="amount" />
	  </tr>
	  <tr>
	  		<@inputTextRow title=uiLabelMap.FinancialsBudgetReferencia name="referencia" />
	  </tr>
	  <tr>
	  		<@inputSelectRow title=uiLabelMap.FinancialsBudgetSubfuenteE required=false list=listSubFuente  displayField="description" key="enumId"  name="subFuenteEsp" default=description?if_exists />
	  </tr>
	  
	  <tr>
	  		<@inputSubmitRow title=uiLabelMap.CommonCreate />
	  </tr>      
    </tbody>
  </table>
  </div>
</form>
