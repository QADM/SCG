/*
Description: Vista para el catálogo de Programas planes
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_PRO_PROGRAMA_PLAN]
-- ----------------------------
DROP VIEW [EGR_PRO_PROGRAMA_PLAN]
GO
CREATE VIEW [EGR_PRO_PROGRAMA_PLAN] AS 
SELECT
	programa_plan.WORK_EFFORT_NAME AS Id_programa_plan,
	CAST (
		programa_plan.WORK_EFFORT_NAME AS VARCHAR (10)
	) + ' ' + programa_plan.description AS Programa_plan
FROM
	WORK_EFFORT programa_plan
WHERE
	programa_plan.NIVEL_ID = 'EJE_RECTOR'
GO
