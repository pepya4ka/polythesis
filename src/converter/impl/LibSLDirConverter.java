package converter.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.pepyachka.converter.Converting;
import com.pepyachka.libsl.Automaton;
import com.pepyachka.libsl.Fun;
import com.pepyachka.libsl.TypeAlias;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pepyachka.libsl.Fun.DEFAULT_RETURN_TYPE_NAME;

public class LibSLDirConverter implements Converting {

    private File dir;
    private final Map<String, Integer> library;
    private final Set<String> includes;
    private final Map<String, TypeAlias> typeAliasMap;
    private final List<Automaton> automatons;

    public LibSLDirConverter(String path) {
        this.dir = new File(path);
        library = new HashMap<>();
        includes = new HashSet<>();
        typeAliasMap = new HashMap<>();
        automatons = new ArrayList<>();
    }

    @Override
    public void printLsl() {
        try (PrintStream fileOut = new PrintStream("src/main/resources/okhttpLibSLFromDir.lsl")) {

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
            fileOut.printf("library %s version \"1.0.0\" language \"java\" url \"https://github.com\";%n", jarPackage.replace(".", "_"));

            fileOut.println();

            includes.forEach(include -> fileOut.printf("include %s;%n", include));
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
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        List<File> files = Arrays.stream(dir.listFiles())
                .flatMap(file -> file.isDirectory() ? Arrays.stream(Objects.requireNonNull(file.listFiles())) : Stream.of(file))
                .filter(file -> file.getName().endsWith(".java"))
                .collect(Collectors.toList());

        for (File file : files) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                CompilationUnit cu = StaticJavaParser.parse(fileInputStream);

                cu.getTypes().forEach(clazz -> {
                    if (clazz.isClassOrInterfaceDeclaration() && !clazz.asClassOrInterfaceDeclaration().isAbstract()) {
                        String simpleClazzName = clazz.getName().asString();
                        Name clazzPackageName = cu.getPackageDeclaration().orElseThrow(IllegalArgumentException::new).getName();
                        String clazzName = getClazzName(simpleClazzName, clazzPackageName);

                        cu.getImports().forEach(importDeclaration -> includes.add(importDeclaration.getName().asString()));

                        Arrays.stream(cu.getPackageDeclaration()
                                        .orElseThrow(IllegalArgumentException::new)
                                        .getName()
                                        .asString()
                                        .split("\\."))
                                .forEach(packageName -> library.put(packageName, library.getOrDefault(packageName, 0) + 1));

                        typeAliasPut(clazzName);

                        Automaton automaton = new Automaton(simpleClazzName);

                        for (MethodDeclaration method : clazz.getMethods()) {
                            Fun fun = new Fun(method.getNameAsString());

                if (!clazz.asClassOrInterfaceDeclaration().isInterface()) {
                    List<String> listOfParametersInImpactFromEnvironment
                            = getListOfParametersInImpactFromEnvironment(method);
                    List<String> listOfNameParamsOfMethod = method.getParameters()
                            .stream()
                            .map(parameter -> parameter.getName().asString())
                            .collect(Collectors.toList());
                    Set<String> setOfNumberParams = listOfParametersInImpactFromEnvironment.stream()
                            .map(listOfNameParamsOfMethod::indexOf)
                            .map(i -> String.format("arg%d", i))
                            .collect(Collectors.toSet());
                    fun.addParamsInImpactFromEnvironment(setOfNumberParams);
                            }

                            NodeList<Parameter> parameters = method.getParameters();
                            parameters.forEach(parameter -> {
                                String simpleParameterClazzName = parameter.getType().asString();
                                TypeAlias parameterTypeTypeAlias = typeAliasPut(simpleParameterClazzName);

                                fun.addParams(typeAliasMap.get(parameterTypeTypeAlias.getClazzName()).getName());
                            });

                            String methodReturnTypeName = method.getType().toString();
                            TypeAlias typeAliasReturnType = new TypeAlias(methodReturnTypeName);
                            if (!methodReturnTypeName.equals(DEFAULT_RETURN_TYPE_NAME)) {
                                typeAliasReturnType = typeAliasPut(methodReturnTypeName);
                            }

                            fun.setReturnType(typeAliasReturnType.getName());
                            automaton.addFun(fun);

                        }
                        automatons.add(automaton);
                    }
                });

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public File getLSL() {
        return null;
    }

    private TypeAlias typeAliasPut(String clazzName) {
        if (clazzName.matches("[A-z]*<[A-Za-z<>\\s?]*[,A-Za-z<>\\s]*>")) {
            clazzName = clazzName.replaceFirst("<[A-Za-z<>\\s?]*[,A-Za-z<>\\s]*>$", "");
        }
        TypeAlias typeAlias = new TypeAlias(clazzName);
        typeAliasMap.put(clazzName, typeAlias);
        return typeAlias;
    }

    private String getClazzName(String simpleClazzName, Name clazzPackageName) {
        return Stream.of(clazzPackageName, simpleClazzName)
                .map(String::valueOf)
                .collect(Collectors.joining("."));
    }

    private List<String> getListOfParametersInImpactFromEnvironment(MethodDeclaration methodDeclaration) {
        return methodDeclaration.getBody()
                .orElseThrow(IllegalArgumentException::new)
                .getStatements()
                .stream()
                .filter(Statement::isExpressionStmt)
                .filter(statement -> {
                    if (methodDeclaration.getParameters().isEmpty()) {
                        return false;
                    }
                    List<String> paramsList = methodDeclaration.getParameters().stream()
                            .map(parameter -> parameter.getName().asString())
                            .collect(Collectors.toList());
                    Expression expression = statement.toExpressionStmt()
                            .orElseThrow(IllegalArgumentException::new)
                            .getExpression();
                    if (expression.isAssignExpr()) {
                        return paramsList.contains(expression.asAssignExpr()
                                .getTarget()
                                .toString());
                    } else if (expression.isMethodCallExpr() && expression.asMethodCallExpr().hasScope()) {
                        return paramsList.contains(expression.asMethodCallExpr()
                                .getScope()
                                .orElseThrow(IllegalArgumentException::new)
                                .toString());

                    }
                    return false;
                })
                .map(
                        statement -> statement.toExpressionStmt()
                                .orElseThrow(IllegalArgumentException::new)
                                .getExpression()
                )
                .map(expression -> {
                    if (expression.isAssignExpr()) {
                        return expression.asAssignExpr()
                                .getTarget()
                                .toString();
                    } else if (expression.isMethodCallExpr()) {
                        return expression.asMethodCallExpr()
                                .getScope()
                                .orElseThrow(IllegalArgumentException::new)
                                .toString();
                    }
                    return "";
                })
                .collect(Collectors.toList());
    }
}