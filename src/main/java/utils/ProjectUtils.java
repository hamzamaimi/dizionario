package utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectUtils {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static void setResponseType(HttpServletResponse resp){
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }
    public static EntityManagerFactory getEntityManagerFactory(){
        return Persistence.createEntityManagerFactory("dizionario_pu");
    }

    public static void responseWithErrorAndCloseEntityManagers(EntityManagerFactory entityManagerFactory,
                                                         JSONObject jsonObjectResponse, EntityManager entityManager,
                                                         PrintWriter out, String errorMessage){
        jsonObjectResponse.put("error", errorMessage);
        out.print(jsonObjectResponse);
        out.flush();
        closeEntityManagerFactoryAndEntityManager(entityManagerFactory, entityManager);
    }

    public static void closeEntityManagerFactoryAndEntityManager(EntityManagerFactory emf, EntityManager em){
        emf.close();
        em.close();
    }

    public static String generateRandomString (int l, String initialChars) {
        String alphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ01234556789";
        StringBuilder s = new StringBuilder(l);
        int i;
        for ( i=0; i<l; i++) {
            int ch = (int)(alphaNumericStr.length() * Math.random());
            s.append(alphaNumericStr.charAt(ch));
        }
        return (initialChars + s).substring(0, l);
    }

    /**
     *
     * @param initialChars Characters that must be as first characters in the token, often is an id to keep the token unique
     * @return Returns the first 32 chars of the generated token
     */
    public static String createToken(String initialChars) {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String encodeToString = base64Encoder.encodeToString(randomBytes);
        return (initialChars + encodeToString).substring(0, 32);
    }

    /**
     * return an optional object containing a user if token is associated with a user otherwise is null
     *
     * @param authToken the authentication Token
     * @param em entity manager for executes queries
     */
    public static Optional<User> getUserFromAuthToken(String authToken, EntityManager em){
        em.getTransaction().begin();
        Optional result = em.createQuery("FROM User u WHERE u.authToken = :authenticationToken")
                .setParameter("authenticationToken", authToken)
                .getResultList().stream().findFirst();
        em.getTransaction().commit();
        return result;
    }

    public static JSONObject getParameterFromJson(HttpServletRequest request) throws IOException {
        String bodyParameters = request.getReader().lines().collect(Collectors.joining());
        return new JSONObject(bodyParameters);
    }

    public static String generateAndPersistActivationCode(User user, EntityManager em) {
        String activationCode = ProjectUtils.generateRandomString(7, String.valueOf(user.getId()));
        em.getTransaction().begin();
        user.setActivationCode(activationCode);
        em.getTransaction().commit();
        return  activationCode;
    }

    public static Optional<User> getUserFromEmail(String email, EntityManager em) {
        em.getTransaction().begin();
        Optional<User> result = em.createQuery("FROM User u WHERE u.email = :email")
                .setParameter("email", email)
                .getResultList().stream().findFirst();
        em.getTransaction().commit();
        return result;
    }
}
