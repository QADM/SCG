/*
Description: Vista para el catálogo de UOs
Author: CGZ
Date: 2013-09-04 17:12:42
*/


-- ----------------------------
-- View structure for [COM_ADM_UNIDAD_ORGANIZACIONAL]
-- ----------------------------
DROP VIEW [COM_ADM_UNIDAD_ORGANIZACIONAL]
GO
CREATE VIEW [COM_ADM_UNIDAD_ORGANIZACIONAL] AS 
SELECT
	PG.PARTY_ID AS Id_unidad_organizacional,
	CAST (PG.PARTY_ID AS VARCHAR(10)) + ' ' + PG.GROUP_NAME AS Unidad_organizacional
FROM
	PARTy P,
	PARTY_GROUP PG
WHERE
	p.nivel_id = 'UNIDAD_ORGANIZACIONA'
AND PG.PARTY_ID = P.PARTY_ID
GO
