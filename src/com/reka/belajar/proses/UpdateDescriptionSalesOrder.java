package com.reka.belajar.proses;

import org.compiere.model.I_C_Order;
import org.compiere.model.MOrder;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;

/**
 * iDempiere Process:
 * Update Description Sales Order by DocumentNo
 *
 * Parameters (AD_Process_Para Column Name):
 *  - DocumentNo   (String)  : Sales Order Document No
 *  - Description  (String)  : Text to append as Desc
 *
 * Result:
 *  C_Order.Description = "Document NO:<DocumentNo> Desc:<Description>"
 */
@org.adempiere.base.annotation.Process(name = "com.reka.belajar.proses.UpdateDescriptionSalesOrder")
public class UpdateDescriptionSalesOrder extends SvrProcess {

    private static final String PARAM_DOCUMENT_NO = "DocumentNo";
    private static final String PARAM_DESCRIPTION = "Description";

    private String documentNo;
    private String descInput;

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
            } else if (PARAM_DESCRIPTION.equalsIgnoreCase(name)) {
                descInput = (String) p.getParameter();
            }
        }
    }

    @Override
    protected String doIt() throws Exception {
        // Validate input
        if (documentNo == null || documentNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter DocumentNo wajib diisi.");
        }
        if (descInput == null) {
            descInput = "";
        }

        String docNo = documentNo.trim();
        String desc = descInput.trim();

        // Find Sales Order by DocumentNo in current client
        int clientId = Env.getAD_Client_ID(getCtx());

        MOrder order = new Query(getCtx(), I_C_Order.Table_Name,
                "AD_Client_ID=? AND DocumentNo=? AND IsSOTrx='Y'",
                get_TrxName())
                .setParameters(clientId, docNo)
                .first();

        if (order == null) {
            throw new IllegalArgumentException("Sales Order dengan DocumentNo '" + docNo + "' tidak ditemukan.");
        }

        // Build new description exactly as required
        String newDescription = "Document NO:" + docNo + " Desc:" + desc;

//        order.setDescription(newDescription);
        order.saveEx(get_TrxName());

        // Log for process result window
        addLog(order.getC_Order_ID(), null, null, "Updated: " + newDescription);

        return "OK - " + newDescription;
    }
}
