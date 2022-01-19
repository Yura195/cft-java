Необходимо:
- java 11

собрать: 
```bash 
./gradlew build
```
параметры запуска:
режим сортировки (-a,-d) соотвественно сортировка по возрастанию или убыванию;
тип данных (-i,-s) соотвественно число или строка;

запустить: из папки проекта после сборки
```bash
java -cp build/libs/cft-1.0-SNAPSHOT.jar com.ter.cft.Main {data type flag} [{sort direction type}] {output file path} {input file path} [{input file path} ...]
```

пример запуска
```bash
java -cp build/libs/cft-1.0-SNAPSHOT.jar com.ter.cft.Main -i -a out.txt in1.txt in2.txt in3.txt
```