package tech.wg.servise;

import tech.wg.context.GlobalVariable;
import tech.wg.dao.KeywordsRepository;
import tech.wg.dao.QuestionRepository;
import tech.wg.dao.UserGameStateRepository;
import tech.wg.tools.Grammar;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TheGameService {
    private final Random random = new Random();
    private final KeywordsRepository wordsRepository;
    private final QuestionRepository questionRepository;
    private final UserGameStateRepository userGameStateRepository;
    private final Grammar grammar;
    private String wordToGuess;
    private char[] arrayRandomWord;
    private int[] guessedLetters;
    private boolean stopped = true;
    private int choosePersonLetter;
    private int back = 1;

    public TheGameService(KeywordsRepository wordsRepository, QuestionRepository questionRepository,
                          UserGameStateRepository userGameStateRepository, Grammar grammar) {
        this.wordsRepository = wordsRepository;
        this.questionRepository = questionRepository;
        this.userGameStateRepository = userGameStateRepository;
        this.grammar = grammar;
    }

    public void theGameContinue() {
        String progress = userGameStateRepository.getProgress(GlobalVariable.getCurrentUser().getLogin());
        if ("".equals(progress)) {
            grammar.write("Нет игры, которую можно продолжить. Начни новую игру");
            return;
        }
        String[] a = progress.trim().split(";");
        wordToGuess = a[0];
        arrayRandomWord = new char[wordToGuess.length()];
        guessedLetters = new int[wordToGuess.length()];
        for (int i = 0; i < arrayRandomWord.length; i++) {
            arrayRandomWord[i] = a[1].charAt(i);
            if (arrayRandomWord[i] != '*') {
                guessedLetters[i] = i + 1;
            }
        }
        grammar.write(Arrays.toString(arrayRandomWord));
        continuous();
    }

    public void theGameNew() {
        gameBegin();
        guessedLetters = new int[wordToGuess.length()];
        continuous();
    }

    private void continuous() {
        while (back == 1) {
            stopped = true;
            startGame();
            if (back == 1) {
                isGameFinished();
            }
        }
        if (stopped) {
            grammar.write("Поздравляю, вы набрали 100 очков");
        }
    }

    private String chooseWordToGuess() {
        List<String> myWords = wordsRepository.readKeywords();
        int randomIndexWord = random.nextInt(myWords.size());
        wordToGuess = myWords.get(randomIndexWord);
        return wordToGuess;
    }

    private void gameBegin() {
        arrayRandomWord = new char[chooseWordToGuess().length()];
        Arrays.fill(arrayRandomWord, 0, wordToGuess.length(), '*');
        grammar.write(Arrays.toString(arrayRandomWord));
    }

    private void startGame() {
        while (stopped) {
            boolean retern = true;
            grammar.write("Выбери номер буквы или нажми 0, чтобы вернуться назад");
            if (!grammar.hasNextInt()) {
                grammar.write("Необходимо написать цифру");
                grammar.readLine();
                continue;
            }
            choosePersonLetter = grammar.readInt();
            if (choosePersonLetter == 0) {
                back = 0;
                stopped = false;
                break;
            }
            grammar.readLine();
            for (int i = 0; i < wordToGuess.length(); i++) {
                if (guessedLetters[i] == choosePersonLetter) {
                    grammar.write("Данная буква уже отгадана, выберете другую букву");
                    retern = false;
                    break;
                }
            }
            if (!retern) {
                continue;
            }
            char randomChar = wordToGuess.charAt(choosePersonLetter - 1);
            List<String> quesans = questionRepository.getQuestionAnswerByLetter(String.valueOf(randomChar));
            String[] questions = new String[quesans.size() / 2];
            for (int i = 1, j = 0; i < quesans.size(); i += 2, j++) {
                questions[j] = quesans.get(i);
            }
            String question = questions[random.nextInt(questions.length)];
            grammar.write(question);
            while (stopped) {
                grammar.write("Введите первую букву ответа или нажми 0, чтобы вернуться назад");
                String answer = grammar.readLine();
                if ("0".equals(answer)) {
                    break;
                }
                if (answer.toUpperCase().charAt(0) == randomChar) {
                    grammar.write("Верно");
                    stopped = false;
                } else {
                    grammar.write("Введена не верная буква, попробуте еще раз");
                }
            }
        }
    }

    private void isGameFinished() {
        arrayRandomWord[choosePersonLetter - 1] = wordToGuess.charAt(choosePersonLetter - 1);
        grammar.write(Arrays.toString(arrayRandomWord));
        back = 0;
        stopped = true;
        for (int i = 0; i < wordToGuess.length(); i++) {
            if ('*' == arrayRandomWord[i]) {
                back = 1;
                stopped = false;
                break;
            }
        }
        guessedLetters[choosePersonLetter - 1] = choosePersonLetter;
        userGameStateRepository.writeProgress(GlobalVariable.getCurrentUser().getLogin(), wordToGuess, arrayRandomWord);
    }
}
