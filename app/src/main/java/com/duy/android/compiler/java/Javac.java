package com.duy.android.compiler.java;

import com.sun.tools.javac.Main;

import java.io.PrintWriter;

import javax.tools.DiagnosticListener;


public class Javac {

    public static int compile(String[] args) {
        return Main.compile(args);
    }

    public static int compile(String[] args, PrintWriter printWriter) {
        return Main.compile(args, printWriter);
    }

    public static int compile(String[] args, DiagnosticListener listener) {
        return Main.compile(args, listener);
    }

}
