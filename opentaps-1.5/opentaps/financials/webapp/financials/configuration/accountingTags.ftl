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
<script type="text/javascript">
var patron = new Array(2,2,2)
function mascara(d,sep,pat,nums){
	if(d.valant != d.value){
		val = d.value
		largo = val.length
		val = val.split(sep)
		val2 = ''
		for(r=0;r<val.length;r++){
			val2 += val[r]	
		}
		if(nums){
			for(z=0;z<val2.length;z++){
				if(isNaN(val2.charAt(z))){
					letra = new RegExp(val2.charAt(z),"g")
					val2 = val2.replace(letra,"")
				}
			}
		}
		val = ''
		val3 = new Array()
		for(s=0; s<pat.length; s++){
			val3[s] = val2.substring(0,pat[s])
			val2 = val2.substr(pat[s])
		}
		for(q=0;q<val3.length; q++){
			if(q ==0){
				val = val3[q]
			}
			else{
				if(val3[q] != ""){
					val += sep + val3[q]
					}
			}
		}
		d.value = val
		d.valant = val
		if(d.value.length==8){
			var dias=parseInt(d.value.substring(0,2));
			var meses=parseInt(d.value.substring(3,5));
			if(dias>31){
				alert("Error El número de días no puede ser mayor a 31");
				d.value = "";
				d.valant = "";
			}
			if(meses>12){
				alert("Error El número de meses no puede ser mayor a 12");
				d.value = "";
				d.valant = "";
			}
		}
	}
}
</script>
<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>

<#assign disableSelectValues = {"Y": uiLabelMap.CommonDisabled, "N": uiLabelMap.CommonEnabled} />
<#assign Nodo = {"R": "Rama", "H": "Hoja"} />

<@frameSection title=uiLabelMap.FinancialsAccountingTags>

<@paginate name="ExelTAGS" list=Enumlists >
    <#noparse>
        <@navigationHeader/>

    </#noparse>
</@paginate>
  <#list tagsByType.keySet() as type>
    <div class="screenlet">
      <div class="screenlet-header"><span class="boxhead">${type.description}</span></div>
      <@form name="deleteAccountingTagForm" url="deleteAccountingTag" enumId="" />
      <form method="post" action="<@ofbizUrl>updateAccountingTag</@ofbizUrl>" name="updateAccountingTag">
        <@inputHiddenUseRowSubmit />
        <@inputHiddenRowCount list=tagsByType.get(type) />
        <table class="listTable" style="border:0">
          <tr class="listTableHeader">
            <@displayCell text=uiLabelMap.Codigo />
            <@displayCell text=uiLabelMap.Id />
            <@displayCell text=uiLabelMap.Nombre />
            <@displayCell text=uiLabelMap.CommonDescription />
      		<@displayCell text=uiLabelMap.Nivel />
      		<@displayCell text=uiLabelMap.ParentId />
			<@displayCell text="Fecha Inicio"/>
			<@displayCell text="Fecha Fin"/>
			<@displayCell text="Nodo"/>
            <@displayCell text=uiLabelMap.CommonEnabled />
            <td/>
            <td/>
          </tr>
          <#list tagsByType.get(type) as tag>
            <tr class="${tableRowClass(tag_index)}">
              <@inputHidden name="enumId" value=tag.enumId index=tag_index />
              <@displayCell text=tag.sequenceId/>
              <@displayCell text=tag.enumId />

              <@inputTextCell name="enumCode" default=tag.enumCode! size=12 maxlength=30 index=tag_index />
              <@inputTextCell name="description" default=tag.description! size=12   maxlength=30 index=tag_index />

   			 <td>
	            <select name="niv" size="1" >
		         <#list nivels as niveles>
			            <#if tag.enumTypeId==niveles.enumTypeId>
			            	<#assign nivel=niveles.nivelId />
			           		 <option <#if (niveles?has_content&&tag.nivelId==niveles.nivelId) > selected="selected" </#if> value="${(nivel)?if_exists}" >${niveles.get("descripcion",locale)}</option>
						</#if>	
				 </#list>
	            </select>
            </td>

   			  <@inputTextCell name="parentEnumId" default=tag.parentEnumId!  size=10 maxlength=10 index=tag_index />
	       <td>
	              <input type="text" class="inputBox"  size=12 name="fechaIn" onkeyup="mascara(this,'/',patron,true)"  value=${tag.fechaInicio?string("dd/MM/yy")}>
	       </td>
	       <td>
	              <input type="text" class="inputBox" name="fechaF"  onkeyup="mascara(this,'/',patron,true)"  size=12 value=${tag.fechaFin?string("dd/MM/yy")}>
	       </td>
             
              <@inputSelectHashCell name="node" default=tag.node! index=tag_index hash=Nodo />
              <@inputSelectHashCell name="disabled" default=tag.disabled!"N" index=tag_index hash=disableSelectValues />
              <@inputHiddenRowSubmit submit=false index=tag_index/>
              <@inputSubmitIndexedCell title="${uiLabelMap.CommonUpdate}" index=tag_index/>
              <td>
	            <select name="niv" size="1" style="visibility:hidden">
		         <#list nivels as niveles>
			            <#if tag.enumTypeId==niveles.enumTypeId>
			            	<#assign nivel=niveles.nivelId />
			           		 <option <#if (niveles?has_content&&tag.nivelId==niveles.nivelId) > selected="selected" </#if> value="${(nivel)?if_exists}" >${niveles.get("descripcion",locale)}</option>
						</#if>	
				 </#list>
	            </select>
            </td>
             <td>
	              <input type="text" class="inputBox"  style="visibility:hidden"  size=2 name="fechaIn" value=${tag.fechaInicio?string("dd/MM/yy")}>
	       </td>
	       <td>
	              <input type="text" class="inputBox"  style="visibility:hidden" name="fechaF"  size=2 value=${tag.fechaFin?string("dd/MM/yy")}>
	       </td>
            </tr>
          </#list>
        </table>
      </form>
    </div>
  </#list>
</@frameSection>

