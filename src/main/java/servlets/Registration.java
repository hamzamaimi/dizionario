package servlets;

import jakarta.persistence.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import utils.SendEmail;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name="registration",urlPatterns={"/registration"})
public class Registration extends HttpServlet {
    protected final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("dizionario_pu");

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bodyParameters = request.getReader().lines().collect(Collectors.joining());
        JSONObject jsonParameters = new JSONObject(bodyParameters);
        EntityManager em = entityManagerFactory.createEntityManager();
        String name = jsonParameters.get("name").toString();
        String surname = jsonParameters.get("surname").toString();
        String email = jsonParameters.get("email").toString();
        String password = jsonParameters.get("password").toString();

        if(name.trim().isEmpty() || surname.trim().isEmpty()){
            response.getWriter().println("name and surname are required");
            return;
        }
        if(nameOrSurnameIsInvalid(name, surname)){
            response.getWriter().println("name and surname must contain only letters");
            return;
        }
        if(emailIsInvalid(email)){
            response.getWriter().println("invalid email format");
            return;
        }
        if(emailIsAlreadyInUse(email, em)){
            response.getWriter().println("email already taken");
            return;
        }
        if(passwordIsInvalid(password)){
            response.getWriter().println("password obsolete");
            return;
        }
        persistNewUser(em, name, surname, email, password);
        em.close();
        response.getWriter().println("user correctly created");
    }

    private void persistNewUser(EntityManager em, String name, String surname, String email, String password) throws IOException {
        //function to compare cripted password with uncripted: BCrypt.checkpw(password, hashed)
        SendEmail.sendNewEmail("hamzamaimi0901@gmail.com", "ciao", "hola");
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        em.getTransaction().begin();
        em.persist(new User(name,surname,email,hashed));
        em.getTransaction().commit();
    }

    private boolean nameOrSurnameIsInvalid(String name, String surname) {
        Pattern p = Pattern.compile("^[a-zA-Z]{1,30}$");
        return !p.matcher(name).matches() || !p.matcher(surname).matches();
    }

    private boolean emailIsInvalid(String email) {
        Pattern p = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        return !p.matcher(email).matches();
    }

    private boolean passwordIsInvalid(String password) {
        //Minimum eight characters, at least one letter, one number and one special character (@.$!%*#?&):
        Pattern p = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@.$!%*#?&])[A-Za-z\\d@.$!%*#?&]{8,}$");
        return !p.matcher(password).matches();
    }

    private boolean emailIsAlreadyInUse(String email, EntityManager em) {
        em.getTransaction().begin();
        List result = em.createQuery("SELECT u FROM User u WHERE u.email = ?1")
                .setParameter(1, email)
                .getResultList();
        em.getTransaction().commit();

        return !result.isEmpty();
    }


}