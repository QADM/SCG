/*
Description: Vista para el catálogo de Municipios
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_GEO_MUNICIPIO]
-- ----------------------------
DROP VIEW [COM_GEO_MUNICIPIO]
GO
CREATE VIEW [COM_GEO_MUNICIPIO] AS 
SELECT
	geo.GEO_ID AS Id_municipio,
	CAST (geo.GEO_ID AS VARCHAR(10)) + ' ' + geo.GEO_NAME AS Municipio
FROM
	GEO geo
WHERE
	geo.GEO_TYPE_ID = 'MUNICIPALITY'
GO
