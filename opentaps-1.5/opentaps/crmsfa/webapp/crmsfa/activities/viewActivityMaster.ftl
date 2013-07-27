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

<#-- This basically renders a special screen for emails vs other activities -->

<#-- TODO: re-factor all of ActivitiesScreens.xml#viewActivity into this FTL from the screen widget -->

<#if (workEffort?has_content) && (workEffort.workEffortPurposeTypeId?has_content) && (workEffort.workEffortPurposeTypeId == "WEPT_TASK_EMAIL")>
    ${screens.render("component://crmsfa/widget/crmsfa/screens/activities/ActivitiesScreens.xml#viewEmailActivity")}
<#else>
    ${screens.render("component://crmsfa/widget/crmsfa/screens/activities/ActivitiesScreens.xml#viewActivity")}
</#if>