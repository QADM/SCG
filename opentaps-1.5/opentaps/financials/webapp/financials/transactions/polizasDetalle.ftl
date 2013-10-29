
<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>
<#assign poliza="" />
<#assign administrativa=""/>
<#assign funcional=""/>
<#assign programatica=""/>
<#assign TOG=""/>
<#assign fuenteRecursos=""/>
<#assign geografica=""/>
<#assign sectorial=""/>
<#assign catAux=""/>
<#assign rIngresos=""/>
<#assign ur=""/>
<#assign uo=""/>
<#assign ue=""/>
<#assign funcion=""/>
<#assign subfun=""/>
<#assign final=""/>
<#assign pp=""/>
<#assign ppl=""/>
<#assign spp=""/>
<#assign act=""/>
<#assign tg=""/>
<#assign cap=""/>
<#assign con=""/>
<#assign pg=""/>
<#assign pe=""/>
<#assign fu=""/>
<#assign sfu=""/>
<#assign sfue=""/>
<#assign ef=""/>
<#assign reg=""/>
<#assign mun=""/>
<#assign loc=""/>
<#assign rub=""/>
<#assign clas=""/>
<#assign conrub=""/>
<#assign n5=""/>
<#assign desc=""/>
<#assign ip=""/>
<#assign iph=""/>
<#assign ipd=""/>
<#assign sec=""/>
<#assign subsec=""/>
<#assign ar=""/>



<#list mapTransPresupPol?keys as poliza>
		<#if mapTransPresupPol.get(poliza)??>
						<#if poliza=="tipoPoliza">
							<#assign poliza=mapTransPresupPol.get(poliza) />
						</#if>	
									
						<#if poliza=="unidadOrganizacional">
							<#assign uo=poliza?replace("unidadOrganizacional","<b>Unidad Organizacional</b>")+": "+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="unidadEjecutora">
							<#assign ue=poliza?replace("unidadEjecutora","<b>Unidad Ejecutora</b>")+": "+mapTransPresupPol.get(poliza) />
						<#elseif poliza=="unidadResponsable">
							<#assign ur=poliza?replace("unidadResponsable","<b>Unidad Responsable</b>")+": "+mapTransPresupPol.get(poliza) />
						</#if>
						
						<#if poliza=="funcion">
							<#assign funcion= poliza?replace("funcion","<b>Funcion</b>")+": "+mapTransPresupPol.get(poliza) />
						<#elseif poliza=="subFuncion">
							<#assign subfun= poliza?replace("subFuncion","<b>Subfunción</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="finalidad" >
							<#assign final=poliza?replace("finalidad","<b>Finalidad</b>")+": "+ mapTransPresupPol.get(poliza)/>
						</#if>
						
						<#if poliza=="actividad">
							<#assign act=poliza?replace("actividad","<b>Actividad</b>")+":"+ mapTransPresupPol.get(poliza)/>
						<#elseif poliza=="programaPlan">
							<#assign ppl=poliza?replace("programaPlan","<b>Programa del Plan</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="subProgramaPresupuestario" >
							<#assign spp=poliza?replace("subProgramaPresupuestario","<b>SubPrograma Presupuestario</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="programaPresupuestario" >
							<#assign pp=poliza?replace("programaPresupuestario","<b>Programa Presupuestario</b>")+": "+ mapTransPresupPol.get(poliza)/>
						</#if>
						
					    <#if poliza=="tipoGasto">
							<#assign tg=poliza?replace("tipoGasto","<b>Tipo de  gasto</b>")+":"+ mapTransPresupPol.get(poliza)/>
						<#elseif poliza=="capitulo">
							<#assign cap=poliza?replace("capitulo","<b>Capítulo</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="concepto" >
							<#assign con=poliza?replace("concepto","<b>Concepto</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="partidaGenerica" >
							<#assign pg=poliza?replace("partidaGenerica","<b>Partida Generica</b>")+": "+ mapTransPresupPol.get(poliza)/>
					    <#elseif poliza=="partidaEspecifica" >
							<#assign pe=poliza?replace("partidaEspecifica","<b>Partida Específica</b>")+": "+ mapTransPresupPol.get(poliza)/>
						</#if>
						
						<#if poliza=="fuente">
							<#assign fu=poliza?replace("fuente","<b>Fuente</b>")+":"+ mapTransPresupPol.get(poliza)/>
						<#elseif poliza=="subFuenteEspecifica" >
							<#assign sfue=poliza?replace("subFuenteEspecifica","<b>Subfuente Específica</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="subFuente">
							<#assign sfu=poliza?replace("subFuente","<b>Subfuente</b>")+":"+ mapTransPresupPol.get(poliza) />
				
							</#if>
						
						<#if poliza=="entidadFederativa">
							<#assign ef=poliza?replace("entidadFederativa","<b>Entidad Federativa</b>")+":"+ mapTransPresupPol.get(poliza)/>
						<#elseif poliza=="region">
							<#assign reg=poliza?replace("region","<b>Región</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="municipio" >
							<#assign mun=poliza?replace("municipio","<b>Municipio</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="localidad" >
							<#assign loc=poliza?replace("localidad","<b>Localidad</b>")+":"+ mapTransPresupPol.get(poliza) />
						</#if>
						
						<#if poliza=="sector">
							<#assign sec=poliza?replace("sector","<b>Sector</b>")+":"+ mapTransPresupPol.get(poliza)/>
						<#elseif poliza=="subSector">
							<#assign subsec=poliza?replace("subSector","<b>Subsector</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="area" >
							<#assign ar=poliza?replace("area","<b>Area</b>")+":"+ mapTransPresupPol.get(poliza) />
						</#if>
						
						<#if poliza=="idPago">
							<#assign ip=poliza?replace("idPago","<b>Id de Pago</b>")+":"+ mapTransPresupPol.get(poliza)/>
						<#elseif poliza=="idProductoH">
							<#assign iph=poliza?replace("idProductoH","<b>Id de Producto H</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="idProductoD" >
							<#assign ipd=poliza?replace("idProductoD","<b>Id de producto D</b>")+":"+ mapTransPresupPol.get(poliza) />
						</#if>
						
						<#if poliza=="rubro">
							<#assign rub=poliza?replace("rubro","<b>Rubro</b>")+":"+ mapTransPresupPol.get(poliza)/>
						<#elseif poliza=="clase">
							<#assign clas=poliza?replace("clase","<b>Clase</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="conceptoRub" >
							<#assign conrub=poliza?replace("conceptoRub","<b>Concepto Rubro</b>")+":"+ mapTransPresupPol.get(poliza) />
						<#elseif poliza=="nivel5" >
							<#assign n5=poliza?replace("nivel5","<b>Nivel 5</b>")+": "+ mapTransPresupPol.get(poliza)/>
					    <#elseif poliza=="description" >
							<#assign desc=poliza?replace("description","<b>Tipo</b>")+": "+ mapTransPresupPol.get(poliza)/>
						</#if>
						
		</#if>
</#list>
	<#assign administrativa=ur+","+uo+","+ue+","/>
	<#assign funcional=final+","+funcion+","+subfun+","/>
	<#assign programatica=ppl+","+pp+","+spp+","+act+","/>
	<#assign TOG=tg+","+cap+","+con+","+pg+","+pe+","/>
	<#assign fuenteRecursos=fu+","+sfu+","+sfue+","/>
	<#assign geografica=ef+","+reg+","+mun+","+loc+","/>
	<#assign sectorial=sec+","+subsec+","+ar+","/>
	<#assign catAux=ip+","+iph+","+ipd+","/>
	<#assign rIngresos=rub+","+clas+","+conrub+","+n5+","+desc+","/>

<!--
	<#list mapTransPresupPol?keys as poliza>
			<#if mapTransPresupPol.get(poliza)??>
			<br>${poliza}:${mapTransPresupPol.get(poliza)}</br>
			<#else>
			<br>${poliza}:-</br>
			
			</#if>
			
	</#list>
	-->

<script type="text/javascript">
	var administrativa='${administrativa}';
	var funcional='${funcional}';
	var programatica='${programatica}';
	var TOG='${TOG}';
	var fuenteRecursos='${fuenteRecursos}';
	var geografica='${geografica}';
	var sectorial='${sectorial}';
	var catAux='${catAux}';
	var rIngresos='${rIngresos}';
	function imprime(cadena){
		var cadenaTemp="";
		var i=0;
		for(i=0;i<cadena.length;i++){
			if(cadena.charAt(i)!=",")
				cadenaTemp+=cadena.charAt(i);
			if(cadena.charAt(i)==","&&cadena.charAt(i+1)!=","){
				document.write("<br><font size=1 >"+cadenaTemp+"</font></br>");
				cadenaTemp="";
			}
		}
	}
</script>
<table width=100%>
			<tr><td align="center"><b>${poliza}</b></td></tr>
			<tr><td colspan="2" ><table width=100%>
							<th><font size=2>Información General</font></th>
							<tr><td>
							<script type="text/javascript">
									imprime(rIngresos);
								</script></td></tr>
						</table width=100%></td>
			</tr>
			<#if poliza=="EGRESO">
				<tr>
					<td >
						<table width=100%>
							<th><font size=2>Clasificación Administrativa </font></th>
							<tr><td><script type="text/javascript">
									imprime(administrativa);
								</script></td></tr>
						</table>
					</td>
					<td >
						<table width=100%>
							<th><font size=2>Clasificación Funcional</font> </th>
							<tr><td><script type="text/javascript">
									imprime(funcional);
								</script></td></tr>
						</table width=100%>
					</td>
				</tr>
				<tr>
					<td  >
						<table width=100%>
							<th><font size=2>Clasificación Programática</font> </th>
							<tr><td><script type="text/javascript">
									imprime(programatica);
								</script></td></tr>
						</table>
					</td>
					<td >
						<table width=100%>
							<th><font size=2>Tipo Objeto de Gasto</font> </th>
							<tr><td><script type="text/javascript">
									imprime(TOG);
								</script></td></tr>
						</table>
					</td>
				</tr>
				<tr>
					<td  >
						<table width=100%>
							<th><font size=2>Clasificación Fuente de los Recursos</font> </th>
							<tr><td><script type="text/javascript">
									imprime(fuenteRecursos);
								</script></td></tr>
						</table>
					</td>
					<td >
						<table width=100%>
							<th><font size=2>Clasificación Geográfica</font></th>
							<tr><td><script type="text/javascript">
									imprime(geografica);
								</script></td></tr>
						</table>
					</td>
				</tr>
				<tr>
					<td  >
						<table width=100%>
							<th><font size=2>Clasifiación Sectorial </font></th>
							<tr><td><script type="text/javascript">
									imprime(sectorial);
								</script></td></tr>
						</table>
					</td>
					<td >
						<table width=100%>
							<th><font size=2>Catálogos Auxiliares</font></th>
							
							<tr><td><script type="text/javascript">
								
									imprime(catAux);
								</script></td></tr>
						</table>
					</td>
				</tr>
			<#elseif poliza=="INGRESO">	
				<tr>
					<td >
						<table width=100%>
							<th><font size=2>Clasificación Fuente de los Recursos</font> </th>
							<tr><td><script type="text/javascript">
									imprime(fuenteRecursos);
								</script></td></tr>
						</table>
					</td>
					<td >
						<table width=100%>
							<th><font size=2>Clasificación Geográfica</font> </th>
							<tr><td><script type="text/javascript">
									imprime(geografica);
								</script></td></tr>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table width=100%>
							<th><font size=2>Rubro de Ingresos</font> </th>
							<tr><td><script type="text/javascript">
									imprime(fuenteRecursos);
								</script></td></tr>
						</table>
					</td>
					<td >
						<table width=100%>
							<th><font size=2>Catálogos Auxiliares</font></th>
							<tr><td><script type="text/javascript">
									imprime(catAux);
								</script></td></tr>
						</table>
					</td>
				</tr>
	  </#if>
</table>
