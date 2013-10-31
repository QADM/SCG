<#--
 Permite agregar y parametrizar las clasificaciones
-->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<#assign mustBalanceSelectValues = {"Y": uiLabelMap.CommonYes, "N": uiLabelMap.CommonNo} />

<@frameSection title=uiLabelMap.ClassificationTagsPostingEgress>
  <form method="post" action="<@ofbizUrl>updateClassificationTag</@ofbizUrl>" name="updateClassificationTag">
    <@inputHidden name="acctgTagUsageTypeId" value="EGRESO" />
    <@inputHidden name="organizationPartyId" value=organizationPartyId />
    <table class="listTable" style="border:0">
      <tr class="listTableHeader">
        <@displayCell text="" />
        <@displayCell text=uiLabelMap.FinancialsClassification />
        <@displayCell text="" />
        <@displayCell text=uiLabelMap.FinancialsClassification />
        <@displayCell text="" />
        <@displayCell text=uiLabelMap.FinancialsClassification />
      </tr>
      <#-- layout 3 columns -->
      <#list 1..15 as i>
            <#if (i % 3) == 1>
              <tr class="${tableRowClass(i % 6)}">
            </#if>
            <#assign classificationId = "clasificacion" + i />
            <@displayTitleCell title=i />
            <@inputSelectCell name=classificationId default=(configuration.get(classificationId))! list=tagClassification key="clasificacionId" required=false ; type>
              ${type.descripcion}
            </@inputSelectCell>
            
            <#if (i % 3) == 0>
              </tr>
            </#if>
          </#list>
      <@inputSubmitRow title=uiLabelMap.CommonUpdate />
    </table>
  </form>
</@frameSection>
