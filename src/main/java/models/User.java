package models;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "surname", nullable = false)
    private String surname;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "authToken")
    private String authToken;
    @Column(name = "tokenCreationDate")
    private Date tokenCreationDate;
    @Column(name = "wrongAttempts")
    private int wrongAttempts = 0;
    @Column(name = "activationCode")
    private String activationCode;
    @Column(name = "isActive", nullable = false, columnDefinition="tinyint(1) default 0")
    private boolean isActive;

    public User() {}

    public User(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }
    public String getActivationCode() {return activationCode;}

    public void setActivationCode(String activationCode) {this.activationCode = activationCode;}

    public boolean isActive() {return isActive;}

    public void setIsActive(boolean active) {isActive = active;}

    public int getWrongAttempts() {
        return wrongAttempts;
    }

    public void setWrongAttempts(int wrongAttempts) {
        this.wrongAttempts = wrongAttempts;
    }
    public Date getTokenCreationDate() {
        return tokenCreationDate;
    }

    public void setTokenCreationDate(Date tokenCreationDate) {
        this.tokenCreationDate = tokenCreationDate;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setId (int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
