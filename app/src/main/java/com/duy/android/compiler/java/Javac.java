package com.duy.android.compiler.java;

import com.sun.tools.javac.Main;

import java.io.PrintWriter;

import javax.tools.DiagnosticListener;

/*
 * A class that executes Javac
 */
public class Javac {

/*
 * @param args the arguments to process javac with
 * @returns int the result code
 */
 
    public static int compile(String[] args) {
        return Main.compile(args);
    }

/*
 * @param args the arguments to process
 * @param writer the writer to use for logging
 * @returns int the result code
 */
    public static int compile(String[] args, PrintWriter writer) {
        return Main.compile(args, writer);
    }

/*
 * @param args the arguments to process
 * @param listener currently not working ?
 * @returns int the result code
 */
    public static int compile(String[] args, DiagnosticListener listener) {
        return Main.compile(args, listener);
    }

}
