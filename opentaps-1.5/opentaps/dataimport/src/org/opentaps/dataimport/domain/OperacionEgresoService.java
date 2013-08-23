package org.opentaps.dataimport.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.base.entities.AcctgTrans;
import org.opentaps.base.entities.AcctgTransEntry;
import org.opentaps.base.entities.AcctgTransPresupuestal;
import org.opentaps.base.entities.Enumeration;
import org.opentaps.base.entities.Geo;
import org.opentaps.base.entities.GlAccountOrganization;
import org.opentaps.base.entities.Party;
import org.opentaps.base.entities.ProductCategory;
import org.opentaps.base.entities.TipoDocumento;
import org.opentaps.base.entities.WorkEffort;
import org.opentaps.dataimport.UtilImport;
import org.opentaps.dataimport.domain.MotorContable;
import org.opentaps.domain.DomainService;
import org.opentaps.domain.dataimport.EgresoDiarioDataImportRepositoryInterface;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.entity.hibernate.Session;
import org.opentaps.foundation.entity.hibernate.Transaction;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.service.ServiceException;

import com.ibm.icu.util.Calendar;

public class OperacionEgresoService extends DomainService{

	private Session session;
	
	public Map<String, Object> registraEgreso(Map<String, Object> context) throws ServiceException
	{
		Map<String,Object> output = null;
		String tipoDocumento= (String) context.get("tipoDocumento");
		Date fechaRegistro= (Date) context.get("fechaRegistro");
		Date fechaContable= (Date) context.get("fechaContable");
		BigDecimal monto= (BigDecimal) context.get("monto");
		String organizacionContable= (String) context.get("organizacionContable");
		String refDoc= (String) context.get("refDoc");
		String secuencia= (String) context.get("secuencia");
		String usuario= (String) context.get("usuario");
		String lote= (String) context.get("lote");
		String tipoCatalogo= (String) context.get("tipoCatalogo");
		String idPago= (String) context.get("idPago");
		String idProducto= (String) context.get("idProducto");
		String ciclo= (String) context.get("ciclo");
	    String ue_S= (String) context.get("ue");
	    String subf_S= (String) context.get("subf");
	    String act_S= (String) context.get("act");
	    String tg_S= (String) context.get("tg");
	    String pe_S= (String) context.get("pe");
        String sfe_S= (String) context.get("sfe");
        String loc_S= (String) context.get("loc");
        String area_S= (String) context.get("area");
        String concatenacion= (String) context.get("concatenacion");
        
        try
        {
        	this.session = this.getInfrastructure().getSession();
        	EgresoDiarioDataImportRepositoryInterface imp_repo = this
				.getDomainsDirectory().getDataImportDomain()
				.getEgresoDiarioDataImportRepository();
        	LedgerRepositoryInterface ledger_repo = this.getDomainsDirectory()
				.getLedgerDomain().getLedgerRepository();
        	
        	Transaction imp_tx1 = null;
			Transaction imp_tx2 = null;
			Transaction imp_tx3 = null;
			Transaction imp_tx4 = null;
			Transaction imp_tx5 = null;
			Transaction imp_tx6 = null;
			Transaction imp_tx7 = null;
			Transaction imp_tx8 = null;
			Transaction imp_tx9 = null;
			Transaction imp_tx10 = null;
			Transaction imp_tx11 = null;
			Transaction imp_tx12 = null;
			
			String mensaje = null;
			Debug.log("Empieza bloque de validaciones");
			mensaje = UtilImport.validaTipoDoc(mensaje, ledger_repo,
					tipoDocumento);
			mensaje = UtilImport.validaCiclo(mensaje, ciclo,
					fechaContable);

			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					ue_S, "ADMINISTRATIVA");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					subf_S, "CL_FUNCIONAL", "FUNCIONAL");
			mensaje = UtilImport.validaWorkEffort(mensaje, ledger_repo,
					act_S, "ACTIVIDAD");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					tg_S, "TIPO_GASTO", "TIPO GASTO");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, pe_S, "PARTIDA ESPECIFICA",
					"PRODUCTO ESPECIFICO");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					sfe_S, "CL_FUENTE_RECURSOS", "FUENTE DE LOS RECURSOS");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					loc_S, "GEOGRAFICA");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					area_S, "CL_SECTORIAL", "SECTORIAL");

			if (mensaje == null) {
				String message = "Failed to import Egreso Diario ["
						+ concatenacion + "], Error message : "
						+ mensaje;
				//storeImportEgresoDiarioError(rowdata, message, imp_repo);
				//continue;
			}

			// Creacion de objetos
			Debug.log("Empieza creacion de objetos");
			TipoDocumento tipoDoc = UtilImport.obtenTipoDocumento(
					ledger_repo, tipoDocumento);

			Party ue = UtilImport.obtenParty(ledger_repo, ue_S);
			Enumeration subf = UtilImport.obtenEnumeration(ledger_repo,
					subf_S, "CL_FUNCIONAL");
			WorkEffort act = UtilImport.obtenWorkEffort(ledger_repo,
					act_S);
			Enumeration tg = UtilImport.obtenEnumeration(ledger_repo,
					tg_S, "TIPO_GASTO");
			ProductCategory pe = UtilImport.obtenProductCategory(
					ledger_repo, pe_S, "PARTIDA ESPECIFICA");
			Geo loc = UtilImport.obtenGeo(ledger_repo, loc_S);
			Enumeration sfe = UtilImport.obtenEnumeration(ledger_repo,
					sfe_S, "CL_FUENTE_RECURSOS");
			Enumeration area = UtilImport.obtenEnumeration(ledger_repo,
					area_S, "CL_SECTORIAL");

			// Empieza bloque de vigencias
			Debug.log("Empieza bloque de vigencias");
			// Vigencias
			mensaje = UtilImport.validaVigencia(mensaje, "FUNCIONAL", subf,
					fechaContable);
			mensaje = UtilImport.validaVigencia(mensaje, "TIPO GASTO", tg,
					fechaContable);
			mensaje = UtilImport.validaVigencia(mensaje,
					"FUENTE DE LOS RECURSOS", sfe,
					fechaContable);
			mensaje = UtilImport.validaVigencia(mensaje, "SECTORIAL", area,
					fechaContable);

			if (!mensaje.isEmpty()) {
				String message = "Failed to import Egreso Diario ["
						+ concatenacion + "], Error message : "
						+ mensaje;
				//storeImportEgresoDiarioError(rowdata, message, imp_repo);
				//continue;
			}
			
			// Obtenemos los padres de cada nivel.
			String uo = UtilImport.obtenPadreParty(ledger_repo,
					ue.getPartyId());
			String ur = UtilImport.obtenPadreParty(ledger_repo, uo);
			String fun = UtilImport.obtenPadreEnumeration(ledger_repo,
					subf.getEnumId());
			String fin = UtilImport.obtenPadreEnumeration(ledger_repo, fun);
			String spp = UtilImport.obtenPadreWorkEffort(ledger_repo,
					act.getWorkEffortId());
			String pp = UtilImport.obtenPadreWorkEffort(ledger_repo, spp);
			String eje = UtilImport.obtenPadreWorkEffort(ledger_repo, pp);
			String pg = UtilImport.obtenPadreProductCategory(ledger_repo,
					pe.getProductCategoryId());
			String con = UtilImport.obtenPadreProductCategory(ledger_repo,
					pg);
			String cap = UtilImport.obtenPadreProductCategory(ledger_repo,
					con);
			String sf = UtilImport.obtenPadreEnumeration(ledger_repo,
					sfe.getEnumId());
			String f = UtilImport.obtenPadreEnumeration(ledger_repo, sf);
			String mun = UtilImport.obtenPadreGeo(ledger_repo,
					loc.getGeoId());
			String reg = UtilImport.obtenPadreGeo(ledger_repo, mun);
			String ef = UtilImport.obtenPadreGeo(ledger_repo, reg);
			String subsec = UtilImport.obtenPadreEnumeration(ledger_repo,
					area.getEnumId());
			String sec = UtilImport.obtenPadreEnumeration(ledger_repo,
					subsec);

			Debug.log("Motor Contable");
			MotorContable motor = new MotorContable(ledger_repo);
			Map<String, String> cuentas = motor.cuentasEgresoDiario(
					tipoDoc.getAcctgTransTypeId(), pg,
					organizacionContable, tg_S,
					idPago, idProducto,
					""); //rowdata.getIdProductoH()

			if (cuentas.get("Mensaje") != null) {
				String message = "Failed to import Egreso Diario ["
						+ concatenacion + "], Error message : "
						+ cuentas.get("Mensaje");
				//storeImportEgresoDiarioError(rowdata, message, imp_repo);
				//continue;
			}

			try {

				imp_tx1 = null;
				imp_tx2 = null;
				imp_tx3 = null;
				imp_tx4 = null;
				imp_tx5 = null;
				imp_tx6 = null;
				imp_tx7 = null;
				imp_tx8 = null;
				imp_tx9 = null;
				imp_tx10 = null;
				imp_tx11 = null;
				imp_tx12 = null;

				AcctgTrans egresoDiario = new AcctgTrans();

				Calendar cal = Calendar.getInstance();
				cal.setTime(fechaRegistro);
				egresoDiario.setTransactionDate(new Timestamp(cal
						.getTimeInMillis()));
				egresoDiario.setIsPosted("Y");
				cal.setTime(fechaContable);
				egresoDiario.setPostedDate(new Timestamp(cal
						.getTimeInMillis()));
				egresoDiario.setAcctgTransTypeId(tipoDoc
						.getAcctgTransTypeId());
				egresoDiario.setLastModifiedByUserLogin(usuario);
				egresoDiario.setPartyId(ue.getPartyId());
				egresoDiario.setPostedAmount(monto);
				egresoDiario.setWorkEffortId(act.getWorkEffortId());

				// ACCTG_TRANS_PRESUPUESTAL
				Debug.log("ACCTG_TRANS_PRESUPUESTAL");
				AcctgTransPresupuestal aux = new AcctgTransPresupuestal();
				aux.setCiclo(ciclo);
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
				aux.setAgrupador(refDoc);
				aux.setIdTipoDoc(tipoDocumento);
				aux.setSecuencia(secuencia);
				//aux.setLote(lote);
				aux.setClavePres(concatenacion);

				if (cuentas.get("Cuenta Cargo Presupuesto") != null) {
					Debug.log("Cuenta Presupuestal");

					egresoDiario.setDescription(tipoDoc.getDescripcion()
							+ "-" + refDoc + "-P");

					// id Transaccion
					egresoDiario.setAcctgTransId(tipoDoc.getDescripcion()
							+ "-" + refDoc + "-P");

					AcctgTrans trans = ledger_repo.findOne(
							AcctgTrans.class, ledger_repo.map(
									AcctgTrans.Fields.acctgTransId,
									egresoDiario.getAcctgTransId()));

					if (trans != null) {
						Debug.log("Trans Modif");
						String message = "La transaccion con id: "
								+ egresoDiario.getAcctgTransId()
								+ "ya existe.";
						Debug.log(message);
						//storeImportEgresoDiarioError(rowdata, message,
							//	imp_repo);
						//continue;
					}

					Debug.log("Trans Nueva");
					egresoDiario
							.setCreatedByUserLogin(usuario);

					egresoDiario.setGlFiscalTypeId(cuentas
							.get("GlFiscalTypePresupuesto"));
					imp_tx1 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(egresoDiario);
					imp_tx1.commit();

					Debug.log("commit Aux");
					aux.setAcctgTransId(egresoDiario.getAcctgTransId());
					imp_tx3 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(aux);
					imp_tx3.commit();

					Debug.log("commit CCP");
					AcctgTransEntry acctgentry = UtilImport
							.generaAcctgTransEntry(
									egresoDiario,
									organizacionContable,
									"00001",
									"D",
									cuentas.get("Cuenta Cargo Presupuesto"),
									sfe.getEnumId());
					// Tags seteados.
					acctgentry.setAcctgTagEnumId1(subf.getEnumId());
					acctgentry.setAcctgTagEnumId2(tg.getEnumId());
					acctgentry.setAcctgTagEnumId3(sfe.getEnumId());
					acctgentry.setAcctgTagEnumId4(area.getEnumId());
					imp_tx5 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx5.commit();

					Debug.log("commit GlAO");
					GlAccountOrganization glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(
									ledger_repo,
									monto,
									cuentas.get("Cuenta Cargo Presupuesto"),
									organizacionContable,"D");
					imp_tx7 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx7.commit();

					Debug.log("commit CAP");
					acctgentry = UtilImport.generaAcctgTransEntry(
							egresoDiario, organizacionContable,
							"00002", "C",
							cuentas.get("Cuenta Abono Presupuesto"),
							sfe.getEnumId());
					// Tags seteados.
					acctgentry.setAcctgTagEnumId1(subf.getEnumId());
					acctgentry.setAcctgTagEnumId2(tg.getEnumId());
					acctgentry.setAcctgTagEnumId3(sfe.getEnumId());
					acctgentry.setAcctgTagEnumId4(area.getEnumId());
					imp_tx9 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx9.commit();

					Debug.log("commit GlAO");
					glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(
									ledger_repo,
									monto,
									cuentas.get("Cuenta Abono Presupuesto"),
									organizacionContable,"A");
					imp_tx11 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx11.commit();

				}

				if (cuentas.get("Cuenta Cargo Contable") != null) {

					Debug.log("Cuenta Contable");
					egresoDiario.setDescription(tipoDoc.getDescripcion()
							+ "-" + refDoc + "-C");

					// id Transaccion
					egresoDiario.setAcctgTransId(UtilImport
							.getAcctgTransIdDiario(refDoc,
									secuencia, "C"));

					AcctgTrans trans = ledger_repo.findOne(
							AcctgTrans.class, ledger_repo.map(
									AcctgTrans.Fields.acctgTransId,
									egresoDiario.getAcctgTransId()));

					if (trans != null) {
						Debug.log("Trans Modif");
						String message = "La transaccion con id: "
								+ egresoDiario.getAcctgTransId()
								+ "ya existe.";
						Debug.log(message);
						//storeImportEgresoDiarioError(rowdata, message,
							//	imp_repo);
						//continue;
					}

					egresoDiario.setGlFiscalTypeId(cuentas
							.get("GlFiscalTypeContable"));
					imp_tx2 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(egresoDiario);
					imp_tx2.commit();

					aux.setAcctgTransId(egresoDiario.getAcctgTransId());
					imp_tx4 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(aux);
					imp_tx4.commit();

					AcctgTransEntry acctgentry = UtilImport
							.generaAcctgTransEntry(egresoDiario,
									organizacionContable,
									"00001", "D",
									cuentas.get("Cuenta Cargo Contable"),
									sfe.getEnumId());
					// Tags seteados.
					acctgentry.setAcctgTagEnumId1(subf.getEnumId());
					acctgentry.setAcctgTagEnumId2(tg.getEnumId());
					acctgentry.setAcctgTagEnumId3(sfe.getEnumId());
					acctgentry.setAcctgTagEnumId4(area.getEnumId());
					imp_tx6 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx6.commit();

					GlAccountOrganization glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(ledger_repo,
									monto,
									cuentas.get("Cuenta Cargo Contable"),
									organizacionContable,"D");
					imp_tx8 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx8.commit();

					acctgentry = UtilImport.generaAcctgTransEntry(
							egresoDiario, organizacionContable,
							"00002", "C",
							cuentas.get("Cuenta Abono Contable"),
							sfe.getEnumId());
					// Tags seteados.
					acctgentry.setAcctgTagEnumId1(subf.getEnumId());
					acctgentry.setAcctgTagEnumId2(tg.getEnumId());
					acctgentry.setAcctgTagEnumId3(sfe.getEnumId());
					acctgentry.setAcctgTagEnumId4(area.getEnumId());
					imp_tx10 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(acctgentry);
					imp_tx10.commit();

					glAccountOrganization = UtilImport
							.actualizaGlAccountOrganization(ledger_repo,
									monto,
									cuentas.get("Cuenta Abono Contable"),
									organizacionContable,"A");
					imp_tx12 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx12.commit();
				}

				if (mensaje.isEmpty()) {
					String message = "Successfully imported Egreso Diario ["
							+ concatenacion + "].";
					//this.storeImportEgresoDiarioSuccess(rowdata, imp_repo);
					//Debug.logInfo(message, MODULE);
					//imported = imported + 1;
				}
        }
			catch (Exception ex) {
				String message = "Failed to import Egreso Diario ["
						+ concatenacion + "], Error message : "
						+ ex.getMessage();
				//storeImportEgresoDiarioError(rowdata, message, imp_repo);

				// rollback all if there was an error when importing item
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
				if (imp_tx5 != null) {
					imp_tx5.rollback();
				}
				if (imp_tx6 != null) {
					imp_tx6.rollback();
				}
				if (imp_tx7 != null) {
					imp_tx7.rollback();
				}
				if (imp_tx8 != null) {
					imp_tx8.rollback();
				}
				if (imp_tx9 != null) {
					imp_tx9.rollback();
				}
				if (imp_tx10 != null) {
					imp_tx10.rollback();
				}
				if (imp_tx11 != null) {
					imp_tx11.rollback();
				}
				if (imp_tx12 != null) {
					imp_tx12.rollback();
				}

				//Debug.logError(ex, message, MODULE);
				throw new ServiceException(ex.getMessage());
			}
		//this.importedRecords = imported;
			output = ServiceUtil.returnSuccess();
			output.put("messageOut", "Registro Exitoso");

	} catch (InfrastructureException ex) {
		//Debug.logError(ex, MODULE);
		output = ServiceUtil.returnError(ex.getMessage());
		throw new ServiceException(ex.getMessage());
	} catch (RepositoryException ex) {
		//Debug.logError(ex, MODULE);
		output = ServiceUtil.returnError(ex.getMessage());
		throw new ServiceException(ex.getMessage());
	} finally {
		if (session != null) {
			session.close();
		}
	}
        return output;
}
}
