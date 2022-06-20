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
package com.mammb.code.jpa.fluent.modelgen.writer;

import com.mammb.code.jpa.fluent.modelgen.Context;
import com.mammb.code.jpa.fluent.modelgen.JpaMetaModelEnhanceProcessor;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelAttribute;
import com.mammb.code.jpa.fluent.modelgen.model.StaticMetamodelEntity;

import javax.annotation.processing.FilerException;
import javax.tools.FileObject;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * Helper to write the actual enhanced metamodel class
 * using the {@link javax.annotation.processing.Filer} API.
 *
 * @author Naotsugu Kobayashi
 */
public class CriteriaModelClassWriter {

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
    protected CriteriaModelClassWriter(Context context, StaticMetamodelEntity entity) {
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
    public static CriteriaModelClassWriter of(Context context, StaticMetamodelEntity entity) {
        return new CriteriaModelClassWriter(context, entity);
    }


    /**
     * Write a generated class file.
     */
    public void writeFile() {

        context.logDebug("Create meta model : {}", entity.getQualifiedName());

        try {

            String body = generateBody().toString();

            FileObject fo = context.getFiler().createSourceFile(
                entity.getTargetEntityQualifiedName() + "Root_", entity.getElement());

            try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {

                if (!imports.getSelfPackage().isEmpty()) {
                    pw.println("package " + imports.getSelfPackage() + ";");
                    pw.println();
                }

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

                pw.println(body);
                pw.flush();
            }

        } catch (FilerException e) {
            context.logError("Problem with Filer: {}", e.getMessage());
        } catch (Exception e) {
            context.logError("Problem opening file to write MetaModel for {} : {}", entity.getSimpleName(), e.getMessage());
        }
    }


    /**
     * Generate the body of class.
     * @return the body of class
     */
    protected StringBuilder generateBody() {
        StringBuilder sb = new StringBuilder();
        sb.append(generateClassDeclaration()).append(System.lineSeparator());
        return sb;
    }


    /**
     * Generate the class declaration.
     * @return the class declaration
     */
    protected String generateClassDeclaration() {

        StringBuilder sb = new StringBuilder();

        if (entity.getSuperClass().isBlank()) {
            sb.append("""
                @SuppressWarnings("unchecked")
                @Generated(value = "%1$s")
                public class %2$sRoot_<T extends %2$s> implements RootAware<T>%4$s {

                    protected final Root<T> root;
                    protected final CriteriaQuery<?> query;
                    protected final CriteriaBuilder builder;

                    public %2$sRoot_(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                        this.root = root;
                        this.query = query;
                        this.builder = builder;
                    }

                    public %2$sRoot_(Root<T> root) { this(root, null, null); }

                    @Override
                    public Root<T> get() { return root; }

                    public CriteriaBuilder builder() { return builder; }

                """.formatted(
                JpaMetaModelEnhanceProcessor.class.getName(),      // %1$s
                entity.getTargetEntityName(),                      // %2$s
                imports.add(entity.getSuperEntityQualifiedName()), // %3$s
                criteriaRootImplements(entity, true)               // %4$s
            ));
        } else {
            sb.append("""
                @SuppressWarnings("unchecked")
                @Generated(value = "%1$s")
                public class %2$sRoot_<T extends %2$s> extends %3$sRoot_<T>%4$s {

                    @Override
                    public Root<T> get() {
                        return root;
                    }

                    public %2$sRoot_(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                        super(root, query, builder);
                    }

                    public %2$sRoot_(Root<T> root) {
                        super(root, null, null);
                    }

                """.formatted(
                JpaMetaModelEnhanceProcessor.class.getName(),      // %1$s
                entity.getTargetEntityName(),                      // %2$s
                imports.add(entity.getSuperEntityQualifiedName()), // %3$s
                criteriaRootImplements(entity, false)              // %4$s
            ));
        }

        entity.getAttributes().forEach(attr -> sb.append(generateRootMethod(attr)));

        sb.append('\n');
        sb.append(generateJoinClass()).append('\n');
        sb.append(generatePathClass()).append('\n');

        sb.append("}");
        return sb.toString();
    }


    /**
     * Generate root methods.
     * @param attribute the static metamodel attribute
     * @return root methods
     */
    protected String generateRootMethod(StaticMetamodelAttribute attribute) {
        StringBuilder sb = new StringBuilder();
        if (attribute.getAttributeType().isList()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                    public %3$sRoot_.Join_<%3$s> join%4$s() {
                        return new %3$sRoot_.Join_<>(query, builder) {
                            @Override
                            public ListJoin<? extends %1$s, %3$s> get() {
                                return %1$sRoot_.this.get().join(%1$s_.%5$s);
                            }
                        };
                    }

                """, imports));
            }
            sb.append(attribute.bindTo("""
                public Criteria.CollectionExp<%3$s, List<%3$s>, Expression<List<%3$s>>> get%4$s() {
                    return new Criteria.CollectionExp(() -> ((Root<%1$s>) %1$sRoot_.this.get()).get(%1$s_.%5$s), builder);
                }

            """, imports));
        } else if (attribute.getAttributeType().isSet()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                    public %3$sRoot_.Join_<%3$s> join%4$s() {
                        return new %3$sRoot_.Join_<>(query, builder) {
                            @Override
                            public SetJoin<? extends %1$s, %3$s> get() {
                                return %1$sRoot_.this.get().join(%1$s_.%5$s);
                            }
                        };
                    }

                """, imports));
            }
            sb.append(attribute.bindTo("""
                public Criteria.CollectionExp<%3$s, Set<%3$s>, Expression<Set<%3$s>>> get%4$s() {
                    return new Criteria.CollectionExp(() -> ((Root<%1$s>) %1$sRoot_.this.get()).get(%1$s_.%5$s), builder);
                }

            """, imports));
        } else if (attribute.getAttributeType().isCollection()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                    public %3$sRoot_.Join_<%3$s> join%4$s() {
                        return new %3$sRoot_.Join_<>(query, builder) {
                            @Override
                            public CollectionJoin<? extends %1$s, %3$s> get() {
                                return %1$sRoot_.this.get().join(%1$s_.%5$s);
                            }
                        };
                    }

                """, imports));
            }
            sb.append(attribute.bindTo("""
                public Criteria.CollectionExp<%3$s, Collection<%3$s>, Expression<Collection<%3$s>>> get%4$s() {
                    return new Criteria.CollectionExp(() -> ((Root<%1$s>) %1$sRoot_.this.get()).get(%1$s_.%5$s), builder);
                }

            """, imports));
        } else if (attribute.getAttributeType().isSingular()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                    public %3$sRoot_.Join_<%3$s> join%4$s() {
                        return new %3$sRoot_.Join_<>(query, builder) {
                            @Override
                            public Join<? extends %1$s, %3$s> get() {
                                return %1$sRoot_.this.get().join(%1$s_.%5$s);
                            }
                        };
                    }

                    public %3$sRoot_.Path_<%3$s> get%4$s() {
                        return new %3$sRoot_.Path_<>(query, builder) {
                            @Override
                            public Path<%3$s> get() {
                                return %1$sRoot_.this.get().get(%1$s_.%5$s);
                            }
                        };
                    }

                """, imports));
            } else {
                sb.append(attribute.bindTo("""
                        public PATH_CLASSNAME get%4$s() {
                            return new PATH_CLASSNAME(() -> %1$sRoot_.this.get().get(%1$s_.%5$s), builder);
                        }

                    """, imports).replace("PATH_CLASSNAME", criteriaPathClassName(attribute)));
            }
        } else if (attribute.getAttributeType().isMap()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                    public %3$sRoot_.Join_<%3$s> join%4$s() {
                        return new %3$sRoot_.Join_<>(query, builder) {
                            @Override
                            public MapJoin<? extends %1$s, %2$s, %3$s> get() {
                                return %1$sRoot_.this.get().join(%1$s_.%5$s);
                            }
                        };
                    }

                """, imports));
            }
            sb.append(attribute.bindTo("""
                public Expression<Map<%2$s, %3$s>> get%4$s() {
                    return ((Root<%1$s>) %1$sRoot_.this.get()).get(%1$s_.%5$s);
                }

            """, imports));
        }
        return sb.toString();
    }


    /**
     * Generate the join class.
     * @return generated join class
     */
    protected String generateJoinClass() {

        StringBuilder sb = new StringBuilder();

        if (entity.getSuperClass().isBlank()) {
            sb.append("""
                        public static abstract class Join_<T extends %1$s> implements Supplier<Join<?, T>>%3$s {

                            protected final CriteriaQuery<?> query;
                            protected final CriteriaBuilder builder;

                            public Join_(CriteriaQuery<?> query, CriteriaBuilder builder) {
                                this.query = query;
                                this.builder = builder;
                            }

                            @Override
                            public abstract Join<?, T> get();

                            public CriteriaBuilder builder() { return builder; }

                    """.formatted(
                entity.getTargetEntityName(),                      // %1$s
                imports.add(entity.getSuperEntityQualifiedName()), // %2$s
                criteriaJoinImplements(entity, true)               // %3$s
            ));
        } else {
            sb.append("""
                    public static abstract class Join_<T extends %1$s> extends %2$sRoot_.Join_<T>%3$s {

                        public Join_(CriteriaQuery<?> query, CriteriaBuilder builder) {
                            super(query, builder);
                        }

                        @Override
                        public abstract Join<?, T> get();

                """.formatted(
                entity.getTargetEntityName(),                      // %1$s
                imports.add(entity.getSuperEntityQualifiedName()), // %2$s
                criteriaJoinImplements(entity, false)              // %3$s
            ));
        }
        entity.getAttributes().forEach(attr -> sb.append(generateJoinMethod(attr)));

        sb.append("    }").append(System.lineSeparator());
        return sb.toString();
    }


    /**
     * Generate join methods
     * @param attribute attribute
     * @return Generated join methods
     */
    protected String generateJoinMethod(StaticMetamodelAttribute attribute) {

        StringBuilder sb = new StringBuilder();

        if (attribute.getAttributeType().isList()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                        public %3$sRoot_.Join_<%3$s> join%4$s() {
                            return new %3$sRoot_.Join_<>(query, builder) {
                                @Override
                                public ListJoin<T, %3$s> get() {
                                    return %1$sRoot_.Join_.this.get().join(%1$s_.%5$s);
                                }
                            };
                        }

                """, imports));
            } else {
                sb.append(attribute.bindTo("""
                        public ListJoin<T, %3$s> join%4$s() {
                            return get().join(%1$s_.%5$s);
                        }

                """, imports));
            }
            sb.append(attribute.bindTo("""
                    public Criteria.CollectionExp<%3$s, List<%3$s>, Expression<List<%3$s>>> get%4$s() {
                        return new Criteria.CollectionExp(() -> ((Join<?, %1$s>) get()).get(%1$s_.%5$s), builder);
                    }

            """, imports));
        } else if (attribute.getAttributeType().isSet()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                        public %3$sRoot_.Join_<%3$s> join%4$s() {
                            return new %3$sRoot_.Join_<>(query, builder) {
                                @Override
                                public SetJoin<T, %3$s> get() {
                                    return %1$sRoot_.Join_.this.get().join(%1$s_.%5$s);
                                }
                            };
                        }

                """, imports));
            } else {
                sb.append(attribute.bindTo("""
                        public SetJoin<T, %3$s> join%4$s() {
                            return get().join(%1$s_.%5$s);
                        }

                """, imports));
            }
            sb.append(attribute.bindTo("""
                    public Criteria.CollectionExp<%3$s, Set<%3$s>, Expression<Set<%3$s>>> get%4$s() {
                        return new Criteria.CollectionExp(() -> ((Join<?, %1$s>) get()).get(%1$s_.%5$s), builder);
                    }

            """, imports));
        } else if (attribute.getAttributeType().isCollection()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                        public %3$sRoot_.Join_<%3$s> join%4$s() {
                            return new %3$sRoot_.Join_<>(query, builder) {
                                @Override
                                public CollectionJoin<T, %3$s> get() {
                                    return %1$sRoot_.Join_.this.get().join(%1$s_.%5$s);
                                }
                            };
                        }

                """, imports));
            } else {
                sb.append(attribute.bindTo("""
                        public CollectionJoin<T, %3$s> join%4$s() {
                            return get().join(%1$s_.%5$s);
                        }

                """, imports));
            }
            sb.append(attribute.bindTo("""
                    public Criteria.CollectionExp<%3$s, Collection<%3$s>, Expression<Collection<%3$s>>> get%4$s() {
                        return Criteria.CollectionExp(() -> ((Join<?, %1$s>) get()).get(%1$s_.%5$s), builder);
                    }

            """, imports));
        } else if (attribute.getAttributeType().isSingular()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                        public %3$sRoot_.Join_<%3$s> join%4$s() {
                            return new %3$sRoot_.Join_<>(query, builder) {
                                @Override
                                public Join<T, %3$s> get() {
                                    return %1$sRoot_.Join_.this.get().join(%1$s_.%5$s);
                                }
                            };
                        }

                        public %3$sRoot_.Path_<%3$s> get%4$s() {
                            return new %3$sRoot_.Path_<>(query, builder) {
                                @Override
                                public Path<%3$s> get() {
                                    return ((Join<?, %1$s>) %1$sRoot_.Join_.this.get()).get(%1$s_.%5$s);
                                }
                            };
                        }

                """, imports));
            } else {
                sb.append(attribute.bindTo("""
                        public PATH_CLASSNAME get%4$s() {
                            return new PATH_CLASSNAME(() -> ((Join<?, %1$s>) get()).get(%1$s_.%5$s), builder);
                        }

                """, imports).replace("PATH_CLASSNAME", criteriaPathClassName(attribute)));
            }
        } else if (attribute.getAttributeType().isMap()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                        public %3$sRoot_.Join_<%3$s> join%4$s() {
                            return new %3$sRoot_.Join_<>(query, builder) {
                                @Override
                                public MapJoin<T, %2$s, %3$s> get() {
                                    return %1$sRoot_.Join_.this.get().join(%1$s_.%5$s);
                                }
                            };
                        }

                """, imports));
            } else {
                sb.append(attribute.bindTo("""
                        public MapJoin<T, %2$s, %3$s> join%4$s() {
                            return get().join(%1$s_.%5$s);
                        }

                """, imports));
            }
            sb.append(attribute.bindTo("""
                    public Expression<Map<%2$s, %3$s>> get%4$s() {
                        return ((Join<?, %1$s>) get()).get(%1$s_.%5$s);
                    }

            """, imports));
        }
        return sb.toString();
    }


    /**
     * Generate path class.
     * @return Generated path class
     */
    protected String generatePathClass() {

        StringBuilder sb = new StringBuilder();

        if (entity.getSuperClass().isBlank()) {
            sb.append("""
                    public static abstract class Path_<T extends %1$s> implements Supplier<Path<T>>%3$s {

                        protected final CriteriaQuery<?> query;
                        protected final CriteriaBuilder builder;

                        public Path_(CriteriaQuery<?> query, CriteriaBuilder builder) {
                            this.query = query;
                            this.builder = builder;
                        }

                        @Override
                        public abstract Path<T> get();

                        public CriteriaBuilder builder() { return builder; }

                """.formatted(
                entity.getTargetEntityName(),                      // %1$s
                imports.add(entity.getSuperEntityQualifiedName()), // %2$s
                criteriaPathImplements(entity, true)               // %3$s
            ));
        } else {
            sb.append("""
                    public static abstract class Path_<T extends %1$s> extends %2$sRoot_.Path_<T>%3$s {

                        public Path_(CriteriaQuery<?> query, CriteriaBuilder builder) {
                            super(query, builder);
                        }

                        @Override
                        public abstract Path<T> get();

                """.formatted(
                entity.getTargetEntityName(),                      // %1$s
                imports.add(entity.getSuperEntityQualifiedName()), // %2$s
                criteriaPathImplements(entity, false)              // %3$s
            ));
        }
        entity.getAttributes().forEach(attr -> sb.append(generatePathMethod(attr)));

        sb.append("    }").append('\n');
        return sb.toString();
    }


    /**
     * Generate path methods.
     * @param attribute attribute
     * @return Generated path methods
     */
    protected String generatePathMethod(StaticMetamodelAttribute attribute) {

        StringBuilder sb = new StringBuilder();

        if (attribute.getAttributeType().isList()) {
            sb.append(attribute.bindTo("""
                    public Criteria.CollectionExp<%3$s, List<%3$s>, Expression<List<%3$s>>> get%4$s() {
                        return new Criteria.CollectionExp(() -> ((Path<%1$s>) get()).get(%1$s_.%5$s), builder);
                    }

            """, imports));
        } else if (attribute.getAttributeType().isSet()) {
            sb.append(attribute.bindTo("""
                    public Criteria.CollectionExp<%3$s, Set<%3$s>, Expression<Set<%3$s>>> get%4$s() {
                        return new Criteria.CollectionExp(() -> ((Path<%1$s>) get()).get(%1$s_.%5$s), builder);
                    }

            """, imports));
        } else if (attribute.getAttributeType().isCollection()) {
            sb.append(attribute.bindTo("""
                    public Criteria.CollectionExp<%3$s, Collection<%3$s>, Expression<Collection<%3$s>>> get%4$s() {
                        return Criteria.CollectionExp(() -> ((Path<%1$s>) get()).get(%1$s_.%5$s), builder);
                    }

            """, imports));
        } else if (attribute.getAttributeType().isSingular()) {
            if (attribute.getValueType().isStruct()) {
                sb.append(attribute.bindTo("""
                        public %3$sRoot_.Path_<%3$s> get%4$s() {
                            return new %3$sRoot_.Path_<>(query, builder) {
                                @Override
                                public Path<%3$s> get() {
                                    return %1$sRoot_.Path_.this.get().get(%1$s_.%5$s);
                                }
                            };
                        }

                """, imports));
            } else {
                sb.append(attribute.bindTo("""
                        public PATH_CLASSNAME get%4$s() {
                            return new PATH_CLASSNAME(() -> ((Path<%1$s>) get()).get(%1$s_.%5$s), builder);
                        }

                """, imports).replace("PATH_CLASSNAME", criteriaPathClassName(attribute)));
            }
        } else if (attribute.getAttributeType().isMap()) {
            sb.append(attribute.bindTo("""
                    public Expression<Map<%2$s, %3$s>> get%4$s() {
                        return ((Path<%1$s>) get()).get(%1$s_.%5$s);
                    }

            """, imports));
        }
        return sb.toString();
    }


    /**
     * Get the criteria implements clause.
     * @param entity the static metamodel entity
     * @param append append implements?
     * @return the criteria implements clause
     */
    protected String criteriaRootImplements(StaticMetamodelEntity entity, boolean append) {
        if (entity.getTargetEntity().isComparable()) {
            return (append ? ", " : " implements ") + "Criteria.ComparableExpression<T, Root<T>>";
        } else {
            return (append ? ", " : " implements ") + "Criteria.AnyExpression<T, Root<T>>";
        }
    }


    /**
     * Get the criteria implements clause.
     * @param entity the static metamodel entity
     * @param append append implements?
     * @return the criteria implements clause
     */
    protected String criteriaJoinImplements(StaticMetamodelEntity entity, boolean append) {
        return criteriaRootImplements(entity, append).replace("Root<T>", "Join<?, T>");
    }


    /**
     * Get the criteria implements clause.
     * @param entity the static metamodel entity
     * @param append append implements?
     * @return the criteria implements clause
     */
    protected String criteriaPathImplements(StaticMetamodelEntity entity, boolean append) {
        return criteriaRootImplements(entity, append).replace("Root", "Path");
    }


    /**
     * Get the criteria path class name from given attribute.
     * @param attribute the attribute
     * @return the criteria path class name
     */
    protected String criteriaPathClassName(StaticMetamodelAttribute attribute) {
        if (attribute.getValueType().isString()) {
            return "Criteria.StringPath";
        } else if (attribute.getValueType().isBoolean()) {
            return "Criteria.BooleanPath";
        } else if (attribute.getValueType().isNumber()) {
            return "Criteria.NumberPath";
        } else if (attribute.getValueType().isComparable()) {
            return "Criteria.ComparablePath<" + imports.add(attribute.getValueType().getName()) + ">";
        } else {
            return "Criteria.AnyPath<" + imports.add(attribute.getValueType().getName()) + ">";
        }
    }

}