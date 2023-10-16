package br.com.matheusmirro.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController()
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity<String> create(@RequestBody UserModel userModel) throws Exception {

        var findUser = this.userRepository.findByUsername(userModel.getUsername());

        var hashedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(hashedPassword);

        if (findUser == null) {
            this.userRepository.save(userModel);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Usuário criado com sucesso");

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe!");
        }
    }
}
