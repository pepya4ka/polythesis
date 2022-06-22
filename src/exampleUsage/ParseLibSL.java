package exampleUsage;

import org.jetbrains.research.libsl.LibSL;
import org.jetbrains.research.libsl.asg.Library;

import java.io.File;

public class ParseLibSL {

    private static final String PATH_LIBSL_FILE_FROM_JAR_FILE = "src/main/resources/okhttpLibSLFromJarFile.lsl";
    private static final String PATH_LIBSL_FILE_OKHTTP3 = "src/main/resources/okhttp3.lsl";
    private static final String PATH_LIBSL_FILE_FROM_DIR = "src/main/resources/okhttpLibSLFromDir.lsl";

    public static void main(String[] args) {
        File lslFile = new File(PATH_LIBSL_FILE_FROM_DIR);
        LibSL libSL = new LibSL(PATH_LIBSL_FILE_FROM_DIR);
        Library library = libSL.loadFromFile(lslFile);

        System.out.println(library);
    }

}
