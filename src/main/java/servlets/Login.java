package servlets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import utils.ParametersLabels;
import utils.ProjectUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static utils.ParametersLabels.AUTHENTICATION_ERROR;
import static utils.ProjectUtils.*;

@WebServlet(name="login",urlPatterns={"/login"})
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObjectRequest = ProjectUtils.getParameterFromJson(req);

        //SET RESPONSE TYPE
        setResponseType(resp);
        JSONObject jsonObjectResponse = new JSONObject();

        PrintWriter out = resp.getWriter();

        EntityManagerFactory entityManagerFactory = ProjectUtils.getEntityManagerFactory();
        EntityManager em = entityManagerFactory.createEntityManager();

        String email = "";
        String password = "";

        try{
            email = jsonObjectRequest.getString(ParametersLabels.EMAIL);
            password = jsonObjectRequest.getString(ParametersLabels.PASSWORD);
        }catch (Exception e){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory, jsonObjectResponse, em, out,
                    "email or password is missing.");
            return;
        }

        Optional<User> optionalUser = ProjectUtils.getUserFromEmail(email, em);

        if(!optionalUser.isPresent()){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory, jsonObjectResponse, em, out,
                    AUTHENTICATION_ERROR);
            return;
        }
        User user = optionalUser.get();

        if(!isPasswordCorrect(user, password)){
            addWrongLoginAttemptForUser(em, user);
            responseWithErrorAndCloseEntityManagers(entityManagerFactory, jsonObjectResponse, em, out,
                    AUTHENTICATION_ERROR);
            return;
        }

        closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);

        jsonObjectResponse.put("success", "Log in done!");
        jsonObjectResponse.put(ParametersLabels.AUTH_TOKEN, user.getAuthToken());
        jsonObjectResponse.put("account is active", user.getIsActive());
        out.print(jsonObjectResponse);
        out.flush();
    }

    private static void addWrongLoginAttemptForUser(EntityManager em, User user) {
        em.getTransaction().begin();
        user.setWrongAttempts(user.getWrongAttempts()+1);
        em.getTransaction().commit();
    }

    private boolean isPasswordCorrect(User user, String password) {
        return BCrypt.checkpw(password, user.getPassword());
    }
}
