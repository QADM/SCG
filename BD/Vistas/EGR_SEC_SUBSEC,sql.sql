/*
Description: Vista para el catálogo de Subsectores
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_SEC_SUBSEC]
-- ----------------------------
DROP VIEW [EGR_SEC_SUBSEC]
GO
CREATE VIEW [EGR_SEC_SUBSEC] AS 
SELECT
	subsector.ENUM_ID AS Id_subsector,
	CAST (
		subsector.ENUM_ID AS VARCHAR (10)
	) + ' ' + subsector.DESCRIPTION AS Subsector
FROM
	ENUMERATION subsector
WHERE
	subsector.ENUM_TYPE_ID = 'CL_SECTORIAL'
AND subsector.NIVEL_ID = 'SUBSECTOR'
GO
