File lslFile = new File(PATH_LIBSL_FILE_FROM_DIR);
LibSL libSL = new LibSL(PATH_LIBSL_FILE_FROM_DIR);
Library library = libSL.loadFromFile(lslFile);

System.out.println(library);