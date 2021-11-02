package com.duy.multidex;

import java.io.IOException;
import java.io.InputStream;

/**
 * An element of the class path in which class files can be found.
 */
interface ClassPathElement {

    char SEPARATOR_CHAR = '/';

    /**
     * Open a "file" from this {@code ClassPathElement}.
     * @param path a '/' separated relative path to the wanted file.
     * @return an {@code InputStream} ready to read the requested file.
     * @throws IOException if the path can not be found or if an error occurred while opening it.
     */
    InputStream open(String path) throws IOException;

    void close() throws IOException;

    Iterable<String> list();

}
