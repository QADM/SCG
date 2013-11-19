package org.opentaps.dataimport.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.opentaps.base.constants.StatusItemConstants;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.ClasifPresupuestal;
import org.opentaps.base.entities.CustomTimePeriod;
import org.opentaps.base.entities.DataImportPresupuestoIngreso;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.EstructuraClave;
import org.opentaps.base.entities.Geo;
import org.opentaps.base.entities.GlAccountHistory;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.LoteTransaccion;
import org.opentaps.base.entities.MiniGuiaContable;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.ProductCategory;
import org.opentaps.dataimport.UtilImport;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.PresupuestoIngresoDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.PresupuestoIngresoImportServiceInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

public class PresupuestoIngresoImportService extends DomainService implements
		PresupuestoIngresoImportServiceInterface {
	private static final String MODULE = PresupuestoIngresoImportService.class
			.getName();
	// session object, using to store/search pojos.
	private Session session;
	private String organizationPartyId;
	private String lote;
	public int importedRecords;

	public PresupuestoIngresoImportService() {
		super();
	}

	public PresupuestoIngresoImportService(Infrastructure infrastructure,
			User user, Locale locale) throws ServiceException {
		super(infrastructure, user, locale);
	}

	/** {@inheritDoc} */
	public void setOrganizationPartyId(String organizationPartyId) {
		this.organizationPartyId = organizationPartyId;
	}

	/** {@inheritDoc} */
	public void setLote(String lote) {
		this.lote = lote;
	}

	/** {@inheritDoc} */
	public int getImportedRecords() {
		return importedRecords;
	}

	/** {@inheritDoc} */
	public void importPresupuestoIngreso() throws ServiceException {

		try {
			this.session = this.getInfrastructure().getSession();

			PresupuestoIngresoDataImportRepositoryInterface imp_repo = this
					.getDomainsDirectory().getDataImportDomain()
					.getPresupuestoIngresoDataImportRepository();
			LedgerRepositoryInterface ledger_repo = this.getDomainsDirectory()
					.getLedgerDomain().getLedgerRepository();

			List<DataImportPresupuestoIngreso> dataforimp = imp_repo
					.findNotProcessesDataImportPresupuestoIngresoEntries();

			int imported = 0;
			Transaction imp_tx1 = null;
			Transaction imp_tx2 = null;
			Transaction imp_tx3 = null;
			Transaction imp_tx4 = null;

			if (UtilImport.validaLote(ledger_repo, lote, "PresupuestoIngreso")) {
				boolean loteValido = true;
				for (DataImportPresupuestoIngreso rowdata : dataforimp) {
					// Empieza bloque de validaciones
					Debug.log("Empieza bloque de validaciones");
					String mensaje = "";
					ContenedorContable contenedor = new ContenedorContable();
					// mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					// rowdata.getUr(), "UR");
					// mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					// rowdata.getUo(), "UO");
					// mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					// rowdata.getUe(), "UE");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getRub(), "RU", "RUB");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getTip(), "TI", "TIP");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getCla(), "CL", "CLA");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getCon(), "CO", "CON");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getN5(), "N5", "N5");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getF(), "CLAS_FR", "F");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getSf(), "CLAS_FR", "SF");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getSfe(), "CLAS_FR", "SFE");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getEf(), "EF");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getReg(), "REG");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getMun(), "MUN");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getLoc(), "LOC");

					// clasificaciones
					//
					// mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					// rowdata.getUe(), "ADMINISTRATIVA");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getN5(), "NIVEL_5_ING",
					// "RUBRO DEL INGRESO");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo, rowdata.getSfe(),
					// "CL_FUENTE_RECURSOS", "FUENTE DE LOS RECURSOS");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getLoc(), "GEOGRAFICA");
					String anio = "20" + rowdata.getCiclo();

					// Se obtiene la estructura de la clave valida para el ciclo
					EstructuraClave estructura = ledger_repo.findList(
							EstructuraClave.class,
							ledger_repo.map(EstructuraClave.Fields.ciclo,
									anio,
									EstructuraClave.Fields.acctgTagUsageTypeId,
									"INGRESO")).get(0);
					// Se obtiene el tipo de clasficacion
					List<Clasificacion> listaClasif = new ArrayList<Clasificacion>();
					if (estructura.getClasificacion1() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif1 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion1()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif1);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion1()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion2() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif2 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion2()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif2);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion2()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion3() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif3 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion3()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif3);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion3()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion4() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif4 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion4()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif4);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion4()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion5() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif5 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion5()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif5);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion5()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion6() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif6 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion6()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif6);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion6()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion7() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif7 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion7()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif7);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion7()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion8() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif8 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion8()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif8);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion8()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion9() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif9 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion9()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif9);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion9()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion10() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif10 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion10()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif10);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion10()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion11() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif11 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion11()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif11);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion11()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion12() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif12 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion12()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif12);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion12()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion13() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif13 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion13()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif13);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion13()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion14() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif14 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion14()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif14);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion14()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}
					if (estructura.getClasificacion15() != null) {
						Clasificacion c = new Clasificacion();
						String tipoClasif15 = ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion15()))
								.get(0).getTablaRelacion();
						c.setTipoObjeto(tipoClasif15);
						c.setValor(rowdata.getClasificacion1());
						c.setTipoEnum(ledger_repo
								.findList(
										ClasifPresupuestal.class,
										ledger_repo
												.map(ClasifPresupuestal.Fields.clasificacionId,
														estructura
																.getClasificacion15()))
								.get(0).getClasificacionId());
						listaClasif.add(c);
					}					

					// Creacion de objetos
					Debug.log("Empieza creacion de objetos");

					// Party ur = UtilImport.obtenParty(ledger_repo,
					// rowdata.getUr());
					// Party uo = UtilImport.obtenParty(ledger_repo,
					// rowdata.getUo());
					// Party ue = UtilImport.obtenParty(ledger_repo,
					// rowdata.getUe());
					// ProductCategory rub = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getRub(), "RU");
					// ProductCategory tip = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getTip(), "TI");
					// ProductCategory cla = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getCla(), "CL");
					// ProductCategory con = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getCon(), "CO");
					// ProductCategory n5 = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getN5(), "N5");
					// Enumeration f = UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getF(), "CLAS_FR");
					// Enumeration sf = UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getSf(), "CLAS_FR");
					// Enumeration sfe =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getSfe(), "CLAS_FR");
					// Geo ef = UtilImport.obtenGeo(ledger_repo,
					// rowdata.getEf());
					// Geo reg = UtilImport.obtenGeo(ledger_repo,
					// rowdata.getReg());
					// Geo mun = UtilImport.obtenGeo(ledger_repo,
					// rowdata.getMun());
					// Geo loc = UtilImport.obtenGeo(ledger_repo,
					// rowdata.getLoc());

					// clasificaciones
					// Party ue = UtilImport.obtenParty(ledger_repo,
					// rowdata.getUe());
					// ProductCategory n5 = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getN5(), "NIVEL_5_ING");
					// Enumeration sfe =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getSfe(), "CL_FUENTE_RECURSOS");
					// Geo loc = UtilImport
					// .obtenGeo(ledger_repo, rowdata.getLoc());

					// import Presupuestos Ingreso as many as possible
					try {
						// id maximo
						Debug.log("Busqueda idMax");
						String id = ledger_repo.getNextSeqId("AcctgTrans");
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.DAY_OF_MONTH, 1);
//						 String anio = "20" + rowdata.getCiclo();
						 cal.set(Calendar.YEAR, Integer.parseInt(anio));

						for (int mes = 1; mes < 13; mes++) {
							imp_tx1 = null;
							imp_tx2 = null;
							imp_tx3 = null;
							imp_tx4 = null;
							AcctgTrans presupuestoIngreso = new AcctgTrans();
							presupuestoIngreso.setDescription(contenedor
									.getClavePresupuestal());

							// id Transaccion
							List<AcctgTrans> trans = ledger_repo.findList(
									AcctgTrans.class, ledger_repo
											.map(AcctgTrans.Fields.description,
													presupuestoIngreso
															.getDescription()));

							if (trans.isEmpty()) {
								Debug.log("Trans Nueva");
								if (mes < 10) {
									presupuestoIngreso.setAcctgTransId(id
											+ " I" + rowdata.getCiclo() + "-0"
											+ mes);
								} else {
									presupuestoIngreso.setAcctgTransId(id
											+ " I" + rowdata.getCiclo() + "-"
											+ mes);
								}
								presupuestoIngreso
										.setCreatedByUserLogin(rowdata
												.getUsuario());
							} else {
								Debug.log("Trans Modif");
								String message = "La transaccion ya existe";
								storeImportPresupuestoIngresoError(rowdata,
										message, imp_repo);
								continue;
							}

							cal.set(Calendar.MONTH, mes - 1);

							// Bloque de Validacion de Clasificaciones
							contenedor = UtilImport.validaClasificaciones(
									listaClasif, ledger_repo, "I",
									cal.getTime());
							if (!contenedor.getMensaje().isEmpty()
									|| !mensaje.isEmpty()) {
								loteValido = false;

								storeImportPresupuestoIngresoError(rowdata,
										contenedor.getMensaje(), imp_repo);
								continue;
							}

							if (!mensaje.isEmpty()) {
								loteValido = false;
								String message = "Failed to import Presupuesto Egreso ["
										// clasificaciones
										// + rowdata.getClavePres()
										+ "], Error message : " + mensaje;
								storeImportPresupuestoIngresoError(rowdata,
										message, imp_repo);
								continue;
							}
							presupuestoIngreso
									.setTransactionDate(new Timestamp(cal
											.getTimeInMillis()));
							presupuestoIngreso.setIsPosted("Y");
							presupuestoIngreso.setPostedDate(new Timestamp(cal
									.getTimeInMillis()));
							presupuestoIngreso
									.setAcctgTransTypeId("TINGRESOESTIMADO");
							presupuestoIngreso
									.setLastModifiedByUserLogin(rowdata
											.getUsuario());
							
							presupuestoIngreso = UtilImport
									.setPartyWorkEffortEnAcctTrans(
											presupuestoIngreso, contenedor);
							
							switch (mes) {
							case 1:
								presupuestoIngreso.setPostedAmount(rowdata
										.getEnero());
								break;
							case 2:
								presupuestoIngreso.setPostedAmount(rowdata
										.getFebrero());
								break;
							case 3:
								presupuestoIngreso.setPostedAmount(rowdata
										.getMarzo());
								break;
							case 4:
								presupuestoIngreso.setPostedAmount(rowdata
										.getAbril());
								break;
							case 5:
								presupuestoIngreso.setPostedAmount(rowdata
										.getMayo());
								break;
							case 6:
								presupuestoIngreso.setPostedAmount(rowdata
										.getJunio());
								break;
							case 7:
								presupuestoIngreso.setPostedAmount(rowdata
										.getJulio());
								break;
							case 8:
								presupuestoIngreso.setPostedAmount(rowdata
										.getAgosto());
								break;
							case 9:
								presupuestoIngreso.setPostedAmount(rowdata
										.getSeptiembre());
								break;
							case 10:
								presupuestoIngreso.setPostedAmount(rowdata
										.getOctubre());
								break;
							case 11:
								presupuestoIngreso.setPostedAmount(rowdata
										.getNoviembre());
								break;
							case 12:
								presupuestoIngreso.setPostedAmount(rowdata
										.getDiciembre());
								break;
							}

							mensaje = UtilImport.validaMonto(
									presupuestoIngreso.getPostedAmount(),
									mensaje);

							if (!mensaje.isEmpty()) {
								loteValido = false;

								storeImportPresupuestoIngresoError(rowdata,
										mensaje, imp_repo);
								continue;
							}

							Debug.log("Obtencion Dinamico FiscalType");
							MiniGuiaContable miniguia = ledger_repo
									.findOne(
											MiniGuiaContable.class,
											ledger_repo
													.map(MiniGuiaContable.Fields.acctgTransTypeId,
															presupuestoIngreso
																	.getAcctgTransTypeId()));

							if (miniguia == null) {
								loteValido = false;
								String message = "Tipo de transaccion no registrada en MiniGuia";
								storeImportPresupuestoIngresoError(rowdata,
										message, imp_repo);
								continue;
							}

							presupuestoIngreso.setGlFiscalTypeId(miniguia
									.getGlFiscalTypeIdPres());
							imp_tx1 = this.session.beginTransaction();
							ledger_repo.createOrUpdate(presupuestoIngreso);
							imp_tx1.commit();

							// Obtenemos los padres de cada nivel.
							// clasificaciones
							// String uo =
							// UtilImport.obtenPadreParty(ledger_repo,
							// ue.getPartyId());
							// String ur =
							// UtilImport.obtenPadreParty(ledger_repo,
							// uo);
							// String con =
							// UtilImport.obtenPadreProductCategory(
							// ledger_repo, n5.getProductCategoryId());
							// String cla =
							// UtilImport.obtenPadreProductCategory(
							// ledger_repo, con);
							// String tip =
							// UtilImport.obtenPadreProductCategory(
							// ledger_repo, cla);
							// String rub =
							// UtilImport.obtenPadreProductCategory(
							// ledger_repo, tip);
							// String sf = UtilImport.obtenPadreEnumeration(
							// ledger_repo, sfe.getEnumId());
							// String f = UtilImport.obtenPadreEnumeration(
							// ledger_repo, sf);
							// String mun =
							// UtilImport.obtenPadreGeo(ledger_repo,
							// loc.getGeoId());
							// String reg =
							// UtilImport.obtenPadreGeo(ledger_repo,
							// mun);
							// String ef = UtilImport.obtenPadreGeo(ledger_repo,
							// reg);

							// ACCTG_TRANS_PRESUPUESTAL
							AcctgTransPresupuestal aux = new AcctgTransPresupuestal();
							aux.setAcctgTransId(presupuestoIngreso
									.getAcctgTransId());
							aux.setClasificacion1(rowdata.getClasificacion1());
							aux.setClasificacion2(rowdata.getClasificacion2());
							aux.setClasificacion3(rowdata.getClasificacion3());
							aux.setClasificacion4(rowdata.getClasificacion4());
							aux.setClasificacion5(rowdata.getClasificacion5());
							aux.setClasificacion6(rowdata.getClasificacion5());
							aux.setClasificacion7(rowdata.getClasificacion6());
							aux.setClasificacion8(rowdata.getClasificacion7());
							aux.setClasificacion9(rowdata.getClasificacion8());
							aux.setClasificacion10(rowdata.getClasificacion9());
							aux.setClasificacion11(rowdata.getClasificacion10());
							aux.setClasificacion12(rowdata.getClasificacion11());
							aux.setClasificacion13(rowdata.getClasificacion12());
							aux.setClasificacion14(rowdata.getClasificacion13());
							aux.setClasificacion15(rowdata.getClasificacion14());
							aux.setClavePres(contenedor.getClavePresupuestal());
							aux.setAgrupador(rowdata.getAgrupador());
							aux.setLote(lote);
							imp_tx2 = this.session.beginTransaction();
							ledger_repo.createOrUpdate(aux);
							imp_tx2.commit();

							// History
							Debug.log("Busca periodos");
							List<CustomTimePeriod> periodos = UtilImport
									.obtenPeriodos(ledger_repo,
											organizationPartyId,
											presupuestoIngreso.getPostedDate());

							// C/D
							Debug.log("Obtencion de Cuentas Dinamico");
							String seqId = "00001", flag = "D", cuenta = miniguia
									.getCuentaCargo(), naturaleza = "D", tipo = "Debit";

							for (int j = 0; j < 2; j++) {
								if (j != 0) {
									seqId = "00002";
									flag = "C";
									cuenta = miniguia.getCuentaAbono();
									naturaleza = "A";
									tipo = "Credit";
								}
								AcctgTransEntry acctgentry = UtilImport
										.generaAcctgTransEntry(
												presupuestoIngreso,
												organizationPartyId, seqId,
												flag, cuenta, null);
								for (int i = 0; i < contenedor.getEnumeration()
										.size(); i++) {
									String indice = new Integer(i + 1)
											.toString();
									String campo = "acctgTagEnumId" + indice;
									acctgentry.set(campo, contenedor
											.getEnumeration().get(i)
											.getEnumId());
								}
								imp_tx3 = this.session.beginTransaction();
								ledger_repo.createOrUpdate(acctgentry);
								imp_tx3.commit();

								// GlAccountOrganization
								Debug.log("Empieza GlAccountOrganization");
								GlAccountOrganization glAccountOrganization = UtilImport
										.actualizaGlAccountOrganization(
												ledger_repo, presupuestoIngreso
														.getPostedAmount(),
												cuenta, organizationPartyId,
												naturaleza);
								imp_tx4 = this.session.beginTransaction();
								ledger_repo
										.createOrUpdate(glAccountOrganization);
								imp_tx4.commit();

								// GlAccountHistory
								Debug.log("Busca histories");
								List<GlAccountHistory> histories = UtilImport
										.actualizaGlAccountHistories(
												ledger_repo, periodos, cuenta,
												presupuestoIngreso
														.getPostedAmount(),
												tipo);
								Debug.log("Se impactan las histories regresadas");
								for (GlAccountHistory history : histories) {
									Transaction txHistory = null;
									txHistory = this.session.beginTransaction();
									ledger_repo.createOrUpdate(history);
									txHistory.commit();
								}
							}
						}

						if (mensaje.isEmpty()) {
							String message = "Se importo correctamente Presupuesto Egreso ["
									+ "].";
							// clasificaciones
							// + rowdata.getClavePres() + "].";
							this.storeImportPresupuestoIngresoSuccess(rowdata,
									imp_repo);
							Debug.logInfo(message, MODULE);
							imported = imported + 1;
						}
					} catch (Exception ex) {
						String message = ex.getMessage();
						storeImportPresupuestoIngresoError(rowdata, message,
								imp_repo);

						// rollback all if there was an error when importing
						// item
						if (imp_tx1 != null) {
							imp_tx1.rollback();
						}
						if (imp_tx2 != null) {
							imp_tx2.rollback();
						}
						if (imp_tx3 != null) {
							imp_tx3.rollback();
						}
						if (imp_tx4 != null) {
							imp_tx4.rollback();
						}

						Debug.logError(ex, message, MODULE);
						throw new ServiceException(ex.getMessage());
					}
				}
				// Se inserta el Lote.
				if (!lote.equalsIgnoreCase("X") && loteValido) {
					LoteTransaccion loteTrans = new LoteTransaccion();
					loteTrans.setIdLote(lote);
					loteTrans.setTipoTransaccion("PresupuestoIngreso");
					Transaction transLote = null;
					transLote = this.session.beginTransaction();
					ledger_repo.createOrUpdate(loteTrans);
					transLote.commit();
				}
			}
			this.importedRecords = imported;

		} catch (InfrastructureException ex) {
			Debug.logError(ex, MODULE);
			throw new ServiceException(ex.getMessage());
		} catch (RepositoryException ex) {
			Debug.logError(ex, MODULE);
			throw new ServiceException(ex.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	/**
	 * Helper method to store Presupuesto Ingreso import success into
	 * <code>DataImportPresupuestoIngreso</code> entity row.
	 * 
	 * @param rowdata
	 *            item of <code>DataImportPresupuestoIngreso</code> entity that
	 *            was successfully imported
	 * @param imp_repo
	 *            repository of accounting
	 * @throws org.opentaps.foundation.repository.RepositoryException
	 */
	private void storeImportPresupuestoIngresoSuccess(
			DataImportPresupuestoIngreso rowdata,
			PresupuestoIngresoDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// mark as success
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_IMPORTED);
		rowdata.setImportError(null);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

	/**
	 * Helper method to store Presupuesto Ingreso import error into
	 * <code>DataImportPresupuestoIngreso</code> entity row.
	 * 
	 * @param rowdata
	 *            item of <code>DataImportPresupuestoIngreso</code> entity that
	 *            was unsuccessfully imported
	 * @param message
	 *            error message
	 * @param imp_repo
	 *            repository of accounting
	 * @throws org.opentaps.foundation.repository.RepositoryException
	 */
	private void storeImportPresupuestoIngresoError(
			DataImportPresupuestoIngreso rowdata, String message,
			PresupuestoIngresoDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// store the exception and mark as failed
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_FAILED);
		Debug.log("message: " + message);
		rowdata.setImportError(message);
		Debug.log("ImportError:" + rowdata.getImportError());
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}
}
