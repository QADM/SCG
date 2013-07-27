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
<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#-- This file has been modified by Open Source Strategies, Inc. -->

<#if quote?exists>
  <form action="<@ofbizUrl>copyQuote</@ofbizUrl>" method="post" style="margin: 0;">
    <input type="hidden" name="quoteId" value="${quoteId}"/>
    <div class="tabletext">
      <b>${uiLabelMap.OrderCopyQuote}:</b>
      ${uiLabelMap.OrderOrderQuoteItems}&nbsp;<input type="checkbox" class="checkBox" name="copyQuoteItems" value="Y" checked="checked"/>
      ${uiLabelMap.OrderOrderQuoteAdjustments}&nbsp;<input type="checkbox" class="checkBox" name="copyQuoteAdjustments" value="Y" checked="checked"/>
      ${uiLabelMap.OrderOrderQuoteRoles}&nbsp;<input type="checkbox" class="checkBox" name="copyQuoteRoles" value="Y" checked="checked"/>
      ${uiLabelMap.OrderOrderQuoteAttributes}&nbsp;<input type="checkbox" class="checkBox" name="copyQuoteAttributes" value="Y" checked="checked"/>
      ${uiLabelMap.OrderOrderQuoteCoefficients}&nbsp;<input type="checkbox" class="checkBox" name="copyQuoteCoefficients" value="Y" checked="checked"/>
      ${uiLabelMap.CrmQuoteItemOptions}&nbsp;<input type="checkbox" class="checkBox" name="copyQuoteItemOptions" value="Y" checked="checked"/>
    </div>
    <input type="submit" class="smallSubmit" value="${uiLabelMap.CommonCopy}"/>
  </form>
</#if>
