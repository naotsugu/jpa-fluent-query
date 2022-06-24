package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelAttribute;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

import java.util.Map;

public class JoinModelClassGenerator extends AttributeClassGenerator {


    private JoinModelClassGenerator(Context context, StaticMetamodelEntity entity, ImportBuilder imports) {
        super(context, entity, imports);
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the static metamodel entity
     * @param imports the import sentences
     * @return Class writer
     */
    public static JoinModelClassGenerator of(Context context, StaticMetamodelEntity entity, ImportBuilder imports) {
        return new JoinModelClassGenerator(context, entity, imports);
    }


    protected Template parentClassTemplate() {
        return Template.of("""
            static class Join_<T extends $EntityClass$> implements Supplier<Join<?, T>>, Criteria.AnyExpression<T, Join<?, T>> {
                private final Supplier<Join<?, T>> join;
                protected final CriteriaQuery<?> query;
                protected final CriteriaBuilder builder;
                public Join_(Supplier<Join<?, T>> join, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    this.join = join;
                    this.query = query;
                    this.builder = builder;
                }
                @Override public Join<?, T> get() { return join.get(); }
                @Override public CriteriaBuilder builder() { return builder; }
                public CriteriaQuery<?> query() { return query; }
                $AttributeMethods$
            }
            """);
    }

    protected Template childClassTemplate() {
        return Template.of("""
            static class Join_<T extends $EntityClass$> extends $SuperClass$Model.Join_<T> implements Criteria.AnyExpression<T, Join<?, T>> {
                public Join_(Supplier<Join<?, T>> join, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    super(join, query, builder);
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
                    return new $ValueType$Model.Path_<>(() -> get().get($EnclosingType$_.$attributeName$), query(), builder());
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
                    return new $ValueType$Model.Join_<>(() -> ((Join<?, $EnclosingType$>) get()).join($EnclosingType$_.$attributeName$), query(), builder());
                }
            """).bind(map));
        } else {
            sb.append(Template.of("""
                public $AttributeJavaType$Join<T, $ValueType$> join$AttributeName$() {
                    return ((Join<?, $EnclosingType$>) get()).join($EnclosingType$_.$attributeName$);
                }
            """).bind(map));
        }
        sb.append(Template.of("""
            public Criteria.CollectionExp<$ValueType$, $AttributeJavaType$<$ValueType$>, Expression<$AttributeJavaType$<$ValueType$>>> get$AttributeName$() {
                return new Criteria.CollectionExp(() -> ((Join<?, $EnclosingType$>) get()).get($EnclosingType$_.$attributeName$), builder());
            }
        """).bind(map));
    }
    protected void mapAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().isStruct()) {
            sb.append(Template.of("""
                public $EnclosingType$Map.Join_<$ValueType$> join$AttributeName$() {
                    return new $EnclosingType$Map.Join_<>(() -> ((Join<?, $EnclosingType$>) get()).join($EnclosingType$_.$attributeName$), query(), builder());
                }
            """).bind(map));
        } else {
            sb.append(Template.of("""
                public MapJoin<T, $KeyType$, $ValueType$> join$AttributeName$() {
                    return get().join($EnclosingType$_.$attributeName$);
                }
            """).bind(map));
        }
        sb.append(Template.of("""
            public Expression<Map<$KeyType$, $ValueType$>> get$AttributeName$() {
                return ((Join<?, $EnclosingType$>) get()).get($EnclosingType$_.$attributeName$);
            }
        """).bind(map));
    }

}
