package servlets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Translation;
import models.User;
import org.json.JSONObject;
import utils.ParametersLabels;
import utils.ProjectUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import static utils.ProjectUtils.*;

@WebServlet(name="manageTranslation",urlPatterns={"/manageTranslation"})
public class ManageTranslations extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObjectRequest = ProjectUtils.getParameterFromJson(req);

        //SET RESPONSE TYPE
        setResponseType(resp);
        JSONObject jsonObjectResponse = new JSONObject();

        PrintWriter out = resp.getWriter();

        EntityManagerFactory entityManagerFactory = ProjectUtils.getEntityManagerFactory();
        EntityManager em = entityManagerFactory.createEntityManager();

        String authToken = getAuthTokenFromReq(jsonObjectRequest, jsonObjectResponse, out, entityManagerFactory, em);
        String groupName = getGroupNameFromReq(jsonObjectRequest, jsonObjectResponse, out, entityManagerFactory, em);

        if(authToken == null || groupName == null){
            return;
        }

        Optional<User> optionalUser = getUserFromAuthToken(authToken, em);
        if(!optionalUser.isPresent()){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory, jsonObjectResponse, em, out,
                    ParametersLabels.TOKEN_ERROR);
            return;
        }

        List<Translation> translationList = getTranslationsFromDb(optionalUser.get(), em, groupName);

        jsonObjectResponse.put("success", "get request correctly executed!");
        jsonObjectResponse.put("translations", translationList);

        closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);
        out.print(jsonObjectResponse);
        out.flush();
    }

    private static String getGroupNameFromReq(JSONObject jsonObjectRequest, JSONObject jsonObjectResponse, PrintWriter out, EntityManagerFactory entityManagerFactory, EntityManager em) {
        String groupName = null;
        try{
            groupName = jsonObjectRequest.getString(ParametersLabels.GROUP_NAME);
        }catch (Exception e){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory, jsonObjectResponse, em, out,
                    ParametersLabels.GROUP_NAME_ERROR);
        }
        return groupName;
    }

    private static String getAuthTokenFromReq(JSONObject jsonObjectRequest, JSONObject jsonObjectResponse, PrintWriter out, EntityManagerFactory entityManagerFactory, EntityManager em) {
       String authToken = null;
        try{
            authToken = jsonObjectRequest.getString(ParametersLabels.AUTH_TOKEN);
        }catch (Exception e){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory, jsonObjectResponse, em, out,
                    ParametersLabels.TOKEN_ERROR);
        }
        return authToken;
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObjectRequest = ProjectUtils.getParameterFromJson(req);
        if(jsonObjectRequest.has("wordId")){
            doUpdate(req, resp);
        }

    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp) {
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private List<Translation> getTranslationsFromDb(User user, EntityManager em, String groupName) {
        TypedQuery<Translation> query = em.createQuery(
                "SELECT e FROM Translation e WHERE e.groupName = ?1 AND e.userId = ?2" , Translation.class);
        query.setParameter(1, groupName);
        query.setParameter(2, user.getId());
        return query.getResultList();
    }


}
