package br.com.denyssousa.todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Se quiser apenas get, usar @Getters
 * Se quiser apenas set, usar @Setters
 * Se quiser diferenciar por variável
 * colocar acima da mesma
 */
@Data
@Entity(name = "tb_usuarios")
public class UserModel {

    @Id
    @GeneratedValue(generator = "UIUID")
    private UUID id;

    @Column(unique = true)
    private String username;
    private String name;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;
}