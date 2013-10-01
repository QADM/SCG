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

<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>

<#if userLogin?has_content>
  <div id="main-navigation">
    <ul>
      <#list displayApps as display>
        <#assign thisApp = display.getContextRoot()>
        <#assign permission = true>
        <#assign selected = false>
        <#assign permissions = display.getBasePermission()>
        <#list permissions as perm>
          <#if (perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session) && !authz.hasPermission(session, perm, requestParameters))>
            <#-- User must have ALL permissions in the base-permission list -->
            <#assign permission = false>
          </#if>
        </#list>
        <#if permission == true>
          <#if thisApp == contextPath || contextPath + "/" == thisApp>
            <#assign selected = true>
          </#if>
          <#assign thisURL = thisApp>
          <#if thisApp != "/">
            <#assign thisURL = thisURL + "/control/main">
          </#if>
          <#if layoutSettings.suppressTab?exists && display.name == layoutSettings.suppressTab>
            <!-- do not display this component-->
            <#else>
            	<#if display.title!="Setup" || display.description!="Setup" >
            		
           			 <li<#if selected> class="selected"</#if>><a href="${thisURL}${externalKeyParam}" <#if uiLabelMap?exists>  title="${uiLabelMap[display.description]}"><#if uiLabelMap[display.title]="Data Import">Carga de catálogos<#elseif uiLabelMap[display.title]="opentaps">Menú principal <#else>${uiLabelMap[display.title]}</#if><#else> title="${display.description}"><#if display.title="Data Import">Carga de catálogos<#elseif display.title="Data Import">Menú principal<#else>${display.title}</#if></#if></a></li>
         		</#if>
          </#if>
        </#if>
      </#list>
    </ul>
    <br class="clear"/>
  </div>
</#if>
