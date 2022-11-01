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
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@WebServlet(name="authentication",urlPatterns={"/authentication"})
public class Authentication extends HttpServlet {
    protected final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("dizionario_pu");

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        User firstUser = new User("hamza","maimi","franco@gmail.com","laMiaPassword");
        User secondUser = new User("giuseppe","maimi","hamzamaimi0901@gmail.com","laMiaPassword");
        User thirdUser = new User("micio","maimi","hamzamaimi0901@gmail.com","laMiaPassword");
        User[] utenti = {firstUser, secondUser, thirdUser};

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(utenti[0]);
        entityManager.getTransaction().commit();
        entityManager.close();

        PrintWriter out = response.getWriter();
        out.println("User has been created");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bodyParameters = request.getReader().lines().collect(Collectors.joining());
        JSONObject jsonParameters = new JSONObject(bodyParameters);
        EntityManager em = entityManagerFactory.createEntityManager();
        String name = jsonParameters.get("name").toString();
        String surname = jsonParameters.get("surname").toString();

        if(name.trim().isEmpty() || surname.trim().isEmpty()){
            response.getWriter().println("name and surname are required");
            return;
        }
        if(nameOrSurnameIsInvalid(name, surname)){
            response.getWriter().println("name and surname must contain only letters");
            return;
        }
        if(emailIsInvalid(jsonParameters.get("email").toString())){
            response.getWriter().println("invalid email format");
            return;
        }
        if(emailIsAlreadyInUse(jsonParameters.get("email").toString(), em)){
            response.getWriter().println("email already taken");
            return;
        }
        if(passwordIsInvalid(jsonParameters.get("password").toString())){
            response.getWriter().println("password obsolete");
        }
    }

    private boolean nameOrSurnameIsInvalid(String name, String surname) {
        Pattern p = Pattern.compile("^[a-zA-Z]{1,30}$");
        return !p.matcher(name).matches() || !p.matcher(surname).matches();
    }

    private boolean emailIsInvalid(String email) {
        Pattern p = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        return !p.matcher(email).matches();
    }

    private boolean passwordIsInvalid(String password) {
        //Minimum eight characters, at least one letter, one number and one special character (@.$!%*#?&):
        Pattern p = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@.$!%*#?&])[A-Za-z\\d@.$!%*#?&]{8,}$");
        return !p.matcher(password).matches();
    }

    private boolean emailIsAlreadyInUse(String email, EntityManager em) {
        em.getTransaction().begin();
        List result = em.createQuery("SELECT u FROM User u WHERE u.email = ?1")
                .setParameter(1, email)
                .getResultList();
        em.getTransaction().commit();

        return !result.isEmpty();
    }


}