/*
Description: Vista para el catálogo de Tipos de Gasto
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_COG_TIPO_GASTO]
-- ----------------------------
DROP VIEW [EGR_COG_TIPO_GASTO]
GO
CREATE VIEW [EGR_COG_TIPO_GASTO] AS 
SELECT
	tipo_gasto.ENUM_ID AS Id_tipo_gasto,
	CAST (
		tipo_gasto.ENUM_ID AS VARCHAR (10)
	) + ' ' + tipo_gasto.DESCRIPTION AS Tipo_de_gasto
FROM
	ENUMERATION tipo_gasto
WHERE
	tipo_gasto.enum_type_id = 'TIPO_GASTO'
AND tipo_gasto.sequence_ID IS NOT NULL
GO
