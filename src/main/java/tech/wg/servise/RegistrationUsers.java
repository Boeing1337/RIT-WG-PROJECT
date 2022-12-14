package tech.wg.servise;

import tech.ioc.annotations.Component;
import tech.ioc.annotations.InjectObject;
import tech.wg.context.GlobalVariable;
import tech.wg.dao.PlayersScoreRepository;
import tech.wg.dao.ScoreEntity;
import tech.wg.dao.UserEntity;
import tech.wg.dao.UserRepository;
import tech.wg.servise.login.LoginResolver;
import tech.wg.tools.Grammar;

import java.util.Optional;

@Component
public class RegistrationUsers {
    @InjectObject
    private Grammar grammar;
    @InjectObject
    private UserRepository userRepository;
    @InjectObject
    private Encryption encryption;
    @InjectObject
    private GameMenu gameMenu;
    @InjectObject
    private PlayersScoreRepository playersScoreRepository;
    @InjectObject
    private LoginResolver loginResolver;


    private boolean checkDoublePass(String pass1, String pass2) {
        return pass2.equals(pass1);
    }

    public void registrationUser() {
        boolean allows = true;
        String pass1 = null;
        while (allows) {
            grammar.println("Введите логин или нажмите 0, чтобы вернуться в предыдущее меню");
            Optional<String> optionalLogin = loginResolver.resolve();
            if (optionalLogin.isEmpty()) {
                continue;
            }
            String login = optionalLogin.get();
            if ("0".equals(login)) {
                return;
            }
            if (userRepository.isUserPresents(login)) {
                grammar.println("Такой логин уже занят. Пожалуйста, попробуйте еще раз");
                continue;
            }
            while (allows) {
                grammar.println("Введите пароль, или нажмите 0, чтобы вернуться в предыдущее меню");
                pass1 = grammar.nextLine();
                if ("0".equals(pass1)) {
                    break;
                }
                if ("".equals(pass1)) {
                    grammar.println("Пароль должен содержать значение");
                    continue;
                }
                while (allows) {
                    grammar.println("Введите пароль повторно, или нажмите 0, чтобы вернуться в предыдущее меню");
                    String pass2 = grammar.nextLine();
                    if ("0".equals(pass2)) {
                        break;
                    }
                    if (checkDoublePass(pass1, pass2)) {
                        allows = false;
                    } else {
                        grammar.println("Ваш первый и второй пароль разные. Пожалуйста, попробуйте еще раз");
                    }
                }
            }
            if (!allows) {
                createdFiles(login, pass1);
            }
        }
    }

    private void createdFiles(String login, String pass1) {
        if (userRepository.createUser(login, encryption.action(pass1))) {
            GlobalVariable.setCurrentUser(new UserEntity(login, pass1));
            playersScoreRepository.saveScore(new ScoreEntity(GlobalVariable.currentUser.getLogin(),
                    0, 0, 0.00, 0));
            gameMenu.startGameMenu();
        } else {
            grammar.println("К сожалению произошла критическая ошибка при создании логина, пожалуйста" +
                    " перезайдите в игру и попробуйте снова");
        }

    }
}
