package com.duy.ide.javaide.utils;

import java.io.File;



public class FileUtils {
    public static boolean hasExtension(File file, String... exts) {
        for (String ext : exts) {
            if (file.getPath().toLowerCase().endsWith(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    public static boolean canEdit(File file) {
        String[] exts = {
        ".java",
        ".xml",
        ".txt",
        ".gradle",
        ".json",
        ".kt",
        ".sh",
        ".md",
        ".yml",
        ".py",
        ".properties",
        ".php",
        ".pro",
        ".bat",
        ".rc",
        ".mtd",
        ".mtl",
        ".m3u",
        ".mf",
        ".sf",
        ".log",
        ".css",
        ".cfg",
        ".ini",
        ".conf",
        ".prop",
        ".htm",
        ".html",
        ".c",
        ".h",
        ".js",
        ".cc",
        ".go",
        ".cpp",
        ".hpp",
        ".lua",
        ".smali",
        ".jj",
        ".jjt",
        ".as",
        ".ada",
        ".adb",
        ".ads",
        ".ahk",
        ".build",
        ".g",
        ".ans",
        ".inp",
        ".h",
        ".mak",
        ".mac",
        ".applescript",
        ".asp",
        ".asa",
        ".aj",
        ".agc",
        ".aea",
        ".mar",
        ".mips",
        ".cmd",
        ".bsh",
        ".c",
        ".cfc",
        ".chl",
        ".mpol",
        ".il",
        ".coffee",
        ".cc",
        ".cpp",
        ".hh",
        ".hpp",
        ".cxx",
        ".cs",
        ".dart",
        ".dot",
        ".flex",
        ".jsp",
        ".icn",
        ".io",
        ".fx",
        ".l",
        ".makefile",
        ".pom",
        ".objc",
        ".r",
        ".rb",
        ".rbw",
        ".rbs",
        ".scala",
        ".bashrc",
        ".url",
        ".vb",
        ".yaml"
        };
        return file.canWrite() && hasExtension(file, exts);
    }

}
