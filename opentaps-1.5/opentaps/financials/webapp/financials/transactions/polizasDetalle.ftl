
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
<#list mapTransPresupPol?keys as poliza>
		<#if mapTransPresupPol.get(poliza)??>
						<#if poliza=="tipoPoliza">
							<#assign poliza=mapTransPresupPol.get(poliza) />
						</#if>
						<#if poliza=="unidadOrganizacional"||poliza=="unidadEjecutora"||poliza=="unidadResponsable">
							<#assign administrativa=administrativa+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							
							<#assign administrativa=administrativa?replace("unidadOrganizacional","<b>Unidad Organizacional</b>")?replace("unidadEjecutora","<b>Unidad Ejecutora</b>")?replace("unidadResponsable","<b>Unidad Responsable</b>")/>
						</#if>
						<#if poliza=="funcion"||poliza=="subFuncion"||poliza=="finalidad">
							<#assign funcional=funcional+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							<#assign funcional=funcional?replace("funcion","<b>Funcion</b>")?replace("subFuncion","<b>Subfunción</b>")?replace("finalidad","<b>Finalidad</b>") />
						</#if>
						<#if poliza=="actividad"||poliza=="programaPlan"||poliza=="subProgramaPresupuestario"||poliza=="programaPresupuestario">
							<#assign programatica=programatica+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							<#assign programatica=programatica?replace("actividad","<b>actividad</b>")?replace("programaPlan","<b>Programa del Plan</b>")?replace("subProgramaPresupuestario","<b>SubPrograma Presupuestario</b>")?replace("programaPresupuestario","<b>Programa Presupuestario</b>") />
						</#if>
						<#if poliza=="tipoGasto"||poliza=="capitulo"||poliza=="concepto"||poliza=="concepto"||poliza="partidaGenerica"||poliza="partidaEspecifica">
							<#assign TOG=TOG+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							<#assign TOG=TOG?replace("tipoGasto","<b>Tipo de  gasto</b>")?replace("capitulo","<b>Capítulo</b>")?replace("concepto","<b>Concepto</b>")?replace("partidaGenerica","<b>Partida Generica</b>")?replace("partidaEspecifica","<b>Partida Específica</b>") />
						</#if>
						<#if poliza=="fuente"||poliza=="subFuente"||poliza=="subFuenteEspecifica">
							<#assign fuenteRecursos=fuenteRecursos+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							<#assign fuenteRecursos=fuenteRecursos?replace("fuente","<b>Fuente</b>")?replace("subFuente","<b>Subfuente</b>")?replace("subFuenteEspecifica","<b>Subfuente Específica</b>") />
						</#if>
						<#if poliza=="entidadFederativa"||poliza=="region"||poliza=="municipio"||poliza=="localidad">
							<#assign geografica=geografica+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							<#assign geografica=geografica?replace("entidadFederativa","<b>Entidad Federativa</b>")?replace("region","<b>Región</b>")?replace("localidad","<b>Localidad</b>")?replace("municipio","<b>Municipio</b>") />
						</#if>
						<#if poliza=="sector"||poliza=="subSector"||poliza=="area">
							<#assign sectorial=sectorial+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							<#assign sectorial=sectorial?replace("sector","<b>Sector</b>")?replace("subSector","<b>Subsector</b>")?replace("area","<b>Area</b>") />
						</#if>
						<#if poliza=="idPago"||poliza=="idProductoH"||poliza=="idProductoD">
							<#assign catAux=catAux+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							<#assign catAux=catAux?replace("idPago","<b>Id de Pago</b>")?replace("idProductoH","<b>Id de Producto H</b>")?replace("idProductoD","<b>Id de producto D</b>") />
						</#if>
						<#if poliza=="rubro"||poliza=="clase"||poliza=="conceptoRub"||poliza=="nivel5"||poliza="description">
							<#assign rIngresos=rIngresos+poliza+": "+mapTransPresupPol.get(poliza)+"," />
							<#assign rIngresos=rIngresos?replace("rubro","<b>Rubro</b>")?replace("clase","<b>Clase</b>")?replace("conceptoRub","<b>Concepto Rubro</b>")?replace("nivel5","<b>Nivel 5</b>")?replace("description","<b>Tipo</b>") />
						</#if>
		</#if>
</#list>

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
			if(cadena.charAt(i)==","){
				document.write("<br><font size=1 >"+cadenaTemp+"</font></br>");
				cadenaTemp="";
			}
		}
	}
</script>
<table width=100%>
			<tr><td align="center"><b>${poliza}</b></td></tr>
			<tr><td colspan="2" ><table width=100%>
							<tr><td><script type="text/javascript">
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
									imprime(catAUX);
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
