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

import com.duy.dx.cf.iface.Method;
import com.duy.dx.cf.iface.MethodList;

import java.util.ArrayList;

import com.duy.dx.rop.code.AccessFlags;
import com.duy.dx.rop.code.FillArrayDataInsn;
import com.duy.dx.rop.code.Insn;
import com.duy.dx.rop.code.InvokePolymorphicInsn;
import com.duy.dx.rop.code.PlainCstInsn;
import com.duy.dx.rop.code.PlainInsn;
import com.duy.dx.rop.code.RegOps;
import com.duy.dx.rop.code.RegisterSpec;
import com.duy.dx.rop.code.RegisterSpecList;
import com.duy.dx.rop.code.Rop;
import com.duy.dx.rop.code.Rops;
import com.duy.dx.rop.code.SourcePosition;
import com.duy.dx.rop.code.SwitchInsn;
import com.duy.dx.rop.code.ThrowingCstInsn;
import com.duy.dx.rop.code.ThrowingInsn;
import com.duy.dx.rop.code.TranslationAdvice;
import com.duy.dx.rop.cst.Constant;
import com.duy.dx.rop.cst.CstCallSiteRef;
import com.duy.dx.rop.cst.CstFieldRef;
import com.duy.dx.rop.cst.CstInteger;
import com.duy.dx.rop.cst.CstMethodRef;
import com.duy.dx.rop.cst.CstNat;
import com.duy.dx.rop.cst.CstString;
import com.duy.dx.rop.cst.CstType;
import com.duy.dx.rop.type.Type;
import com.duy.dx.rop.type.TypeBearer;
import com.duy.dx.rop.type.TypeList;
import com.duy.dx.util.IntList;

/**
 * Machine implementation for use by {@link com.duy.dx.cf.code.Ropper}.
 */
/*package*/ final class RopperMachine extends ValueAwareMachine {
    /** {@code non-null;} array reflection class */
    private static final com.duy.dx.rop.cst.CstType ARRAY_REFLECT_TYPE =
        new com.duy.dx.rop.cst.CstType(com.duy.dx.rop.type.Type.internClassName("java/lang/reflect/Array"));

    /**
     * {@code non-null;} method constant for use in converting
     * {@code multianewarray} instructions
     */
    private static final com.duy.dx.rop.cst.CstMethodRef MULTIANEWARRAY_METHOD =
        new com.duy.dx.rop.cst.CstMethodRef(ARRAY_REFLECT_TYPE,
                         new CstNat(new com.duy.dx.rop.cst.CstString("newInstance"),
                                    new CstString("(Ljava/lang/Class;[I)" +
                                                "Ljava/lang/Object;")));

    /** {@code non-null;} {@link com.duy.dx.cf.code.Ropper} controlling this instance */
    private final com.duy.dx.cf.code.Ropper ropper;

    /** {@code non-null;} method being converted */
    private final ConcreteMethod method;

    /** {@code non-null:} list of methods from the class whose method is being converted */
    private final MethodList methods;

    /** {@code non-null;} translation advice */
    private final com.duy.dx.rop.code.TranslationAdvice advice;

    /** max locals of the method */
    private final int maxLocals;

    /** {@code non-null;} instructions for the rop basic block in-progress */
    private final ArrayList<com.duy.dx.rop.code.Insn> insns;

    /** {@code non-null;} catches for the block currently being processed */
    private com.duy.dx.rop.type.TypeList catches;

    /** whether the catches have been used in an instruction */
    private boolean catchesUsed;

    /** whether the block contains a {@code return} */
    private boolean returns;

    /** primary successor index */
    private int primarySuccessorIndex;

    /** {@code >= 0;} number of extra basic blocks required */
    private int extraBlockCount;

    /** true if last processed block ends with a jsr or jsr_W*/
    private boolean hasJsr;

    /** true if an exception can be thrown by the last block processed */
    private boolean blockCanThrow;

    /**
     * If non-null, the ReturnAddress that was used by the terminating ret
     * instruction. If null, there was no ret instruction encountered.
     */

    private ReturnAddress returnAddress;

    /**
     * {@code null-ok;} the appropriate {@code return} op or {@code null}
     * if it is not yet known
     */
    private com.duy.dx.rop.code.Rop returnOp;

    /**
     * {@code null-ok;} the source position for the return block or {@code null}
     * if it is not yet known
     */
    private com.duy.dx.rop.code.SourcePosition returnPosition;

    /**
     * Constructs an instance.
     *
     * @param ropper {@code non-null;} ropper controlling this instance
     * @param method {@code non-null;} method being converted
     * @param advice {@code non-null;} translation advice to use
     * @param methods {@code non-null;} list of methods defined by the class
     *     that defines {@code method}.
     */
    public RopperMachine(Ropper ropper, ConcreteMethod method,
                         TranslationAdvice advice, MethodList methods) {
        super(method.getEffectiveDescriptor());

        if (methods == null) {
            throw new NullPointerException("methods == null");
        }

        if (ropper == null) {
            throw new NullPointerException("ropper == null");
        }

        if (advice == null) {
            throw new NullPointerException("advice == null");
        }

        this.ropper = ropper;
        this.method = method;
        this.methods = methods;
        this.advice = advice;
        this.maxLocals = method.getMaxLocals();
        this.insns = new ArrayList<com.duy.dx.rop.code.Insn>(25);
        this.catches = null;
        this.catchesUsed = false;
        this.returns = false;
        this.primarySuccessorIndex = -1;
        this.extraBlockCount = 0;
        this.blockCanThrow = false;
        this.returnOp = null;
        this.returnPosition = null;
    }

    /**
     * Gets the instructions array. It is shared and gets modified by
     * subsequent calls to this instance.
     *
     * @return {@code non-null;} the instructions array
     */
    public ArrayList<com.duy.dx.rop.code.Insn> getInsns() {
        return insns;
    }

    /**
     * Gets the return opcode encountered, if any.
     *
     * @return {@code null-ok;} the return opcode
     */
    public com.duy.dx.rop.code.Rop getReturnOp() {
        return returnOp;
    }

    /**
     * Gets the return position, if known.
     *
     * @return {@code null-ok;} the return position
     */
    public com.duy.dx.rop.code.SourcePosition getReturnPosition() {
        return returnPosition;
    }

    /**
     * Gets ready to start working on a new block. This will clear the
     * {@link #insns} list, set {@link #catches}, reset whether it has
     * been used, reset whether the block contains a
     * {@code return}, and reset {@link #primarySuccessorIndex}.
     */
    public void startBlock(com.duy.dx.rop.type.TypeList catches) {
        this.catches = catches;

        insns.clear();
        catchesUsed = false;
        returns = false;
        primarySuccessorIndex = 0;
        extraBlockCount = 0;
        blockCanThrow = false;
        hasJsr = false;
        returnAddress = null;
    }

    /**
     * Gets whether {@link #catches} was used. This indicates that the
     * last instruction in the block is one of the ones that can throw.
     *
     * @return whether {@code catches} has been used
     */
    public boolean wereCatchesUsed() {
        return catchesUsed;
    }

    /**
     * Gets whether the block just processed ended with a
     * {@code return}.
     *
     * @return whether the block returns
     */
    public boolean returns() {
        return returns;
    }

    /**
     * Gets the primary successor index. This is the index into the
     * successors list where the primary may be found or
     * {@code -1} if there are successors but no primary
     * successor. This may return something other than
     * {@code -1} in the case of an instruction with no
     * successors at all (primary or otherwise).
     *
     * @return {@code >= -1;} the primary successor index
     */
    public int getPrimarySuccessorIndex() {
        return primarySuccessorIndex;
    }

    /**
     * Gets how many extra blocks will be needed to represent the
     * block currently being translated. Each extra block should consist
     * of one instruction from the end of the original block.
     *
     * @return {@code >= 0;} the number of extra blocks needed
     */
    public int getExtraBlockCount() {
        return extraBlockCount;
    }

    /**
     * @return true if at least one of the insn processed since the last
     * call to startBlock() can throw.
     */
    public boolean canThrow() {
        return blockCanThrow;
    }

    /**
     * @return true if a JSR has ben encountered since the last call to
     * startBlock()
     */
    public boolean hasJsr() {
        return hasJsr;
    }

    /**
     * @return {@code true} if a {@code ret} has ben encountered since
     * the last call to {@code startBlock()}
     */
    public boolean hasRet() {
        return returnAddress != null;
    }

    /**
     * @return {@code null-ok;} return address of a {@code ret}
     * instruction if encountered since last call to startBlock().
     * {@code null} if no ret instruction encountered.
     */
    public ReturnAddress getReturnAddress() {
        return returnAddress;
    }

    /** {@inheritDoc} */
    @Override
    public void run(Frame frame, int offset, int opcode) {
        /*
         * This is the stack pointer after the opcode's arguments have been
         * popped.
         */
        int stackPointer = maxLocals + frame.getStack().size();

        // The sources have to be retrieved before super.run() gets called.
        com.duy.dx.rop.code.RegisterSpecList sources = getSources(opcode, stackPointer);
        int sourceCount = sources.size();

        super.run(frame, offset, opcode);

        com.duy.dx.rop.code.SourcePosition pos = method.makeSourcePosistion(offset);
        com.duy.dx.rop.code.RegisterSpec localTarget = getLocalTarget(opcode == com.duy.dx.cf.code.ByteOps.ISTORE);
        int destCount = resultCount();
        com.duy.dx.rop.code.RegisterSpec dest;

        if (destCount == 0) {
            dest = null;
            switch (opcode) {
                case com.duy.dx.cf.code.ByteOps.POP:
                case com.duy.dx.cf.code.ByteOps.POP2: {
                    // These simply don't appear in the rop form.
                    return;
                }
            }
        } else if (localTarget != null) {
            dest = localTarget;
        } else if (destCount == 1) {
            dest = com.duy.dx.rop.code.RegisterSpec.make(stackPointer, result(0));
        } else {
            /*
             * This clause only ever applies to the stack manipulation
             * ops that have results (that is, dup* and swap but not
             * pop*).
             *
             * What we do is first move all the source registers into
             * the "temporary stack" area defined for the method, and
             * then move stuff back down onto the main "stack" in the
             * arrangement specified by the stack op pattern.
             *
             * Note: This code ends up emitting a lot of what will
             * turn out to be superfluous moves (e.g., moving back and
             * forth to the same local when doing a dup); however,
             * that makes this code a bit easier (and goodness knows
             * it doesn't need any extra complexity), and all the SSA
             * stuff is going to want to deal with this sort of
             * superfluous assignment anyway, so it should be a wash
             * in the end.
             */
            int scratchAt = ropper.getFirstTempStackReg();
            com.duy.dx.rop.code.RegisterSpec[] scratchRegs = new com.duy.dx.rop.code.RegisterSpec[sourceCount];

            for (int i = 0; i < sourceCount; i++) {
                com.duy.dx.rop.code.RegisterSpec src = sources.get(i);
                com.duy.dx.rop.type.TypeBearer type = src.getTypeBearer();
                com.duy.dx.rop.code.RegisterSpec scratch = src.withReg(scratchAt);
                insns.add(new com.duy.dx.rop.code.PlainInsn(com.duy.dx.rop.code.Rops.opMove(type), pos, scratch, src));
                scratchRegs[i] = scratch;
                scratchAt += src.getCategory();
            }

            for (int pattern = getAuxInt(); pattern != 0; pattern >>= 4) {
                int which = (pattern & 0x0f) - 1;
                com.duy.dx.rop.code.RegisterSpec scratch = scratchRegs[which];
                com.duy.dx.rop.type.TypeBearer type = scratch.getTypeBearer();
                insns.add(new com.duy.dx.rop.code.PlainInsn(com.duy.dx.rop.code.Rops.opMove(type), pos,
                                        scratch.withReg(stackPointer),
                                        scratch));
                stackPointer += type.getType().getCategory();
            }
            return;
        }

        com.duy.dx.rop.type.TypeBearer destType = (dest != null) ? dest : com.duy.dx.rop.type.Type.VOID;
        com.duy.dx.rop.cst.Constant cst = getAuxCst();
        int ropOpcode;
        com.duy.dx.rop.code.Rop rop;
        com.duy.dx.rop.code.Insn insn;

        if (opcode == com.duy.dx.cf.code.ByteOps.MULTIANEWARRAY) {
            blockCanThrow = true;

            // Add the extra instructions for handling multianewarray.

            extraBlockCount = 6;

            /*
             * Add an array constructor for the int[] containing all the
             * dimensions.
             */
            com.duy.dx.rop.code.RegisterSpec dimsReg =
                com.duy.dx.rop.code.RegisterSpec.make(dest.getNextReg(), com.duy.dx.rop.type.Type.INT_ARRAY);
            rop = com.duy.dx.rop.code.Rops.opFilledNewArray(com.duy.dx.rop.type.Type.INT_ARRAY, sourceCount);
            insn = new com.duy.dx.rop.code.ThrowingCstInsn(rop, pos, sources, catches,
                    com.duy.dx.rop.cst.CstType.INT_ARRAY);
            insns.add(insn);

            // Add a move-result for the new-filled-array
            rop = com.duy.dx.rop.code.Rops.opMoveResult(com.duy.dx.rop.type.Type.INT_ARRAY);
            insn = new com.duy.dx.rop.code.PlainInsn(rop, pos, dimsReg, com.duy.dx.rop.code.RegisterSpecList.EMPTY);
            insns.add(insn);

            /*
             * Add a const-class instruction for the specified array
             * class.
             */

            /*
             * Remove as many dimensions from the originally specified
             * class as are given in the explicit list of dimensions,
             * so as to pass the right component class to the standard
             * Java library array constructor.
             */
            com.duy.dx.rop.type.Type componentType = ((com.duy.dx.rop.cst.CstType) cst).getClassType();
            for (int i = 0; i < sourceCount; i++) {
                componentType = componentType.getComponentType();
            }

            com.duy.dx.rop.code.RegisterSpec classReg =
                com.duy.dx.rop.code.RegisterSpec.make(dest.getReg(), com.duy.dx.rop.type.Type.CLASS);

            if (componentType.isPrimitive()) {
                /*
                 * The component type is primitive (e.g., int as opposed
                 * to Integer), so we have to fetch the corresponding
                 * TYPE class.
                 */
                com.duy.dx.rop.cst.CstFieldRef typeField =
                    CstFieldRef.forPrimitiveType(componentType);
                insn = new com.duy.dx.rop.code.ThrowingCstInsn(com.duy.dx.rop.code.Rops.GET_STATIC_OBJECT, pos,
                                           com.duy.dx.rop.code.RegisterSpecList.EMPTY,
                                           catches, typeField);
            } else {
                /*
                 * The component type is an object type, so just make a
                 * normal class reference.
                 */
                insn = new com.duy.dx.rop.code.ThrowingCstInsn(com.duy.dx.rop.code.Rops.CONST_OBJECT, pos,
                                           com.duy.dx.rop.code.RegisterSpecList.EMPTY, catches,
                                           new com.duy.dx.rop.cst.CstType(componentType));
            }

            insns.add(insn);

            // Add a move-result-pseudo for the get-static or const
            rop = com.duy.dx.rop.code.Rops.opMoveResultPseudo(classReg.getType());
            insn = new com.duy.dx.rop.code.PlainInsn(rop, pos, classReg, com.duy.dx.rop.code.RegisterSpecList.EMPTY);
            insns.add(insn);

            /*
             * Add a call to the "multianewarray method," that is,
             * Array.newInstance(class, dims). Note: The result type
             * of newInstance() is Object, which is why the last
             * instruction in this sequence is a cast to the right
             * type for the original instruction.
             */

            com.duy.dx.rop.code.RegisterSpec objectReg =
                com.duy.dx.rop.code.RegisterSpec.make(dest.getReg(), com.duy.dx.rop.type.Type.OBJECT);

            insn = new com.duy.dx.rop.code.ThrowingCstInsn(
                    com.duy.dx.rop.code.Rops.opInvokeStatic(MULTIANEWARRAY_METHOD.getPrototype()),
                    pos, com.duy.dx.rop.code.RegisterSpecList.make(classReg, dimsReg),
                    catches, MULTIANEWARRAY_METHOD);
            insns.add(insn);

            // Add a move-result.
            rop = com.duy.dx.rop.code.Rops.opMoveResult(MULTIANEWARRAY_METHOD.getPrototype()
                    .getReturnType());
            insn = new com.duy.dx.rop.code.PlainInsn(rop, pos, objectReg, com.duy.dx.rop.code.RegisterSpecList.EMPTY);
            insns.add(insn);

            /*
             * And finally, set up for the remainder of this method to
             * add an appropriate cast.
             */

            opcode = com.duy.dx.cf.code.ByteOps.CHECKCAST;
            sources = com.duy.dx.rop.code.RegisterSpecList.make(objectReg);
        } else if (opcode == com.duy.dx.cf.code.ByteOps.JSR) {
            // JSR has no Rop instruction
            hasJsr = true;
            return;
        } else if (opcode == com.duy.dx.cf.code.ByteOps.RET) {
            try {
                returnAddress = (ReturnAddress)arg(0);
            } catch (ClassCastException ex) {
                throw new RuntimeException(
                        "Argument to RET was not a ReturnAddress", ex);
            }
            // RET has no Rop instruction.
            return;
        }

        ropOpcode = jopToRopOpcode(opcode, cst);
        rop = com.duy.dx.rop.code.Rops.ropFor(ropOpcode, destType, sources, cst);

        com.duy.dx.rop.code.Insn moveResult = null;
        if (dest != null && rop.isCallLike()) {
            /*
             * We're going to want to have a move-result in the next
             * basic block.
             */
            extraBlockCount++;

            Type returnType;
            if (rop.getOpcode() == com.duy.dx.rop.code.RegOps.INVOKE_CUSTOM) {
                returnType = ((CstCallSiteRef) cst).getReturnType();
            } else {
                returnType = ((com.duy.dx.rop.cst.CstMethodRef) cst).getPrototype().getReturnType();
            }
            moveResult = new com.duy.dx.rop.code.PlainInsn(com.duy.dx.rop.code.Rops.opMoveResult(returnType),
                                       pos, dest, com.duy.dx.rop.code.RegisterSpecList.EMPTY);

            dest = null;
        } else if (dest != null && rop.canThrow()) {
            /*
             * We're going to want to have a move-result-pseudo in the
             * next basic block.
             */
            extraBlockCount++;

            moveResult = new com.duy.dx.rop.code.PlainInsn(
                    com.duy.dx.rop.code.Rops.opMoveResultPseudo(dest.getTypeBearer()),
                    pos, dest, com.duy.dx.rop.code.RegisterSpecList.EMPTY);

            dest = null;
        }
        if (ropOpcode == com.duy.dx.rop.code.RegOps.NEW_ARRAY) {
            /*
             * In the original bytecode, this was either a primitive
             * array constructor "newarray" or an object array
             * constructor "anewarray". In the former case, there is
             * no explicit constant, and in the latter, the constant
             * is for the element type and not the array type. The rop
             * instruction form for both of these is supposed to be
             * the resulting array type, so we initialize / alter
             * "cst" here, accordingly. Conveniently enough, the rop
             * opcode already gets constructed with the proper array
             * type.
             */
            cst = CstType.intern(rop.getResult());
        } else if ((cst == null) && (sourceCount == 2)) {
            com.duy.dx.rop.type.TypeBearer firstType = sources.get(0).getTypeBearer();
            com.duy.dx.rop.type.TypeBearer lastType = sources.get(1).getTypeBearer();

            if ((lastType.isConstant() || firstType.isConstant()) &&
                 advice.hasConstantOperation(rop, sources.get(0),
                                             sources.get(1))) {

                if (lastType.isConstant()) {
                    /*
                     * The target architecture has an instruction that can
                     * build in the constant found in the second argument,
                     * so pull it out of the sources and just use it as a
                     * constant here.
                     */
                    cst = (com.duy.dx.rop.cst.Constant) lastType;
                    sources = sources.withoutLast();

                    // For subtraction, change to addition and invert constant
                    if (rop.getOpcode() == com.duy.dx.rop.code.RegOps.SUB) {
                        ropOpcode = com.duy.dx.rop.code.RegOps.ADD;
                        com.duy.dx.rop.cst.CstInteger cstInt = (com.duy.dx.rop.cst.CstInteger) lastType;
                        cst = CstInteger.make(-cstInt.getValue());
                    }
                } else {
                    /*
                     * The target architecture has an instruction that can
                     * build in the constant found in the first argument,
                     * so pull it out of the sources and just use it as a
                     * constant here.
                     */
                    cst = (com.duy.dx.rop.cst.Constant) firstType;
                    sources = sources.withoutFirst();
                }

                rop = com.duy.dx.rop.code.Rops.ropFor(ropOpcode, destType, sources, cst);
            }
        }

        SwitchList cases = getAuxCases();
        ArrayList<com.duy.dx.rop.cst.Constant> initValues = getInitValues();
        boolean canThrow = rop.canThrow();

        blockCanThrow |= canThrow;

        if (cases != null) {
            if (cases.size() == 0) {
                // It's a default-only switch statement. It can happen!
                insn = new com.duy.dx.rop.code.PlainInsn(com.duy.dx.rop.code.Rops.GOTO, pos, null,
                                     com.duy.dx.rop.code.RegisterSpecList.EMPTY);
                primarySuccessorIndex = 0;
            } else {
                IntList values = cases.getValues();
                insn = new SwitchInsn(rop, pos, dest, sources, values);
                primarySuccessorIndex = values.size();
            }
        } else if (ropOpcode == com.duy.dx.rop.code.RegOps.RETURN) {
            /*
             * Returns get turned into the combination of a move (if
             * non-void and if the return doesn't already mention
             * register 0) and a goto (to the return block).
             */
            if (sources.size() != 0) {
                com.duy.dx.rop.code.RegisterSpec source = sources.get(0);
                TypeBearer type = source.getTypeBearer();
                if (source.getReg() != 0) {
                    insns.add(new com.duy.dx.rop.code.PlainInsn(com.duy.dx.rop.code.Rops.opMove(type), pos,
                                            com.duy.dx.rop.code.RegisterSpec.make(0, type),
                                            source));
                }
            }
            insn = new com.duy.dx.rop.code.PlainInsn(com.duy.dx.rop.code.Rops.GOTO, pos, null, com.duy.dx.rop.code.RegisterSpecList.EMPTY);
            primarySuccessorIndex = 0;
            updateReturnOp(rop, pos);
            returns = true;
        } else if (cst != null) {
            if (canThrow) {
                if (rop.getOpcode() == com.duy.dx.rop.code.RegOps.INVOKE_POLYMORPHIC) {
                    insn = makeInvokePolymorphicInsn(rop, pos, sources, catches, cst);
                } else {
                    insn = new ThrowingCstInsn(rop, pos, sources, catches, cst);
                }
                catchesUsed = true;
                primarySuccessorIndex = catches.size();
            } else {
                insn = new PlainCstInsn(rop, pos, dest, sources, cst);
            }
        } else if (canThrow) {
            insn = new ThrowingInsn(rop, pos, sources, catches);
            catchesUsed = true;
            if (opcode == com.duy.dx.cf.code.ByteOps.ATHROW) {
                /*
                 * The op athrow is the only one where it's possible
                 * to have non-empty successors and yet not have a
                 * primary successor.
                 */
                primarySuccessorIndex = -1;
            } else {
                primarySuccessorIndex = catches.size();
            }
        } else {
            insn = new PlainInsn(rop, pos, dest, sources);
        }

        insns.add(insn);

        if (moveResult != null) {
            insns.add(moveResult);
        }

        /*
         * If initValues is non-null, it means that the parser has
         * seen a group of compatible constant initialization
         * bytecodes that are applied to the current newarray. The
         * action we take here is to convert these initialization
         * bytecodes into a single fill-array-data ROP which lays out
         * all the constant values in a table.
         */
        if (initValues != null) {
            extraBlockCount++;
            insn = new FillArrayDataInsn(Rops.FILL_ARRAY_DATA, pos,
                    com.duy.dx.rop.code.RegisterSpecList.make(moveResult.getResult()), initValues,
                    cst);
            insns.add(insn);
        }
    }

    /**
     * Helper for {@link #run}, which gets the list of sources for the.
     * instruction.
     *
     * @param opcode the opcode being translated
     * @param stackPointer {@code >= 0;} the stack pointer after the
     * instruction's arguments have been popped
     * @return {@code non-null;} the sources
     */
    private com.duy.dx.rop.code.RegisterSpecList getSources(int opcode, int stackPointer) {
        int count = argCount();

        if (count == 0) {
            // We get an easy out if there aren't any sources.
            return com.duy.dx.rop.code.RegisterSpecList.EMPTY;
        }

        int localIndex = getLocalIndex();
        com.duy.dx.rop.code.RegisterSpecList sources;

        if (localIndex >= 0) {
            // The instruction is operating on a local variable.
            sources = new com.duy.dx.rop.code.RegisterSpecList(1);
            sources.set(0, com.duy.dx.rop.code.RegisterSpec.make(localIndex, arg(0)));
        } else {
            sources = new com.duy.dx.rop.code.RegisterSpecList(count);
            int regAt = stackPointer;
            for (int i = 0; i < count; i++) {
                com.duy.dx.rop.code.RegisterSpec spec = com.duy.dx.rop.code.RegisterSpec.make(regAt, arg(i));
                sources.set(i, spec);
                regAt += spec.getCategory();
            }

            switch (opcode) {
                case com.duy.dx.cf.code.ByteOps.IASTORE: {
                    /*
                     * The Java argument order for array stores is
                     * (array, index, value), but the rop argument
                     * order is (value, array, index). The following
                     * code gets the right arguments in the right
                     * places.
                     */
                    if (count != 3) {
                        throw new RuntimeException("shouldn't happen");
                    }
                    com.duy.dx.rop.code.RegisterSpec array = sources.get(0);
                    com.duy.dx.rop.code.RegisterSpec index = sources.get(1);
                    com.duy.dx.rop.code.RegisterSpec value = sources.get(2);
                    sources.set(0, value);
                    sources.set(1, array);
                    sources.set(2, index);
                    break;
                }
                case com.duy.dx.cf.code.ByteOps.PUTFIELD: {
                    /*
                     * Similar to above: The Java argument order for
                     * putfield is (object, value), but the rop
                     * argument order is (value, object).
                     */
                    if (count != 2) {
                        throw new RuntimeException("shouldn't happen");
                    }
                    com.duy.dx.rop.code.RegisterSpec obj = sources.get(0);
                    RegisterSpec value = sources.get(1);
                    sources.set(0, value);
                    sources.set(1, obj);
                    break;
                }
            }
        }

        sources.setImmutable();
        return sources;
    }

    /**
     * Sets or updates the information about the return block.
     *
     * @param op {@code non-null;} the opcode to use
     * @param pos {@code non-null;} the position to use
     */
    private void updateReturnOp(com.duy.dx.rop.code.Rop op, com.duy.dx.rop.code.SourcePosition pos) {
        if (op == null) {
            throw new NullPointerException("op == null");
        }

        if (pos == null) {
            throw new NullPointerException("pos == null");
        }

        if (returnOp == null) {
            returnOp = op;
            returnPosition = pos;
        } else {
            if (returnOp != op) {
                throw new SimException("return op mismatch: " + op + ", " +
                                       returnOp);
            }

            if (pos.getLine() > returnPosition.getLine()) {
                // Pick the largest line number to be the "canonical" return.
                returnPosition = pos;
            }
        }
    }

    /**
     * Gets the register opcode for the given Java opcode.
     *
     * @param jop {@code jop >= 0;} the Java opcode
     * @param cst {@code null-ok;} the constant argument, if any
     * @return {@code >= 0;} the corresponding register opcode
     */
    private int jopToRopOpcode(int jop, com.duy.dx.rop.cst.Constant cst) {
        switch (jop) {
            case com.duy.dx.cf.code.ByteOps.POP:
            case com.duy.dx.cf.code.ByteOps.POP2:
            case com.duy.dx.cf.code.ByteOps.DUP:
            case com.duy.dx.cf.code.ByteOps.DUP_X1:
            case com.duy.dx.cf.code.ByteOps.DUP_X2:
            case com.duy.dx.cf.code.ByteOps.DUP2:
            case com.duy.dx.cf.code.ByteOps.DUP2_X1:
            case com.duy.dx.cf.code.ByteOps.DUP2_X2:
            case com.duy.dx.cf.code.ByteOps.SWAP:
            case com.duy.dx.cf.code.ByteOps.JSR:
            case com.duy.dx.cf.code.ByteOps.RET:
            case com.duy.dx.cf.code.ByteOps.MULTIANEWARRAY: {
                // These need to be taken care of specially.
                break;
            }
            case com.duy.dx.cf.code.ByteOps.NOP: {
                return com.duy.dx.rop.code.RegOps.NOP;
            }
            case com.duy.dx.cf.code.ByteOps.LDC:
            case com.duy.dx.cf.code.ByteOps.LDC2_W: {
                return com.duy.dx.rop.code.RegOps.CONST;
            }
            case com.duy.dx.cf.code.ByteOps.ILOAD:
            case com.duy.dx.cf.code.ByteOps.ISTORE: {
                return com.duy.dx.rop.code.RegOps.MOVE;
            }
            case com.duy.dx.cf.code.ByteOps.IALOAD: {
                return com.duy.dx.rop.code.RegOps.AGET;
            }
            case com.duy.dx.cf.code.ByteOps.IASTORE: {
                return com.duy.dx.rop.code.RegOps.APUT;
            }
            case com.duy.dx.cf.code.ByteOps.IADD:
            case com.duy.dx.cf.code.ByteOps.IINC: {
                return com.duy.dx.rop.code.RegOps.ADD;
            }
            case com.duy.dx.cf.code.ByteOps.ISUB: {
                return com.duy.dx.rop.code.RegOps.SUB;
            }
            case com.duy.dx.cf.code.ByteOps.IMUL: {
                return com.duy.dx.rop.code.RegOps.MUL;
            }
            case com.duy.dx.cf.code.ByteOps.IDIV: {
                return com.duy.dx.rop.code.RegOps.DIV;
            }
            case com.duy.dx.cf.code.ByteOps.IREM: {
                return com.duy.dx.rop.code.RegOps.REM;
            }
            case com.duy.dx.cf.code.ByteOps.INEG: {
                return com.duy.dx.rop.code.RegOps.NEG;
            }
            case com.duy.dx.cf.code.ByteOps.ISHL: {
                return com.duy.dx.rop.code.RegOps.SHL;
            }
            case com.duy.dx.cf.code.ByteOps.ISHR: {
                return com.duy.dx.rop.code.RegOps.SHR;
            }
            case com.duy.dx.cf.code.ByteOps.IUSHR: {
                return com.duy.dx.rop.code.RegOps.USHR;
            }
            case com.duy.dx.cf.code.ByteOps.IAND: {
                return com.duy.dx.rop.code.RegOps.AND;
            }
            case com.duy.dx.cf.code.ByteOps.IOR: {
                return com.duy.dx.rop.code.RegOps.OR;
            }
            case com.duy.dx.cf.code.ByteOps.IXOR: {
                return com.duy.dx.rop.code.RegOps.XOR;
            }
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
            case com.duy.dx.cf.code.ByteOps.D2F: {
                return com.duy.dx.rop.code.RegOps.CONV;
            }
            case com.duy.dx.cf.code.ByteOps.I2B: {
                return com.duy.dx.rop.code.RegOps.TO_BYTE;
            }
            case com.duy.dx.cf.code.ByteOps.I2C: {
                return com.duy.dx.rop.code.RegOps.TO_CHAR;
            }
            case com.duy.dx.cf.code.ByteOps.I2S: {
                return com.duy.dx.rop.code.RegOps.TO_SHORT;
            }
            case com.duy.dx.cf.code.ByteOps.LCMP:
            case com.duy.dx.cf.code.ByteOps.FCMPL:
            case com.duy.dx.cf.code.ByteOps.DCMPL: {
                return com.duy.dx.rop.code.RegOps.CMPL;
            }
            case com.duy.dx.cf.code.ByteOps.FCMPG:
            case com.duy.dx.cf.code.ByteOps.DCMPG: {
                return com.duy.dx.rop.code.RegOps.CMPG;
            }
            case com.duy.dx.cf.code.ByteOps.IFEQ:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPEQ:
            case com.duy.dx.cf.code.ByteOps.IF_ACMPEQ:
            case com.duy.dx.cf.code.ByteOps.IFNULL: {
                return com.duy.dx.rop.code.RegOps.IF_EQ;
            }
            case com.duy.dx.cf.code.ByteOps.IFNE:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPNE:
            case com.duy.dx.cf.code.ByteOps.IF_ACMPNE:
            case com.duy.dx.cf.code.ByteOps.IFNONNULL: {
                return com.duy.dx.rop.code.RegOps.IF_NE;
            }
            case com.duy.dx.cf.code.ByteOps.IFLT:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPLT: {
                return com.duy.dx.rop.code.RegOps.IF_LT;
            }
            case com.duy.dx.cf.code.ByteOps.IFGE:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPGE: {
                return com.duy.dx.rop.code.RegOps.IF_GE;
            }
            case com.duy.dx.cf.code.ByteOps.IFGT:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPGT: {
                return com.duy.dx.rop.code.RegOps.IF_GT;
            }
            case com.duy.dx.cf.code.ByteOps.IFLE:
            case com.duy.dx.cf.code.ByteOps.IF_ICMPLE: {
                return com.duy.dx.rop.code.RegOps.IF_LE;
            }
            case com.duy.dx.cf.code.ByteOps.GOTO: {
                return com.duy.dx.rop.code.RegOps.GOTO;
            }
            case com.duy.dx.cf.code.ByteOps.LOOKUPSWITCH: {
                return com.duy.dx.rop.code.RegOps.SWITCH;
            }
            case com.duy.dx.cf.code.ByteOps.IRETURN:
            case com.duy.dx.cf.code.ByteOps.RETURN: {
                return com.duy.dx.rop.code.RegOps.RETURN;
            }
            case com.duy.dx.cf.code.ByteOps.GETSTATIC: {
                return com.duy.dx.rop.code.RegOps.GET_STATIC;
            }
            case com.duy.dx.cf.code.ByteOps.PUTSTATIC: {
                return com.duy.dx.rop.code.RegOps.PUT_STATIC;
            }
            case com.duy.dx.cf.code.ByteOps.GETFIELD: {
                return com.duy.dx.rop.code.RegOps.GET_FIELD;
            }
            case com.duy.dx.cf.code.ByteOps.PUTFIELD: {
                return com.duy.dx.rop.code.RegOps.PUT_FIELD;
            }
            case com.duy.dx.cf.code.ByteOps.INVOKEVIRTUAL: {
                com.duy.dx.rop.cst.CstMethodRef ref = (com.duy.dx.rop.cst.CstMethodRef) cst;
                // The java bytecode specification does not explicitly disallow
                // invokevirtual calls to any instance method, though it
                // specifies that instance methods and private methods "should" be
                // called using "invokespecial" instead of "invokevirtual".
                // Several bytecode tools generate "invokevirtual" instructions for
                // invocation of private methods.
                //
                // The dalvik opcode specification on the other hand allows
                // invoke-virtual to be used only with "normal" virtual methods,
                // i.e, ones that are not private, static, final or constructors.
                // We therefore need to transform invoke-virtual calls to private
                // instance methods to invoke-direct opcodes.
                //
                // Note that it assumes that all methods for a given class are
                // defined in the same dex file.
                //
                // NOTE: This is a slow O(n) loop, and can be replaced with a
                // faster implementation (at the cost of higher memory usage)
                // if it proves to be a hot area of code.
                if (ref.getDefiningClass().equals(method.getDefiningClass())) {
                    for (int i = 0; i < methods.size(); ++i) {
                        final Method m = methods.get(i);
                        if (AccessFlags.isPrivate(m.getAccessFlags()) &&
                                ref.getNat().equals(m.getNat())) {
                            return com.duy.dx.rop.code.RegOps.INVOKE_DIRECT;
                        }
                    }
                }
                // If the method reference is a signature polymorphic method
                // substitute invoke-polymorphic for invoke-virtual. This only
                // affects MethodHandle.invoke and MethodHandle.invokeExact.
                if (ref.isSignaturePolymorphic()) {
                    return com.duy.dx.rop.code.RegOps.INVOKE_POLYMORPHIC;
                }
                return com.duy.dx.rop.code.RegOps.INVOKE_VIRTUAL;
            }
            case com.duy.dx.cf.code.ByteOps.INVOKESPECIAL: {
                /*
                 * Determine whether the opcode should be
                 * INVOKE_DIRECT or INVOKE_SUPER. See vmspec-2 section 6
                 * on "invokespecial" as well as section 4.8.2 (7th
                 * bullet point) for the gory details.
                 */
                /* TODO: Consider checking that invoke-special target
                 * method is private, or constructor since otherwise ART
                 * verifier will reject it.
                 */
                com.duy.dx.rop.cst.CstMethodRef ref = (com.duy.dx.rop.cst.CstMethodRef) cst;
                if (ref.isInstanceInit() ||
                    (ref.getDefiningClass().equals(method.getDefiningClass()))) {
                    return com.duy.dx.rop.code.RegOps.INVOKE_DIRECT;
                }
                return com.duy.dx.rop.code.RegOps.INVOKE_SUPER;
            }
            case com.duy.dx.cf.code.ByteOps.INVOKESTATIC: {
                return com.duy.dx.rop.code.RegOps.INVOKE_STATIC;
            }
            case com.duy.dx.cf.code.ByteOps.INVOKEINTERFACE: {
                return com.duy.dx.rop.code.RegOps.INVOKE_INTERFACE;
            }
            case com.duy.dx.cf.code.ByteOps.INVOKEDYNAMIC: {
                return com.duy.dx.rop.code.RegOps.INVOKE_CUSTOM;
            }
            case com.duy.dx.cf.code.ByteOps.NEW: {
                return com.duy.dx.rop.code.RegOps.NEW_INSTANCE;
            }
            case com.duy.dx.cf.code.ByteOps.NEWARRAY:
            case com.duy.dx.cf.code.ByteOps.ANEWARRAY: {
                return com.duy.dx.rop.code.RegOps.NEW_ARRAY;
            }
            case com.duy.dx.cf.code.ByteOps.ARRAYLENGTH: {
                return com.duy.dx.rop.code.RegOps.ARRAY_LENGTH;
            }
            case com.duy.dx.cf.code.ByteOps.ATHROW: {
                return com.duy.dx.rop.code.RegOps.THROW;
            }
            case com.duy.dx.cf.code.ByteOps.CHECKCAST: {
                return com.duy.dx.rop.code.RegOps.CHECK_CAST;
            }
            case com.duy.dx.cf.code.ByteOps.INSTANCEOF: {
                return com.duy.dx.rop.code.RegOps.INSTANCE_OF;
            }
            case com.duy.dx.cf.code.ByteOps.MONITORENTER: {
                return com.duy.dx.rop.code.RegOps.MONITOR_ENTER;
            }
            case ByteOps.MONITOREXIT: {
                return RegOps.MONITOR_EXIT;
            }
        }

        throw new RuntimeException("shouldn't happen");
    }

    private Insn makeInvokePolymorphicInsn(Rop rop, SourcePosition pos, RegisterSpecList sources,
                                           TypeList catches, Constant cst) {
        com.duy.dx.rop.cst.CstMethodRef cstMethodRef = (CstMethodRef) cst;
        return new InvokePolymorphicInsn(rop, pos, sources, catches, cstMethodRef);
    }
}
