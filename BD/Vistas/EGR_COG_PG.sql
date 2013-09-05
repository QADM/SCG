/*
Description: Vista para el catálogo de Partidas genericas
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_COG_PG]
-- ----------------------------
DROP VIEW [EGR_COG_PG]
GO
CREATE VIEW [EGR_COG_PG] AS 
SELECT
	partida_generica.PRODUCT_CATEGORY_ID AS Id_partida_generica,
	CAST (
		partida_generica.PRODUCT_CATEGORY_ID AS VARCHAR (10)
	) + ' ' + partida_generica.DESCRIPTION AS Partida_generica
FROM
	dbo.PRODUCT_CATEGORY AS partida_generica
WHERE
	partida_generica.PRODUCT_CATEGORY_TYPE_ID = 'PARTIDA GENERICA'
AND partida_generica.PRODUCT_CATEGORY_ID IS NOT NULL
GO
