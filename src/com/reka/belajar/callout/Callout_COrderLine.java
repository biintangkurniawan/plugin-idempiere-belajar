package com.reka.belajar.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MProduct;

public class Callout_COrderLine implements IColumnCallout {

    @Override
    public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
        if (mField.getColumnName().equals("M_Product_ID"))
            return setDescriptionBasedOnProduct(ctx, WindowNo, mTab, mField, value);

        return null;
    }

    private String setDescriptionBasedOnProduct(Properties ctx, int windowNo, GridTab mTab, GridField mField,
                                                Object value) {
        if (value == null)
            return null;

        int M_Product_ID = 0;
        if (value instanceof Integer) {
            M_Product_ID = (Integer) value;
        } else {
            try {
                M_Product_ID = Integer.parseInt(value.toString());
            } catch (Exception e) {
                return null;
            }
        }

        if (M_Product_ID <= 0)
            return null;

        MProduct p = MProduct.get(ctx, M_Product_ID);
        if (p == null)
            return null;

        mTab.setValue("Description", p.getName());
        return null;
    }
}
