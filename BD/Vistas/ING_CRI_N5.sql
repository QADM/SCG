/*
Description: Vista para el catálogo de N5s
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [ING_CRI_N5]
-- ----------------------------
DROP VIEW [ING_CRI_N5]
GO
CREATE VIEW [ING_CRI_N5] AS 
SELECT
	n5.PRODUCT_CATEGORY_ID AS Id_nivel_5,
	CAST (
		n5.PRODUCT_CATEGORY_ID AS VARCHAR (10)
	) + ' ' + n5.DESCRIPTION AS Nivel_5
FROM
	dbo.PRODUCT_CATEGORY AS n5
WHERE
	n5.PRODUCT_CATEGORY_TYPE_ID = 'NIVEL_5_ING'
AND n5.PRODUCT_CATEGORY_ID IS NOT NULL
GO
