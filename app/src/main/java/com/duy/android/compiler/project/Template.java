package com.duy.android.compiler.project;

import android.support.annotation.NonNull;

import java.lang.reflect.Modifier;

public class Template {
    public static final String MAIN_CLASS_TEMPLATE =
            "package %2$s;\n" +
                    "\n" +
                    "public class %1$s {\n\n" +
                    "  public static void main(String[] args) {\n" +
                    "   \n\tSystem.out.println(\"Hello world\");" +
                    "  }\n" +
                    "}\n";

    private static final String INTERFACE_TEMPLATE =
            "\n" +
                    "public interface %1$s {\n" +
                    "\n\t" +
                    "}\n";
    private static final String ENUM_TEMPLATE =
            "\n" +
                    "public enum %1$s {\n" +
                    "\n\t" +
                    "}\n";

    /**
     * %1 visibility
     * %2 modifier
     * %3 name
     * %4
     */
    private static final String JAVA_TEMPLATE =
            "%5$s\n" + "\n" +
                    "%1$s%2$s%3$s %4$s {\n" +
                    "  \n\t" +
                    "}\n";

    @NonNull
    public static String createClass(String packageName, String name) {
        return String.format(MAIN_CLASS_TEMPLATE, name, packageName);
    }

    @NonNull
    public static String createEnum(String name) {
        return String.format(ENUM_TEMPLATE, name);
    }

    @NonNull
    public static String createInterface(@NonNull String name) {
        return String.format(INTERFACE_TEMPLATE, name);
    }

    public static String createJava(String zpackage, String name, int kind, int visibility, int modifier, boolean b) {
        String m = "";
        switch (modifier) {
            case Modifier.ABSTRACT:
                m = "abstract ";
                break;
            case Modifier.FINAL:
                m = "final ";
                break;
        }
        String v = "";
        switch (visibility) {
            case Modifier.PUBLIC:
                v = "public ";
                break;
        }
        String k = "class";
        switch (kind) {
            case 0:
                k = "class";
                break;
            case 1:
                k = "interface";
                break;
            case 2:
                k = "enum";
                break;
        }
        zpackage = zpackage.isEmpty() ? "" : "package " + zpackage + ";";

        return String.format(JAVA_TEMPLATE, v, m, k, name, zpackage);
    }

}
