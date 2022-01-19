package com.ter.cft;

import java.util.List;

/**
 * Конфигурация запуска приложения
 */
public class Config {
    public final Type type;
    public final Direction direction;
    public final String outputFile;
    public final List<String> inputFiles;

    /**
     * Конструктор объектов
     * @param type тип данных
     * @param direction направление сортировки
     * @param inputFiles входные файлы
     * @param outputFile выходной файл
     */
    public Config(Type type, Direction direction, List<String> inputFiles, String outputFile) {
        this.type = type;
        this.direction = direction;
        this.inputFiles = inputFiles;
        this.outputFile = outputFile;
    }
}
