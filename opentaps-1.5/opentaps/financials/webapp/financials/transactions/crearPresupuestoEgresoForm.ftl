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
  <input type="hidden" name="acctgTransTypeId" value="TPRESUPAPROBADO"/>
  <input type="hidden" name="description" value="Carga Manual Presupuesto Egreso"/>
  <div class="form" style="border:0">
    <@inputHidden name="performFind" value="Y"/>
  	<table class="twoColumnForm">
    <tbody>
      
    </tbody>
  </table>
  </div>
</form>
