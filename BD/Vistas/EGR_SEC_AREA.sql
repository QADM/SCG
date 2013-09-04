/*
Description: Vista para el catálogo de Areas
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_SEC_AREA]
-- ----------------------------
DROP VIEW [EGR_SEC_AREA]
GO
CREATE VIEW [EGR_SEC_AREA] AS 
SELECT
	area.ENUM_ID AS Id_area,
	CAST (area.ENUM_ID AS VARCHAR(10)) + ' ' + area.DESCRIPTION AS Area
FROM
	ENUMERATION area
WHERE
	area.ENUM_TYPE_ID = 'CL_SECTORIAL'
AND area.NIVEL_ID = 'AREA'
GO
