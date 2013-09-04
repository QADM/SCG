<#--
 * Lista de resultados preliminares de p�lizas contables
 * Author: Vidal Garc�a
 * Versi�n 1.0
 * Fecha de Creaci�n: Julio 2013
-->

<#-- Parametrized find form for transactions. -->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>





        
        	<table class="listTable">
            	<tr class="listTableHeader">	
            		<@displayCell text="N�mero de P�liza"/>                
                	<@displayCell text="Cuenta"/>
                	<@displayCell text="D�bito/Cr�dito"/>
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



        

