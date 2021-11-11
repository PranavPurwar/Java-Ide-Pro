/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.duy.dx.cf.code;

import com.duy.dx.rop.cst.CstCallSiteRef;
import com.duy.dx.rop.cst.CstType;
import com.duy.dx.rop.type.Prototype;
import com.duy.dx.rop.type.Type;
import com.duy.dx.rop.type.TypeBearer;
import com.duy.dx.util.Hex;

/**
 * {@link Machine} which keeps track of known values but does not do
 * smart/realistic reference type calculations.
 */
public class ValueAwareMachine extends BaseMachine {
    /**
     * Constructs an instance.
     *
     * @param prototype {@code non-null;} the prototype for the associated
     * method
     */
    public ValueAwareMachine(Prototype prototype) {
        super(prototype);
    }

    /** {@inheritDoc} */
    @Override
    public void run(Frame frame, int offset, int opcode) {
        switch (opcode) {
            case com.duy.dx.cf.code.ByteOps.NOP:
            case com.duy.dx.cf.code.ByteOps.IASTORE:
            case com.duy.dx.cf.code.ByteOps.POP:
            case com.duy.dx.cf.code.ByteOps.POP2:
            case com.duy.dx.cf.code.ByteOps.IFEQ:
            case com.duy.dx.cf.code.ByteOps.IFNE:
            case com.duy.dx.cf.code.ByteOps.IFLT:
            case com.duy.dx.cf.code.ByteOps.IFGE:
            case com.duy.dx.cf.code.ByteOps.IFGT:
            case com.duy.dx.cf.code.ByteOps.IFLE:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPEQ:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPNE:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPLT:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPGE:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPGT:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPLE:
            case com.duy.dx.cf.code.ByteOps.IF_ACMPEQ:
            case com.duy.dx.cf.code.ByteOps.IF_ACMPNE:
            case com.duy.dx.cf.code.ByteOps.GOTO:
            case com.duy.dx.cf.code.ByteOps.RET:
            case com.duy.dx.cf.code.ByteOps.LOOKUPSWITCH:
            case com.duy.dx.cf.code.ByteOps.IRETURN:
            case com.duy.dx.cf.code.ByteOps.RETURN:
            case com.duy.dx.cf.code.ByteOps.PUTSTATIC:
            case com.duy.dx.cf.code.ByteOps.PUTFIELD:
            case com.duy.dx.cf.code.ByteOps.ATHROW:
            case com.duy.dx.cf.code.ByteOps.MONITORENTER:
            case com.duy.dx.cf.code.ByteOps.MONITOREXIT:
            case com.duy.dx.cf.code.ByteOps.IFNULL:
            case com.duy.dx.cf.code.ByteOps.IFNONNULL: {
                // Nothing to do for these ops in this class.
                clearResult();
                break;
            }
            case com.duy.dx.cf.code.ByteOps.LDC:
            case com.duy.dx.cf.code.ByteOps.LDC2_W: {
                setResult((com.duy.dx.rop.type.TypeBearer) getAuxCst());
                break;
            }
            case com.duy.dx.cf.code.ByteOps.ILOAD:
            case com.duy.dx.cf.code.ByteOps.ISTORE: {
                setResult(arg(0));
                break;
            }
            case com.duy.dx.cf.code.ByteOps.IALOAD:
            case com.duy.dx.cf.code.ByteOps.IADD:
            case com.duy.dx.cf.code.ByteOps.ISUB:
            case com.duy.dx.cf.code.ByteOps.IMUL:
            case com.duy.dx.cf.code.ByteOps.IDIV:
            case com.duy.dx.cf.code.ByteOps.IREM:
            case com.duy.dx.cf.code.ByteOps.INEG:
            case com.duy.dx.cf.code.ByteOps.ISHL:
            case com.duy.dx.cf.code.ByteOps.ISHR:
            case com.duy.dx.cf.code.ByteOps.IUSHR:
            case com.duy.dx.cf.code.ByteOps.IAND:
            case com.duy.dx.cf.code.ByteOps.IOR:
            case com.duy.dx.cf.code.ByteOps.IXOR:
            case com.duy.dx.cf.code.ByteOps.IINC:
            case com.duy.dx.cf.code.ByteOps.I2L:
            case com.duy.dx.cf.code.ByteOps.I2F:
            case com.duy.dx.cf.code.ByteOps.I2D:
            case com.duy.dx.cf.code.ByteOps.L2I:
            case com.duy.dx.cf.code.ByteOps.L2F:
            case com.duy.dx.cf.code.ByteOps.L2D:
            case com.duy.dx.cf.code.ByteOps.F2I:
            case com.duy.dx.cf.code.ByteOps.F2L:
            case com.duy.dx.cf.code.ByteOps.F2D:
            case com.duy.dx.cf.code.ByteOps.D2I:
            case com.duy.dx.cf.code.ByteOps.D2L:
            case com.duy.dx.cf.code.ByteOps.D2F:
            case com.duy.dx.cf.code.ByteOps.I2B:
            case com.duy.dx.cf.code.ByteOps.I2C:
            case com.duy.dx.cf.code.ByteOps.I2S:
            case com.duy.dx.cf.code.ByteOps.LCMP:
            case com.duy.dx.cf.code.ByteOps.FCMPL:
            case com.duy.dx.cf.code.ByteOps.FCMPG:
            case com.duy.dx.cf.code.ByteOps.DCMPL:
            case com.duy.dx.cf.code.ByteOps.DCMPG:
            case com.duy.dx.cf.code.ByteOps.ARRAYLENGTH: {
                setResult(getAuxType());
                break;
            }
            case com.duy.dx.cf.code.ByteOps.DUP:
            case com.duy.dx.cf.code.ByteOps.DUP_X1:
            case com.duy.dx.cf.code.ByteOps.DUP_X2:
            case com.duy.dx.cf.code.ByteOps.DUP2:
            case com.duy.dx.cf.code.ByteOps.DUP2_X1:
            case com.duy.dx.cf.code.ByteOps.DUP2_X2:
            case com.duy.dx.cf.code.ByteOps.SWAP: {
                clearResult();
                for (int pattern = getAuxInt(); pattern != 0; pattern >>= 4) {
                    int which = (pattern & 0x0f) - 1;
                    addResult(arg(which));
                }
                break;
            }

            case com.duy.dx.cf.code.ByteOps.JSR: {
                setResult(new ReturnAddress(getAuxTarget()));
                break;
            }
            case com.duy.dx.cf.code.ByteOps.GETSTATIC:
            case com.duy.dx.cf.code.ByteOps.GETFIELD:
            case com.duy.dx.cf.code.ByteOps.INVOKEVIRTUAL:
            case com.duy.dx.cf.code.ByteOps.INVOKESTATIC:
            case com.duy.dx.cf.code.ByteOps.INVOKEINTERFACE: {
                com.duy.dx.rop.type.Type type = ((com.duy.dx.rop.type.TypeBearer) getAuxCst()).getType();
                if (type == com.duy.dx.rop.type.Type.VOID) {
                    clearResult();
                } else {
                    setResult(type);
                }
                break;
            }
            case com.duy.dx.cf.code.ByteOps.INVOKESPECIAL: {
                com.duy.dx.rop.type.Type thisType = arg(0).getType();
                if (thisType.isUninitialized()) {
                    frame.makeInitialized(thisType);
                }
                com.duy.dx.rop.type.Type type = ((TypeBearer) getAuxCst()).getType();
                if (type == com.duy.dx.rop.type.Type.VOID) {
                    clearResult();
                } else {
                    setResult(type);
                }
                break;
            }
            case com.duy.dx.cf.code.ByteOps.INVOKEDYNAMIC: {
                com.duy.dx.rop.type.Type type = ((CstCallSiteRef) getAuxCst()).getReturnType();
                if (type == com.duy.dx.rop.type.Type.VOID) {
                    clearResult();
                } else {
                    setResult(type);
                }
                break;
            }
            case com.duy.dx.cf.code.ByteOps.NEW: {
                com.duy.dx.rop.type.Type type = ((com.duy.dx.rop.cst.CstType) getAuxCst()).getClassType();
                setResult(type.asUninitialized(offset));
                break;
            }
            case com.duy.dx.cf.code.ByteOps.NEWARRAY:
            case com.duy.dx.cf.code.ByteOps.CHECKCAST:
            case com.duy.dx.cf.code.ByteOps.MULTIANEWARRAY: {
                com.duy.dx.rop.type.Type type = ((com.duy.dx.rop.cst.CstType) getAuxCst()).getClassType();
                setResult(type);
                break;
            }
            case com.duy.dx.cf.code.ByteOps.ANEWARRAY: {
                com.duy.dx.rop.type.Type type = ((CstType) getAuxCst()).getClassType();
                setResult(type.getArrayType());
                break;
            }
            case ByteOps.INSTANCEOF: {
                setResult(Type.INT);
                break;
            }
            default: {
                throw new RuntimeException("shouldn't happen: " +
                                           Hex.u1(opcode));
            }
        }

        storeResults(frame);
    }
}
