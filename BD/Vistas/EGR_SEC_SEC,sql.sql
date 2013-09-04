/*
Description: Vista para el catálogo de Sectores
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_SEC_SEC]
-- ----------------------------
DROP VIEW [EGR_SEC_SEC]
GO
CREATE VIEW [EGR_SEC_SEC] AS 
SELECT
	sector.ENUM_ID AS Id_sector,
	CAST (
		sector.ENUM_ID AS VARCHAR (10)
	) + ' ' + sector.DESCRIPTION AS Sector
FROM
	ENUMERATION sector
WHERE
	sector.ENUM_TYPE_ID = 'CL_SECTORIAL'
AND sector.NIVEL_ID = 'SECTOR'
GO
