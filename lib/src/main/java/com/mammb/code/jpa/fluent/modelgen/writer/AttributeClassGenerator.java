package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelAttribute;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

import java.util.Map;
import java.util.Objects;

public abstract class AttributeClassGenerator {

    /** Context of processing. */
    private final Context context;

    /** Representation of static metamodel. */
    private final StaticMetamodelEntity entity;

    /** Import sentences. */
    private final ImportBuilder imports;


    protected AttributeClassGenerator(Context context, StaticMetamodelEntity entity, ImportBuilder imports) {
        this.context = context;
        this.entity = entity;
        this.imports = imports;
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();
        if (entity.getSuperClass().isBlank()) {
            sb.append(parentClassTemplate().bind(
                "$EntityClass$", imports.add(entity.getTargetEntityQualifiedName()),
                "$AttributeMethods$", attributeMethods()).indentString(1)
            );
        } else {
            sb.append(childClassTemplate().bind(
                "$EntityClass$", imports.add(entity.getTargetEntityQualifiedName()),
                "$SuperClass$",  imports.add(entity.getSuperEntityQualifiedName()),
                "$AttributeMethods$", attributeMethods()).indentString(1)
            );
        }
        return sb.toString();

    }

    private String attributeMethods() {

        StringBuilder sb = new StringBuilder();

        for (StaticMetamodelAttribute attr : entity.getAttributes()) {

            var map = Map.of(
                "$EnclosingType$",     imports.add(attr.getEnclosingType().getName()),
                "$ValueType$",         imports.add(attr.getValueType().getName()),
                "$KeyType$",           attr.getAttributeType().isMap() ? imports.add(attr.getKeyType().getName()) : "",
                "$AttributeName$",     capitalize(attr.getName()),
                "$attributeName$",     attr.getName(),
                "$CriteriaPathClass$", criteriaPathClassName(attr),
                "$AttributeJavaType$", attr.getAttributeType().isList() ? imports.add("java.util.List")
                                     : attr.getAttributeType().isSet() ? imports.add("java.util.Set")
                                     : attr.getAttributeType().isCollection() ? imports.add("java.util.Collection")
                                     : attr.getAttributeType().isMap() ? imports.add("java.util.Map") : "");

            if (attr.getAttributeType().isSingular()) {
                singularAttribute(attr, map, sb);
            } else if (attr.getAttributeType().isMap()) {
                mapAttribute(attr, map, sb);
            } else {
                collectionAttribute(attr, map, sb);
            }
        }
        return sb.toString();
    }

    protected abstract Template parentClassTemplate();

    protected abstract Template childClassTemplate();

    protected abstract void singularAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb);

    protected abstract void collectionAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb);

    protected abstract void mapAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb);


    /**
     * Capitalize the given string.
     * @param str the given string
     * @return Capitalized string
     */
    static String capitalize(String str) {
        return (Objects.isNull(str) || str.isEmpty())
            ? str
            : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Get the criteria path class name from given attribute.
     * @param attr the attribute
     * @return the criteria path class name
     */
    protected String criteriaPathClassName(StaticMetamodelAttribute attr) {
        if (attr.getValueType().isString()) {
            return "Criteria.StringPath";
        } else if (attr.getValueType().isBoolean()) {
            return "Criteria.BooleanPath";
        } else if (attr.getValueType().isNumber()) {
            return "Criteria.NumberPath";
        } else if (attr.getValueType().isComparable()) {
            return "Criteria.ComparablePath<" + imports.add(attr.getValueType().getName()) + ">";
        } else {
            return "Criteria.AnyPath<" + imports.add(attr.getValueType().getName()) + ">";
        }
    }
}
