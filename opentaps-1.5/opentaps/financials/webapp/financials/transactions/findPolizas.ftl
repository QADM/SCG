<#--
 * Formulario de busqueda de pólizas contables
 * Author: Vidal García
 * Versión 1.0
 * Fecha de Creación: Julio 2013
-->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<form name="buscarPolizasContables" method="post" action="">
  <@inputHidden name="performFind" value="Y"/>
  <table class="twoColumnForm">
    <tbody>
      <@inputTextRow title=uiLabelMap.FinancialsAgrupador name="agrupador" size="20" maxlength="20"/>
      <@inputSelectRow title=uiLabelMap.FinancialsTipoTransaccion required=false list=transactionTypes  displayField="description" name="acctgTransTypeId" default=acctgTransTypeId?if_exists />
      <@inputSelectRow title=uiLabelMap.FinancialsTipoPoliza required=false list=listaTipoPoliza  displayField="descripcion" name="tipoPoliza" />
      <@inputDateRow title=uiLabelMap.FinancialsFechaContable name="postedDate" default="" />
      <@inputSubmitRow title=uiLabelMap.CommonFind />
    </tbody>
  </table>
</form>
