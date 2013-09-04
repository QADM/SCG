/*
Description: Vista para el catálogo de Conceptos
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [EGR_COG_CON]
-- ----------------------------
DROP VIEW [EGR_COG_CON]
GO
CREATE VIEW [EGR_COG_CON] AS 
SELECT
	concepto.PRODUCT_CATEGORY_ID AS Id_concepto,
	CAST (
		concepto.PRODUCT_CATEGORY_ID AS VARCHAR (10)
	) + ' ' + concepto.DESCRIPTION AS Concepto
FROM
	dbo.PRODUCT_CATEGORY AS concepto
WHERE
	concepto.PRODUCT_CATEGORY_TYPE_ID = 'CONCEPTO'
AND concepto.PRODUCT_CATEGORY_ID IS NOT NULL
GO
