/*
Description: Vista para el catálogo de Capitulos del Gasto
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_COG_CAP]
-- ----------------------------
DROP VIEW [EGR_COG_CAP]
GO
CREATE VIEW [EGR_COG_CAP] AS 
SELECT
	capitulo.PRODUCT_CATEGORY_ID AS Id_capitulo,
	CAST (
		capitulo.PRODUCT_CATEGORY_ID AS VARCHAR (10)
	) + ' ' + capitulo.DESCRIPTION AS Capitulo
FROM
	dbo.PRODUCT_CATEGORY AS capitulo
WHERE
	capitulo.PRODUCT_CATEGORY_TYPE_ID = 'CAPITULO'
AND capitulo.PRODUCT_CATEGORY_ID IS NOT NULL
GO
