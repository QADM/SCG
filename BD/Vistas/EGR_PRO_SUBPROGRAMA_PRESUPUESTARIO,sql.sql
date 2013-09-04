/*
Description: Vista para el catálogo de Subrogramas presupuestarios
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_PRO_SUBPROGRAMA_PRESUPUESTARIO]
-- ----------------------------
DROP VIEW [EGR_PRO_SUBPROGRAMA_PRESUPUESTARIO]
GO
CREATE VIEW [EGR_PRO_SUBPROGRAMA_PRESUPUESTARIO] AS 
SELECT
	subprograma_presupuestario.WORK_EFFORT_NAME AS Id_subprograma_presupuestario,
	CAST (
		subprograma_presupuestario.WORK_EFFORT_NAME AS VARCHAR (10)
	) + ' ' + subprograma_presupuestario.description AS Subprograma_presupuestario
FROM
	WORK_EFFORT subprograma_presupuestario
WHERE
	subprograma_presupuestario.NIVEL_ID = 'SUBPROGRAMA_PRESUPUE'
GO
