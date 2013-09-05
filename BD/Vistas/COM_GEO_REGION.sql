/*
Description: Vista para el catálogo de Regiones
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_GEO_REGION]
-- ----------------------------
DROP VIEW [COM_GEO_REGION]
GO
CREATE VIEW [COM_GEO_REGION] AS 
SELECT
	geo.GEO_ID AS Id_region,
	CAST (geo.GEO_ID AS VARCHAR(10)) + ' ' + geo.GEO_NAME AS Region
FROM
	GEO geo
WHERE
	geo.GEO_TYPE_ID = 'REGION'
GO
