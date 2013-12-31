<#--
 Permite agregar y parametrizar las clasificaciones
-->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<#assign mustBalanceSelectValues = {"Y": uiLabelMap.CommonYes, "N": uiLabelMap.CommonNo} />

<@frameSection title=uiLabelMap.ClassificationTagsPostingEntry>
 
    <#-- might be null if the usage is not configured yet -->
    <#assign configuration = configurations.get(usage)! />
    <div class="screenlet">
      <div class="screenlet-header"><span class="boxhead">${usage.description}</span></div>
  <form method="post" action="<@ofbizUrl>updateClassificationInvoice</@ofbizUrl>" name="updateClassificationInvoice">
    <@inputHidden name="acctgTagUsageTypeId" value=usage.acctgTagUsageTypeId />
    <@inputHidden name="organizationPartyId" value=organizationPartyId />
    <table class="listTable" style="border:0">
      <tr class="listTableHeader">
        <@displayCell text="" />
        <@displayCell text=uiLabelMap.FinancialsClassification/>
        <@displayCell text="" />
        <@displayCell text=uiLabelMap.FinancialsClassification />
        <@displayCell text="" />
        <@displayCell text=uiLabelMap.FinancialsClassification />
      </tr>
      <#-- layout 3 columns -->
      <#if usage.acctgTagUsageTypeId?contains("PRCH_INV_ITEMS")>
      <#list 1..10 as i>
            <#if (i % 3) == 1>
              <tr class="${tableRowClass(i % 6)}">
            </#if>
            <#assign classificationId = "clasifTypeId" + i />
            <@displayTitleCell title=i />
            <@inputSelectCell name=classificationId default=(configuration.get(classificationId))! list=tagClassification key="clasificacionId" required=false ; type>
              ${type.descripcion}
            </@inputSelectCell>
            
            <#if (i % 3) == 0>
              </tr>
            </#if>
          </#list>
        </#if>
      <@inputSubmitRow title=uiLabelMap.CommonUpdate />
    </table>
  </form>
  </div>

</@frameSection>
