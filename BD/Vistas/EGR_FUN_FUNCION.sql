/*
Description: Vista para el catálogo de Funciones
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_FUN_FUNCION]
-- ----------------------------
DROP VIEW [EGR_FUN_FUNCION]
GO
CREATE VIEW [EGR_FUN_FUNCION] AS 
SELECT
	funcion.ENUM_ID AS Id_funcion,
	CAST (
		funcion.ENUM_ID AS VARCHAR (10)
	) + ' ' + funcion.DESCRIPTION AS Funcion
FROM
	ENUMERATION funcion
WHERE
	funcion.ENUM_TYPE_ID = 'CL_FUNCIONAL'
AND funcion.NIVEL_ID = 'FUNCION'
GO
