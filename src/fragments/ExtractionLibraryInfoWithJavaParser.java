cu.getImports().forEach(importDeclaration -> includes.add(importDeclaration.getName().asString()));

Arrays.stream(cu.getPackageDeclaration()
                .orElseThrow(IllegalArgumentException::new)
                .getName()
                .asString()
                .split("\\."))
        .forEach(packageName -> library.put(packageName, library.getOrDefault(packageName, 0) + 1));
