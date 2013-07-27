<#--
 * Lista de resultados preliminares de pólizas contables
 * Author: Vidal García
 * Versión 1.0
 * Fecha de Creación: Julio 2013
-->

<#-- Parametrized find form for transactions. -->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<@paginate name="listaDePolizasContables" list=acctgTransListBuilder rememberPage=false orgCurrencyUomId="${orgCurrencyUomId}">
    <#noparse>
        <@navigationHeader/>
        <table class="listTable">
            <tr class="listTableHeader">
                <@headerCell title="Número de Póliza" orderBy="agrupador"/>
                <@headerCell title="Código de Transacción" orderBy="acctgTransId"/>
                <@headerCell title="Tipo de Transacción" orderBy="description"/>
                <@headerCell title="Fecha Contable" orderBy="postedDate DESC"/>
                <@displayCell text="Monto" blockClass="textright"/>
            </tr>
            <#list pageRows as row>
            <tr class="${tableRowClass(row_index)}">
                <@displayCell text=row.agrupador  />
				<@displayLinkCell text=row.acctgTransId href="viewAcctgTransPolizas?acctgTransId=${row.acctgTransId}"/>
                <@displayCell text=row.description/>
                <@displayDateCell date=row.postedDate/>
                <@displayCurrencyCell amount=row.amount currencyUomId=parameters.orgCurrencyUomId class="textright"/>
            </tr>
            </#list>
        </table>
    </#noparse>
</@paginate>
