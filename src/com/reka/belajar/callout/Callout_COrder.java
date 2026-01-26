package com.reka.belajar.callout;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;

public class Callout_COrder implements IColumnCallout{

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		if(mField.getColumnName().equals("Description"))
			return setDescriptionBaseOnPOReference(ctx,WindowNo,mTab,mField,value);
		return null;
	}

	private String setDescriptionBaseOnPOReference(Properties ctx, int windowNo, GridTab mTab, GridField mField,
			Object value) {
		if(value==null)
			return null;
		String POReference = (String) value;
		POReference = "SET = "+POReference;
		mTab.setValue("POReference", POReference);
		return null;
	}

}
