package exampleUsage;

import com.pepyachka.converter.impl.LibSLDirConverter;

public class AnalysisFromDir {

    private static final String PATH_DIR_MY_LIB = "src/main/resources/testlib/com/pepyachka";
    private static final String PATH_DIR_OKHTTP_LIB = "src/main/resources/testlib/okhttp3";

    public static void main(String[] args) {
        LibSLDirConverter libSLDirConverter = new LibSLDirConverter(PATH_DIR_OKHTTP_LIB);
        libSLDirConverter.createLSL();
        libSLDirConverter.printLsl();
    }
}
