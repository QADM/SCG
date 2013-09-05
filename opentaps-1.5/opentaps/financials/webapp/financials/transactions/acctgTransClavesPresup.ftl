<#--
 * Lista de resultados preliminares de pólizas contables
 * Author: Vidal García
 * Versión 1.0
 * Fecha de Creación: Julio 2013
-->

<#-- Parametrized find form for transactions. -->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>





        
        	<table class="listTable">
            	<tr class="listTableHeader">	
            		<@displayCell text="Claves"/>                                	             			                           
	            </tr>
    	        <#list acctgPolizasClavesPresup as row>
            		<tr>            		
	                	<@displayLinkCell text=row.clavePres href="viewGlFiscalType?clavePres=${row.clavePres}&agrupador=${row.agrupador}"/>	                					                		 	              
            		</tr>
            	</#list>
        	</table>



        

