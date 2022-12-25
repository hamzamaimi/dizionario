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

import static utils.ProjectUtils.closeEntityManagerFactoryAndEntityManager;
import static utils.ProjectUtils.setResponseType;

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
            closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);
            jsonObjectResponse.put("error", "authentication token or activation code is missing.");
            out.print(jsonObjectResponse);
            out.flush();
            log(e.getMessage(), e);
            return;
        }

        Optional<User> optionalUser = ProjectUtils.getUserFromAuthToken(authToken, em);

        if(!optionalUser.isPresent()){
            jsonObjectResponse.put("error", "authentication error");
            out.print(jsonObjectResponse);
            out.flush();
            closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);
            return;
        }

        String activationCodeFromDb = optionalUser.get().getActivationCode();
        if(!activationCodeFromDb.equals(activationCode)){
            closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);
            jsonObjectResponse.put("error", "activation code is wrong!");
            out.print(jsonObjectResponse);
            out.flush();
            return;
        }

        setIsActiveFieldTrue(optionalUser.get(), em);
        jsonObjectResponse.put("success", "Account is been activated.");
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
            throw new RuntimeException(e);
        }
    }

}
