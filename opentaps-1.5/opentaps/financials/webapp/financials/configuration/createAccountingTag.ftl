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

<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>
    <script  languaje="JavaScript">
    <!--
    	var uno=0;
	     provincias = new Array(' ');
	     provincias[0] = new Array(' ');
	     niveles=new Array();
	     niveles[0] = new Array();
	     <#assign contador=1/>
	     <#list tagTypes as tagsss>
	     	<#assign valor=" "/>
	     	<#assign valor2=" "/>
	     	<#list nivels as niveles>
	     		<#if niveles.parentTypeId=="ACCOUNTING_TAG">
	     			<#if niveles.clasificacionId==tagsss.clasificacionId>
						<#assign valor=valor+"','"+niveles.descripcion/>
						<#assign valor2=valor2+"','"+niveles.nivelId/>
					</#if>
	     		</#if>
	     	</#list>
	     	<#assign valor=valor?replace("&aacute;","á")/>
	     	<#assign valor=valor?replace("&eacute;","é")/>
	     	<#assign valor=valor?replace("&iacute;","í")/>
	     	<#assign valor=valor?replace("&oacute;","ó")/>
	     	<#assign valor=valor?replace("&uacute;","ú")/>
	     	<#assign valor=valor?replace("&Aacute;","Á")/>
	     	<#assign valor=valor?replace("&Eacute;","É")/>
	     	<#assign valor=valor?replace("&Iacute;","Í")/>
	     	<#assign valor=valor?replace("&Oacute;","Ó")/>
	     	<#assign valor=valor?replace("&Uacute;","Ú")/>
	     	provincias[${contador}]=new Array('${valor?j_string}');
	     	niveles[${contador}]=new Array('${valor2}');
	     	<#assign contador=contador+1/>
	     </#list>	   
	     function cambiar(formulario){
			  var i = 0;
			  var select1 = formulario['enumTypeId'];
			  var select2 = formulario['nivelId'];
			  var vector = provincias[select1.selectedIndex];
			  var vector2= niveles[select1.selectedIndex];
			  if(vector.length)select2.length=vector.length;
			  while(vector[i]){
				    select2.options[i].value = vector2[i];
				    select2.options[i].text = vector[i];
				    i++;
			  }
			  select2.options[0].selected = 1;
			  
		}

		-->
    </script> 
<#assign Nodo = {"R": "Rama", "H": "Hoja"} />
<@frameSection title=uiLabelMap.FinancialsCreateAccountingTag>
  <form method="post" action="<@ofbizUrl>createAccountingTag</@ofbizUrl>" name="createAccountingTag"  onsubmint="valida(this.form)">
    <table class="twoColumnForm" style="border:0">
    <tr>
    	<td class="titleCell"><span><b><font  size=1 color=#B40404>Tipo</font><b></span>
    	</td>
	    <td>
     <select name="enumTypeId" size="1"  onchange="cambiar(this.form)">
		    	<option value=" ">-</option>
		        <#list tagTypes as tagsty>
		        	<option  value="${tagsty.enumTypeId}">${tagsty.get("description",locale)}</option>
		        </#list>
		    </select>
	    </td>
    </tr>
      <tr>
  <td class="titleCell"><span><b><font  size=1 color=#B40404>Nivel<font><b></span>
    	</td>
	    <td>  
      <select name="nivelId">
		  <option value=" ">-</option>
	  </select>
	  	    </td>
    </tr>
    
      <@inputTextRow title=uiLabelMap.Codigo name="sequenceId" size=10 maxlength=20 titleClass="requiredField"/>
      <@inputTextRow title=uiLabelMap.CommonName name="enumCode" titleClass="requiredField" />
      <@inputTextRow title=uiLabelMap.CommonDescription name="description" size=60  titleClass="requiredField"/>	
       <@inputSelectHashRow  title="Nodo" name="node"  hash=Nodo />

      <@inputTextRow title=uiLabelMap.ParentId  size=10 name="parentEnumId" size=10  />
      <@inputDateRow title="Fecha inicio"  name="fechaIni" size=12 default="" titleClass="requiredField" />
      <@inputDateRow title="Fecha fin" name="fechaFi"   size=12 default="" titleClass="requiredField" />
      <@inputSubmitRow title=uiLabelMap.CommonCreate />
    </table>
  </form>
</@frameSection>
n>
