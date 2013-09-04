/*
Description: Vista para el catálogo de UEs
Author: CGZ
Date: 2013-09-04 17:12:42
*/


-- ----------------------------
-- View structure for [COM_ADM_UNIDAD_EJECUTORA]
-- ----------------------------
DROP VIEW [COM_ADM_UNIDAD_EJECUTORA]
GO
CREATE VIEW [COM_ADM_UNIDAD_EJECUTORA] AS 
SELECT
	PG.PARTY_ID AS Id_unidad_ejecutora,
	CAST (PG.PARTY_ID AS VARCHAR(10)) + ' ' + PG.GROUP_NAME AS Unidad_ejecutora
FROM
	PARTy P,
	PARTY_GROUP PG
WHERE
	p.nivel_id = 'UNIDAD_EJECUTORA'
AND PG.PARTY_ID = P.PARTY_ID
GO