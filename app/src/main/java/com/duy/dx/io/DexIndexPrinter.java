package com.duy.dx .io;

import com.duy.dex.ClassDef;
import com.duy.dex.Dex;
import com.duy.dex.FieldId;
import com.duy.dex.MethodId;
import com.duy.dex.ProtoId;
import com.duy.dex.TableOfContents;
import java.io.File;
import java.io.IOException;

/**
 * Executable that prints all indices of a dex file.
 */
public final class DexIndexPrinter {
    private final Dex dex;
    private final TableOfContents tableOfContents;

    public DexIndexPrinter(File file) throws IOException {
        this.dex = new Dex(file);
        this.tableOfContents = dex.getTableOfContents();
    }

    private void printMap() {
        for (TableOfContents.Section section : tableOfContents.sections) {
            if (section.off != -1) {
                System.out.println("section " + Integer.toHexString(section.type)
                        + " off=" + Integer.toHexString(section.off)
                        + " size=" + Integer.toHexString(section.size)
                        + " byteCount=" + Integer.toHexString(section.byteCount));
            }
        }
    }

    private void printStrings() throws IOException {
        int index = 0;
        for (String string : dex.strings()) {
            System.out.println("string " + index + ": " + string);
            index++;
        }
    }

    private void printTypeIds() throws IOException {
        int index = 0;
        for (Integer type : dex.typeIds()) {
            System.out.println("type " + index + ": " + dex.strings().get(type));
            index++;
        }
    }

    private void printProtoIds() throws IOException {
        int index = 0;
        for (ProtoId protoId : dex.protoIds()) {
            System.out.println("proto " + index + ": " + protoId);
            index++;
        }
    }

    private void printFieldIds() throws IOException {
        int index = 0;
        for (FieldId fieldId : dex.fieldIds()) {
            System.out.println("field " + index + ": " + fieldId);
            index++;
        }
    }

    private void printMethodIds() throws IOException {
        int index = 0;
        for (MethodId methodId : dex.methodIds()) {
            System.out.println("methodId " + index + ": " + methodId);
            index++;
        }
    }

    private void printTypeLists() throws IOException {
        if (tableOfContents.typeLists.off == -1) {
            System.out.println("No type lists");
            return;
        }
        Dex.Section in = dex.open(tableOfContents.typeLists.off);
        for (int i = 0; i < tableOfContents.typeLists.size; i++) {
            int size = in.readInt();
            System.out.print("Type list i=" + i + ", size=" + size + ", elements=");
            for (int t = 0; t < size; t++) {
                System.out.print(" " + dex.typeNames().get((int) in.readShort()));
            }
            if (size % 2 == 1) {
                in.readShort(); // retain alignment
            }
            System.out.println();
        }
    }

    private void printClassDefs() {
        int index = 0;
        for (ClassDef classDef : dex.classDefs()) {
            System.out.println("class def " + index + ": " + classDef);
            index++;
        }
    }

    public static void main(String[] args) throws IOException {
        DexIndexPrinter indexPrinter = new DexIndexPrinter(new File(args[0]));
        indexPrinter.printMap();
        indexPrinter.printStrings();
        indexPrinter.printTypeIds();
        indexPrinter.printProtoIds();
        indexPrinter.printFieldIds();
        indexPrinter.printMethodIds();
        indexPrinter.printTypeLists();
        indexPrinter.printClassDefs();
    }
}
