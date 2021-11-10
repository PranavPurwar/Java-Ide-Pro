package com.duy.dx .command.grep;

import com.duy.dex.ClassData;
import com.duy.dex.ClassDef;
import com.duy.dex.Dex;
import com.duy.dex.EncodedValueReader;
import com.duy.dex.MethodId;
import com.duy.dx .io.CodeReader;
import com.duy.dx .io.instructions.DecodedInstruction;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class Grep {
    private final Dex dex;
    private final CodeReader codeReader = new CodeReader();
    private final Set<Integer> stringIds;

    private final PrintWriter out;
    private int count = 0;

    private ClassDef currentClass;
    private ClassData.Method currentMethod;

    public Grep(final Dex dex, Pattern pattern, final PrintWriter out) {
        this.dex = dex;
        this.out = out;

        stringIds = getStringIds(dex, pattern);

        codeReader.setStringVisitor(new CodeReader.Visitor() {
            public void visit(DecodedInstruction[] all, DecodedInstruction one) {
                encounterString(one.getIndex());
            }
        });
    }

    private void readArray(EncodedValueReader reader) {
        for (int i = 0, size = reader.readArray(); i < size; i++) {
            switch (reader.peek()) {
            case EncodedValueReader.ENCODED_STRING:
                encounterString(reader.readString());
                break;
            case EncodedValueReader.ENCODED_ARRAY:
                readArray(reader);
                break;
            }
        }
    }

    private void encounterString(int index) {
        if (stringIds.contains(index)) {
            out.println(location() + " " + dex.strings().get(index));
            count++;
        }
    }

    private String location() {
        String className = dex.typeNames().get(currentClass.getTypeIndex());
        if (currentMethod != null) {
            MethodId methodId = dex.methodIds().get(currentMethod.getMethodIndex());
            return className + "." + dex.strings().get(methodId.getNameIndex());
        } else {
            return className;
        }
    }

    /**
     * Prints usages to out. Returns the number of matches found.
     */
    public int grep() {
        for (ClassDef classDef : dex.classDefs()) {
            currentClass = classDef;
            currentMethod = null;

            if (classDef.getClassDataOffset() == 0) {
                continue;
            }

            ClassData classData = dex.readClassData(classDef);

            // find the strings in encoded constants
            int staticValuesOffset = classDef.getStaticValuesOffset();
            if (staticValuesOffset != 0) {
                readArray(new EncodedValueReader(dex.open(staticValuesOffset)));
            }

            // find the strings in method bodies
            for (ClassData.Method method : classData.allMethods()) {
                currentMethod = method;
                if (method.getCodeOffset() != 0) {
                    codeReader.visitAll(dex.readCode(method).getInstructions());
                }
            }
        }

        currentClass = null;
        currentMethod = null;
        return count;
    }

    private Set<Integer> getStringIds(Dex dex, Pattern pattern) {
        Set<Integer> stringIds = new HashSet<Integer>();
        int stringIndex = 0;
        for (String s : dex.strings()) {
            if (pattern.matcher(s).find()) {
                stringIds.add(stringIndex);
            }
            stringIndex++;
        }
        return stringIds;
    }
}
