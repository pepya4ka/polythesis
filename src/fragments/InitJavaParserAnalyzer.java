CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
combinedTypeSolver.add(new ReflectionTypeSolver());

JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);