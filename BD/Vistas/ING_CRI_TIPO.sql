/*
Description: Vista para el catálogo de Tipos de Ingreso
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [ING_CRI_TIPO]
-- ----------------------------
DROP VIEW [ING_EJERCICIO_INGRESO]
GO
CREATE VIEW [ING_EJERCICIO_INGRESO] AS 
SELECT
	tipo.PRODUCT_CATEGORY_ID AS Id_tipo,
	CAST (
		tipo.PRODUCT_CATEGORY_ID AS VARCHAR (10)
	) + ' ' + tipo.DESCRIPTION AS Tipo
FROM
	dbo.PRODUCT_CATEGORY AS tipo
WHERE
	tipo.PRODUCT_CATEGORY_TYPE_ID = 'TIPO'
AND tipo.PRODUCT_CATEGORY_ID IS NOT NULL
GO
