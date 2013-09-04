/*
Description: Vista para el catálogo de Periodos
Author: CGZ
Date: 2013-09-04 17:12:42
*/

-- ----------------------------
-- View structure for [COM_PERIODO]
-- ----------------------------
DROP VIEW [COM_PERIODO]
GO
CREATE VIEW [COM_PERIODO] AS 
SELECT
	PERIODO.Id_periodo AS Id_periodo,
	PERIODO.n_periodo + ' ' + PERIODO.periodo AS Periodo
FROM
	(
		SELECT
			1 AS Id_periodo,
			'01' AS n_periodo,
			'Enero' AS periodo
		UNION
			SELECT
				2 AS Id_periodo,
				'02' AS n_periodo,
				'Febrero' AS periodo
			UNION
				SELECT
					3 AS Id_periodo,
					'03' AS n_periodo,
					'Marzo' AS periodo
				UNION
					SELECT
						4 AS Id_periodo,
						'04' AS n_periodo,
						'Abril' AS periodo
					UNION
						SELECT
							5 AS Id_periodo,
							'05' AS n_periodo,
							'Mayo' AS periodo
						UNION
							SELECT
								6 AS Id_periodo,
								'06' AS n_periodo,
								'Junio' AS periodo
							UNION
								SELECT
									7 AS Id_periodo,
									'07' AS n_periodo,
									'Julio' AS periodo
								UNION
									SELECT
										8 AS Id_periodo,
										'08' AS n_periodo,
										'Agosto' AS periodo
									UNION
										SELECT
											9 AS Id_periodo,
											'09' AS n_periodo,
											'Septiembre' AS periodo
										UNION
											SELECT
												10 AS Id_periodo,
												'10' AS n_periodo,
												'Octubre' AS periodo
											UNION
												SELECT
													11 AS Id_periodo,
													'11' AS n_periodo,
													'Noviembre' AS periodo
												UNION
													SELECT
														12 AS Id_periodo,
														'12' AS n_periodo,
														'Diciembre' AS periodo
	) PERIODO
GO
