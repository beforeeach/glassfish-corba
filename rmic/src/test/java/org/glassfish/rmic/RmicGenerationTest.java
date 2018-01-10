package org.glassfish.rmic;
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

import org.glassfish.rmic.classes.covariantReturn.DogFinder;
import org.glassfish.rmic.classes.exceptiondetailsc.ExceptionSourceServantPOA;
import org.glassfish.rmic.classes.giopheaderpadding.FooServantPOA;
import org.glassfish.rmic.classes.hcks.RmiII;
import org.glassfish.rmic.classes.hcks.RmiIIServant;
import org.glassfish.rmic.classes.hcks.RmiIIServantPOA;
import org.glassfish.rmic.classes.inneraccess.Rainbow;
import org.glassfish.rmic.classes.islocal.MessageBuilderServantPOA;
import org.glassfish.rmic.classes.preinvokepostinvoke.MyServant;
import org.glassfish.rmic.classes.rmipoacounter.CounterImpl;
import org.glassfish.rmic.classes.systemexceptions.ServerInvokerServantPOA;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.fail;

/**
 * Tests RMIC by comparing the kept generated source files against the expected files.
 */
public class RmicGenerationTest {

    private static int testNum = 0;
    private static File rootDir;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @BeforeClass
    public static void clearRootDir() throws IOException {
        rootDir = Files.createTempDirectory("rmic").toFile();
    }

    @Test
    public void whenDefaultGeneratorSpecified_reportJRMPNoLongerSupported() throws Exception {
        GenerationControl generator = new GenerationControl(FooServantPOA.class);

        try {
            generator.generate();
            fail("Should have reported JRMP no longer supported");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("no longer supported"));
        }
     }

    @Test
    public void generateIiopStubsWithoutPoa() throws Exception {
        GenerationControl generator = new GenerationControl(RmiIIServant.class);
        generator.addArgs("-iiop", "-keep");

        generator.generate();

        checkGeneratedFiles(generator, "without_poas", ".java");
    }

    @Test
    public void whenKeepNotSpecified_deleteGeneratedJavaFiles() throws Exception {
        GenerationControl generator = new GenerationControl(RmiIIServant.class);
        generator.addArgs("-iiop");

        generator.generate();

        checkGeneratedFilesDeleted(generator, ".java");
    }

    @Test
    public void afterProcessing_classFilesArePresent() throws Exception {
        GenerationControl generator = new GenerationControl(RmiIIServant.class);
        generator.addArgs("-iiop");

        generator.generate();

        checkClassFilesPresent(generator, "without_poas");
    }


    @Test
    public void generateIiopStubsFromInterface() throws Exception {
        GenerationControl generator = new GenerationControl(RmiII.class);
        generator.addArgs("-iiop", "-keep");

        generator.generate();

        checkGeneratedFiles(generator, "stub_from_interface", ".java");
    }

    @Test
    public void generateIiopStubsWithPoa() throws Exception {
        GenerationControl generator = new GenerationControl(
                ExceptionSourceServantPOA.class, MyServant.class, MessageBuilderServantPOA.class, CounterImpl.class,
                ServerInvokerServantPOA.class, RmiIIServantPOA.class, FooServantPOA.class);
        generator.addArgs("-iiop", "-keep", "-poa");
        generator.generate();

        checkGeneratedFiles(generator, "with_poas", ".java");
    }

    @Test
    public void generateIdlForInnerClass() throws Exception {
        GenerationControl generator = new GenerationControl(Rainbow.getInterfaceCheckerClass());
        generator.addArgs("-idl", "-keep");
        generator.generate();

        checkGeneratedFiles(generator, "idl", ".idl");
    }

    @Test
    public void whenKeepFlagNotSpecified_dontDeleteGeneratedIdlFiles() throws Exception {
        GenerationControl generator = new GenerationControl(Rainbow.getInterfaceCheckerClass());
        generator.addArgs("-idl");
        generator.generate();

        checkGeneratedFiles(generator, "idl", ".idl");
    }

    @Test
    public void generateIdlForInnerClassUsingDotNotation() throws Exception {
        GenerationControl generator = new GenerationControl(Rainbow.getQualifiedCheckerClassName());
        generator.addArgs("-idl", "-keep");
        generator.generate();

        checkGeneratedFiles(generator, "idl", ".idl");
    }

    // NOTE: The test case from which this was based (http://hg.openjdk.java.net/jdk/jdk/file/9a29aa153c20/test/jdk/sun/rmi/rmic/covariantReturns)
    // doesn't actually seem to test the feature. This one verifies that we can generate the stubs, but they appear to me to be incorrect,
    // in that the generated derived type is actually returning the same type as its parent rather than the specified covariant type.
    // More testing will be needed to see if it matters.
    @Test
    public void canHandleCovariantReturns() throws Exception {
        GenerationControl generator = new GenerationControl(DogFinder.class);
        generator.addArgs("-iiop", "-keep");
        generator.generate();
    }

    @Test
    public void whenBinaryIsMissing_dontCompileSources() throws Exception {
        File generatedFile = new File(TestUtils.getClassPathString() + "Interface.java");
        BufferedWriter writer = new BufferedWriter(new FileWriter(generatedFile));
        writer.write("public class Interface implements java.rmi.Remote { }");
        writer.close();

        GenerationControl generator = new GenerationControl("Interface");
        generator.addArgs("-iiop");

        try {
            generator.generate();
            fail("Should not have succeeded in generating a stub");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("Class Interface not found"));
        }
    }

    // Confirms that the generated files match those in the specified directory of master files
    private void checkGeneratedFiles(GenerationControl generator, String mastersSubDir, String suffix) throws IOException {
        File masterDir = new File("src/test/masters/" + mastersSubDir);

        String[] generatedFilePaths = getFilePaths(generator.getDestDir(), suffix);
        String[] expectedFilePaths = getFilePaths(masterDir, suffix);

        assertThat("In " + generator.getDestDir(), generatedFilePaths, arrayContaining(expectedFilePaths));
        compareGeneratedFiles(masterDir, generator.getDestDir(), expectedFilePaths);
    }

    // Verifies that the generated files were deleted
    private void checkGeneratedFilesDeleted(GenerationControl generator, String suffix) throws IOException {
        String[] generatedFilePaths = getFilePaths(generator.getDestDir(), suffix);

        assertThat("In " + generator.getDestDir(), generatedFilePaths, emptyArray());
    }

    // Confirms that the generated files match those in the specified directory of master files
    private void checkClassFilesPresent(GenerationControl generator, String mastersSubDir) throws IOException {
        File masterDir = new File("src/test/masters/" + mastersSubDir);

        String[] generatedFilePaths = getFilePaths(generator.getDestDir(), ".class");
        String[] expectedFilePaths = toClassFilePaths(getFilePaths(masterDir, ".java"));

        assertThat("In " + generator.getDestDir(), generatedFilePaths, arrayContaining(expectedFilePaths));
    }

    private String[] toClassFilePaths(String[] sourceFilePaths) {
        String[] result = new String[sourceFilePaths.length];
        for (int i = 0; i < sourceFilePaths.length; i++)
            result[i] = sourceFilePaths[i].replace(".java", ".class");
        return result;
    }

    // Returns a sorted array of paths to files with the specified suffix under the specified directory, relative to that directory
    private String[] getFilePaths(File rootDir, String suffix) {
        ArrayList<String> files = new ArrayList<>();
        appendFiles(files, rootDir, rootDir.getAbsolutePath().length() + 1, suffix);
        Collections.sort(files);
        return files.toArray(new String[files.size()]);
    }

    @SuppressWarnings("ConstantConditions")
    private void appendFiles(ArrayList<String> files, File currentDir, int rootDirLength, String suffix) {
        for (File file : currentDir.listFiles())
            if (file.isDirectory())
                appendFiles(files, file, rootDirLength, suffix);
            else if (file.getName().endsWith(suffix))
                files.add(getRelativePath(file, rootDirLength));
    }

    private String getRelativePath(File file, int rootDirLength) {
        return file.getAbsolutePath().substring(rootDirLength);
    }
    
    private void compareGeneratedFiles(File expectedDir, File actualDir, String... generatedFileNames) throws IOException {
        for (String filePath : generatedFileNames)
            compareFiles(filePath, expectedDir, actualDir);
    }

    private void compareFiles(String filePath, File masterDirectory, File generationDirectory) throws IOException {
        File expectedFile = new File(masterDirectory, filePath);
        File actualFile = new File(generationDirectory, filePath);

        compareFiles(expectedFile, actualFile);
    }

    private void compareFiles(File expectedFile, File actualFile) throws IOException {
        LineNumberReader expected = new LineNumberReader(new FileReader(expectedFile));
        LineNumberReader actual = new LineNumberReader(new FileReader(actualFile));

        String expectedLine = "";
        String actualLine = "";
        while (expectedLine != null && actualLine != null && expectedLine.equals(actualLine)) {
            expectedLine = expected.readLine();
            actualLine = actual.readLine();
        }

        if (expectedLine == null && actualLine == null) return;

        if (expectedLine == null)
            fail("Unexpected line in generated file at " + actual.getLineNumber() + ": " + actualLine);
        else if (actualLine == null)
            fail("Actual file ends unexpectedly at line " + expected.getLineNumber());
        else if (!expectedLine.trim().startsWith("* IGNORE"))
            fail("Generated file mismatch at line " + actual.getLineNumber() +
                    "\nshould be <" + expectedLine + "> " +
                    "\nbut found <" + actualLine + ">");

    }


    private class GenerationControl {
        private ArrayList<String> argList = new ArrayList<>();
        private String[] classNames;
        private File destDir;
        private String warning;

        @SuppressWarnings("ResultOfMethodCallIgnored")
        GenerationControl(String... classNames) {
            this.classNames = classNames;

            String classPath = TestUtils.getClassPathString();
            destDir = new File(rootDir + "/" + (++testNum));
            destDir.mkdirs();
            addArgs("-classpath", classPath, "-d", destDir.getAbsolutePath());
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        private GenerationControl(Class<?>... classes) {
            this(toNameList(classes));
        }

        private void addArgs(String... args) {
            argList.addAll(Arrays.asList(args));
        }

        File getDestDir() {
            return destDir;
        }

        String getWarning() {
            return warning;
        }

        private void generate() throws IOException {
            for (String name : classNames)
                addArgs(name);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Main compiler = new Main(out, "rmic");
            String[] argv = argList.toArray(new String[argList.size()]);
            if (!compiler.compile(argv))
                throw createException(out);
            else
                warning = toMessage(out);
        }

        private AssertionError createException(ByteArrayOutputStream out) throws IOException {
            String message = toMessage(out);
            if (message == null) message = "No error message reported";
            return new AssertionError(message);
        }

        private String toMessage(ByteArrayOutputStream out) throws IOException {
            out.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
            BufferedReader reader = new BufferedReader(new InputStreamReader(bais));

            StringBuilder sb;
            String line = reader.readLine();
            if (line == null)
                return null;
            else {
                sb = new StringBuilder(line);
                while ((line = reader.readLine()) != null && !line.startsWith("Usage:"))
                    sb.append("/n").append(line);
                return sb.toString();
            }
        }
    }

    private static String[] toNameList(Class<?>[] classes) {
        String[] nameList = new String[classes.length];
        for (int i = 0; i < classes.length; i++)
            nameList[i] = classes[i].getName();
        return nameList;
    }
}
