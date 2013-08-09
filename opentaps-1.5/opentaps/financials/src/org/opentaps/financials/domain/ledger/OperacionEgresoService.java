package org.opentaps.financials.domain.ledger;

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
		Map<String,Object> output;
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
	    String adm1= (String) context.get("adm1");
	    String adm2= (String) context.get("adm2");
	    String adm3= (String) context.get("adm3");
	    String fun1= (String) context.get("fun1");
	    String fun2= (String) context.get("fun2");
	    String fun3= (String) context.get("fun3");
	    String prog1= (String) context.get("prog1");
	    String prog2= (String) context.get("prog2");
	    String prog3= (String) context.get("prog3");
	    String prog4= (String) context.get("prog4");
	    String cog1= (String) context.get("cog1");
	    String cog2= (String) context.get("cog2");
	    String cog3= (String) context.get("cog3");
	    String cog4= (String) context.get("cog4");
	    String cog5= (String) context.get("cog5");
	    String rec1= (String) context.get("rec1");
        String rec2= (String) context.get("rec2");
        String rec3= (String) context.get("rec3");
        String geo1= (String) context.get("geo1");
        String geo2= (String) context.get("geo2");
        String geo3= (String) context.get("geo3");
        String geo4= (String) context.get("geo4");
        String sec1= (String) context.get("sec1");
        String sec2= (String) context.get("sec2");
        String sec3= (String) context.get("sec3");
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
			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					adm1, "UR");
			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					adm2, "UO");
			mensaje = UtilImport.validaParty(mensaje, ledger_repo,
					adm3, "UE");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					fun1, "CLAS_FUN", "FIN");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					fun2, "CLAS_FUN", "FUN");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					fun3, "CLAS_FUN", "SUBF");
			mensaje = UtilImport.validaWorkEffort(mensaje, ledger_repo,
					prog1, "EJE");
			mensaje = UtilImport.validaWorkEffort(mensaje, ledger_repo,
					prog2, "PP");
			mensaje = UtilImport.validaWorkEffort(mensaje, ledger_repo,
					prog3, "SPP");
			mensaje = UtilImport.validaWorkEffort(mensaje, ledger_repo,
					prog4, "ACT");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					cog1, "TIPO_GASTO", "tg");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cog2, "CA", "CAP");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cog3, "CON", "CON");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cog4, "PG", "PG");
			mensaje = UtilImport.validaProductCategory(mensaje,
					ledger_repo, cog5, "PE", "PE");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					geo1, "EF");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					geo2, "REG");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					geo3, "MUN");
			mensaje = UtilImport.validaGeo(mensaje, ledger_repo,
					geo4, "LOC");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					rec1, "CLAS_FR", "F");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					rec2, "CLAS_FR", "SF");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					rec3, "CLAS_FR", "SFE");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					sec1, "CLAS_SECT", "SEC");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					sec2, "CLAS_SECT", "SUBSEC");
			mensaje = UtilImport.validaEnumeration(mensaje, ledger_repo,
					sec3, "CLAS_SECT", "AREA");
			mensaje = UtilImport.validaTipoDoc(mensaje, ledger_repo,
					tipoDocumento);
			mensaje = UtilImport.validaCiclo(mensaje,ciclo,
					fechaContable);

			if (mensaje == null) {
				String message = "Failed to import Egreso Diario ["
						+ concatenacion + "], Error message : "
						+ mensaje;
				//storeImportEgresoDiarioError(rowdata, message, imp_repo);
				//continue;
			}

			// Creacion de objetos
			Debug.log("Empieza creacion de objetos");
			Party ur = UtilImport.obtenParty(ledger_repo, adm1);
			Party uo = UtilImport.obtenParty(ledger_repo, adm2);
			Party ue = UtilImport.obtenParty(ledger_repo, adm3);
			Enumeration fin = UtilImport.obtenEnumeration(ledger_repo,
					fun1, "CLAS_FUN");
			Enumeration fun = UtilImport.obtenEnumeration(ledger_repo,
					fun2, "CLAS_FUN");
			Enumeration subf = UtilImport.obtenEnumeration(ledger_repo,
					fun3, "CLAS_FUN");
			WorkEffort eje = UtilImport.obtenWorkEffort(ledger_repo,
					prog1);
			WorkEffort pp = UtilImport.obtenWorkEffort(ledger_repo,
					prog2);
			WorkEffort spp = UtilImport.obtenWorkEffort(ledger_repo,
					prog3);
			WorkEffort act = UtilImport.obtenWorkEffort(ledger_repo,
					prog4);
			Enumeration tg = UtilImport.obtenEnumeration(ledger_repo,
					cog1, "TIPO_GASTO");
			ProductCategory cap = UtilImport.obtenProductCategory(
					ledger_repo, cog2, "CA");
			ProductCategory con = UtilImport.obtenProductCategory(
					ledger_repo, cog3, "CON");
			ProductCategory pg = UtilImport.obtenProductCategory(
					ledger_repo, cog4, "PG");
			ProductCategory pe = UtilImport.obtenProductCategory(
					ledger_repo, cog5, "PE");
			Geo ef = UtilImport.obtenGeo(ledger_repo, geo1);
			Geo reg = UtilImport.obtenGeo(ledger_repo, geo2);
			Geo mun = UtilImport.obtenGeo(ledger_repo, geo3);
			Geo loc = UtilImport.obtenGeo(ledger_repo, geo4);
			Enumeration f = UtilImport.obtenEnumeration(ledger_repo,
					rec1, "CLAS_FR");
			Enumeration sf = UtilImport.obtenEnumeration(ledger_repo,
					rec2, "CLAS_FR");
			Enumeration sfe = UtilImport.obtenEnumeration(ledger_repo,
					rec3, "CLAS_FR");
			Enumeration sec = UtilImport.obtenEnumeration(ledger_repo,
					sec1, "CLAS_SECT");
			Enumeration subsec = UtilImport.obtenEnumeration(ledger_repo,
					sec2, "CLAS_SECT");
			Enumeration area = UtilImport.obtenEnumeration(ledger_repo,
					sec3, "CLAS_SECT");
			TipoDocumento tipoDoc = UtilImport.obtenTipoDocumento(
					ledger_repo, tipoDocumento);

			// Empieza bloque de vigencias
			Debug.log("Empieza bloque de vigencias");
			// Vigencias
			mensaje = UtilImport.validaVigencia(mensaje, "SUBF", subf,
					fechaContable);
			mensaje = UtilImport.validaVigencia(mensaje, "TG", tg,
					fechaContable);
			mensaje = UtilImport.validaVigencia(mensaje, "SFE", sfe,
					fechaContable);
			mensaje = UtilImport.validaVigencia(mensaje, "AREA", area,
					fechaContable);

			if (mensaje == null) {
				String message = "Failed to import Egreso Diario ["
						+ concatenacion+ "], Error message : "
						+ mensaje;
				//storeImportEgresoDiarioError(rowdata, message, imp_repo);
				//continue;
			}

			Debug.log("Motor Contable");
			MotorContable motor = new MotorContable(ledger_repo);
			Map<String, String> cuentas = motor.cuentasDiarias(
					tipoDoc.getAcctgTransTypeId(), cog4,
					pe.getProductCategoryId(),
					organizacionContable, cog1,
					null, tipoCatalogo, idPago,
					null, null, null, true, null, null,
					idProducto);

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
				aux.setUnidadResponsable(ur.getPartyId());
				aux.setUnidadOrganizacional(uo.getPartyId());
				aux.setUnidadEjecutora(ue.getPartyId());
				aux.setFinalidad(fin.getEnumId());
				aux.setFuncion(fun.getEnumId());
				aux.setSubFuncion(subf.getEnumId());
				aux.setProgramaPlan(prog1);
				aux.setProgramaPresupuestario(prog2);
				aux.setSubProgramaPresupuestario(prog3);
				aux.setActividad(prog4);
				aux.setTipoGasto(tg.getEnumId());
				aux.setCapitulo(cap.getProductCategoryId());
				aux.setConcepto(con.getProductCategoryId());
				aux.setPartidaGenerica(pg.getProductCategoryId());
				aux.setPartidaEspecifica(pe.getProductCategoryId());
				aux.setFuente(f.getEnumId());
				aux.setSubFuente(sf.getEnumId());
				aux.setSubFuenteEspecifica(sfe.getEnumId());
				aux.setEntidadFederativa(geo1);
				aux.setRegion(geo2);
				aux.setMunicipio(geo3);
				aux.setLocalidad(geo4);
				aux.setSector(sec.getEnumId());
				aux.setSubSector(subsec.getEnumId());
				aux.setArea(area.getEnumId());
				aux.setAgrupador(refDoc);
				aux.setIdTipoDoc(tipoDocumento);
				aux.setSecuencia(secuencia);
				aux.setLote(lote);
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

					egresoDiario.setGlFiscalTypeId("BUDGET");
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
									organizacionContable);
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
									organizacionContable);
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
							.get("GlFiscalType"));
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
									organizacionContable);
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
									organizacionContable);
					imp_tx12 = this.session.beginTransaction();
					ledger_repo.createOrUpdate(glAccountOrganization);
					imp_tx12.commit();
				}

				String message = "Successfully imported Egreso Diario ["
						+ concatenacion + "].";
				//this.storeImportEgresoDiarioSuccess(rowdata, imp_repo);
				//Debug.logInfo(message, MODULE);
				//imported = imported + 1;
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
