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
            		<@displayCell text="Número de Póliza"/>                
                	<@displayCell text="Cuenta"/>
                	<@displayCell text="Débito/Crédito"/>
                	<@displayCell text="Monto"/>             			                           
	            </tr>
    	        <#list acctgPolizasDetalleListado as row>
            		<tr class="${tableRowClass(row_index)}">
	                	<@displayCell text=row.agrupador/>				
                		<@displayCell text=row.accountName/>
                		<@displayCell text=row.debitCreditFlag/>
                		<@displayCurrencyCell amount=row.amount currencyUomId=parameters.orgCurrencyUomId class="textleft"/> 	                
            		</tr>
            	</#list>
        	</table>



        

