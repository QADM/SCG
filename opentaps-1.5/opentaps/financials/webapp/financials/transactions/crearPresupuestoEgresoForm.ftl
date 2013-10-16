<#--
 * Ftl que permite ingresar Presupuesto Egreso Manual
 * ECO
 *  
 *  
-->
<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<form method="POST" name="crearPresupuestoEgresoForm" action="${createAcctgPresupuestoEgresoFormTarget}">  
  <input type="hidden" name="organizationPartyId" value="${organizationPartyId}"/>  
  <input type="hidden" name="acctgTransTypeId" value="TPRESUPAPROBADO"/>
  <input type="hidden" name="description" value="Carga Manual Presupuesto Egreso"/>
  <div class="form" style="border:0">
    <@inputHidden name="performFind" value="Y"/>
  	<table class="twoColumnForm">
    <tbody>
     <tr>
	      <@displayTitleCell title=uiLabelMap.FinancialsTransactionType />
		  <@displayCell text=uiLabelMap.FinancialsBudgetTipoTransaccionEgreso />
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
	  		<@inputSelectRow title=uiLabelMap.FinancialsBudgetUnidadE required=false list=listUnidadE key="partyId"  displayField="groupName" name="unidadEjecutora" default=partyId?if_exists titleClass="requiredField"/>
	  </tr>	
      <tr>
      	<@inputSelectRow title=uiLabelMap.FinancialsBudgetSubfuenteE required=false list=listSubFuente  displayField="description" key="enumId"  name="subFuenteEsp" default=description?if_exists titleClass="requiredField"/>
      </tr>
      <tr>
      	<@inputSelectRow title=uiLabelMap.FinancialsBudgetSubfuncion required=false list=listsubfuncion  displayField="description" key="enumId"  name="subfuncion" default=description?if_exists titleClass="requiredField"/>
        
      </tr>
      <tr>
      	<@inputSelectRow title=uiLabelMap.FinancialsBudgetActividad required=false list=listactividad  displayField="description" key="workEffortId"  name="actividad" default=workEffortId?if_exists titleClass="requiredField"/>
        
      </tr>   
      <tr>
      	<@inputSelectRow title=uiLabelMap.FinancialsBudgetTipoGasto required=false list=listtipoGasto  displayField="description" key="enumId"  name="tipoGasto" default=description?if_exists titleClass="requiredField"/>
        
      </tr>      
      <tr>
      	<@inputSelectRow title=uiLabelMap.FinancialsBudgetPE required=false list=listpartidaEspecifica  
      		displayField="description" key="productCategoryId"  name="partidaEspecifica" default=productCategoryId?if_exists titleClass="requiredField" />
        
      </tr> 
      <tr>
      	<@displayTitleCell title=uiLabelMap.FinancialsFederalEntity titleClass="requiredField" />
      	<@padresGeo name="EntidadFederativa" geoCode="MEX" hijoName="Region" nietos="Municipio,Localidad"/>
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
      	<@padresGeo name="Localidad" />
      </tr>    
      <tr>
      	<@inputSelectRow title=uiLabelMap.FinancialsBudgetArea required=false list=listarea  displayField="description" key="enumId"  name="area" default=description?if_exists titleClass="requiredField"/>        
      </tr>                                                          
      <tr>
        <@displayTitleCell title=uiLabelMap.FinancialsReferenceDocument titleClass="requiredField"/>
        <@inputTextCell name="referencia" maxlength=60   />
      </tr>                     
      <tr>       
        <@inputTextRow title=uiLabelMap.FinancialsPostedAmount name="amount" titleClass="requiredField" />
      </tr>
      <@inputSubmitRow title=uiLabelMap.CommonCreate />
      
    </tbody>
  </table>
  </div>
</form>
