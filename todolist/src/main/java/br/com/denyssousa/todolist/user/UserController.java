package br.com.denyssousa.todolist.user;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
 
  @Autowired //Cuida do siclo de vida, sem precisar fazer new
  IUserRepository userRepository;

      /**
     * Métodos de acesso do http
     * Get - busca uma informação
     * Post - adicionar um dado/informação
     * Put - atualizar um dado/info
     * Delete - remover um dado
     * Patch  - Alterar somente uma parte da info/dado
     * Body - vem dentro do body com  @requestbody
     */
    
    @PostMapping("/new")
    public ResponseEntity<Object> create(@RequestBody UserModel usuario){
        var userFind = this.userRepository.findByUsername(usuario.getUsername());
    
        if (userFind != null){
            return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body("Usuario já cdastrado");
        }
        
        String passoword = BCrypt.withDefaults().hashToString(12, usuario.getPassword().toCharArray());
        usuario.setPassword(passoword);
    
        var userCreate = this.userRepository.save(usuario);
        return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(userCreate);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Object> status() {
    	Date dataHoraAtual = new Date();
    	String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
    	String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
    	return ResponseEntity
    			 .status(HttpStatus.OK)
    			 .body("Aplicação em execução. " + data + " " + hora);
    }
}
