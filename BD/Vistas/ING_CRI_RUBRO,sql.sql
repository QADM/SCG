/*
Description: Vista para el catálogo de Rubros
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [ING_CRI_RUBRO]
-- ----------------------------
DROP VIEW [ING_CRI_RUBRO]
GO
CREATE VIEW [ING_CRI_RUBRO] AS 
SELECT
	rubro.PRODUCT_CATEGORY_ID AS Id_rubro,
	CAST (
		rubro.PRODUCT_CATEGORY_ID AS VARCHAR (10)
	) + ' ' + rubro.DESCRIPTION AS Rubro
FROM
	dbo.PRODUCT_CATEGORY AS rubro
WHERE
	rubro.PRODUCT_CATEGORY_TYPE_ID = 'RUBRO'
AND rubro.PRODUCT_CATEGORY_ID IS NOT NULL
GO
