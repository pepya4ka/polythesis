package exampleUsage;

import com.pepyachka.converter.LibSLConverter;
import com.pepyachka.converter.impl.LibSLJarConverter;

import java.io.*;

public class AnalysisFromJarFile extends ClassLoader {

    private static final String MY_JAR_PATH = "/Users/pepyachka/IdeaProjects/javasymbolsolver-maven-sample/src/main/resources/SimpleMavenProjectForCreatingJarFile-1.0-SNAPSHOT.jar";
//    private static final String MY_JAR_PATH = "src/main/resources/SimpleMavenProjectForCreatingJarFile-1.0-SNAPSHOT.jar";
    private static final String OK_HTTP_JAR_PATH_KOTLIN = "/Users/pepyachka/IdeaProjects/javasymbolsolver-maven-sample/src/main/resources/okhttp-4.9.0.jar";
    private static final String OK_HTTP_JAR_PATH_JAVA = "/Users/pepyachka/IdeaProjects/javasymbolsolver-maven-sample/src/main/resources/okhttp-4.9.0.jar";

    public static void main(String[] args) throws IOException {
        LibSLConverter libSLConverter = new LibSLJarConverter(MY_JAR_PATH);
        libSLConverter.createLSL();
        libSLConverter.printLsl();
    }
}