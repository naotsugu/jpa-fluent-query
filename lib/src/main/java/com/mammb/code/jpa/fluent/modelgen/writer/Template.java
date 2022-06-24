package com.mammb.code.jpa.fluent.modelgen.writer;

import java.util.Map;

public class Template {

    private String string;

    public Template(String string) {
        this.string = string;
    }

    public static Template of(String string) {
        return new Template(string);
    }

    public Template bind(Map<String, String> map) {
        string = map.entrySet().stream()
            .reduce(string, (s, e) -> s.replace(e.getKey(), e.getValue()) , (s1, s2) -> s1 + s2);
        return this;
    }

    public Template bind(String key, String value) {
        string = string.replace(key, value);
        return this;
    }

    public Template bind(String k1, String v1, String k2, String v2) {
        bind(k1, v1);
        bind(k2, v2);
        return this;
    }

    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3) {
        bind(k1, v1, k2, v2);
        bind(k3, v3);
        return this;
    }

    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4) {
        bind(k1, v1, k2, v2, k3, v3);
        bind(k4, v4);
        return this;
    }

    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5) {
        bind(k1, v1, k2, v2, k3, v3, k4, v4);
        bind(k5, v5);
        return this;
    }

    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6) {
        bind(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
        bind(k6, v6);
        return this;
    }

    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7) {
        bind(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
        bind(k7, v7);
        return this;
    }

    public Template bind(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8) {
        bind(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
        bind(k8, v8);
        return this;
    }

    public String toString() {
        return string;
    }

    public String get() {
        return string.substring(firstCharIndexOf(string));
    }

    public String indentString(int tab) {
        var ret = string.indent(tab * 4);
        return ret.substring(firstCharIndexOf(ret));
    }

    private static int firstCharIndexOf(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

}
