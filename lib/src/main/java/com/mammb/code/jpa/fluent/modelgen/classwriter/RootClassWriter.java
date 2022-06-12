/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jpa.fluent.modelgen.classwriter;

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.JpaMetaModelEnhanceProcessor;

import javax.tools.FileObject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

/**
 * Helper to write the root class factory class
 * using the {@link javax.annotation.processing.Filer} API.
 *
 * @author Naotsugu Kobayashi
 */
public class RootClassWriter {

    /** the default package name. */
    private static final String API_PACKAGE_NAME = "com.mammb.code.jpa.core";

    /** Context of processing. */
    private final Context context;

    /** Metamodel classes. */
    private final List<String> modelClasses;


    /**
     * Constructor.
     * @param context the context of processing
     */
    protected RootClassWriter(Context context) {
        this.context = context;
        this.modelClasses = context.getGeneratedModelClasses().stream().sorted().toList();
    }


    /**
     * Create a class writer instance.
     * @param context the context of processing
     * @return Root class factory writer
     */
    public static RootClassWriter of(Context context) {
        return new RootClassWriter(context);
    }


    /**
     * Write a root class factory class file.
     */
    public void writeClass() {
        try {

            var packageName = createPackageName();
            ImportBuilder imports = ImportBuilder.of(packageName.isBlank() ? API_PACKAGE_NAME : packageName);

            FileObject fo = context.getFiler().createSourceFile(imports.getSelfPackage() + ".Root_");

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                // write package
                if (!imports.getSelfPackage().isBlank()) {
                    pw.println("package " + packageName + ";");
                }
                pw.println();

                // write import
                imports.add("javax.annotation.processing.Generated");
                imports.add("java.util.function.BiFunction");
                imports.add("java.util.function.Function");
                imports.add("jakarta.persistence.criteria.CriteriaBuilder");
                imports.add("jakarta.persistence.criteria.CriteriaQuery");
                imports.add("jakarta.persistence.EntityManager");
                imports.add("jakarta.persistence.criteria.Root");
                if (context.isAddCriteria()) {
                    imports.add(API_PACKAGE_NAME + ".RootSource");
                }
                for (String metaName : modelClasses) {
                    imports.add(metaName.substring(0, metaName.lastIndexOf('_')));
                    imports.add(metaName + "Root_");
                }
                pw.println(imports.generateImports(context.isJakarta()));
                pw.println();

                // write class
                pw.println("@Generated(value = \"%s\")".formatted(JpaMetaModelEnhanceProcessor.class.getName()));
                pw.println("public abstract class Root_ {");
                pw.println();


                for (String metaName : modelClasses) {
                    var entityFqcn = metaName.substring(0, metaName.lastIndexOf('_'));
                    var entitySimpleName = entityFqcn.substring(entityFqcn.lastIndexOf('.') + 1);
                    if (context.isAddCriteria()) {
                        pw.println("""
                                    public static %1$s_Root_<%1$s> %2$s(Root<%1$s> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                                        return new %1$s_Root_<>(root, query, builder);
                                    }
                                    public static RootSource<%1$s, %1$s_Root_<%1$s>> %2$s() {
                                        return new RootSource<%1$s, %1$s_Root_<%1$s>>() {
                                            @Override public %1$s_Root_<%1$s> root(CriteriaQuery<?> query, CriteriaBuilder builder) {
                                                return new %1$s_Root_<%1$s>(query.from(rootClass()), query, builder);
                                            }
                                            @Override public Class<%1$s> rootClass() { return %1$s.class; }
                                        };
                                    }
                                """.formatted(
                                entitySimpleName,
                                uncapitalize(entitySimpleName)
                        ));
                    } else {
                        pw.println("""
                                public static %1$s_Root_<%1$s> %2$s(Root<%1$s> root) {
                                    return new %1$s_Root_<>(root);
                                }
                                public static %1$s_Root_<%1$s> %2$s(CriteriaQuery<?> query) {
                                    return %2$s(query.from(%1$s.class));
                                }
                            """.formatted(
                                entitySimpleName,
                                uncapitalize(entitySimpleName)
                        ));
                    }
                }

                pw.println("}");
                pw.flush();
            }

        } catch (Exception e) {
            context.logError("Problem opening file to write Root factory class : " + e.getMessage());
        }
    }


    /**
     * Get the generated package name.
     * @return the generated package name
     */
    private String createPackageName() {
        var name = modelClasses.stream()
            .reduce(modelClasses.get(0), RootClassWriter::getCommonPrefix);
        if (name.isBlank()) {
            name = modelClasses.stream().reduce("", (s1, s2) -> {
                if (s1.isBlank()) {
                    return s2;
                } else {
                    return (s1.split("\\.").length > s2.split("\\.").length) ? s2 : s1;
                }
            });
        }
        return name.substring(0, name.lastIndexOf('.'));
    }


    /**
     * Get the common prefix string for a given string.
     * @param s1 the string to be compared
     * @param s2 the string to be compared
     * @return the common prefix string
     */
    private static String getCommonPrefix(String s1, String s2) {
        var minLength = Math.min(s1.length(), s2.length());
        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return s1.substring(0, i);
            }
        }
        return s1.substring(0, minLength);
    }


    /**
     * Uncapitalize the given string.
     * @param str the given string
     * @return Capitalized string
     */
    protected static String uncapitalize(String str) {
        return (Objects.isNull(str) || str.isEmpty())
            ? str
            : str.substring(0, 1).toLowerCase() + str.substring(1);
    }

}
