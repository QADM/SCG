<#--
 * Lista de resultados preliminares de pólizas contables
 * Author: Vidal García
 * Versión 1.0
 * Fecha de Creación: Julio 2013
-->

<#-- Parametrized find form for transactions. -->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

	<table class="listTable">
            	
    	        <#list acctgPolizasDetalleLista as row>
            		<tr class="${tableRowClass(row_index)}">
	                					
	                	<tr><td class="titleCell"></span><font size=2><b>${uiLabelMap.FinancialsAgrupador}</b></td><td></span><font size=1>${row.agrupador}</td></tr>
	                	<tr><td class="titleCell"></span><font size=2><b>${uiLabelMap.FinancialsFechaContable}</b></td><td></span><font size=1>${row.postedDate}</td></tr>
	                	<tr><td class="titleCell"></span><font size=2><b>${uiLabelMap.FinancialDocumentType}</b></td><td></span><font size=1>${row.descripcion}</td></tr>
	                	<tr><td class="titleCell"></span><font size=2><b>${uiLabelMap.FinancialsTipoPoliza}</b></td><td></span><font size=1>${row.tipoPoliza}</td></tr>
	                	<tr><td class="titleCell"></span><font size=2><b>${uiLabelMap.Organizacion}</b></td><td></span><font size=1>${row.groupName}</td></tr>	                		                	                		 	              
	                	<tr><td class="titleCell"></span><font size=2><b>${uiLabelMap.Monto}</b></td><td></span><font size=1>$ ${row.amount}</td></tr>	                		                	
            		</tr>
            	</#list>
        	</table>




        
        	
        	
        	
        	



        

