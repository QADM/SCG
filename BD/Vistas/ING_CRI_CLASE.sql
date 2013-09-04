/*
Description: Vista para el catálogo de Clases
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [ING_CRI_CLASE]
-- ----------------------------
DROP VIEW [ING_CRI_CLASE]
GO
CREATE VIEW [ING_CRI_CLASE] AS 
SELECT
	clase.PRODUCT_CATEGORY_ID AS Id_clase,
	CAST (
		clase.PRODUCT_CATEGORY_ID AS VARCHAR (10)
	) + ' ' + clase.DESCRIPTION AS Clase
FROM
	dbo.PRODUCT_CATEGORY AS clase
WHERE
	clase.PRODUCT_CATEGORY_TYPE_ID = 'CLASE'
AND clase.PRODUCT_CATEGORY_ID IS NOT NULL
GO
