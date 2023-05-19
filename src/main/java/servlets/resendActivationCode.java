package servlets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import org.json.JSONObject;
import utils.ProjectUtils;
import utils.SendEmail;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static utils.ParametersLabels.*;
import static utils.ProjectUtils.*;

@WebServlet(name="resendActivationCode",urlPatterns={"/resendActivationCode"})
public class resendActivationCode extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonObjectRequest = ProjectUtils.getParameterFromJson(request);

        //SET RESPONSE TYPE
        setResponseType(response);
        JSONObject jsonObjectResponse = new JSONObject();
        PrintWriter out = response.getWriter();

        EntityManagerFactory entityManagerFactory = ProjectUtils.getEntityManagerFactory();
        EntityManager em = entityManagerFactory.createEntityManager();

        String authToken = getJwtFromCookiesIfPresent(request.getCookies());
        if("".equals(authToken)){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObjectResponse,em,out,
                    AUTH_TOKEN_MISSING);
            return;
        }

        Optional<User> optionalUser = ProjectUtils.getUserFromAuthToken(authToken, em);

        if(!optionalUser.isPresent()){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObjectResponse,em,out,
                    TOKEN_ERROR);
            return;
        }

        String userEmail = optionalUser.get().getEmail();
        SendEmail.sendNewEmail(userEmail, PROJECT_NAME+" ATTIVA L'ACCOUNT",
                "QUESTO È IL TUO TOKEN DI VALIDAZIONE: "+ optionalUser.get().getActivationCode());


        jsonObjectResponse.put("success", EMAIL_SENT);
        jsonObjectResponse.put("email", userEmail);
        out.print(jsonObjectResponse);
        out.flush();
        closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);
    }

}
