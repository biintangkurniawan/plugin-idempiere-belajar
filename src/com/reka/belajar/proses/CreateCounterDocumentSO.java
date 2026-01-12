package com.reka.belajar.proses;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.adempiere.base.annotation.Process;
import org.compiere.model.I_C_Order;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

@Process(name = "com.reka.belajar.proses.CreateCounterDocumentSO")
public class CreateCounterDocumentSO extends SvrProcess {

    private static final String PARAM_DOCUMENT_NO = "DocumentNo";
    private String documentNo;

    @Override
    protected void prepare() {
        ProcessInfoParameter[] params = getParameter();
        if (params == null) return;

        for (ProcessInfoParameter p : params) {
            if (p == null) continue;
            String name = p.getParameterName();
            if (name == null) continue;

            if (PARAM_DOCUMENT_NO.equalsIgnoreCase(name)) {
                documentNo = (String) p.getParameter();
            }
        }
    }

    @Override
    protected String doIt() throws Exception {
        if (documentNo == null || documentNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter DocumentNo wajib diisi.");
        }

        String docNo = documentNo.trim();
        int clientId = Env.getAD_Client_ID(getCtx());

        MOrder src = new Query(getCtx(), I_C_Order.Table_Name,
                "AD_Client_ID=? AND DocumentNo=? AND IsSOTrx='Y'",
                get_TrxName())
            .setParameters(clientId, docNo)
            .first();

        if (src == null) {
            throw new IllegalArgumentException("Sales Order dengan DocumentNo '" + docNo + "' tidak ditemukan.");
        }

        MOrder counter = new MOrder(getCtx(), 0, get_TrxName());

        counter.setAD_Org_ID(src.getAD_Org_ID());
        counter.setPOReference(src.getDocumentNo());

        counter.setIsSOTrx(true);
        counter.setC_DocTypeTarget_ID(src.getC_DocTypeTarget_ID());
        counter.setC_DocType_ID(0);

        counter.setC_BPartner_ID(src.getC_BPartner_ID());
        counter.setC_BPartner_Location_ID(src.getC_BPartner_Location_ID());
        counter.setBill_BPartner_ID(src.getBill_BPartner_ID());
        counter.setBill_Location_ID(src.getBill_Location_ID());

        counter.setM_PriceList_ID(src.getM_PriceList_ID());
        counter.setM_Warehouse_ID(src.getM_Warehouse_ID());
        counter.setC_Currency_ID(src.getC_Currency_ID());

        counter.setPaymentRule(src.getPaymentRule());
        counter.setC_PaymentTerm_ID(src.getC_PaymentTerm_ID());
        counter.setDeliveryRule(src.getDeliveryRule());
        counter.setDeliveryViaRule(src.getDeliveryViaRule());
        counter.setSalesRep_ID(src.getSalesRep_ID());

        Timestamp now = new Timestamp(System.currentTimeMillis());
        counter.setDateOrdered(now);
        counter.setDatePromised(now);

        counter.saveEx();

        MOrderLine[] srcLines = src.getLines(true, null);
        if (srcLines == null || srcLines.length == 0) {
            throw new IllegalStateException("Source Sales Order tidak punya line.");
        }

        for (MOrderLine sl : srcLines) {
            MOrderLine cl = new MOrderLine(counter);

            cl.setAD_Org_ID(counter.getAD_Org_ID());
            cl.setLine(sl.getLine());

            cl.setM_Product_ID(sl.getM_Product_ID());
            cl.setC_UOM_ID(sl.getC_UOM_ID());

            BigDecimal qty = sl.getQtyOrdered();
            if (qty == null) qty = Env.ZERO;
            qty = qty.negate();
            cl.setQtyOrdered(qty);
            cl.setQtyEntered(qty);

            cl.setPriceEntered(sl.getPriceEntered());
            cl.setPriceActual(sl.getPriceActual());
            cl.setPriceList(sl.getPriceList());

            cl.setC_Tax_ID(sl.getC_Tax_ID());

            cl.saveEx();
        }

        boolean okCounter = counter.processIt(DocAction.ACTION_Complete);
        counter.saveEx();

        if (!okCounter) {
            throw new IllegalStateException("Gagal complete Counter SO: " + counter.getProcessMsg());
        }

        return "Berhasil membuat Counter Sales Order. Source="
                + src.getDocumentNo()
                + " -> Counter="
                + counter.getDocumentNo()
                + " (C_Order_ID=" + counter.getC_Order_ID() + ")";
    }
}
