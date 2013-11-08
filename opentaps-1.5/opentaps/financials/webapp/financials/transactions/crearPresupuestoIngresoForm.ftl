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
