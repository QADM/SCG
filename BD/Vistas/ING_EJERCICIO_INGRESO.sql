/*
Description: Vista del Ejercicio del Ingreso
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [ING_EJERCICIO_INGRESO]
-- ----------------------------
DROP VIEW [ING_EJERCICIO_INGRESO]
GO
CREATE VIEW [ING_EJERCICIO_INGRESO] AS 
SELECT
	ESTIMADA.acctg_trans_id AS Id_transaccion,
	ESTIMADA.clavei AS Clave,
	ESTIMADA.ciclo AS Id_ciclo,
	ESTIMADA.periodo AS Id_periodo,
	ESTIMADA.unidad_responsable AS Id_unidad_responsable,
	ESTIMADA.unidad_organizacional AS Id_unidad_organizacional,
	ESTIMADA.unidad_ejecutora AS Id_unidad_ejecutora,
	ESTIMADA.rubro AS Id_rubro,
	ESTIMADA.tipo AS Id_tipo,
	ESTIMADA.clase AS Id_clase,
	ESTIMADA.concepto_rub AS Id_concepto,
	ESTIMADA.nivel5 AS Id_nivel_5,
	ESTIMADA.fuente AS Id_fuente,
	ESTIMADA.sub_fuente AS Id_sub_fuente,
	ESTIMADA.sub_fuente_especifica AS Id_sub_fuente_especifica,
	ESTIMADA.entidad_federativa AS Id_entidad_federativa,
	ESTIMADA.region AS Id_region,
	ESTIMADA.municipio AS Id_municipio,
	ESTIMADA.localidad AS Id_localidad,
	isnull(ESTIMADA.monto, 0) AS Estimado,
	isnull(MODIFICADA.monto, 0) AS Modificaciones,
	isnull(DEVENGADA.monto, 0) AS Devengado,
	isnull(RECAUDADA.monto, 0) AS Recaudado
FROM
	(
		SELECT
			ACT.acctg_trans_id AS acctg_trans_id,
			APRE.CLAVE_PRES + '-' + CAST (
				MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
			) AS clavei,
			ACT.description,
			YEAR (ACT.posted_date) AS ciclo,
			MONTH (ACT.posted_date) AS periodo,
			APRE.unidad_responsable,
			APRE.unidad_organizacional,
			APRE.unidad_ejecutora,
			APRE.rubro,
			APRE.tipo,
			APRE.clase,
			APRE.concepto_rub,
			APRE.nivel5,
			APRE.fuente,
			APRE.sub_fuente,
			APRE.sub_fuente_especifica,
			APRE.entidad_federativa,
			APRE.region,
			APRE.municipio,
			APRE.localidad,
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
			ACT.acctg_trans_type_id = 'TINGRESOESTIMADO'
		AND ACT.acctg_trans_id = APRE.acctg_trans_id
		AND AEN.acctg_trans_id = ACT.acctg_trans_id
		AND GLA.gl_account_id = AEN.gl_account_id
		AND GLA.GL_ACCOUNT_CLASS_ID = 'INGRESO ESTIMADO'
	) ESTIMADA
LEFT OUTER JOIN (
	SELECT
		APRE.CLAVE_PRES + '-' + CAST (
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
			ACT.acctg_trans_type_id = 'TINGRESOAMPLIACION'
			OR ACT.acctg_trans_type_id = 'TINGRESOREDUCCION'
		)
	AND ACT.acctg_trans_id = APRE.acctg_trans_id
	AND AEN.acctg_trans_id = ACT.acctg_trans_id
	AND GLA.gl_account_id = AEN.gl_account_id
	AND GLA.GL_ACCOUNT_CLASS_ID = 'INGRESO MODIFICADO'
	GROUP BY
		APRE.CLAVE_PRES + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		)
) MODIFICADA ON ESTIMADA.clavei = MODIFICADA.clavei
LEFT OUTER JOIN (
	SELECT
		APRE.CLAVE_PRES + '-' + CAST (
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
		ACT.acctg_trans_type_id = 'TINGRESODEVENGADO'
	AND ACT.acctg_trans_id = APRE.acctg_trans_id
	AND AEN.acctg_trans_id = ACT.acctg_trans_id
	AND GLA.gl_account_id = AEN.gl_account_id
	AND GLA.GL_ACCOUNT_CLASS_ID = 'INGRESO DEVENGADO'
	GROUP BY
		APRE.CLAVE_PRES + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		)
) DEVENGADA ON ESTIMADA.clavei = DEVENGADA.clavei
LEFT OUTER JOIN (
	SELECT
		APRE.CLAVE_PRES + '-' + CAST (
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
		ACT.acctg_trans_type_id LIKE '%ING%'
	AND ACT.acctg_trans_id = APRE.acctg_trans_id
	AND AEN.acctg_trans_id = ACT.acctg_trans_id
	AND GLA.gl_account_id = AEN.gl_account_id
	AND GLA.GL_ACCOUNT_CLASS_ID = 'INGRESO RECAUDADO'
	GROUP BY
		APRE.CLAVE_PRES + '-' + CAST (
			MONTH (ACT.POSTED_DATE) AS VARCHAR (2)
		)
) RECAUDADA ON ESTIMADA.clavei = RECAUDADA.clavei
GO
