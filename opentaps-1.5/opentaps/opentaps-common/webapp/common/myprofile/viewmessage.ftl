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
-->
<#-- Copyright (c) Open Source Strategies, Inc. -->

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<#if parameters.partyId?exists>
    <#assign donePageEscaped = donePage + "?partyId%3d" + parameters.partyId>
</#if>

<div id="headersPane">
    <table>
        <tr>
            <td colspan="2" style="text-align: right; font-weight: bold;">
                <@displayLink text=uiLabelMap.OpentapsReply href="javascript: window.helper.replyMessage('${message.communicationEventId}');"/>
                <@displayLink id="separator" text=uiLabelMap.OpentapsForward href="javascript: window.helper.forwardMessage('${message.communicationEventId}');"/>
                <@displayLink id="separator" text=uiLabelMap.CommonDelete href="javascript: window.helper.deleteMessage('${message.communicationEventId}');"/>
                <@displayLink id="separator" text=uiLabelMap.OpentapsHelp href="#"/>
            </td>
        </tr>
        <tr>
            <@displayCell text=uiLabelMap.OpentapsSubject blockClass="headerTitle" style="font-weight: bold;"/>
            <@displayCell text=message.subject?if_exists/>
        </tr>
        <tr>
            <@displayCell text=uiLabelMap.CommonFrom blockClass="headerTitle" style="font-weight: bold;"/>
            <@displayCell text=message.partyIdFromAddress?if_exists/>
        </tr>
        <tr>
            <@displayCell text=uiLabelMap.CommonDate blockClass="headerTitle" style="font-weight: bold;"/>
            <@displayDateCell date=message.entryDate/>
        </tr>
    </table>
</div>

<div id="bodyPane">
    <@inputTextareaRow title="" name="message" default=message.content?if_exists/>
</div>
