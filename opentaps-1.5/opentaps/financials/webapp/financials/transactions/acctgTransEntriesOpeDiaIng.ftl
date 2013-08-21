<#--
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 *
-->
<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<div class="screenlet-header">
  <#if (acctgTrans?exists) && (acctgTrans.isPosted != "Y")>
    <div style="float: right;"><a href="<@ofbizUrl>createAcctgTransEntryForm?acctgTransId=${acctgTrans.acctgTransId}</@ofbizUrl>" class="buttontext" >${uiLabelMap.FinancialsCreateTransactionEntry}</a></div>
  </#if>
  <span class="boxhead">${uiLabelMap.FinancialsTransactionEntries}</span>
</div>

  <#if acctgTrans?exists>
    <table class="listTable" cellspacing="0" style="border:none;">
      <tr class="listTableHeader">
        <td><span>${uiLabelMap.PartySequenceId}</span></td>
        <td><span>${uiLabelMap.GlAccount}</span></td>
        <td><span>${uiLabelMap.FinancialsDebitCredit}</span></td>
        <td><span>${uiLabelMap.CommonAmount}</span></td>
        <td></td>
      </tr>
      
      <#-- for posted transactions just display the list of entries -->
        <#list acctgTransEntries as entry>
          <tr class="${tableRowClass(entry_index)}">
            <td><a class="linktext" href="<@ofbizUrl>viewAcctgTransEntry?acctgTransId=${entry.acctgTransId}&amp;acctgTransEntrySeqId=${entry.acctgTransEntrySeqId}</@ofbizUrl>">${entry.acctgTransEntrySeqId}</a></td>
            <td><#assign glAccount = delegator.findOne("GlAccount", {"glAccountId" : entry.glAccountId}, true)/><a class="linktext" href="<@ofbizUrl>AccountActivitiesDetail?glAccountId=${glAccount.glAccountId}&amp;organizationPartyId=${session.getAttribute("organizationPartyId")}</@ofbizUrl>">${glAccount.accountCode?default(glAccount.glAccountId)}</a>: ${glAccount.accountName?default("")}</td>
            <td>${entry.debitCreditFlag}</td>
            <@displayCurrencyCell amount=entry.amount currencyUomId=entry.currencyUomId class="tabletext" />
            <td><#assign StatusItem = delegator.findOne("StatusItem", {"statusId" : entry.reconcileStatusId}, true)>
            	${StatusItem.description}</td>
          </tr>
          <#-- List possible tags in separate lines -->
          <#list tagTypes as tag>
            <!-- only display tags that are set to something -->
            <#assign fieldName = "acctgTagEnumId${tag.index}"/>
            <#if entry.get(fieldName)?has_content>
              <tr class="${tableRowClass(entry_index)}">
                <td/>
                <td colspan="4"><span style="margin-left:20px">${tag.description} :
                <#list tag.tagValues as tagValue>
                  <#if tagValue.enumId == entry.get(fieldName)!>
                    ${tagValue.description}</span></td>
                    <#break/>
                  </#if>
                </#list>
              </tr>
            </#if>
          </#list>
        </#list>
    </table>
  </#if>
