package servlets;

import jakarta.persistence.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import org.json.JSONObject;

import java.io.*;
import java.util.stream.Collectors;

@WebServlet(name="authentication",urlPatterns={"/authentication"})
public class Authentication extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("dizionario_pu");
        User firstUser = new User("hamza","maimi","hamzamaimi0901@gmail.com","laMiaPassword");
        User secondUser = new User("giuseppe","maimi","hamzamaimi0901@gmail.com","laMiaPassword");
        User thirdUser = new User("micio","maimi","hamzamaimi0901@gmail.com","laMiaPassword");
        User[] utenti = {firstUser, secondUser, thirdUser};

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.persist(utenti[0]);
        entityTransaction.commit();
        entityManager.close();

        entityManagerFactory.close();
        PrintWriter out = response.getWriter();
        out.println("User has been created");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bodyParameters = request.getReader().lines().collect(Collectors.joining());
        JSONObject jsonParameters = new JSONObject(bodyParameters);
        if(!checkParameters(jsonParameters, response)){
            return;
        }
    }

    private boolean checkParameters(JSONObject jsonParameters, HttpServletResponse response) {
        String name = jsonParameters.get("name").toString();
        String surname = jsonParameters.get("surname").toString();
        String password = jsonParameters.get("password").toString();
        String email = jsonParameters.get("email").toString();

        return false;
    }
}