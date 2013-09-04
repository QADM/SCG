/*
Description: Vista para el catálogo de Fuentes
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_FUE_FUENTE]
-- ----------------------------
DROP VIEW [COM_FUE_FUENTE]
GO
CREATE VIEW [COM_FUE_FUENTE] AS 
SELECT
	fuente.ENUM_ID AS Id_fuente,
	CAST (
		fuente.ENUM_ID AS VARCHAR (10)
	) + ' ' + fuente.DESCRIPTION AS Fuente
FROM
	ENUMERATION fuente
WHERE
	fuente.ENUM_TYPE_ID = 'CL_FUENTE_RECURSOS'
AND fuente.NIVEL_ID = 'FUENTE'
GO