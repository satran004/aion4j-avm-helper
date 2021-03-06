package org.aion4j.avm.helper.local;

import org.aion.avm.core.ClassToolchain;
import org.aion.avm.core.miscvisitors.NamespaceMapper;
import org.aion.avm.core.miscvisitors.PreRenameClassAccessRules;
import org.aion.avm.core.miscvisitors.UserClassMappingVisitor;
import org.aion.avm.core.rejection.RejectionClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AVMClassVerifier {

    private boolean isDebugMode;
    private List<String> inputClasses;

    public AVMClassVerifier(boolean debugMode) {
        this.isDebugMode = debugMode;
    }

    public void setInputClasses(List<String> inputClasses) {
        this.inputClasses = inputClasses;
    }

    public void verify(String clazz, String path) throws IOException {
        commonFilterClass(clazz, path);
    }

    private byte[] commonFilterClass(String classDotName, String path) throws IOException {
        byte[] testBytes = Files.readAllBytes(Paths.get(path));
        return commonFilterBytes(classDotName, testBytes);
    }

    private byte[] commonFilterBytes(String classDotName, byte[] testBytes) throws IOException {
        Set<String> userClassDotNameSet = new HashSet<>(inputClasses);
        PreRenameClassAccessRules singletonRules = createAccessRules(userClassDotNameSet);
        NamespaceMapper mapper = new NamespaceMapper(singletonRules);
        byte[] filteredBytes = new ClassToolchain.Builder(testBytes, 0)
                .addNextVisitor(new RejectionClassVisitor(singletonRules, mapper, isDebugMode))
                .addNextVisitor(new UserClassMappingVisitor(mapper, isDebugMode))
                .addWriter(new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS))
                .build()
                .runAndGetBytecode();

        return filteredBytes;
    }

    private static PreRenameClassAccessRules createAccessRules(Set<String> userClassDotNameSet) {
        return new PreRenameClassAccessRules(userClassDotNameSet, userClassDotNameSet);
    }
}
