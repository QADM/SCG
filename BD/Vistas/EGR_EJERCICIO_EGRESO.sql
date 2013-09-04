/*
Description: Vista del Ejercicio del Gasto
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_EJERCICIO_EGRESO]
-- ----------------------------
DROP VIEW [EGR_EJERCICIO_EGRESO]
GO
CREATE VIEW [EGR_EJERCICIO_EGRESO] AS 
SELECT
	APROBADO.acctg_trans_id AS Id_transaccion,
	APROBADO.clavei AS Clave,
	APROBADO.ciclo AS Id_ciclo,
	APROBADO.periodo AS Id_periodo,
	APROBADO.unidad_responsable AS Id_unidad_responsable,
	APROBADO.unidad_organizacional AS Id_unidad_organizacional,
	APROBADO.unidad_ejecutora AS Id_unidad_ejecutora,
	APROBADO.fuente AS Id_fuente,
	APROBADO.sub_fuente AS Id_subfuente,
	APROBADO.sub_fuente_especifica AS Id_subfuente_especifica,
	APROBADO.entidad_federativa AS Id_entidad_federativa,
	APROBADO.region AS Id_region,
	APROBADO.municipio AS Id_municipio,
	APROBADO.localidad AS Id_localidad,
	APROBADO.finalidad AS Id_finalidad,
	APROBADO.funcion AS Id_funcion,
	APROBADO.sub_funcion AS Id_subfuncion,
	APROBADO.programa_plan AS Id_programa_plan,
	APROBADO.programa_presupuestario AS Id_programa_presupuestario,
	APROBADO.sub_programa_presupuestario AS Id_subprograma_presupuestario,
	APROBADO.actividad AS Id_actividad_institucional,
	APROBADO.tipo_gasto AS Id_tipo_de_gasto,
	APROBADO.capitulo AS Id_capitulo,
	APROBADO.concepto AS Id_concepto,
	APROBADO.partida_generica AS Id_partida_generica,
	APROBADO.partida_especifica AS Id_partida_especifica,
	APROBADO.sector AS Id_sector,
	APROBADO.sub_sector AS Id_subsector,
	APROBADO.area AS Id_area,
	APROBADO.monto AS Aprobado,
	ISNULL(MODIFICADO.monto, 0) AS Modificaciones,
	ISNULL(COMPROMETIDO.monto, 0) AS Comprometido,
	ISNULL(DEVENGADO.monto, 0) AS Devengado,
	ISNULL(EJERCIDO.monto, 0) AS Ejercido,
	ISNULL(PAGADO.monto, 0) AS Pagado
FROM
	(
		SELECT
			ACT.acctg_trans_id AS acctg_trans_id,
			APRE.clave_pres + '-' + CAST (
				MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
			) AS clavei,
			YEAR (ACT.posted_date) AS ciclo,
			MONTH (ACT.posted_date) AS periodo,
			APRE.unidad_responsable,
			APRE.unidad_organizacional,
			APRE.unidad_ejecutora,
			APRE.fuente,
			APRE.sub_fuente,
			APRE.sub_fuente_especifica,
			APRE.entidad_federativa,
			APRE.region,
			APRE.municipio,
			APRE.localidad,
			APRE.finalidad,
			APRE.funcion,
			APRE.sub_funcion,
			APRE.programa_plan,
			APRE.programa_presupuestario,
			APRE.sub_programa_presupuestario,
			APRE.actividad,
			APRE.tipo_gasto,
			APRE.capitulo,
			APRE.concepto,
			APRE.partida_generica,
			APRE.partida_especifica,
			APRE.sector,
			APRE.sub_sector,
			APRE.area,
			dbo.obten_monto_por_naturaleza (
				AEN.amount,
				GLA.naturaleza,
				AEN.debit_credit_flag
			) AS monto
		FROM
			ACCTG_TRANS_PRESUPUESTAL APRE,
			ACCTG_TRANS ACT,
			ACCTG_TRANS_ENTRY AEN,
			GL_ACCOUNT GLA
		WHERE
			ACT.ACCTG_TRANS_TYPE_ID = 'TPRESUPAPROBADO'
		AND ACT.acctg_trans_id = APRE.acctg_trans_id
		AND AEN.acctg_trans_id = ACT.acctg_trans_id
		AND GLA.gl_account_id = AEN.gl_account_id
		AND GLA.GL_ACCOUNT_CLASS_ID = 'PRESUPUESTO APROBADO'
	) APROBADO
LEFT OUTER JOIN (
	SELECT
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		) AS clavei,
		SUM (
			dbo.obten_monto_por_naturaleza (
				AEN.amount,
				GLA.naturaleza,
				AEN.debit_credit_flag
			)
		) AS monto
	FROM
		ACCTG_TRANS_PRESUPUESTAL APRE,
		ACCTG_TRANS ACT,
		ACCTG_TRANS_ENTRY AEN,
		GL_ACCOUNT GLA
	WHERE
		(
			ACT.ACCTG_TRANS_TYPE_ID LIKE 'TEGRESOAMPLIACION'
			OR ACT.ACCTG_TRANS_TYPE_ID LIKE 'TEGRESOREDUCCION'
		)
	AND ACT.acctg_trans_id = APRE.acctg_trans_id
	AND AEN.acctg_trans_id = ACT.acctg_trans_id
	AND GLA.gl_account_id = AEN.gl_account_id
	AND GLA.GL_ACCOUNT_CLASS_ID = 'PRESUPUESTO MODIFICA'
	GROUP BY
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		)
) MODIFICADO ON APROBADO.clavei = MODIFICADO.clavei
LEFT OUTER JOIN (
	SELECT
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		) AS clavei,
		SUM (
			dbo.obten_monto_por_naturaleza (
				AEN.amount,
				GLA.naturaleza,
				AEN.debit_credit_flag
			)
		) AS monto
	FROM
		ACCTG_TRANS_PRESUPUESTAL APRE,
		ACCTG_TRANS ACT,
		ACCTG_TRANS_ENTRY AEN,
		GL_ACCOUNT GLA
	WHERE
		ACT.ACCTG_TRANS_TYPE_ID LIKE 'TEGRESOCOMPROMETIDO'
	AND ACT.acctg_trans_id = APRE.acctg_trans_id
	AND AEN.acctg_trans_id = ACT.acctg_trans_id
	AND GLA.gl_account_id = AEN.gl_account_id
	AND GLA.GL_ACCOUNT_CLASS_ID = 'PRESUPUESTO COMPROME'
	GROUP BY
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		)
) COMPROMETIDO ON APROBADO.clavei = COMPROMETIDO.clavei
LEFT OUTER JOIN (
	SELECT
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		) AS clavei,
		SUM (
			dbo.obten_monto_por_naturaleza (
				AEN.amount,
				GLA.naturaleza,
				AEN.debit_credit_flag
			)
		) AS monto
	FROM
		ACCTG_TRANS_PRESUPUESTAL APRE,
		ACCTG_TRANS ACT,
		ACCTG_TRANS_ENTRY AEN,
		GL_ACCOUNT GLA
	WHERE
		ACT.ACCTG_TRANS_TYPE_ID LIKE 'TEGRESODEVENGADO'
	AND ACT.acctg_trans_id = APRE.acctg_trans_id
	AND AEN.acctg_trans_id = ACT.acctg_trans_id
	AND GLA.gl_account_id = AEN.gl_account_id
	AND GLA.GL_ACCOUNT_CLASS_ID = 'PRESUPUESTO DEVENGAD'
	GROUP BY
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		)
) DEVENGADO ON APROBADO.clavei = DEVENGADO.clavei
LEFT OUTER JOIN (
	SELECT
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		) AS clavei,
		SUM (
			dbo.obten_monto_por_naturaleza (
				AEN.amount,
				GLA.naturaleza,
				AEN.debit_credit_flag
			)
		) AS monto
	FROM
		ACCTG_TRANS_PRESUPUESTAL APRE,
		ACCTG_TRANS ACT,
		ACCTG_TRANS_ENTRY AEN,
		GL_ACCOUNT GLA
	WHERE
		ACT.ACCTG_TRANS_TYPE_ID LIKE 'TEGRESOEJERCIDO'
	AND ACT.acctg_trans_id = APRE.acctg_trans_id
	AND AEN.acctg_trans_id = ACT.acctg_trans_id
	AND GLA.gl_account_id = AEN.gl_account_id
	AND GLA.GL_ACCOUNT_CLASS_ID = 'PRESUPUESTO EJERCIDO'
	GROUP BY
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		)
) EJERCIDO ON APROBADO.clavei = EJERCIDO.clavei
LEFT OUTER JOIN (
	SELECT
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		) AS clavei,
		SUM (
			dbo.obten_monto_por_naturaleza (
				AEN.amount,
				GLA.naturaleza,
				AEN.debit_credit_flag
			)
		) AS monto
	FROM
		ACCTG_TRANS_PRESUPUESTAL APRE,
		ACCTG_TRANS ACT,
		ACCTG_TRANS_ENTRY AEN,
		GL_ACCOUNT GLA
	WHERE
		ACT.ACCTG_TRANS_TYPE_ID LIKE 'TEGRESOPAGADO'
	AND ACT.acctg_trans_id = APRE.acctg_trans_id
	AND AEN.acctg_trans_id = ACT.acctg_trans_id
	AND GLA.gl_account_id = AEN.gl_account_id
	AND GLA.GL_ACCOUNT_CLASS_ID = 'PRESUPUESTO PAGADO'
	GROUP BY
		APRE.clave_pres + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		)
) PAGADO ON APROBADO.clavei = PAGADO.clavei
GO
