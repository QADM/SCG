/*
Description: Vista para el catálogo de URs
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_ADM_UNIDAD_RESPONSABLE]
-- ----------------------------
DROP VIEW [COM_ADM_UNIDAD_RESPONSABLE]
GO
CREATE VIEW [COM_ADM_UNIDAD_RESPONSABLE] AS 
SELECT
	PG.PARTY_ID AS Id_unidad_responsable,
	CAST (PG.PARTY_ID AS VARCHAR(10)) + ' ' + PG.GROUP_NAME AS Unidad_responsable
FROM
	PARTy P,
	PARTY_GROUP PG
WHERE
	p.nivel_id = 'UNIDAD_RESPONSABLE'
AND PG.PARTY_ID = P.PARTY_ID
GO
