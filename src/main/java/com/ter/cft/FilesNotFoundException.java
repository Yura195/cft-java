package com.ter.cft;

/**
 *  Ошибка отсутствия передаваемых файлов
 */
public class FilesNotFoundException extends Exception {
    public FilesNotFoundException(String message) {
        super(message);
    }
}
