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

@WebServlet(name="activateAccount",urlPatterns={"/activateAccount"})
public class ActivateAccount extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        EntityManagerFactory entityManagerFactory = ProjectUtils.getEntityManagerFactory();
        EntityManager em = entityManagerFactory.createEntityManager();

        JSONObject jsonObject = ProjectUtils.getParameterFromJson(request);
        String authToken = jsonObject.getString(ParametersLabels.AUTH_TOKEN);
        String activationCode = jsonObject.getString(ParametersLabels.ACTIVATION_CODE);
        Optional<User> optionalUser = ProjectUtils.getUserFromAuthToken(authToken, em);

        //SET RESPONSE TYPE
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        jsonObject = new JSONObject();
        PrintWriter out = response.getWriter();

        if(!optionalUser.isPresent()){
            jsonObject.put("error", "authentication error");
            out.print(jsonObject);
            out.flush();
            em.close();
            entityManagerFactory.close();
            return;
        }

        String authenticationCodeFromDb = optionalUser.get().getActivationCode();

        em.close();
        entityManagerFactory.close();
    }

}
