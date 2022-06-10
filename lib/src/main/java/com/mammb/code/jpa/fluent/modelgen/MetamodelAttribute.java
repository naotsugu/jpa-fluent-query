package com.mammb.code.jpa.fluent.modelgen;

public interface MetamodelAttribute {

    /**
     * Get the attribute name.
     * e.g. {@code userName}, if you have the following code :
     * <pre>{@code
     *     public static volatile SingularAttribute<RootEntity, String> userName;
     * }</pre>
     * @return the attribute name
     */
    String getName();


    /**
     * Get the attribute type.
     * e.g. {@code jakarta.persistence.metamodel.SingularAttribute}
     * @return the attribute type
     */
    AttributeType getAttributeType();


    /**
     * Get the type containing the represented attribute.
     *
     * e.g. {@code RootEntity}, if you have the following code :
     * <pre>{@code
     *     public static volatile SingularAttribute<RootEntity, String> userName;
     * }</pre>
     *
     * @return The type containing the represented attribute
     */
    TypeArgument getEnclosingType();


    /**
     * Get the type of the key of the represented Map.
     *
     * e.g. {@code String}, if you have the following code :
     * <pre>{@code
     *     public static volatile MapAttribute<RootEntity, String, Child> map;
     * }</pre>
     *
     * @return The type of the key of the represented Map
     */
    TypeArgument getKeyType();


    /**
     * Get the type of the represented attribute.
     *
     * e.g. {@code String}, if you have the following code :
     * <pre>{@code
     *     public static volatile SingularAttribute<RootEntity, String> userName;
     * }</pre>
     *
     * @return The type of the represented attribute
     */
    TypeArgument getValueType();

}
