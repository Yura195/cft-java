package com.ter.cft;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Config config;
        try {
            config = parsArgs(args);
        } catch (FilesNotFoundException | FlagNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
            return;
        }

        List<String> rawContent = config.inputFiles.stream()
                .map(Paths::get)
                .map(Path::toFile)
                .filter(file -> file.exists() && file.isFile())
                .flatMap(file -> readFile(file).stream())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<?> sorted = config.type == Type.STRING
                ? sortString(rawContent, config.direction)
                : sortInt(rawContent.stream().flatMap(s -> toInteger(s).stream()).collect(Collectors.toList()), config.direction);

        writeResult(config.outputFile, sorted);
    }

    /**
     * Разбор параметров и создание конфига запуска
     *
     * @param args список переданных параметров
     * @return  конфиг запуска
     * @throws FilesNotFoundException если не указан выходной или входные файлы
     * @throws FlagNotFoundException если не указан ни одни из флагов отвечающих за тип данных в файлах
     */
    private static Config parsArgs(String[] args) throws FilesNotFoundException, FlagNotFoundException {
        Type type = null;
        Direction direction = Direction.ASC;
        List<String> files = new LinkedList<>();

        for (String arg : args) {
            switch (arg) {
                case "-d":
                    direction = Direction.DESC;
                    break;
                case "-a":
                    direction = Direction.ASC;
                    break;
                case "-s":
                    type = Type.STRING;
                    break;
                case "-i":
                    type = Type.INTEGER;
                    break;
                default:
                    if (type == null) {
                        throw new FlagNotFoundException();
                    } else {
                        files.add(arg);
                    }
                    break;
            }
        }

        if (files.size() < 1) {
            throw new FilesNotFoundException("Input and output files are not set");
        } else if (files.size() < 2) {
            throw new FilesNotFoundException("Input files are not set");
        }

        return new Config(type, direction, files.subList(1, files.size()), files.get(0));
    }

    /**
     * Чтение файла
     *
     * @param file файл(объект файла)
     * @return список строк из файла
     */
    public static List<String> readFile(File file) {
        List<String> result = List.of();
        try {
            result = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.out.printf("Cannot read file %s. Reason: %s", file.getAbsolutePath(), e.getMessage());
        }
        return result;
    }

    /**
     * Перевод строки в число
     *
     * @param s строка
     * @return Optional.empty если преобразование не удалось, Optional.value в случае успеха
     */
    public static Optional<Integer> toInteger(String s) {
        try {
            return Optional.of(Integer.valueOf(s));
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    /**
     * Сортировка строковых элементов списка алгоритмом "сортировка слиянием"
     * @param elements список сортируемых элементов
     * @param direction параметр сортировки
     * @return список отсортированных элементов
     */
    private static List<String> sortString(List<String> elements, Direction direction) {
        Comparator<String> comparator = String::compareTo;
        if (direction == Direction.ASC) {
            return sort(elements, comparator);
        } else {
            return sort(elements, comparator.reversed());
        }
    }

    /**
     * Сортировка целочисленных элементов списка алгоритмом "сортировка слиянием"
     * @param elements список сортируемых элементов
     * @param direction параметр сортировки
     * @return список отсортированных элементов
     */
    private static List<Integer> sortInt(List<Integer> elements, Direction direction) {
        Comparator<Integer> comparator = Integer::compareTo;
        if (direction == Direction.ASC) {
            return sort(elements, comparator);
        } else {
            return sort(elements, comparator.reversed());
        }
    }

    /**
     * Сортировка элементов списка алгоритмом "сортировка слиянием"
     * @param elements список сортируемых элементов
     * @param comparator компаратор для сравнения двух элементов
     * @return отсортированный список
     */
    private static <T> List<T> sort(List<T> elements, Comparator<T> comparator) {

        if (elements.size() == 1) return elements;

        List<T> left = sort(elements.subList(0, elements.size() / 2), comparator);
        List<T> right = sort(elements.subList(left.size(), elements.size()), comparator);

        return merge(left, right, comparator);
    }

    /**
     * Получение отсортированного массива из элементов двух отсортированных массивов
     * @param a1 отсортированный массив элементов
     * @param a2 отсортированный массив элементов
     * @param comparator компаратор для сравнения двух элементов
     * @param <T> тип элемента массива
     * @return массив отсортированный порядком заданном comparator из элементов двух входных массивов
     */
    private static <T> List<T> merge(List<T> a1, List<T> a2, Comparator<T> comparator) {
        int resultLength = a1.size() + a2.size();
        List<T> result = new ArrayList<>(resultLength);
        int c1 = 0;
        int c2 = 0;

        do {
            if (c1 == a1.size()) {
                result.add(a2.get(c2));
                c2++;
            } else if (c2 == a2.size()) {
                result.add(a1.get(c1));
                c1++;
            } else if (comparator.compare(a1.get(c1), a2.get(c2)) < 0) {
                result.add(a1.get(c1));
                c1++;
            } else {
                result.add(a2.get(c2));
                c2++;
            }
        } while (result.size() < resultLength);

        return result;

    }

    /**
     * Запись массива в файл
     *
     * @param outFile путь к файлу в который будет идти запись массива
     * @param result  отсортированный массив из входных файлов
     * @param <T>  тип элемента массива
     */
    private static <T> void writeResult(String outFile, List<? extends Object> result) {
        File file = new File(outFile);

        try {
            if (!file.createNewFile()) {
                System.out.println("Could not create output file. Reason: output file already exists");
            }
            PrintWriter printer = new PrintWriter(file);
            result.stream().forEachOrdered(el -> printer.println(el.toString()));
            printer.close();
        } catch (IOException e) {
            System.out.printf("Could not create or write output file. Reason: %s", e.getMessage());
        }
    }
}
