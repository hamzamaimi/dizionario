package servlets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import utils.ParametersLabels;
import utils.ProjectUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static utils.ProjectUtils.responseWithErrorAndCloseEntityManagers;
import static utils.ProjectUtils.setResponseType;

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

        em.getTransaction().begin();
//        Object result = em.createQuery("INSERT INTO Translation (originalWord) values ('hello')");
        em.getTransaction().commit();


        String authToken;
        try{
            authToken = jsonObjectRequest.getString(ParametersLabels.AUTH_TOKEN);
        }catch (Exception e){
            responseWithErrorAndCloseEntityManagers(entityManagerFactory, jsonObjectResponse, em, out,
                    "Token is wrong!");
            return;
        }

//        getTranslationsFromDb();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }




}
