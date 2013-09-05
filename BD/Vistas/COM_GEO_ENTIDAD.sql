/*
Description: Vista para el catálogo de Entidades
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_GEO_ENTIDAD]
-- ----------------------------
DROP VIEW [COM_GEO_ENTIDAD]
GO
CREATE VIEW [COM_GEO_ENTIDAD] AS 
SELECT
	geo.GEO_ID AS Id_entidad,
	CAST (geo.GEO_ID AS VARCHAR(10)) + ' ' + geo.GEO_NAME AS Entidad_federativa
FROM
	GEO geo
WHERE
	geo.GEO_TYPE_ID = 'ENTIDAD_FEDERATIVA'
GO
