package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelAttribute;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;
import java.util.Map;

public class RootModelClassGenerator extends AttributeClassGenerator {

    private RootModelClassGenerator(Context context, StaticMetamodelEntity entity, ImportBuilder imports) {
        super(context, entity, imports);
    }

    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the static metamodel entity
     * @param imports the import sentences
     * @return Class writer
     */
    public static RootModelClassGenerator of(Context context, StaticMetamodelEntity entity, ImportBuilder imports) {
        return new RootModelClassGenerator(context, entity, imports);
    }

    protected Template parentClassTemplate() {
        return Template.of("""
            public static class Root_<T extends $EntityClass$> implements RootAware<T>, Criteria.AnyExpression<T, Root<T>> {
                private final Root<T> root;
                private final CriteriaQuery<?> query;
                private final CriteriaBuilder builder;
                public Root_(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    this.root = root;
                    this.query = query;
                    this.builder = builder;
                }
                @Override public Root<T> get() { return root; }
                @Override public CriteriaBuilder builder() { return builder; }
                public CriteriaQuery<?> query() { return query; }
                $AttributeMethods$
            }
            """);
    }

    protected Template childClassTemplate() {
        return Template.of("""
            public static class Root_<T extends $EntityClass$> extends $SuperClass$Model.Root_<T> implements Criteria.AnyExpression<T, Root<T>> {
                public Root_(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    super(root, query, builder);
                }
                $AttributeMethods$
            }
            """);
    }



    protected void singularAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().isStruct()) {
            sb.append(Template.of("""
                public $ValueType$Model.Join_<$ValueType$> join$AttributeName$() {
                    return new $ValueType$Model.Join_<>(() -> get().join($EnclosingType$_.$attributeName$), query(), builder());
                }
                public $ValueType$Model.Path_<$ValueType$> get$AttributeName$() {
                    return new $ValueType$Model.Path_<>(() ->get().get($EnclosingType$_.$attributeName$), query(), builder());
                }
            """).bind(map));
        } else {
            sb.append(Template.of("""
                public $CriteriaPathClass$ get$AttributeName$() {
                    return new $CriteriaPathClass$(() -> get().get($EnclosingType$_.$attributeName$), builder());
                }
            """).bind(map));
        }
    }


    protected void collectionAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().isStruct()) {
            sb.append(Template.of("""
                public $ValueType$Model.Join_<$ValueType$> join$AttributeName$() {
                    return new $ValueType$Model.Join_<>(() -> get().join($EnclosingType$_.$attributeName$), query(), builder());
                }
            """).bind(map));
        }
        sb.append(Template.of("""
            public Criteria.CollectionExp<$ValueType$, $AttributeJavaType$<$ValueType$>, Expression<$AttributeJavaType$<$ValueType$>>> get$AttributeName$() {
                return new Criteria.CollectionExp(() -> ((Root<$EnclosingType$>) get()).get($EnclosingType$_.$attributeName$), builder());
            }
        """).bind(map));
    }


    protected void mapAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().isStruct()) {
            sb.append(Template.of("""
                public $ValueType$Model.Join_<$ValueType$> join$AttributeName$() {
                    return new $ValueType$Model.Join_<>(() -> get().join($EnclosingType$_.$attributeName$),query(), builder());
                }
            """).bind(map));
        }
        sb.append(Template.of("""
            public Expression<Map<$KeyType$, $ValueType$>> get$AttributeName$() {
                return get().get($EnclosingType$_.$attributeName$);
            }
        """).bind(map));
    }

}
