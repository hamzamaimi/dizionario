package servlets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import org.json.JSONObject;
import utils.ParametersLabels;
import utils.ProjectUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static utils.ParametersLabels.*;
import static utils.ProjectUtils.*;


@WebServlet(name="activateAccount",urlPatterns={"/activateAccount"})
public class ActivateAccount extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject jsonObjectRequest = ProjectUtils.getParameterFromJson(request);

        //SET RESPONSE TYPE
        setResponseType(response);
        JSONObject jsonObjectResponse = new JSONObject();
        PrintWriter out = response.getWriter();

        EntityManagerFactory entityManagerFactory = ProjectUtils.getEntityManagerFactory();
        EntityManager em = entityManagerFactory.createEntityManager();

        String authToken = "";
        String activationCode = "";
        try {
            authToken = jsonObjectRequest.getString(ParametersLabels.AUTH_TOKEN);
            activationCode = jsonObjectRequest.getString(ParametersLabels.ACTIVATION_CODE);
        }catch (Exception e){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObjectResponse,em,out,
                    AUT_TOK_OR_ACTIVATION_CODE_MISSING);
            log(e.getMessage(), e);
            return;
        }

        Optional<User> optionalUser = ProjectUtils.getUserFromAuthToken(authToken, em);

        if(!optionalUser.isPresent()){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObjectResponse,em,out,
                    AUTHENTICATION_ERROR);
            return;
        }

        String activationCodeFromDb = optionalUser.get().getActivationCode();
        if(!activationCodeFromDb.equals(activationCode)){
            int wrongAttempts = optionalUser.get().getWrongAttempts();
            em.getTransaction().begin();
            optionalUser.get().setWrongAttempts(wrongAttempts + 1);
            em.getTransaction().commit();
            if(wrongAttempts > 4){
                ProjectUtils.generateAndPersistActivationCode(optionalUser.get(), em);
                em.getTransaction().begin();
                optionalUser.get().setWrongAttempts(0);
                em.getTransaction().commit();
                responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObjectResponse,em,out,
                        "activation code has been changed!");
                return;
            }
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObjectResponse,em,out,
                    ACTIVATION_CODE_WRONG);
            return;
        }

        setIsActiveFieldTrue(optionalUser.get(), em);
        jsonObjectResponse.put("success", ACCOUNT_ACTIVATED);
        out.print(jsonObjectResponse);
        out.flush();
        closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);
    }

    private void setIsActiveFieldTrue(User user, EntityManager em) {
        try{
            em.getTransaction().begin();
            user.setIsActive(true);
            em.getTransaction().commit();
        }catch (Exception e){
            log(e.getMessage(), e);
        }
    }

}
