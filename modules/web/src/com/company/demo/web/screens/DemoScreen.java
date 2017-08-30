package com.company.demo.web.screens;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UuidSource;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.AggregationInfo;
import com.haulmont.cuba.gui.components.AggregationInfo.Type;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource.RefreshMode;
import com.haulmont.cuba.gui.data.DsBuilder;
import com.haulmont.cuba.gui.data.impl.ValueGroupDatasourceImpl;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.Map;

public class DemoScreen extends AbstractWindow {
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private Metadata metadata;
    @Inject
    private UuidSource uuidSource;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        ValueGroupDatasourceImpl ds = DsBuilder.create()
                .setId("groupDs")
                .setRefreshMode(RefreshMode.NEVER)
                .buildValuesGroupDatasource();
        ds.setIdName("id");

        // register properties
        ds.addProperty("name", String.class);
        ds.addProperty("count", Integer.class);
        ds.addProperty("value", Integer.class);

        // set random data
        ds.addItem(item("Sam Dough", 10, 10));
        ds.addItem(item("Bill Dodson", 12, 30));
        ds.addItem(item("Bill Dodson", 1, 8));
        ds.addItem(item("John Roman", 0, 3));
        ds.addItem(item("John Roman", 20, 5));
        ds.addItem(item("Peter Wan", 6, 15));

        // create table
        GroupTable table = componentsFactory.createComponent(GroupTable.class);
        table.setWidth("100%");
        table.setHeight("100%");

        // enable aggregation
        table.setAggregatable(true);

        // create columns programmatically before we set datasource
        MetaClass metaClass = ds.getMetaClass();

        MetaPropertyPath nameMpp = metaClass.getPropertyPath("name");
        Table.Column nameColumn = new Table.Column(nameMpp, "Name");
        nameColumn.setType(nameMpp.getRangeJavaClass());
        table.addColumn(nameColumn);

        Table.Column countMpp = new Table.Column(metaClass.getPropertyPath("count"), "Count");
        Table.Column countColumn = countMpp;
        countColumn.setType(metaClass.getPropertyPath("count").getRangeJavaClass());
        AggregationInfo countAggInfo = new AggregationInfo();
        countAggInfo.setPropertyPath(ds.getMetaClass().getPropertyPath("count"));
        countAggInfo.setType(Type.SUM);
        countColumn.setAggregation(countAggInfo);
        table.addColumn(countColumn);

        MetaPropertyPath valueMpp = metaClass.getPropertyPath("value");
        Table.Column valueColumn = new Table.Column(valueMpp, "value");
        valueColumn.setType(valueMpp.getRangeJavaClass());
        AggregationInfo valueAggInfo = new AggregationInfo();
        valueAggInfo.setPropertyPath(ds.getMetaClass().getPropertyPath("count"));
        valueAggInfo.setType(Type.COUNT);
        valueColumn.setAggregation(valueAggInfo);
        table.addColumn(valueColumn);

        // finally, set datasource
        table.setDatasource(ds);

        add(table);
    }

    public KeyValueEntity item(String name, int count, int value) {
        KeyValueEntity entity = metadata.create(KeyValueEntity.class);
        entity.setValue("id", uuidSource.createUuid());
        entity.setValue("name", name);
        entity.setValue("count", count);
        entity.setValue("value", value);
        return entity;
    }
}