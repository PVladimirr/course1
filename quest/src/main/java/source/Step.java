package source;

import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

@Getter
@Setter
public class Step implements Comparable<Step>{
    private int id;
    private int firstId;
    private int secondId;
    private int thirdId;
    private int forthId;
    private String text;
    private boolean isFinish;


    public int show() {
        System.out.println(this.text);
        return getAnswer();
    }

    private int getAnswer() {
        int value = -1;
        if (!isFinish) {
            Scanner scanner = new Scanner(System.in);
            if (this.getSecondId() != 0) {
                boolean isNotCorrect = true;
                String answer = "";
                while (isNotCorrect) { // проверка на корректность входящего запроса
                    answer = scanner.nextLine();
                    if (answer.trim().equals("1") || answer.trim().equals("2") || answer.trim().equals("3") || answer.trim().equals("4")) {
                        isNotCorrect = false;
                    } else {
                        System.out.println("Выберите корректный ответ (1-4).");
                    }
                }
                value = Integer.parseInt(answer);
            } else {
                value = 1;
                System.out.println("Нажмите Enter");
                scanner.nextLine();
            }
        }
        return value;
    }

    @Override
    public int compareTo(Step o) {
        return this.getId() - o.getId();
    }
}
