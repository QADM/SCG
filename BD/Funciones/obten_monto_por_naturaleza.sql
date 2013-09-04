/*
Description: Obtiene el Monto con el signo correspondiente
				toma en cuenta la naturaleza de la cuenta afectada, 
				asi como el tipo de afectació ie CARGO o ABONO
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- Function structure for [obten_monto_por_naturaleza]
-- ----------------------------
DROP FUNCTION [obten_monto_por_naturaleza]
GO


CREATE FUNCTION [obten_monto_por_naturaleza]
( @monto AS decimal =0 ,
  @naturaleza AS varchar =D ,
  @bandera AS varchar =D 
)
RETURNS decimal
AS
BEGIN
DECLARE @Final AS decimal =0 

	IF @naturaleza = 'D' 
	BEGIN
		IF @bandera = 'D'
		BEGIN
			SET @Final = @monto
		END
		ELSE IF @bandera = 'C'
		BEGIN	
			SET @Final = -1 *@monto
		END
		ELSE
		BEGIN
			SET @Final = NULL
		END
	END
	ELSE IF @naturaleza ='A'
	BEGIN
			IF @bandera ='C'
			BEGIN
				SET @Final = @monto
			END
			ELSE IF @bandera ='D'
			BEGIN
				SET @Final = -1 *@monto
			END
			ELSE
			BEGIN
			SET @Final = NULL
			END
	END
	ELSE
	BEGIN
		SET @Final = NULL
	END

  RETURN @Final
END


GO
