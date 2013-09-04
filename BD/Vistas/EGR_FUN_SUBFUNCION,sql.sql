/*
Description: Vista para el catálogo de Subfunciones
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_FUN_SUBFUNCION]
-- ----------------------------
DROP VIEW [EGR_FUN_SUBFUNCION]
GO
CREATE VIEW [EGR_FUN_SUBFUNCION] AS 
SELECT
	subfuncion.ENUM_ID AS Id_subfuncion,
	CAST (
		subfuncion.ENUM_ID AS VARCHAR (10)
	) + ' ' + subfuncion.DESCRIPTION AS Subfuncion
FROM
	ENUMERATION subfuncion
WHERE
	subfuncion.ENUM_TYPE_ID = 'CL_FUNCIONAL'
AND subfuncion.NIVEL_ID = 'SUBFUNCION'
GO
