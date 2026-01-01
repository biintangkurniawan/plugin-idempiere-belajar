package com.reka.belajar.component;

import org.adempiere.base.AnnotationBasedProcessFactory;
import org.adempiere.base.IProcessFactory;
import org.osgi.service.component.annotations.Component;

@Component(
    immediate = true,
    service = IProcessFactory.class,
    property = { "service.ranking:Integer=1" }
)
public class RekaProcessFactory extends AnnotationBasedProcessFactory {

    @Override
    protected String[] getPackages() {
        return new String[] { "com.reka.belajar.proses" };
    }
}
