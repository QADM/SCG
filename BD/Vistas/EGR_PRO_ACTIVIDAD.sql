/*
Description: Vista para el catálogo de Actividades
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_PRO_ACTIVIDAD]
-- ----------------------------
DROP VIEW [EGR_PRO_ACTIVIDAD]
GO
CREATE VIEW [EGR_PRO_ACTIVIDAD] AS 
SELECT
	actividad.WORK_EFFORT_NAME AS Id_actividad_institucional,
	CAST (
		actividad.WORK_EFFORT_NAME AS VARCHAR (10)
	) + ' ' + actividad.description AS Actividad_institucional
FROM
	WORK_EFFORT actividad
WHERE
	actividad.NIVEL_ID = 'ACTIVIDAD_INSTITUCIO'
GO
