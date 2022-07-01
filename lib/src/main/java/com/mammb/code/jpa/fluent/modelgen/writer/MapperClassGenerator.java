package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.JpaMetaModelEnhanceProcessor;
import javax.tools.FileObject;
import java.io.PrintWriter;

public class MapperClassGenerator {

    /** The name of package. */
    public static final String PACKAGE_NAME = "com.mammb.code.jpa.core";

    /** The name of Mapper class. */
    public static final String MAPPER = "Mapper";

    /** Context of processing. */
    private final Context context;


    /**
     * Constructor.
     * @param context the context of processing
     */
    protected MapperClassGenerator(Context context) {
        this.context = context;
    }


    /**
     * Create a mapper class writer instance.
     * @param context the context of processing
     * @return a mapper class writer
     */
    public static MapperClassGenerator of(Context context) {
        return new MapperClassGenerator(context);
    }


    /**
     * Write api classes.
     */
    public void writeClasses() {

        try {

            ImportBuilder imports = ImportBuilder.of(PACKAGE_NAME);
            FileObject fo = context.getFiler().createSourceFile(imports.getSelfPackage() + "." + MAPPER);

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                pw.println(Template.of("""
                    package $packageName$;

                    import javax.annotation.processing.Generated;

                    @Generated(value = "$GeneratorClass$")
                    public interface $Mapper$ {
                    }
                    """).bind(
                    "$packageName$", imports.getSelfPackage(),
                    "$GeneratorClass$", JpaMetaModelEnhanceProcessor.class.getName(),
                    "$Mapper$", MAPPER));

                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write {} class : {}", MAPPER, e.getMessage());
        }

    }
}
