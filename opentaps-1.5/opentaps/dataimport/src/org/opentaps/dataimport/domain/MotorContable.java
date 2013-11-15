package org.opentaps.dataimport.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.opentaps.base.entities.DataImportContableGuide;
import org.opentaps.base.entities.DataImportMatrizEgr;
import org.opentaps.base.entities.DataImportMatrizIng;
import org.opentaps.base.entities.GlAccountCategoryRelation;
import org.opentaps.base.entities.MiniGuiaContable;
import org.opentaps.base.entities.PaymentMethod;
import org.opentaps.base.entities.ProductCategoryMember;
import org.opentaps.base.entities.ProductGlAccount;
import org.opentaps.domain.ledger.LedgerRepositoryInterface;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.repository.RepositoryException;

public class MotorContable {
	LedgerRepositoryInterface ledger_repo;
	Map<String, String> cuentas;

	public MotorContable(LedgerRepositoryInterface ledger_repo) {
		this.ledger_repo = ledger_repo;
		this.cuentas = new HashMap<String, String>();
	}

	public void buscaCuentaCategory(String idCategory,
			String organizationPartyId, String cuenta)
			throws RepositoryException {
		List<ProductCategoryMember> member = ledger_repo.findList(
				ProductCategoryMember.class, ledger_repo.map(
						ProductCategoryMember.Fields.productCategoryId,
						idCategory));

		List<ProductGlAccount> productGl = ledger_repo.findList(
				ProductGlAccount.class, ledger_repo.map(
						ProductGlAccount.Fields.productId, member.get(0)
								.getProductId(),
						ProductGlAccount.Fields.organizationPartyId,
						organizationPartyId));

		if (!productGl.isEmpty()) {
			cuentas.put(cuenta, productGl.get(0).getGlAccountId());
		}
	}

	public void buscaCuentaProduct(String idProduct,
			String organizationPartyId, String cuenta)
			throws RepositoryException {
		Debug.log("BUG en cuentas:");
		Debug.log("productid.- " + idProduct);
		Debug.log("organization.- " + organizationPartyId);
		List<ProductGlAccount> productGl = ledger_repo.findList(
				ProductGlAccount.class, ledger_repo.map(
						ProductGlAccount.Fields.productId, idProduct,
						ProductGlAccount.Fields.organizationPartyId,
						organizationPartyId));

		Debug.log("!productGl.isEmpty().- " + !productGl.isEmpty());
		if (!productGl.isEmpty()) {
			cuentas.put(cuenta, productGl.get(0).getGlAccountId());
		}
	}

	public void buscaCuentaProductCatalogo(String idProduct,
			String organizationPartyId, String cuenta, String idCategory)
			throws RepositoryException {

		List<ProductGlAccount> productGl = ledger_repo.findList(
				ProductGlAccount.class, ledger_repo.map(
						ProductGlAccount.Fields.productId, idProduct,
						ProductGlAccount.Fields.organizationPartyId,
						organizationPartyId));

		if (!productGl.isEmpty()) {
			Debug.log("Se valida que el producto este ligado a la categoria");
			List<ProductCategoryMember> member = ledger_repo.findList(
					ProductCategoryMember.class, ledger_repo.map(
							ProductCategoryMember.Fields.productCategoryId,
							idCategory, ProductCategoryMember.Fields.productId,
							idProduct));

			if (!member.isEmpty()) {
				Debug.log("Se valida que la categoria tenga relacion con la cuenta regresada por la guia");
				List<GlAccountCategoryRelation> relacion = ledger_repo
						.findList(
								GlAccountCategoryRelation.class,
								ledger_repo
										.map(GlAccountCategoryRelation.Fields.glAccountId,
												cuentas.get(cuenta)));

				if (!relacion.isEmpty()
						&& relacion.get(0).getProductCategoryId()
								.equalsIgnoreCase(idCategory)) {
					cuentas.put(cuenta, productGl.get(0).getGlAccountId());
				}
			}
		}
	}

	public void buscaCuentaPago(String idPago, String cuenta)
			throws RepositoryException {
		PaymentMethod payment = ledger_repo.findOne(PaymentMethod.class,
				ledger_repo.map(PaymentMethod.Fields.paymentMethodId, idPago));
		if (payment != null) {
			Debug.log("Se encuentra cuenta " + payment.getGlAccountId());
			cuentas.put(cuenta, payment.getGlAccountId());
		}
	}

	public Map<String, String> cuentasDiarias(String tipoTransaccion,
			String prodGen, String prodEsp, String organizationPartyId,
			String tipoGasto, String n5, String tipoCatalogoC, String idC,
			String tipoCatalogoD, String idD, String tipo, boolean egreso,
			String concepto, String subconcepto, String idProduct)
			throws RepositoryException {

		MiniGuiaContable miniGuia = ledger_repo.findOne(MiniGuiaContable.class,
				ledger_repo.map(MiniGuiaContable.Fields.acctgTransTypeId,
						tipoTransaccion));
		cuentas.put("GlFiscalTypePresupuesto", miniGuia.getGlFiscalTypeIdPres());
		cuentas.put("GlFiscalTypeContable", miniGuia.getGlFiscalTypeIdCont());
		cuentas.put("Cuenta Cargo Presupuesto", miniGuia.getCuentaCargo());
		cuentas.put("Cuenta Abono Presupuesto", miniGuia.getCuentaAbono());

		if (miniGuia.getReferencia().equalsIgnoreCase("M")) {
			Debug.log("Referencia = M");
			if (egreso) {
				Debug.log("Egreso");
				Debug.log("COG " + prodGen);
				Debug.log("TIPOGASTO " + tipoGasto);
				List<DataImportMatrizEgr> matriz = ledger_repo.findList(
						DataImportMatrizEgr.class, ledger_repo.map(
								DataImportMatrizEgr.Fields.cog, prodGen,
								DataImportMatrizEgr.Fields.tipoGasto,
								tipoGasto, DataImportMatrizEgr.Fields.matrizId,
								miniGuia.getTipoMatriz()));
				if (matriz.isEmpty()) {
					Debug.log("Error, elemento en Matriz no existe");
					throw new RepositoryException(
							"Error, elemento en Matriz no existe");
				}
				Debug.log("Cuenta Cargo Contable " + matriz.get(0).getCargo());
				Debug.log("Cuenta Abono Contable " + matriz.get(0).getAbono());
				Debug.log("Matriz " + matriz.get(0).getMatrizId());
				cuentas.put("Cuenta Cargo Contable", matriz.get(0).getCargo());
				cuentas.put("Cuenta Abono Contable", matriz.get(0).getAbono());

				if (matriz.get(0).getMatrizId().equalsIgnoreCase("A.1")) {
					Debug.log("A.1");
					buscaCuentaCategory(prodEsp, organizationPartyId,
							"Cuenta Cargo Contable");
					if (idProduct != null) {
						buscaCuentaProduct(idProduct, organizationPartyId,
								"Cuenta Cargo Contable");
					}
				} else {
					Debug.log("A.2");
					buscaCuentaPago(idC, "Cuenta Abono Contable");
				}

			} else {
				Debug.log("Ingreso");
				List<DataImportMatrizIng> matriz = ledger_repo.findList(
						DataImportMatrizIng.class, ledger_repo.map(
								DataImportMatrizIng.Fields.cri, tipo,
								DataImportMatrizIng.Fields.matrizId,
								miniGuia.getTipoMatriz()));
				if (matriz.isEmpty()) {
					Debug.log("Error, elemento en Matriz no existe");
					throw new RepositoryException(
							"Error, elemento en Matriz no existe");
				}
				cuentas.put("Cuenta Cargo Contable", matriz.get(0).getCargo());
				cuentas.put("Cuenta Abono Contable", matriz.get(0).getAbono());
				if (matriz.get(0).getMatrizId().equalsIgnoreCase("B.1")) {
					Debug.log("B.1");
					buscaCuentaCategory(n5, organizationPartyId,
							"Cuenta Abono Contable");
					if (idProduct != null) {
						buscaCuentaProduct(idProduct, organizationPartyId,
								"Cuenta Cargo Contable");
					}

				} else {
					Debug.log("B.2");
					buscaCuentaPago(tipo, "Cuenta Cargo Contable");
				}
			}

		} else if (miniGuia.getReferencia().equalsIgnoreCase("G")) {
			Debug.log("Referencia = G");
			List<DataImportContableGuide> guia = null;
			if (subconcepto != null) {
				Debug.log("Subconcepto");
				guia = ledger_repo.findList(DataImportContableGuide.class,
						ledger_repo.map(
								DataImportContableGuide.Fields.id_subconcepto,
								subconcepto));
			} else {
				Debug.log("Concepto");
				guia = ledger_repo.findList(DataImportContableGuide.class,
						ledger_repo.map(
								DataImportContableGuide.Fields.id_concepto,
								concepto));
			}

			if (guia.isEmpty()) {
				Debug.log("Error, elemento en Guia no existe");
				throw new RepositoryException(
						"Error, elemento en Guia no existe");
			}

			if (guia.get(0).getRp_cargo() != null
					&& !guia.get(0).getRp_cargo().equalsIgnoreCase("0")) {
				cuentas.put("Cuenta Cargo Presupuesto", guia.get(0)
						.getRp_cargo());
			}
			if (guia.get(0).getRp_abono() != null
					&& !guia.get(0).getRp_abono().equalsIgnoreCase("0")) {
				cuentas.put("Cuenta Abono Presupuesto", guia.get(0)
						.getRp_abono());
			}
			if (guia.get(0).getRc_cargo() != null
					&& !guia.get(0).getRc_cargo().equalsIgnoreCase("0")) {
				cuentas.put("Cuenta Cargo Contable", guia.get(0).getRc_cargo());
			}
			if (guia.get(0).getRc_abono() != null
					&& !guia.get(0).getRc_abono().equalsIgnoreCase("0")) {
				cuentas.put("Cuenta Abono Contable", guia.get(0).getRc_abono());
			}

			Debug.log("Tipo Catalogos");

			if (tipoCatalogoC != null) {
				if (tipoCatalogoC.equalsIgnoreCase("BANCO")) {
					Debug.log("Banco C");
					buscaCuentaPago(idC, "Cuenta Abono Contable");
				} else {
					Debug.log("Producto C");
					buscaCuentaProductCatalogo(idC, organizationPartyId,
							"Cuenta Abono Contable", tipoCatalogoC);
				}
			}
			if (tipoCatalogoD != null) {
				if (tipoCatalogoD.equalsIgnoreCase("BANCO")) {
					Debug.log("Banco D");
					buscaCuentaPago(idD, "Cuenta Cargo Contable");
				} else {
					Debug.log("Producto D");
					buscaCuentaProductCatalogo(idD, organizationPartyId,
							"Cuenta Cargo Contable", tipoCatalogoD);
				}
			}

		} else {
			// miniGuia.getReferencia().equalsIgnoreCase("N")
			Debug.log("Referencia = N");
		}
		return cuentas;
	}

	public boolean validaCuentaAuxiliar(String cuenta)
			throws RepositoryException {
		Debug.log("Se valida que la Cuenta tenga un catálogo auxiliar");
		GlAccountCategoryRelation auxiliar = ledger_repo.findOne(
				GlAccountCategoryRelation.class, ledger_repo.map(
						GlAccountCategoryRelation.Fields.glAccountId,
						cuentas.get(cuenta)));

		if (auxiliar != null) {
			return true;
		}
		return false;
	}

	public void buscaCuentasProductos(String idProductD, String idProductH,
			String idPagoD, String idPagoH, String organizationPartyId)
			throws RepositoryException {

		String mensaje = "";

		if (idPagoH != null) {
			buscaCuentaPago(idPagoH, "Cuenta Abono Contable");
		} else if (validaCuentaAuxiliar("Cuenta Abono Contable")) {
			if (idProductH != null) {
				buscaCuentaProduct(idProductH, organizationPartyId,
						"Cuenta Abono Contable");
			} else {
				mensaje += "idProductoH es obligatorio ";
			}
		}

		if (idPagoD != null) {
			buscaCuentaPago(idPagoD, "Cuenta Cargo Contable");
		} else if (validaCuentaAuxiliar("Cuenta Cargo Contable")) {
			if (idProductD != null) {
				buscaCuentaProduct(idProductD, organizationPartyId,
						"Cuenta Cargo Contable");
			} else {
				mensaje += "idProductoD es obligatorio ";
			}
		}

		if (!mensaje.isEmpty()) {
			cuentas.put("Mensaje", mensaje);
		}

	}

	public Map<String, String> cuentasIngresoDiario(String tipoTransaccion,
			String organizationPartyId, String idPago, String cri,
			String idProductD, String idProductH) throws RepositoryException {
			Debug.log("Entro a cuentasIngresoDiario");
		MiniGuiaContable miniGuia = ledger_repo.findOne(MiniGuiaContable.class,
				ledger_repo.map(MiniGuiaContable.Fields.acctgTransTypeId,
						tipoTransaccion));
		Debug.log("Obtuvo Mini Guia");
		cuentas.put("GlFiscalTypePresupuesto", miniGuia.getGlFiscalTypeIdPres());
		cuentas.put("GlFiscalTypeContable", miniGuia.getGlFiscalTypeIdCont());
		cuentas.put("Cuenta Cargo Presupuesto", miniGuia.getCuentaCargo());
		cuentas.put("Cuenta Abono Presupuesto", miniGuia.getCuentaAbono());

		if (miniGuia.getReferencia().equalsIgnoreCase("M")) {
			Debug.log("Referencia = M");
			List<DataImportMatrizIng> matriz = ledger_repo.findList(
					DataImportMatrizIng.class, ledger_repo.map(
							DataImportMatrizIng.Fields.cri, cri,
							DataImportMatrizIng.Fields.matrizId,
							miniGuia.getTipoMatriz()));
			if (matriz.isEmpty()) {
				Debug.log("Error, elemento en Matriz no existe");
				throw new RepositoryException(
						"Error, elemento en Matriz no existe");
			}

			cuentas.put("Cuenta Cargo Contable", matriz.get(0).getCargo());
			cuentas.put("Cuenta Abono Contable", matriz.get(0).getAbono());

			// if (matriz.get(0).getMatrizId().equalsIgnoreCase("B.1")) {
			// Debug.log("B.1");
			// buscaCuentaCategory(n5, organizationPartyId,
			// "Cuenta Abono Contable");
			// if (idProductD != null) {
			// buscaCuentaProduct(idProductD, organizationPartyId,
			// "Cuenta Cargo Contable");
			// }
			//
			// } else {
			// Debug.log("B.2");
			// buscaCuentaPago(idPago, "Cuenta Cargo Contable");
			// }

			buscaCuentasProductos(idProductD, idProductH, idPago, null,
					organizationPartyId);

		} else {
			// miniGuia.getReferencia().equalsIgnoreCase("N")
			Debug.log("Referencia = N");
		}
		return cuentas;
	}

	public Map<String, String> cuentasEgresoDiario(String tipoTransaccion,
			String cog, String organizationPartyId, String tipoGasto,
			String idPago, String idProductD, String idProductH)
			throws RepositoryException {

		MiniGuiaContable miniGuia = ledger_repo.findOne(MiniGuiaContable.class,
				ledger_repo.map(MiniGuiaContable.Fields.acctgTransTypeId,
						tipoTransaccion));
		cuentas.put("GlFiscalTypePresupuesto", miniGuia.getGlFiscalTypeIdPres());
		cuentas.put("GlFiscalTypeContable", miniGuia.getGlFiscalTypeIdCont());
		cuentas.put("Cuenta Cargo Presupuesto", miniGuia.getCuentaCargo());
		cuentas.put("Cuenta Abono Presupuesto", miniGuia.getCuentaAbono());

		if (miniGuia.getReferencia().equalsIgnoreCase("M")) {
			Debug.log("Referencia = M");
			Debug.log("Egreso");
			Debug.log("COG " + cog);
			Debug.log("TIPOGASTO " + tipoGasto);
			List<DataImportMatrizEgr> matriz = ledger_repo.findList(
					DataImportMatrizEgr.class, ledger_repo.map(
							DataImportMatrizEgr.Fields.cog, cog,
							DataImportMatrizEgr.Fields.tipoGasto, tipoGasto,
							DataImportMatrizEgr.Fields.matrizId,
							miniGuia.getTipoMatriz()));
			if (matriz.isEmpty()) {
				Debug.log("Error, elemento en Matriz no existe");
				throw new RepositoryException(
						"Error, elemento en Matriz no existe");
			}
			Debug.log("Cuenta Cargo Contable " + matriz.get(0).getCargo());
			Debug.log("Cuenta Abono Contable " + matriz.get(0).getAbono());
			Debug.log("Matriz " + matriz.get(0).getMatrizId());
			cuentas.put("Cuenta Cargo Contable", matriz.get(0).getCargo());
			cuentas.put("Cuenta Abono Contable", matriz.get(0).getAbono());

			// if (matriz.get(0).getMatrizId().equalsIgnoreCase("A.1")) {
			// Debug.log("A.1");
			// buscaCuentaCategory(prodEsp, organizationPartyId,
			// "Cuenta Cargo Contable");
			// if (idProduct != null) {
			// buscaCuentaProduct(idProduct, organizationPartyId,
			// "Cuenta Cargo Contable");
			// }
			// } else {
			// Debug.log("A.2");
			// buscaCuentaPago(idC, "Cuenta Abono Contable");
			// }

			buscaCuentasProductos(idProductD, idProductH, null, idPago,
					organizationPartyId);
		} else {
			// miniGuia.getReferencia().equalsIgnoreCase("N")
			Debug.log("Referencia = N");
		}
		return cuentas;
	}
}
