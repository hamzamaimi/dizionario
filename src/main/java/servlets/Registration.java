package servlets;

import jakarta.persistence.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import utils.ParametersLabels;
import utils.ProjectUtils;
import utils.SendEmail;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

import static utils.ParametersLabels.FRONT_END_DOMAIN;
import static utils.ParametersLabels.PROJECT_NAME;
import static utils.ProjectUtils.*;

@WebServlet(name="registration",urlPatterns={"/registration"})
public class Registration extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        EntityManagerFactory entityManagerFactory = ProjectUtils.getEntityManagerFactory();
        JSONObject jsonParameters = ProjectUtils.getParameterFromJson(request);
        EntityManager em = entityManagerFactory.createEntityManager();
        //MAPPING DATA FROM CLIENT
        String name = jsonParameters.get("name").toString();
        String surname = jsonParameters.get("surname").toString();
        String email = jsonParameters.get("email").toString();
        String password = jsonParameters.get("password").toString();

        //SET RESPONSE TYPE
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonObject = new JSONObject();
        PrintWriter out = response.getWriter();


        if(name.trim().isEmpty() || surname.trim().isEmpty()){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObject,em,out,
                    "name and surname are required");
            return;
        }
        if(nameOrSurnameIsInvalid(name, surname)){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObject,em,out,
                    "name and surname must contain only letters");
            return;
        }
        if(emailIsInvalid(email)){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObject,em,out,
                    "invalid email format");
            return;
        }
        if(emailIsAlreadyInUse(email, em)){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObject,em,out,
                    "email already taken");
            return;
        }
        if(passwordIsInvalid(password)){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObject,em,out,
                    "password obsolete");
            return;
        }

        User currentUser = persistNewUserAndSendEmail(em , name, surname, email, password);
        persistAuthToken(ProjectUtils.createToken(String.valueOf(currentUser.getId())), currentUser, em);

        closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);

        jsonObject.put("success", "user correctly created");
        jsonObject.put("userEmail", currentUser.getEmail());
        jsonObject.put("isAccountActive", currentUser.getIsActive());


        //SET HTTP-ONLY COOKIE
        Cookie cookie = getCookie("jwt", currentUser.getAuthToken(), FRONT_END_DOMAIN, true);
        response.addCookie(cookie);

        out.print(jsonObject);
        out.flush();
    }


    private void persistAuthToken(String authToken, User currentUser, EntityManager em) {
        em.getTransaction().begin();
        currentUser.setAuthToken(authToken);
        currentUser.setTokenCreationDate(Date.valueOf(LocalDate.now()));
        em.getTransaction().commit();
    }

    private User persistNewUserAndSendEmail(EntityManager em, String name, String surname, String email, String password) throws IOException {
        //function to compare cripted password with uncripted: BCrypt.checkpw(password, hashed)
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        em.getTransaction().begin();
        User user = new User(name,surname,email,hashed);
        em.persist(user);
        em.getTransaction().commit();
        SendEmail.sendNewEmail(email, PROJECT_NAME+" ATTIVA L'ACCOUNT",
                "QUESTO È IL TUO TOKEN DI VALIDAZIONE: "+ ProjectUtils.generateAndPersistActivationCode(user, em));
        return user;
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
        List result = em.createQuery("FROM User u WHERE u.email = :userEmail")
                .setParameter("userEmail", email)
                .getResultList();
        em.getTransaction().commit();
        return !result.isEmpty();
    }


}