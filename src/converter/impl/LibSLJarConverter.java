package converter.impl;

import com.pepyachka.converter.LibSLConverter;
import com.pepyachka.jar.JarInputStreamPoly;
import com.pepyachka.libsl.Automaton;
import com.pepyachka.libsl.Fun;
import com.pepyachka.libsl.TypeAlias;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.Type;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static com.pepyachka.libsl.Fun.DEFAULT_RETURN_TYPE_NAME;

public class LibSLJarConverter extends LibSLConverter {

    private static final String CONSTRUCTOR_NAME_METHOD = "<init>";

    private final String jarPath;
    private final Map<String, TypeAlias> typeAliasMap;
    private final Map<String, Integer> library;
    private final List<Automaton> automatons;

    public LibSLJarConverter(String filePath) throws IOException {
        super(Files.newInputStream(Paths.get(filePath)));
        this.jarPath = filePath;
        library = new HashMap<>();
        typeAliasMap = new HashMap<>();
        automatons = new ArrayList<>();
    }

    @Override
    public void printLsl() {
        try (PrintStream fileOut = new PrintStream("src/main/resources/okhttpLibSLFromJarFile.lsl")) {

            fileOut.println("libsl \"1.0.0\";");


            String jarPackage = library.entrySet()
                    .stream()
                    .filter(e -> e.getValue().equals(library.values()
                            .stream()
                            .mapToInt(i -> i)
                            .max()
                            .orElseThrow(NoSuchElementException::new)))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining("."));
            fileOut.printf("library %s;%n", jarPackage);

            fileOut.println();

            typeAliasMap.forEach((key, value) -> fileOut.printf("typealias %s = %s;%n", value.getName(), value.getTypeName()));

            fileOut.println();

            automatons.forEach(a -> fileOut.println(a.toString()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createLSL() {

        try (JarInputStreamPoly jarInputStream = new JarInputStreamPoly(source);
             JarFile jarFile = new JarFile(jarPath)) {


            jarInputStream.getJarEntries().forEach(jarEntry -> {
                if (jarEntry.getName().endsWith(".class")) {

                    //Add package path to library for creating package name
                    for (String e : jarEntry.getName().split("/")) {
                        library.put(e, library.getOrDefault(e, 0) + 1);
                    }

                    //Parsing jarEntry
                    try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {

                        ClassParser classParser = new ClassParser(inputStream, jarEntry.getName());
                        JavaClass classParsed = classParser.parse();

                        //Filter for only class
                        if (!classParsed.isInterface()) {
//                            System.out.printf("%s:%n", jarEntry.getName().replace("/", "."));

                            //Add new typeAliases
                            String simpleClassName = getSimpleClassName(classParsed.getClassName());
                            typeAliasMap.put(classParsed.getClassName(), new TypeAlias(classParsed.getClassName()));

                            classParsed.dump(new File(String.format("src/main/resources/temp/%s.java", simpleClassName)));

                            //Create new automaton object
                            Automaton automaton = new Automaton(simpleClassName);

                            //Parsing class methods
                            Arrays.stream(classParsed.getMethods())
                                    .filter(method -> !method.getName().equals(CONSTRUCTOR_NAME_METHOD))
                                    .filter(method -> !method.getName().startsWith("-deprecated_"))
                                    .filter(method -> !method.getName().startsWith("<"))
                                    .forEach(method -> {

                                        Fun fun = new Fun(method.getName().replace("-", ""));

                                        //Adding types of method to typeAliases
                                        List<Type> typesOfMethodArguments = Arrays.asList(method.getArgumentTypes());
                                        typesOfMethodArguments.forEach(type -> {
                                            typeAliasMap.put(type.toString(), new TypeAlias(type));

                                            fun.addParams(typeAliasMap.get(type.toString()).getName());
                                        });

                                        if (!method.getReturnType().toString().equals(DEFAULT_RETURN_TYPE_NAME)) {
                                            typeAliasMap.put(method.getReturnType().toString(), new TypeAlias(method.getReturnType()));
                                        }
                                        fun.setReturnType(method.getReturnType().toString().equals(DEFAULT_RETURN_TYPE_NAME) ? DEFAULT_RETURN_TYPE_NAME : typeAliasMap.get(method.getReturnType().toString()).getName());

                                        automaton.addFun(fun);
                                    });
                            automatons.add(automaton);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getLSL() {
        throw new UnsupportedOperationException();
    }

    private String getSimpleClassName(String className) {
        List<String> listClassNames = Arrays.asList(className.split("\\."));
        Collections.reverse(listClassNames);
        return listClassNames.stream().findFirst().orElseThrow(NoSuchElementException::new);
    }
}
