package com.reka.belajar.component;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_C_OrderLine;

import com.reka.belajar.callout.Callout_COrder;
import com.reka.belajar.callout.Callout_COrderLine;

public class RekaCalloutFactory implements IColumnCalloutFactory {

    @Override
    public IColumnCallout[] getColumnCallouts(String tableName, String columnName) {
        List<IColumnCallout> list = new ArrayList<IColumnCallout>();

        if (I_C_Order.Table_Name.equals(tableName)) {
            list.add(new Callout_COrder());
        }

        if (I_C_OrderLine.Table_Name.equals(tableName)) {
            list.add(new Callout_COrderLine());
        }

        return list != null ? list.toArray(new IColumnCallout[0]) : new IColumnCallout[0];
    }
}
