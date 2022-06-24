package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.JpaMetaModelEnhanceProcessor;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

import javax.annotation.processing.FilerException;
import javax.tools.FileObject;
import java.io.PrintWriter;

public class ModelWriter {

    /** Context of processing. */
    private final Context context;

    /** Representation of static metamodel. */
    private final StaticMetamodelEntity entity;

    /** Import sentences. */
    private final ImportBuilder imports;


    /**
     * Constructor.
     * @param context the context of processing
     * @param entity the representation of static metamodel
     */
    protected ModelWriter(Context context, StaticMetamodelEntity entity) {
        this.context = context;
        this.entity = entity;
        this.imports = ImportBuilder.of(entity.getPackageName());
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @param entity the static metamodel entity
     * @return Class writer
     */
    public static ModelWriter of(Context context, StaticMetamodelEntity entity) {
        return new ModelWriter(context, entity);
    }


    /**
     * Write a generated class file.
     */
    public void writeFile() {
        context.logDebug("Create meta model : {}", entity.getQualifiedName());
        try {
            FileObject fo = context.getFiler().createSourceFile(
                entity.getTargetEntityQualifiedName() + "Model", entity.getElement());
            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {
                String body = generateBody();
                writePackageTo(pw);
                writeImportTo(pw);
                pw.println(body);
                pw.flush();
            }
        } catch (FilerException e) {
            context.logError("Problem with Filer: {}", e.getMessage());
        } catch (Exception e) {
            context.logError("Problem opening file to write Model for {} : {}", entity.getSimpleName(), e.getMessage());
        }
    }


    private void writePackageTo(PrintWriter pw) {
        if (imports.getSelfPackage().isEmpty()) {
            return;
        }
        pw.println("package " + imports.getSelfPackage() + ";");
        pw.println();
    }

    private void writeImportTo(PrintWriter pw) {
        imports.add("jakarta.persistence.criteria.CriteriaBuilder");
        imports.add("jakarta.persistence.criteria.CriteriaQuery");
        imports.add("jakarta.persistence.criteria.Expression");
        imports.add("jakarta.persistence.criteria.Predicate");
        imports.add("jakarta.persistence.criteria.Root");
        imports.add("jakarta.persistence.criteria.Join");
        imports.add("jakarta.persistence.criteria.Path");
        imports.add("jakarta.persistence.criteria.ListJoin");
        imports.add("jakarta.persistence.criteria.SetJoin");
        imports.add("jakarta.persistence.criteria.MapJoin");
        imports.add("jakarta.persistence.criteria.CollectionJoin");
        imports.add("java.util.List");
        imports.add("java.util.Map");
        imports.add("java.util.Set");
        imports.add("java.util.Collection");
        imports.add("java.util.function.Supplier");
        imports.add("javax.annotation.processing.Generated");
        imports.add(ApiClassWriter.PACKAGE_NAME + ".*;");
        pw.println(imports.generateImports(context.isJakarta()));
        pw.println();
    }


    private String generateBody() {
        StringBuilder sb = new StringBuilder();
        sb.append(Template.of("""
            @Generated(value = "$GeneratorClass$")
            public class $ClassName$Model {

                public static class $ClassName$Root extends Root_<$ClassName$> {
                    public $ClassName$Root(Root<$ClassName$> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                        super(root, query, builder);
                    }
                }
                public static $ClassName$Root root(Root<$ClassName$> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    return new $ClassName$Root(root, query, builder);
                }
                public static RootSource<$ClassName$, $ClassName$Root> root() {
                    return new RootSource<$ClassName$, $ClassName$Root>() {
                        @Override public $ClassName$Root root(CriteriaQuery<?> query, CriteriaBuilder builder) {
                            return new $ClassName$Root(query.from(rootClass()), query, builder);
                        }
                        @Override public Class<$ClassName$> rootClass() { return $ClassName$.class; }
                    };
                }

                $RootClass$

                $JoinClass$

                $PathClass$
            }
            """).bind(
            "$GeneratorClass$", JpaMetaModelEnhanceProcessor.class.getName(),
            "$ClassName$",      entity.getTargetEntityName(),
            "$RootClass$",      RootModelClassGenerator.of(context, entity, imports).generate(),
            "$JoinClass$",      JoinModelClassGenerator.of(context, entity, imports).generate(),
            "$PathClass$",      PathModelClassGenerator.of(context, entity, imports).generate())
        );
        return sb.toString();
    }

}
