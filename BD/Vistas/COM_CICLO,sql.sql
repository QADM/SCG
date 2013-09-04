/*
Description: Vista para el catálogo de Ciclos
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_CICLO]
-- ----------------------------
DROP VIEW [COM_CICLO]
GO
CREATE VIEW [COM_CICLO] AS 
SELECT
	YEAR (FROM_DATE) AS Id_ciclo,
	CAST (
		YEAR (FROM_DATE) AS VARCHAR (10)
	) + ' ' + PERIOD_NAME AS Ciclo
FROM
	CUSTOM_TIME_PERIOD
WHERE
	PERIOD_TYPE_ID = 'FISCAL_YEAR'
GO
