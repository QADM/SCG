/*
Description: Vista para el catálogo de Subfuentes
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_FUE_SUBFUENTE]
-- ----------------------------
DROP VIEW [COM_FUE_SUBFUENTE]
GO
CREATE VIEW [COM_FUE_SUBFUENTE] AS 
SELECT
	subfuente.ENUM_ID AS Id_subfuente,
	CAST (
		subfuente.ENUM_ID AS VARCHAR (10)
	) + ' ' + subfuente.DESCRIPTION AS Subfuente
FROM
	ENUMERATION subfuente
WHERE
	subfuente.ENUM_TYPE_ID = 'CL_FUENTE_RECURSOS'
AND subfuente.NIVEL_ID = 'SUBFUENTE'
GO
