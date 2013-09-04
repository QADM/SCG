/*
Description: Vista para el catálogo de Partidas especificas
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_COG_PE]
-- ----------------------------
DROP VIEW [EGR_COG_PE]
GO
CREATE VIEW [EGR_COG_PE] AS 
SELECT
	partida_especifica.PRODUCT_CATEGORY_ID AS Id_partida_especifica,
	CAST (
		partida_especifica.PRODUCT_CATEGORY_ID AS VARCHAR (10)
	) + ' ' + partida_especifica.DESCRIPTION AS Partida_especifica
FROM
	dbo.PRODUCT_CATEGORY AS partida_especifica
WHERE
	partida_especifica.PRODUCT_CATEGORY_TYPE_ID = 'PARTIDA ESPECIFICA'
AND partida_especifica.PRODUCT_CATEGORY_ID IS NOT NULL
GO
