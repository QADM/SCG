/*
Description: Vista para el catálogo de Finalidades
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_FUN_FINALIDAD]
-- ----------------------------
DROP VIEW [EGR_FUN_FINALIDAD]
GO
CREATE VIEW [EGR_FUN_FINALIDAD] AS 
SELECT
	finalidad.ENUM_ID AS Id_finalidad,
	CAST (
		finalidad.ENUM_ID AS VARCHAR (10)
	) + ' ' + finalidad.DESCRIPTION AS Finalidad
FROM
	ENUMERATION finalidad
WHERE
	finalidad.ENUM_TYPE_ID = 'CL_FUNCIONAL'
AND finalidad.NIVEL_ID = 'FINALIDAD'
GO
