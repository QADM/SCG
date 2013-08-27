package org.opentaps.dataimport.domain;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.opentaps.base.constants.StatusItemConstants;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.DataImportPresupuestoEgreso;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.Geo;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.LoteTransaccion;
import org.opentaps.base.entities.MiniGuiaContable;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.ProductCategory;
import org.opentaps.base.entities.WorkEffort;
import org.opentaps.dataimport.UtilImport;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.PresupuestoEgresoDataImportRepositoryInterface;
import org.opentaps.domain.dataimport.PresupuestoEgresoImportServiceInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

public class PresupuestoEgresoImportService extends DomainService implements
		PresupuestoEgresoImportServiceInterface {
	private static final String MODULE = PresupuestoEgresoImportService.class
			.getName();
	// session object, using to store/search pojos.
	private Session session;
	private String organizationPartyId;
	public int importedRecords;
	private String lote;

	public PresupuestoEgresoImportService() {
		super();
	}

	public PresupuestoEgresoImportService(Infrastructure infrastructure,
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
	public void importPresupuestoEgreso() throws ServiceException {
		try {
			this.session = this.getInfrastructure().getSession();

			PresupuestoEgresoDataImportRepositoryInterface imp_repo = this
					.getDomainsDirectory().getDataImportDomain()
					.getPresupuestoEgresoDataImportRepository();

			LedgerRepositoryInterface ledger_repo = this.getDomainsDirectory()
					.getLedgerDomain().getLedgerRepository();

			List<DataImportPresupuestoEgreso> dataforimp = imp_repo
					.findNotProcessesDataImportPresupuestoEgresoEntries();

			int imported = 0;
			Transaction imp_tx1 = null;
			Transaction imp_tx2 = null;
			Transaction imp_tx3 = null;
			Transaction imp_tx4 = null;

			if (UtilImport.validaLote(ledger_repo, lote, "PresupuestoEgreso")) {
				for (DataImportPresupuestoEgreso rowdata : dataforimp) {
					// Validaciones
					String mensaje = "";
					Debug.log("Empieza bloque de validaciones");
					// mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					// rowdata.getUr(), "UR");
					// mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					// rowdata.getUo(), "UO");
					// mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					// rowdata.getUe(), "UE");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getFin(), "CLAS_FUN", "FIN");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getFun(), "CLAS_FUN", "FUN");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getSubf(), "CLAS_FUN", "SUBF");
					// mensaje = UtilImport.validaWorkEffort(mensaje,
					// ledger_repo,
					// rowdata.getEje(), "EJE");
					// mensaje = UtilImport.validaWorkEffort(mensaje,
					// ledger_repo,
					// rowdata.getPp(), "PP");
					// mensaje = UtilImport.validaWorkEffort(mensaje,
					// ledger_repo,
					// rowdata.getSpp(), "SPP");
					// mensaje = UtilImport.validaWorkEffort(mensaje,
					// ledger_repo,
					// rowdata.getAct(), "ACT");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getTg(), "TIPO_GASTO", "tg");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getCap(), "CA", "CAP");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getCon(), "CON", "CON");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getPg(), "PG", "PG");
					// mensaje = UtilImport.validaProductCategory(mensaje,
					// ledger_repo, rowdata.getPe(), "PE", "PE");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getEf(), "EF");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getReg(), "REG");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getMun(), "MUN");
					// mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					// rowdata.getLoc(), "LOC");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getF(), "CLAS_FR", "F");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getSf(), "CLAS_FR", "SF");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getSfe(), "CLAS_FR", "SFE");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getSec(), "CLAS_SECT", "SEC");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getSubsec(), "CLAS_SECT", "SUBSEC");
					// mensaje = UtilImport.validaEnumeration(mensaje,
					// ledger_repo,
					// rowdata.getArea(), "CLAS_SECT", "AREA");

					mensaje = UtilImport.validaParty(mensaje, ledger_repo,
							rowdata.getUe(), "ADMINISTRATIVA");
					mensaje = UtilImport.validaEnumeration(mensaje,
							ledger_repo, rowdata.getSubf(), "CL_FUNCIONAL",
							"FUNCIONAL");
					mensaje = UtilImport.validaWorkEffort(mensaje, ledger_repo,
							rowdata.getAct(), "ACTIVIDAD");
					mensaje = UtilImport.validaEnumeration(mensaje,
							ledger_repo, rowdata.getTg(), "TIPO_GASTO",
							"TIPO GASTO");
					mensaje = UtilImport.validaProductCategory(mensaje,
							ledger_repo, rowdata.getPe(), "PARTIDA ESPECIFICA",
							"PRODUCTO ESPECIFICO");
					mensaje = UtilImport.validaEnumeration(mensaje,
							ledger_repo, rowdata.getSfe(),
							"CL_FUENTE_RECURSOS", "FUENTE DE LOS RECURSOS");
					mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
							rowdata.getLoc(), "GEOGRAFICA");
					mensaje = UtilImport.validaEnumeration(mensaje,
							ledger_repo, rowdata.getArea(), "CL_SECTORIAL",
							"SECTORIAL");

					if (!mensaje.isEmpty()) {
						String message = "Failed to import Presupuesto Egreso ["
								+ rowdata.getClavePres()
								+ "], Error message : " + mensaje;
						storeImportPresupuestoEgresoError(rowdata, message,
								imp_repo);
						continue;
					}

					// Creacion de objetos
					Debug.log("Empieza creacion de objetos");
					// Party ur = UtilImport.obtenParty(ledger_repo,
					// rowdata.getUr());
					// Party uo = UtilImport.obtenParty(ledger_repo,
					// rowdata.getUo());
					// Party ue = UtilImport.obtenParty(ledger_repo,
					// rowdata.getUe());
					// Enumeration fin =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getFin(), "CLAS_FUN");
					// Enumeration fun =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getFun(), "CLAS_FUN");
					// Enumeration subf =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getSubf(), "CLAS_FUN");
					// WorkEffort eje = UtilImport.obtenWorkEffort(ledger_repo,
					// rowdata.getEje());
					// WorkEffort pp = UtilImport.obtenWorkEffort(ledger_repo,
					// rowdata.getPp());
					// WorkEffort spp = UtilImport.obtenWorkEffort(ledger_repo,
					// rowdata.getSpp());
					// WorkEffort act = UtilImport.obtenWorkEffort(ledger_repo,
					// rowdata.getAct());
					// Enumeration tg = UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getFin(), "TIPO_GASTO");
					// ProductCategory cap = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getCap(), "CA");
					// ProductCategory con = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getCon(), "CON");
					// ProductCategory pg = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getPg(), "PG");
					// ProductCategory pe = UtilImport.obtenProductCategory(
					// ledger_repo, rowdata.getPe(), "PE");
					// Geo ef = UtilImport.obtenGeo(ledger_repo,
					// rowdata.getEf());
					// Geo reg = UtilImport.obtenGeo(ledger_repo,
					// rowdata.getReg());
					// Geo mun = UtilImport.obtenGeo(ledger_repo,
					// rowdata.getMun());
					// Geo loc = UtilImport.obtenGeo(ledger_repo,
					// rowdata.getLoc());
					// Enumeration f = UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getF(), "CLAS_FR");
					// Enumeration sf = UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getSf(), "CLAS_FR");
					// Enumeration sfe =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getSfe(), "CLAS_FR");
					// Enumeration sec =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getSec(), "CLAS_SECT");
					// Enumeration subsec =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getSubsec(), "CLAS_SECT");
					// Enumeration area =
					// UtilImport.obtenEnumeration(ledger_repo,
					// rowdata.getArea(), "CLAS_SECT");

					Party ue = UtilImport.obtenParty(ledger_repo,
							rowdata.getUe());
					Enumeration subf = UtilImport.obtenEnumeration(ledger_repo,
							rowdata.getSubf(), "CL_FUNCIONAL");
					WorkEffort act = UtilImport.obtenWorkEffort(ledger_repo,
							rowdata.getAct());
					Enumeration tg = UtilImport.obtenEnumeration(ledger_repo,
							rowdata.getTg(), "TIPO_GASTO");
					ProductCategory pe = UtilImport.obtenProductCategory(
							ledger_repo, rowdata.getPe(), "PARTIDA ESPECIFICA");
					Geo loc = UtilImport
							.obtenGeo(ledger_repo, rowdata.getLoc());
					Enumeration sfe = UtilImport.obtenEnumeration(ledger_repo,
							rowdata.getSfe(), "CL_FUENTE_RECURSOS");
					Enumeration area = UtilImport.obtenEnumeration(ledger_repo,
							rowdata.getArea(), "CL_SECTORIAL");

					// import Presupuestos Egreso as many as possible
					try {
						// id maximo
						Debug.log("Busqueda idMax");
						String id = ledger_repo.getNextSeqId("AcctgTrans");
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.DAY_OF_MONTH, 1);
						String anio = "20" + rowdata.getCiclo();
						cal.set(Calendar.YEAR, Integer.parseInt(anio));

						for (int mes = 1; mes < 13; mes++) {
							imp_tx1 = null;
							imp_tx2 = null;
							imp_tx3 = null;
							imp_tx4 = null;
							AcctgTrans presupuestoEgreso = new AcctgTrans();
							presupuestoEgreso.setDescription(rowdata
									.getClavePres() + "-" + mes);

							// id Transaccion
							List<AcctgTrans> trans = ledger_repo
									.findList(AcctgTrans.class, ledger_repo
											.map(AcctgTrans.Fields.description,
													presupuestoEgreso
															.getDescription()));

							if (trans.isEmpty()) {
								Debug.log("Trans Nueva");
								if (mes < 10) {
									presupuestoEgreso.setAcctgTransId(id + " E"
											+ rowdata.getCiclo() + "-0" + mes);
								} else {
									presupuestoEgreso.setAcctgTransId(id + " E"
											+ rowdata.getCiclo() + "-" + mes);
								}

								presupuestoEgreso.setCreatedByUserLogin(rowdata
										.getUsuario());
							} else {
								String message = "Failed to import Presupuesto Egreso ["
										+ rowdata.getClavePres()
										+ "], Error message : "
										+ "La transaccion ya existe";
								storeImportPresupuestoEgresoError(rowdata,
										message, imp_repo);
								continue;
							}

							cal.set(Calendar.MONTH, mes - 1);

							// Vigencias
							mensaje = UtilImport.validaVigencia(mensaje,
									"FUNCIONAL", subf, cal.getTime());
							mensaje = UtilImport.validaVigencia(mensaje,
									"TIPO GASTO", tg, cal.getTime());
							mensaje = UtilImport.validaVigencia(mensaje,
									"FUENTE DE LOS RECURSOS", sfe,
									cal.getTime());
							mensaje = UtilImport.validaVigencia(mensaje,
									"SECTORIAL", area, cal.getTime());

							if (!mensaje.isEmpty()) {
								String message = "Failed to import Presupuesto Egreso ["
										+ rowdata.getClavePres()
										+ "], Error message : " + mensaje;
								storeImportPresupuestoEgresoError(rowdata,
										message, imp_repo);
								continue;
							}

							presupuestoEgreso.setTransactionDate(new Timestamp(
									cal.getTimeInMillis()));
							presupuestoEgreso.setIsPosted("Y");
							presupuestoEgreso.setPostedDate(new Timestamp(cal
									.getTimeInMillis()));
							presupuestoEgreso
									.setAcctgTransTypeId("TPRESUPAPROBADO");
							presupuestoEgreso
									.setLastModifiedByUserLogin(rowdata
											.getUsuario());
							presupuestoEgreso.setWorkEffortId(act
									.getWorkEffortId());
							presupuestoEgreso.setPartyId(ue.getPartyId());
							switch (mes) {
							case 1:
								presupuestoEgreso.setPostedAmount(rowdata
										.getEnero());
								break;
							case 2:
								presupuestoEgreso.setPostedAmount(rowdata
										.getFebrero());
								break;
							case 3:
								presupuestoEgreso.setPostedAmount(rowdata
										.getMarzo());
								break;
							case 4:
								presupuestoEgreso.setPostedAmount(rowdata
										.getAbril());
								break;
							case 5:
								presupuestoEgreso.setPostedAmount(rowdata
										.getMayo());
								break;
							case 6:
								presupuestoEgreso.setPostedAmount(rowdata
										.getJunio());
								break;
							case 7:
								presupuestoEgreso.setPostedAmount(rowdata
										.getJulio());
								break;
							case 8:
								presupuestoEgreso.setPostedAmount(rowdata
										.getAgosto());
								break;
							case 9:
								presupuestoEgreso.setPostedAmount(rowdata
										.getSeptiembre());
								break;
							case 10:
								presupuestoEgreso.setPostedAmount(rowdata
										.getOctubre());
								break;
							case 11:
								presupuestoEgreso.setPostedAmount(rowdata
										.getNoviembre());
								break;
							case 12:
								presupuestoEgreso.setPostedAmount(rowdata
										.getDiciembre());
								break;
							}

							mensaje = UtilImport.validaMonto(
									presupuestoEgreso.getPostedAmount(),
									mensaje);

							if (!mensaje.isEmpty()) {
								String message = "Failed to import Presupuesto Egreso ["
										+ rowdata.getClavePres()
										+ "], Error message : " + mensaje;
								storeImportPresupuestoEgresoError(rowdata,
										message, imp_repo);
								continue;
							}

							Debug.log("Obtencion Dinamico FiscalType");
							MiniGuiaContable miniguia = ledger_repo
									.findOne(
											MiniGuiaContable.class,
											ledger_repo
													.map(MiniGuiaContable.Fields.acctgTransTypeId,
															presupuestoEgreso
																	.getAcctgTransTypeId()));

							if (miniguia == null) {
								String message = "Failed to import Presupuesto Egreso ["
										+ rowdata.getClavePres()
										+ "], Error message : "
										+ "Tipo de transaccion no registrada en MiniGuia";
								storeImportPresupuestoEgresoError(rowdata,
										message, imp_repo);
								continue;
							}

							presupuestoEgreso.setGlFiscalTypeId(miniguia
									.getGlFiscalTypeIdPres());
							imp_tx1 = this.session.beginTransaction();
							ledger_repo.createOrUpdate(presupuestoEgreso);
							imp_tx1.commit();

							// Obtenemos los padres de cada nivel.
							String uo = UtilImport.obtenPadreParty(ledger_repo,
									ue.getPartyId());
							String ur = UtilImport.obtenPadreParty(ledger_repo,
									uo);
							String fun = UtilImport.obtenPadreEnumeration(
									ledger_repo, subf.getEnumId());
							String fin = UtilImport.obtenPadreEnumeration(
									ledger_repo, fun);
							String spp = UtilImport.obtenPadreWorkEffort(
									ledger_repo, act.getWorkEffortId());
							String pp = UtilImport.obtenPadreWorkEffort(
									ledger_repo, spp);
							String eje = UtilImport.obtenPadreWorkEffort(
									ledger_repo, pp);
							String pg = UtilImport.obtenPadreProductCategory(
									ledger_repo, pe.getProductCategoryId());
							String con = UtilImport.obtenPadreProductCategory(
									ledger_repo, pg);
							String cap = UtilImport.obtenPadreProductCategory(
									ledger_repo, con);
							String sf = UtilImport.obtenPadreEnumeration(
									ledger_repo, sfe.getEnumId());
							String f = UtilImport.obtenPadreEnumeration(
									ledger_repo, sf);
							String mun = UtilImport.obtenPadreGeo(ledger_repo,
									loc.getGeoId());
							String reg = UtilImport.obtenPadreGeo(ledger_repo,
									mun);
							String ef = UtilImport.obtenPadreGeo(ledger_repo,
									reg);
							String subsec = UtilImport.obtenPadreEnumeration(
									ledger_repo, area.getEnumId());
							String sec = UtilImport.obtenPadreEnumeration(
									ledger_repo, subsec);

							// ACCTG_TRANS_PRESUPUESTAL
							Debug.log("ACCTG_TRANS_PRESUPUESTAL");
							AcctgTransPresupuestal aux = new AcctgTransPresupuestal();
							aux.setAcctgTransId(presupuestoEgreso
									.getAcctgTransId());
							aux.setCiclo(rowdata.getCiclo());
							aux.setUnidadResponsable(ur);
							aux.setUnidadOrganizacional(uo);
							aux.setUnidadEjecutora(ue.getPartyId());
							aux.setFinalidad(fin);
							aux.setFuncion(fun);
							aux.setSubFuncion(subf.getEnumId());
							aux.setProgramaPlan(eje);
							aux.setProgramaPresupuestario(pp);
							aux.setSubProgramaPresupuestario(spp);
							aux.setActividad(act.getWorkEffortId());
							aux.setTipoGasto(tg.getEnumId());
							aux.setCapitulo(cap);
							aux.setConcepto(con);
							aux.setPartidaGenerica(pg);
							aux.setPartidaEspecifica(pe.getProductCategoryId());
							aux.setFuente(f);
							aux.setSubFuente(sf);
							aux.setSubFuenteEspecifica(sfe.getEnumId());
							aux.setEntidadFederativa(ef);
							aux.setRegion(reg);
							aux.setMunicipio(mun);
							aux.setLocalidad(loc.getGeoId());
							aux.setSector(sec);
							aux.setSubSector(subsec);
							aux.setArea(area.getEnumId());
							aux.setAgrupador(rowdata.getAgrupador());
							aux.setClavePres(rowdata.getClavePres());
							aux.setLote(lote);
							imp_tx2 = this.session.beginTransaction();
							ledger_repo.createOrUpdate(aux);
							imp_tx2.commit();

							// C/D
							Debug.log("Obtencion de Cuentas Dinamico");
							String seqId = "00001", flag = "D", cuenta = miniguia
									.getCuentaCargo(), naturaleza = "D";

							for (int j = 0; j < 2; j++) {
								if (j != 0) {
									seqId = "00002";
									flag = "C";
									cuenta = miniguia.getCuentaAbono();
									naturaleza = "A";
								}
								AcctgTransEntry acctgentry = UtilImport
										.generaAcctgTransEntry(
												presupuestoEgreso,
												organizationPartyId, seqId,
												flag, cuenta, sfe.getEnumId());
								// Tags seteados.
								acctgentry.setAcctgTagEnumId1(subf.getEnumId());
								acctgentry.setAcctgTagEnumId2(tg.getEnumId());
								acctgentry.setAcctgTagEnumId3(sfe.getEnumId());
								acctgentry.setAcctgTagEnumId4(area.getEnumId());
								imp_tx3 = this.session.beginTransaction();
								ledger_repo.createOrUpdate(acctgentry);
								imp_tx3.commit();

								// GlAccountOrganization
								Debug.log("Empieza GlAccountOrganization");
								GlAccountOrganization glAccountOrganization = UtilImport
										.actualizaGlAccountOrganization(
												ledger_repo, presupuestoEgreso
														.getPostedAmount(),
												cuenta, organizationPartyId,
												naturaleza);
								imp_tx4 = this.session.beginTransaction();
								ledger_repo
										.createOrUpdate(glAccountOrganization);
								imp_tx4.commit();
							}
						}

						if (mensaje.isEmpty()) {
							String message = "Successfully imported Presupuesto Egreso ["
									+ rowdata.getClavePres() + "].";
							this.storeImportPresupuestoEgresoSuccess(rowdata,
									imp_repo);
							Debug.logInfo(message, MODULE);
							imported = imported + 1;
						}
					} catch (Exception ex) {
						String message = "Failed to import Presupuesto Egreso ["
								+ rowdata.getClavePres()
								+ "], Error message : " + ex.getMessage();
						storeImportPresupuestoEgresoError(rowdata, message,
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
				if (!lote.equalsIgnoreCase("X")) {
					LoteTransaccion loteTrans = new LoteTransaccion();
					loteTrans.setIdLote(lote);
					loteTrans.setTipoTransaccion("PresupuestoEgreso");
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
	 * Helper method to store Presupuesto Egreso import success into
	 * <code>DataImportPresupuestoEgreso</code> entity row.
	 * 
	 * @param rowdata
	 *            item of <code>DataImportPresupuestoEgreso</code> entity that
	 *            was successfully imported
	 * @param imp_repo
	 *            repository of accounting
	 * @throws org.opentaps.foundation.repository.RepositoryException
	 */
	private void storeImportPresupuestoEgresoSuccess(
			DataImportPresupuestoEgreso rowdata,
			PresupuestoEgresoDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// mark as success
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_IMPORTED);
		rowdata.setImportError(null);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

	/**
	 * Helper method to store Presupuesto Egreso import error into
	 * <code>DataImportPresupuestoEgreso</code> entity row.
	 * 
	 * @param rowdata
	 *            item of <code>DataImportPresupuestoEgreso</code> entity that
	 *            was unsuccessfully imported
	 * @param message
	 *            error message
	 * @param imp_repo
	 *            repository of accounting
	 * @throws org.opentaps.foundation.repository.RepositoryException
	 */
	private void storeImportPresupuestoEgresoError(
			DataImportPresupuestoEgreso rowdata, String message,
			PresupuestoEgresoDataImportRepositoryInterface imp_repo)
			throws RepositoryException {
		// store the exception and mark as failed
		rowdata.setImportStatusId(StatusItemConstants.Dataimport.DATAIMP_FAILED);
		rowdata.setImportError(message);
		rowdata.setProcessedTimestamp(UtilDateTime.nowTimestamp());
		imp_repo.createOrUpdate(rowdata);
	}

}
