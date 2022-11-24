package dao.questions;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static java.io.File.separator;
import static java.nio.charset.StandardCharsets.UTF_8;

public class QuestionRepository {
    private final ArrayList<List<String>> question = new ArrayList<>();
    private final ArrayList<List<String>> answers = new ArrayList<>();
    private String[] bukvi;

    public static void main(String[] args) throws IOException {
        QuestionRepository d = new QuestionRepository();
        d.submitQuestionsInAConvenient();
    }

    public void reading() {
        try {
            bukvi = new String(Files.readAllBytes(Paths.get("abc and questions"))).split(";(\\r?\\n){2}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitQuestionsInAConvenient() throws IOException {
        Scanner scanner = new Scanner(System.in, UTF_8);
        reading();
        readQuestions();
        readAnswers();
        System.out.println("Введите вопрос");
        String question1 = scanner.nextLine();
        System.out.println("Введите ответ");
        String answers1 = scanner.nextLine();
        for (int i = 0; i < 33; i++) {
            if (answers1.charAt(0) == bukvi[i].charAt(0)) {
                question.get(i).add(question1);
                answers.get(i).add("\n" + answers1 + "\n");
            }
        }
        updateQuestion();
    }

    public void updateQuestion() throws IOException {
        try (FileWriter writer = new FileWriter("." + separator + "abc and questions", UTF_8)) {
            for (int i = 0; i < 33; i++) {
                writer.write(bukvi[i].charAt(0) + ":\n");
                for (int j = 0; j < question.get(i).size(); j++) {
                    writer.write("/" + question.get(i).get(j) + "/");
                    writer.write(answers.get(i).get(j).toUpperCase());
                }
                writer.write(";" + "\n\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void readAnswers() {
        for (int i = 0; i < bukvi.length; i++) {
            String[] a = (bukvi[i].split("/"));
            List<String> b = new ArrayList<>();
            for (int j = 2; j < a.length; j += 2) {
                b.add(a[j]);
            }
            answers.add(b);
        }
    }

    public void readQuestions() {
        for (int i = 0; i < bukvi.length; i++) {
            String[] a = (bukvi[i].split("/"));
            List<String> b = new ArrayList<>();
            for (int j = 1; j < a.length; j += 2) {
                b.add(a[j]);
            }
            question.add(b);
        }
    }

    public List<String> getQuestionBy(String letter) {
        reading();
        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < 33; i++) {
            if (bukvi[i].charAt(0) == letter.charAt(0)) {
                String[] quest = bukvi[i].split("/");
                Collections.addAll(a, quest);
            }
        }
        return a;
    }
}