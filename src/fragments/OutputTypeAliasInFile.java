typeAliasMap.forEach((key, value) -> fileOut.printf("typealias %s = %s;%n", value.getName(), value.getTypeName()));