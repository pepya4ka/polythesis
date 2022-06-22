//Parsing jarEntry
try (InputStream inputStream = jarFile.getInputStream(jarEntry)){

        ClassParser classParser=new ClassParser(inputStream,jarEntry.getName());
        JavaClass classParsed=classParser.parse();
        ...
}