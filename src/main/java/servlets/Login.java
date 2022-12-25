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
import utils.ParametersLabels;
import utils.ProjectUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

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
            closeEntityManagerFactoryAndEntityManager(entityManagerFactory, em);
            jsonObjectResponse.put("error", "email or password is missing.");
            out.print(jsonObjectResponse);
            out.flush();
            log(e.getMessage(), e);
            return;
        }

        Optional<User> optionalUser = ProjectUtils.getUserFromEmailAndPassword(email, password, em);


    }
}
