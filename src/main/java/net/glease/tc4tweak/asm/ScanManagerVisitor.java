package net.glease.tc4tweak.asm;

import static net.glease.tc4tweak.asm.ASMConstants.ASMCALLHOOKSERVER_INTERNAL_NAME;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

class ScanManagerVisitor extends ClassVisitor {
    public ScanManagerVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("generateItemHash") && desc.equals("(Lnet/minecraft/item/Item;I)I")) {
            TC4Transformer.log.debug("Replacing generateItemHash(Lnet/minecraft/item/Item;I)I");
            mv.visitParameter("item", 0);
            mv.visitParameter("meta", 0);
            ASMUtils.writeMethodDeflected(ASMCALLHOOKSERVER_INTERNAL_NAME, name, mv, null, desc);
            return null;
        } else {
            return mv;
        }
    }
}
