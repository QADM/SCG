<#--
 * Lista de resultados preliminares de p�lizas contables
 * Author: Vidal Garc�a
 * Versi�n 1.0
 * Fecha de Creaci�n: Julio 2013
-->

<#-- Parametrized find form for transactions. -->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>





        
        	<table class="listTable">
    	        <#list acctgPolizasClavesPresup as row>
            		<tr>            		
	                	<@displayLinkCell text=row.clavePres href="viewGlFiscalType?clavePres=${row.clavePres}&agrupador=${row.agrupador}"/>	                					                		 	              
            		</tr>
            	</#list>
        	</table>



        

