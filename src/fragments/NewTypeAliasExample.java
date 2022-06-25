private TypeAlias typeAliasPut(String clazzName) {
        if (clazzName.matches("[A-z]*<[A-Za-z<>\\s?]*[,A-Za-z<>\\s]*>")) {
            clazzName = clazzName.replaceFirst("<[A-Za-z<>\\s?]*[,A-Za-z<>\\s]*>$", "");
        }
        TypeAlias typeAlias = new TypeAlias(clazzName);
        typeAliasMap.put(clazzName, typeAlias);
        return typeAlias;
}