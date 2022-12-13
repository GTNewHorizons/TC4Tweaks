package net.glease.tc4tweak.asm;

import static net.glease.tc4tweak.asm.ASMConstants.ASMCALLHOOKSERVER_INTERNAL_NAME;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

class ItemWandCastingVisitor extends ClassVisitor {
    public ItemWandCastingVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ((("getFocus".equals(name)
                        && "(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/wands/ItemFocusBasic;".equals(desc)))
                || ("getFocusItem".equals(name)
                        && "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;".equals(desc))) {
            TC4Transformer.log.debug("Visiting method {}", name);
            return new LoadItemStackNullCheckVisitor(api, mv);
        } else {
            return mv;
        }
    }

    private static class LoadItemStackNullCheckVisitor extends MethodVisitor {
        private Label elseBranchStart = null;

        public LoadItemStackNullCheckVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
            if (elseBranchStart == null) elseBranchStart = label;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            if (opcode == INVOKESTATIC
                    && "net/minecraft/item/ItemStack".equals(owner)
                    && ("func_77949_a".equals(name) || "loadItemStackFromNBT".equals(name))
                    && "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/item/ItemStack;".equals(desc)) {
                TC4Transformer.log.debug("Adding null check");
                mv.visitInsn(DUP);
                Label branchNonnull = new Label();
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        ASMCALLHOOKSERVER_INTERNAL_NAME,
                        "isValidFocusItemStack",
                        "(Lnet/minecraft/item/ItemStack;)Z",
                        false);
                mv.visitJumpInsn(IFNE, branchNonnull);
                mv.visitInsn(POP);
                mv.visitJumpInsn(GOTO, elseBranchStart);
                mv.visitLabel(branchNonnull);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            // dup -> maxStack + 1
            super.visitMaxs(3, maxLocals);
        }
    }
}
