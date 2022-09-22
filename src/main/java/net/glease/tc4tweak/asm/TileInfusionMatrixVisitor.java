package net.glease.tc4tweak.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class TileInfusionMatrixVisitor extends ClassVisitor {

    public TileInfusionMatrixVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("craftCycle") && desc.equals("()V")) {
            TC4Transformer.log.debug("Injecting Math#abs before Random#nextInt calls in CraftCycle()");
            return new LoadMathAbsVisitor(api, mv);
        } else {
            return mv;
        }
    }

    private static class LoadMathAbsVisitor extends MethodVisitor {

        public LoadMathAbsVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if(opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/util/Random") && name.equals("nextInt") && desc.equals("(I)I") && !itf) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Math", "abs", "(I)I", false);
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }

    }

}
