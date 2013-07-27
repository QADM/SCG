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
<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<#assign extraOptions><@inputConfirm href="clearCart" title=uiLabelMap.OpentapsClearItems class="subMenuButtonDangerous"/><a class="subMenuButton" href="<@ofbizUrl>createOrderMainScreen</@ofbizUrl>">${uiLabelMap.OpentapsOrderReturnToOrder}</a><a class="subMenuButton" href="<@ofbizUrl>finalizeOrder?finalizeMode=init</@ofbizUrl>">${uiLabelMap.CommonOptions}</a><a class="subMenuButton" href="#" onclick="redirectUrlAndDisableLink('<@ofbizUrl>processorder</@ofbizUrl>',this,'${uiLabelMap.OpentapsOrderSubmittingLabel}')">${uiLabelMap.OrderCreateOrder}</a></#assign>

<@frameSectionHeader title=uiLabelMap.OrderReviewOrder extra=extraOptions/>

  
