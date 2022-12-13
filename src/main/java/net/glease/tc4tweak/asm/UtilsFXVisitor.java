package net.glease.tc4tweak.asm;

import static net.glease.tc4tweak.asm.ASMConstants.ASMCALLHOOK_INTERNAL_NAME;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

class UtilsFXVisitor extends ClassVisitor {
    public UtilsFXVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("getParticleTexture")) {
            ASMUtils.writeMethodDeflected(ASMCALLHOOK_INTERNAL_NAME, name, mv, null, desc);
            return null;
        }
        return mv;
    }
}
