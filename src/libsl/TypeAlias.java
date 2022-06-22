package libsl;

import org.apache.bcel.generic.Type;

import java.util.*;

//    typealias ArrayInt = array<int>;
public class TypeAlias {

    private final String clazzName;//java.io...
    private final String simpleClazzName;
    private final String typeName;//array<int>
    private final String typeAliasName;//typealias ArrayInt

    public TypeAlias(Type type) {
        this.clazzName = type.toString();
        this.simpleClazzName = getSimpleClassName(type.toString());
        this.typeName = generateTypeName();
        this.typeAliasName = generateTypeAliasName();
    }

    public TypeAlias(String clazzName) {
        this.clazzName = clazzName;
        this.simpleClazzName = getSimpleClassName(clazzName);
        this.typeName = generateTypeName();
        this.typeAliasName = generateTypeAliasName();
    }

    public String getClazzName() {
        return clazzName;
    }

    public String getName() {
        return typeAliasName;
    }

    public String getTypeName() {
        return typeName;
    }

    private String generateTypeAliasName() {
        String typeAliasName = this.simpleClazzName;
        while (typeAliasName.contains("[]")) {
            typeAliasName = "Array" + typeAliasName.replaceFirst("\\[]", "");
        }
        return typeAliasName;
    }

    private String generateTypeName() {
        String s = this.simpleClazzName;
        int count = 0;
        while (s.contains("[]")) {
            count++;
            s = s.replaceFirst("\\[]", "");
        }
        for (int i = 0; i < count; i++) {
            s = String.format("array<%s>", s.toLowerCase(Locale.ROOT));
        }
        return s;
    }

    private String getSimpleClassName(String className) {
        List<String> listClassNames = Arrays.asList(className.split("\\."));
        Collections.reverse(listClassNames);
        return listClassNames.stream().findFirst().orElseThrow(NoSuchElementException::new);
    }
}
