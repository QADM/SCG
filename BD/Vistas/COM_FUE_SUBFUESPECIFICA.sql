/*
Description: Vista para el catálogo de Subfuentes especificas
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_FUE_SUBFUESPECIFICA]
-- ----------------------------
DROP VIEW [COM_FUE_SUBFUESPECIFICA]
GO
CREATE VIEW [COM_FUE_SUBFUESPECIFICA] AS 
SELECT
	subfuente_especifica.ENUM_ID AS Id_subfuente_especifica,
	CAST (
		subfuente_especifica.ENUM_ID AS VARCHAR (10)
	) + ' ' + subfuente_especifica.DESCRIPTION AS Subfuente_especifica
FROM
	ENUMERATION subfuente_especifica
WHERE
	subfuente_especifica.ENUM_TYPE_ID = 'CL_FUENTE_RECURSOS'
AND subfuente_especifica.NIVEL_ID = 'SUBFUENTE_ESPECIFICA'
GO
