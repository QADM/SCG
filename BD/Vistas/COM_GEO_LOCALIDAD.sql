/*
Description: Vista para el catálogo de Localidades
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_GEO_LOCALIDAD]
-- ----------------------------
DROP VIEW [COM_GEO_LOCALIDAD]
GO
CREATE VIEW [COM_GEO_LOCALIDAD] AS 
SELECT
	geo.GEO_ID AS Id_localidad,
	CAST (geo.GEO_ID AS VARCHAR(10)) + ' ' + geo.GEO_NAME AS Localidad
FROM
	GEO geo
WHERE
	geo.GEO_TYPE_ID = 'LOCALIDAD'
GO
