package source;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quest {
    /**
     * ArrayList, содержащий в себе все квестовые ходы
     */
    ArrayList<Step> quest;

    /**
     * В конструкторе производится считывание текстового файла (из папки resources), куда следует положить файл с квестом (инструкции по созданию квеста лежат в той же папке)
     *
     * @throws IOException
     */
    public Quest() throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean isCorrect = false;
        while (!isCorrect) { // проверка на существование такого квеста в папке
            System.out.println("Введите имя квеста:");
            String res = scanner.nextLine();
            File file = new File("src/main/resources/" + res + ".txt");
            if (file.exists()) {
                Path path = Path.of(file.getPath());
                String temp = Files.readString(path); // считываем квест пока что как текстовый файл
                String[] chopped = temp.split("///"); // делим текстовый файл на отдельные страницы квеста
                this.quest = this.prepare(chopped); // складываем отформатированные страницы в папку с квестом
                isCorrect = true;
            } else {
                System.out.println("Уточните наличие данного квеста и правильность его названия.");
            }
        }
    }

    /**
     * Метод по подготовке квеста
     *
     * @param chopped (разделённые страницы квеста в текстовом представлении)
     * @return result (сформированный пакет квестовых страниц в пошаговом представлении)
     */
    private ArrayList<Step> prepare(String[] chopped) {
        ArrayList<Step> result = new ArrayList<>();
        for (int i = 0; i < chopped.length; i++) {
            String text = chopped[i];
            String regex = "\\Q[\\Eid[0-9]*\\Q]\\E"; // поиск местоположения записей по типу [idXXX]
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            ArrayList<Integer> positions = new ArrayList<>(); // позиции начала всех id
            while (matcher.find()) {
                positions.add(matcher.start() + 3);
            }
            ArrayList<Integer> numbers = prepareIds(chopped[i], positions); // подготавливаем id - самого шага и тех шагов, куда ведёт квест
            text = text.replaceAll(regex, ""); // убираем упоминания о шагах в тексте квеста
            Step step = new Step(); // создаём шаг квеста (пока пустой)
            step.setText(text); // заполняем его всеми полученными данными
            step.setId(numbers.get(0));
            step.setFinish(numbers.size() == 1);
            if (!step.isFinish()) {
                if (numbers.size() == 2) {
                    step.setFirstId(numbers.get(1));
                } else {
                    step.setFirstId(numbers.get(1));
                    step.setSecondId(numbers.get(2));
                    step.setThirdId(numbers.get(3));
                    step.setForthId(numbers.get(4));
                }
            }
            result.add(step); // добавляем полученный шаг в лист
        }
        Collections.sort(result); // сортируем данные (на случай, если они были сохранены неправильно)
        return result;
    }

    /**
     * @param s         (строка, из которой мы будем брать значения id)
     * @param positions (позиции, в которых эти id находятся)
     * @return сформированный список позиций
     */
    private ArrayList<Integer> prepareIds(String s, ArrayList<Integer> positions) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            String number = "";
            int pos = 0;
            while (Character.isDigit(s.charAt(positions.get(i) + pos))) {// пока у нас встречаются числа - мы их добавляем в строку (так как это - номер id)
                number += s.charAt(positions.get(i) + pos);
                pos++;
            }
            result.add(Integer.parseInt(number)); // результат превращаем в тип Integer и укладываем в результирующий лист
        }
        return result;
    }

    /**
     * Main-метод, запускающий работу всей программы
     */
    public void go() {
        boolean isNotEnd = true; // квест будет крутиться, пока мы не присвоим в эту переменную false
        Step current = quest.get(0); // начинаем с нолевого элемента
        while (isNotEnd) {
            int result = current.show();
            isNotEnd = result != -1; // проверяем, что квест ещё не закончился
            if (isNotEnd) {
                int idOfNewCurrent = this.findNext(current, result); // получаем id нового шага квеста
                current = quest.get(idOfNewCurrent); // присваиваем в текущий шаг новое событие квеста

                if (current == null || current.getId() != idOfNewCurrent) { // проверка "на всякий случай" - на null и на несоответствие страницы
                    System.out.println("Внимание! Произошла ошибка квеста!");
                    isNotEnd = false;
                }
            }
        }
    }

    /**
     * Поиск id запрошенного следующего шага квеста
     *
     * @param curr   (текущий шаг квеста)
     * @param result (запрошенный порядковый номер id нового шага)
     * @return id нового шага
     */
    private int findNext(Step curr, int result) {
        int idNumber = -1;
        switch (result) {
            case 1:
                idNumber = curr.getFirstId();
                break;
            case 2:
                idNumber = curr.getSecondId();
                break;
            case 3:
                idNumber = curr.getThirdId();
                break;
            case 4:
                idNumber = curr.getForthId();
                break;
        }
        return idNumber;
    }

    public static void main(String[] args) throws IOException {
        Quest q = new Quest();
        q.go();
    }
}
