package converter;

import java.io.InputStream;

public abstract class LibSLConverter implements Converting {

    protected InputStream source;

    protected LibSLConverter(InputStream source) {
        this.source = source;
    }
}
