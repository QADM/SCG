


<@import location="component://opentaps-common/webapp/common/includes/lib/opentapsFormMacros.ftl"/>


		<table>
			<#list mapTransPresup?keys as row>
				<tr>
					<#if mapTransPresup.get(row)??>
						
						<#if row=="finalidad">
							<td>
								${row}
							</td>
							<td>
								${mapTransPresup.get(row)}
							</td>
						</#if>
					
					
					</#if>
				</tr>
			</#list>
		<table>
