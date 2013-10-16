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
	      <@displayTitleCell title=uiLabelMap.FinancialsTransactionDate titleClass="requiredField"/>
	      <@inputDateTimeCell name="fechaTransaccion" default=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
      </tr>
      <tr>
			<@displayTitleCell title=uiLabelMap.FinancialsTransactionDateContable titleClass="requiredField"/>
			<@inputDateTimeCell name="fechaContable" default=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
      </tr>
	  <tr>
	  		<@inputSelectRow title=uiLabelMap.FinancialsBudgetUnidadE required=false list=listUnidadE key="partyId"  displayField="groupName" name="unidadEjecutora" default=partyId?if_exists titleClass="requiredField" />
	  </tr>		  
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsN5 titleClass="requiredField" />
        <@inputSelectCell list=listN5?if_exists displayField="description" name="idN5" default=productCategoryId?if_exists key="productCategoryId"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsFederalEntity titleClass="requiredField" />
      	<@padresGeo name="EntidadFederativa" geoCode="MEX" hijoName="Region"  nietos="Municipio,Localidad"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsRegion titleClass="requiredField" />
      	<@padresGeo name="Region" hijoName="Municipio" nietos="Localidad"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsTown titleClass="requiredField" />
      	<@padresGeo name="Municipio" hijoName="Localidad"/>
      </tr>
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsLocality titleClass="requiredField" />
      	<@padresGeo name="Localidad"/>
      </tr>  
      <tr>
	  	<@inputSelectRow title=uiLabelMap.FinancialsBudgetSubfuenteE required=false list=listSubFuente  displayField="description" key="enumId"  name="subFuenteEsp" default=description?if_exists titleClass="requiredField" />
	  </tr>	                         
       
      <tr>
	  	<@inputTextRow title=uiLabelMap.FinancialsBudgetReferencia name="referencia" titleClass="requiredField" />
	  </tr>  
	  <tr>
	  	<@inputTextRow title=uiLabelMap.FinancialsPostedAmount name="amount" titleClass="requiredField" />
	  </tr>	  
	  
	  <tr>
	  	<@inputSubmitRow title=uiLabelMap.CommonCreate />
	  </tr>      
    </tbody>
  </table>
  </div>
</form>
