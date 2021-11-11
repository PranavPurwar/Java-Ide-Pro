/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.dx.dex.file;

import com.duy.dex.EncodedValueCodec;
import com.duy.dx.rop.annotation.Annotation;
import com.duy.dx.rop.annotation.NameValuePair;
import com.duy.dx.rop.cst.Constant;
import com.duy.dx.rop.cst.CstAnnotation;
import com.duy.dx.rop.cst.CstArray;
import com.duy.dx.rop.cst.CstBoolean;
import com.duy.dx.rop.cst.CstByte;
import com.duy.dx.rop.cst.CstChar;
import com.duy.dx.rop.cst.CstDouble;
import com.duy.dx.rop.cst.CstEnumRef;
import com.duy.dx.rop.cst.CstFieldRef;
import com.duy.dx.rop.cst.CstFloat;
import com.duy.dx.rop.cst.CstInteger;
import com.duy.dx.rop.cst.CstKnownNull;
import com.duy.dx.rop.cst.CstLiteralBits;
import com.duy.dx.rop.cst.CstLong;
import com.duy.dx.rop.cst.CstMethodHandle;
import com.duy.dx.rop.cst.CstMethodRef;
import com.duy.dx.rop.cst.CstProtoRef;
import com.duy.dx.rop.cst.CstShort;
import com.duy.dx.rop.cst.CstString;
import com.duy.dx.rop.cst.CstType;
import com.duy.dx.util.AnnotatedOutput;
import com.duy.dx.util.Hex;

import java.util.Collection;

/**
 * Handler for writing out {@code encoded_values} and parts
 * thereof.
 */
public final class ValueEncoder {
    /** annotation value type constant: {@code byte} */
    private static final int VALUE_BYTE = 0x00;

    /** annotation value type constant: {@code short} */
    private static final int VALUE_SHORT = 0x02;

    /** annotation value type constant: {@code char} */
    private static final int VALUE_CHAR = 0x03;

    /** annotation value type constant: {@code int} */
    private static final int VALUE_INT = 0x04;

    /** annotation value type constant: {@code long} */
    private static final int VALUE_LONG = 0x06;

    /** annotation value type constant: {@code float} */
    private static final int VALUE_FLOAT = 0x10;

    /** annotation value type constant: {@code double} */
    private static final int VALUE_DOUBLE = 0x11;

    /** annotation value type constant: {@code method type} */
    private static final int VALUE_METHOD_TYPE = 0x15;

    /** annotation value type constant: {@code method handle} */
    private static final int VALUE_METHOD_HANDLE = 0x16;

    /** annotation value type constant: {@code string} */
    private static final int VALUE_STRING = 0x17;

    /** annotation value type constant: {@code type} */
    private static final int VALUE_TYPE = 0x18;

    /** annotation value type constant: {@code field} */
    private static final int VALUE_FIELD = 0x19;

    /** annotation value type constant: {@code method} */
    private static final int VALUE_METHOD = 0x1a;

    /** annotation value type constant: {@code enum} */
    private static final int VALUE_ENUM = 0x1b;

    /** annotation value type constant: {@code array} */
    private static final int VALUE_ARRAY = 0x1c;

    /** annotation value type constant: {@code annotation} */
    private static final int VALUE_ANNOTATION = 0x1d;

    /** annotation value type constant: {@code null} */
    private static final int VALUE_NULL = 0x1e;

    /** annotation value type constant: {@code boolean} */
    private static final int VALUE_BOOLEAN = 0x1f;

    /** {@code non-null;} file being written */
    private final DexFile file;

    /** {@code non-null;} output stream to write to */
    private final com.duy.dx.util.AnnotatedOutput out;

    /**
     * Construct an instance.
     *
     * @param file {@code non-null;} file being written
     * @param out {@code non-null;} output stream to write to
     */
    public ValueEncoder(DexFile file, AnnotatedOutput out) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (out == null) {
            throw new NullPointerException("out == null");
        }

        this.file = file;
        this.out = out;
    }

    /**
     * Writes out the encoded form of the given constant.
     *
     * @param cst {@code non-null;} the constant to write
     */
    public void writeConstant(com.duy.dx.rop.cst.Constant cst) {
        int type = constantToValueType(cst);
        int arg;

        switch (type) {
            case VALUE_BYTE:
            case VALUE_SHORT:
            case VALUE_INT:
            case VALUE_LONG: {
                long value = ((com.duy.dx.rop.cst.CstLiteralBits) cst).getLongBits();
                EncodedValueCodec.writeSignedIntegralValue(out, type, value);
                break;
            }
            case VALUE_CHAR: {
                long value = ((CstLiteralBits) cst).getLongBits();
                EncodedValueCodec.writeUnsignedIntegralValue(out, type, value);
                break;
            }
            case VALUE_FLOAT: {
                // Shift value left 32 so that right-zero-extension works.
                long value = ((com.duy.dx.rop.cst.CstFloat) cst).getLongBits() << 32;
                EncodedValueCodec.writeRightZeroExtendedValue(out, type, value);
                break;
            }
            case VALUE_DOUBLE: {
                long value = ((com.duy.dx.rop.cst.CstDouble) cst).getLongBits();
                EncodedValueCodec.writeRightZeroExtendedValue(out, type, value);
                break;
            }
            case VALUE_METHOD_TYPE: {
                int index = file.getProtoIds().indexOf(((com.duy.dx.rop.cst.CstProtoRef) cst).getPrototype());
                EncodedValueCodec.writeUnsignedIntegralValue(out, type, (long) index);
                break;
            }
            case VALUE_METHOD_HANDLE: {
                int index = file.getMethodHandles().indexOf((com.duy.dx.rop.cst.CstMethodHandle) cst);
                EncodedValueCodec.writeUnsignedIntegralValue(out, type, (long) index);
                break;
            }
            case VALUE_STRING: {
                int index = file.getStringIds().indexOf((com.duy.dx.rop.cst.CstString) cst);
                EncodedValueCodec.writeUnsignedIntegralValue(out, type, (long) index);
                break;
            }
            case VALUE_TYPE: {
                int index = file.getTypeIds().indexOf((com.duy.dx.rop.cst.CstType) cst);
                EncodedValueCodec.writeUnsignedIntegralValue(out, type, (long) index);
                break;
            }
            case VALUE_FIELD: {
                int index = file.getFieldIds().indexOf((com.duy.dx.rop.cst.CstFieldRef) cst);
                EncodedValueCodec.writeUnsignedIntegralValue(out, type, (long) index);
                break;
            }
            case VALUE_METHOD: {
                int index = file.getMethodIds().indexOf((com.duy.dx.rop.cst.CstMethodRef) cst);
                EncodedValueCodec.writeUnsignedIntegralValue(out, type, (long) index);
                break;
            }
            case VALUE_ENUM: {
                com.duy.dx.rop.cst.CstFieldRef fieldRef = ((com.duy.dx.rop.cst.CstEnumRef) cst).getFieldRef();
                int index = file.getFieldIds().indexOf(fieldRef);
                EncodedValueCodec.writeUnsignedIntegralValue(out, type, (long) index);
                break;
            }
            case VALUE_ARRAY: {
                out.writeByte(type);
                writeArray((com.duy.dx.rop.cst.CstArray) cst, false);
                break;
            }
            case VALUE_ANNOTATION: {
                out.writeByte(type);
                writeAnnotation(((com.duy.dx.rop.cst.CstAnnotation) cst).getAnnotation(),
                        false);
                break;
            }
            case VALUE_NULL: {
                out.writeByte(type);
                break;
            }
            case VALUE_BOOLEAN: {
                int value = ((com.duy.dx.rop.cst.CstBoolean) cst).getIntBits();
                out.writeByte(type | (value << 5));
                break;
            }
            default: {
                throw new RuntimeException("Shouldn't happen");
            }
        }
    }

    /**
     * Gets the value type for the given constant.
     *
     * @param cst {@code non-null;} the constant
     * @return the value type; one of the {@code VALUE_*} constants
     * defined by this class
     */
    private static int constantToValueType(com.duy.dx.rop.cst.Constant cst) {
        /*
         * TODO: Constant should probable have an associated enum, so this
         * can be a switch().
         */
        if (cst instanceof CstByte) {
            return VALUE_BYTE;
        } else if (cst instanceof CstShort) {
            return VALUE_SHORT;
        } else if (cst instanceof CstChar) {
            return VALUE_CHAR;
        } else if (cst instanceof CstInteger) {
            return VALUE_INT;
        } else if (cst instanceof CstLong) {
            return VALUE_LONG;
        } else if (cst instanceof CstFloat) {
            return VALUE_FLOAT;
        } else if (cst instanceof CstDouble) {
            return VALUE_DOUBLE;
        } else if (cst instanceof CstProtoRef) {
            return VALUE_METHOD_TYPE;
        } else if (cst instanceof CstMethodHandle) {
           return VALUE_METHOD_HANDLE;
        } else if (cst instanceof com.duy.dx.rop.cst.CstString) {
            return VALUE_STRING;
        } else if (cst instanceof com.duy.dx.rop.cst.CstType) {
            return VALUE_TYPE;
        } else if (cst instanceof CstFieldRef) {
            return VALUE_FIELD;
        } else if (cst instanceof CstMethodRef) {
            return VALUE_METHOD;
        } else if (cst instanceof CstEnumRef) {
            return VALUE_ENUM;
        } else if (cst instanceof com.duy.dx.rop.cst.CstArray) {
            return VALUE_ARRAY;
        } else if (cst instanceof com.duy.dx.rop.cst.CstAnnotation) {
            return VALUE_ANNOTATION;
        } else if (cst instanceof CstKnownNull) {
            return VALUE_NULL;
        } else if (cst instanceof CstBoolean) {
            return VALUE_BOOLEAN;
        } else {
            throw new RuntimeException("Shouldn't happen");
        }
    }

    /**
     * Writes out the encoded form of the given array, that is, as
     * an {@code encoded_array} and not including a
     * {@code value_type} prefix. If the output stream keeps
     * (debugging) annotations and {@code topLevel} is
     * {@code true}, then this method will write (debugging)
     * annotations.
     *
     * @param array {@code non-null;} array instance to write
     * @param topLevel {@code true} iff the given annotation is the
     * top-level annotation or {@code false} if it is a sub-annotation
     * of some other annotation
     */
    public void writeArray(com.duy.dx.rop.cst.CstArray array, boolean topLevel) {
        boolean annotates = topLevel && out.annotates();
        com.duy.dx.rop.cst.CstArray.List list = ((com.duy.dx.rop.cst.CstArray) array).getList();
        int size = list.size();

        if (annotates) {
            out.annotate("  size: " + com.duy.dx.util.Hex.u4(size));
        }

        out.writeUleb128(size);

        for (int i = 0; i < size; i++) {
            com.duy.dx.rop.cst.Constant cst = list.get(i);
            if (annotates) {
                out.annotate("  [" + Integer.toHexString(i) + "] " +
                        constantToHuman(cst));
            }
            writeConstant(cst);
        }

        if (annotates) {
            out.endAnnotation();
        }
    }

    /**
     * Writes out the encoded form of the given annotation, that is,
     * as an {@code encoded_annotation} and not including a
     * {@code value_type} prefix. If the output stream keeps
     * (debugging) annotations and {@code topLevel} is
     * {@code true}, then this method will write (debugging)
     * annotations.
     *
     * @param annotation {@code non-null;} annotation instance to write
     * @param topLevel {@code true} iff the given annotation is the
     * top-level annotation or {@code false} if it is a sub-annotation
     * of some other annotation
     */
    public void writeAnnotation(com.duy.dx.rop.annotation.Annotation annotation, boolean topLevel) {
        boolean annotates = topLevel && out.annotates();
        com.duy.dx.dex.file.StringIdsSection stringIds = file.getStringIds();
        TypeIdsSection typeIds = file.getTypeIds();

        CstType type = annotation.getType();
        int typeIdx = typeIds.indexOf(type);

        if (annotates) {
            out.annotate("  type_idx: " + com.duy.dx.util.Hex.u4(typeIdx) + " // " +
                    type.toHuman());
        }

        out.writeUleb128(typeIds.indexOf(annotation.getType()));

        Collection<com.duy.dx.rop.annotation.NameValuePair> pairs = annotation.getNameValuePairs();
        int size = pairs.size();

        if (annotates) {
            out.annotate("  size: " + com.duy.dx.util.Hex.u4(size));
        }

        out.writeUleb128(size);

        int at = 0;
        for (com.duy.dx.rop.annotation.NameValuePair pair : pairs) {
            CstString name = pair.getName();
            int nameIdx = stringIds.indexOf(name);
            com.duy.dx.rop.cst.Constant value = pair.getValue();

            if (annotates) {
                out.annotate(0, "  elements[" + at + "]:");
                at++;
                out.annotate("    name_idx: " + Hex.u4(nameIdx) + " // " +
                        name.toHuman());
            }

            out.writeUleb128(nameIdx);

            if (annotates) {
                out.annotate("    value: " + constantToHuman(value));
            }

            writeConstant(value);
        }

        if (annotates) {
            out.endAnnotation();
        }
    }

    /**
     * Gets the colloquial type name and human form of the type of the
     * given constant, when used as an encoded value.
     *
     * @param cst {@code non-null;} the constant
     * @return {@code non-null;} its type name and human form
     */
    public static String constantToHuman(com.duy.dx.rop.cst.Constant cst) {
        int type = constantToValueType(cst);

        if (type == VALUE_NULL) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(cst.typeName());
        sb.append(' ');
        sb.append(cst.toHuman());

        return sb.toString();
    }

    /**
     * Helper for {@code addContents()} methods, which adds
     * contents for a particular {@link com.duy.dx.rop.annotation.Annotation}, calling itself
     * recursively should it encounter a nested annotation.
     *
     * @param file {@code non-null;} the file to add to
     * @param annotation {@code non-null;} the annotation to add contents for
     */
    public static void addContents(DexFile file, com.duy.dx.rop.annotation.Annotation annotation) {
        TypeIdsSection typeIds = file.getTypeIds();
        StringIdsSection stringIds = file.getStringIds();

        typeIds.intern(annotation.getType());

        for (NameValuePair pair : annotation.getNameValuePairs()) {
            stringIds.intern(pair.getName());
            addContents(file, pair.getValue());
        }
    }

    /**
     * Helper for {@code addContents()} methods, which adds
     * contents for a particular constant, calling itself recursively
     * should it encounter a {@link com.duy.dx.rop.cst.CstArray} and calling {@link
     * #addContents(DexFile, Annotation)} recursively should it
     * encounter a {@link com.duy.dx.rop.cst.CstAnnotation}.
     *
     * @param file {@code non-null;} the file to add to
     * @param cst {@code non-null;} the constant to add contents for
     */
    public static void addContents(DexFile file, Constant cst) {
        if (cst instanceof com.duy.dx.rop.cst.CstAnnotation) {
            addContents(file, ((CstAnnotation) cst).getAnnotation());
        } else if (cst instanceof com.duy.dx.rop.cst.CstArray) {
            com.duy.dx.rop.cst.CstArray.List list = ((CstArray) cst).getList();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                addContents(file, list.get(i));
            }
        } else {
            file.internIfAppropriate(cst);
        }
    }
}
