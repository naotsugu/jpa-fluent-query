package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelAttribute;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;
import java.util.Map;

public class PathModelClassGenerator extends AttributeClassGenerator {


    private PathModelClassGenerator(Context context, StaticMetamodelEntity entity, ImportBuilder imports) {
        super(context, entity, imports);
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the static metamodel entity
     * @param imports the import sentences
     * @return Class writer
     */
    public static PathModelClassGenerator of(Context context, StaticMetamodelEntity entity, ImportBuilder imports) {
        return new PathModelClassGenerator(context, entity, imports);
    }


    protected Template parentClassTemplate() {
        return Template.of("""
            public static class Path_<T extends $EntityClass$> implements Supplier<Path<T>>, Criteria.AnyExpression<T, Path<T>> {
                private final Supplier<Path<T>> path;
                private final CriteriaQuery<?> query;
                private final CriteriaBuilder builder;
                public Path_(Supplier<Path<T>> path, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    this.path = path;
                    this.query = query;
                    this.builder = builder;
                }
                @Override public Path<T> get() { return path.get(); }
                @Override public CriteriaBuilder builder() { return builder; }
                public CriteriaQuery<?> query() { return query; }
                $AttributeMethods$
            }
            """);
    }

    protected Template childClassTemplate() {
        return Template.of("""
            public static class Path_<T extends $EntityClass$> extends $SuperClass$Model.Path_<T> implements Criteria.AnyExpression<T, Path<T>> {
                public Path_(Supplier<Path<T>> path, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    super(path, query, builder);
                }
                $AttributeMethods$
            }
            """);
    }

    protected void singularAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        if (attr.getValueType().isStruct()) {
            sb.append(Template.of("""
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
        sb.append(Template.of("""
            public Criteria.CollectionExp<$ValueType$, $AttributeJavaType$<$ValueType$>, Expression<$AttributeJavaType$<$ValueType$>>> get$AttributeName$() {
                return new Criteria.CollectionExp(() -> ((Path<$EnclosingType$>) get()).get($EnclosingType$_.$attributeName$), builder());
            }
        """).bind(map));
    }

    protected void mapAttribute(StaticMetamodelAttribute attr, Map<String, String> map, StringBuilder sb) {
        sb.append(Template.of("""
            public Expression<Map<$KeyType$", $ValueType$>> get$AttributeName$ {
                return ((Path<$EnclosingType$>) get()).get($EnclosingType$_.$attributeName$);
            }
        """).bind(map));
    }

}
