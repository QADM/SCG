/*
Description: Vista para el catálogo de Programas presupuestarios
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_PRO_PROGRAMA_PRESUPUESTARIO]
-- ----------------------------
DROP VIEW [EGR_PRO_PROGRAMA_PRESUPUESTARIO]
GO
CREATE VIEW [EGR_PRO_PROGRAMA_PRESUPUESTARIO] AS 
SELECT
	programa_presupuestario.WORK_EFFORT_NAME AS Id_programa_presupuestario,
	CAST (
		programa_presupuestario.WORK_EFFORT_NAME AS VARCHAR (10)
	) + ' ' + programa_presupuestario.description AS Programa_presupuestario
FROM
	WORK_EFFORT programa_presupuestario
WHERE
	programa_presupuestario.NIVEL_ID = 'PROGRAMA_PRESUPUESTA'
GO
