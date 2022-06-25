clazz.getMethods().stream()
        .filter(NodeWithPublicModifier::isPublic)
        .forEach(method -> {
            Fun fun = new Fun(method.getNameAsString());
            ...
            String methodReturnTypeName = method.getType().toString();
            TypeAlias typeAliasReturnType = new TypeAlias(methodReturnTypeName);
            fun.setReturnType(typeAliasReturnType.getName());
    });