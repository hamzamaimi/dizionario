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

import static utils.ParametersLabels.AUTHENTICATION_ERROR;
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

        String authToken = "";
        String activationCode = "";
        try {
            authToken = jsonObjectRequest.getString(ParametersLabels.AUTH_TOKEN);
            activationCode = jsonObjectRequest.getString(ParametersLabels.ACTIVATION_CODE);
        }catch (Exception e){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObjectResponse,em,out,
                    "authentication token or activation code is missing.");
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
            responseWithErrorAndCloseEntityManagers(entityManagerFactory,jsonObjectResponse,em,out,
                    "activation code is wrong!");
            return;
        }

        setIsActiveFieldTrue(optionalUser.get(), em);
        jsonObjectResponse.put("success", "Account is been activated.");
        out.print(jsonObjectResponse);
        out.flush();
        closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);
    }
}
