package servlets;

import jakarta.persistence.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;

import java.io.*;

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


        for(int i = 0; i< utenti.length; i++) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.persist(utenti[i]);
            entityTransaction.commit();
            entityManager.close();
        }
        entityManagerFactory.close();
        PrintWriter out = response.getWriter();
        out.println("User has been created");
    }
}