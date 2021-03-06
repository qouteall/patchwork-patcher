package com.patchworkmc.objectholder;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.patchworkmc.generator.ConsumerGenerator;

public class ObjectHolderGenerator {
	private ObjectHolderGenerator() {
		// NO-OP
	}

	public static String generate(String targetClass, ObjectHolder entry, ClassVisitor visitor) {
		String shimName = "patchwork_generated/" + targetClass + "_ObjectHolder_" + entry.getField();

		ConsumerGenerator generator = new ConsumerGenerator(visitor, shimName, entry.getDescriptor(), null);

		// Add a default constructor
		generator.visitDefaultConstructor();

		// Add the accept implementation
		MethodVisitor method = generator.visitAccept();

		{
			method.visitVarInsn(Opcodes.ALOAD, 1);

			method.visitFieldInsn(Opcodes.PUTSTATIC, targetClass, entry.getField(), entry.getDescriptor());

			method.visitInsn(Opcodes.RETURN);

			method.visitMaxs(1, 2);
			method.visitEnd();
		}

		// Add the bridge method and finish the visitor
		generator.visitEnd();

		return shimName;
	}
}
