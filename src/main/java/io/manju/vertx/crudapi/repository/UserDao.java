package io.manju.vertx.crudapi.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import io.manju.vertx.crudapi.entity.User;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * This class handles the user data transactions
 * @author e1066
 */
public class UserDao {
    private static UserDao instance;
    protected EntityManager entityManager;

    public static UserDao getInstance()
    	{
        if (instance == null){
            instance = new UserDao();
        }
        return instance;
    	}

    private UserDao()
    	{
        entityManager = getEntityManager();
    	}

    private EntityManager getEntityManager()
    	{
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("crudHibernatePU");
        if (entityManager == null) {
            entityManager = factory.createEntityManager();
        }
        return entityManager;
    	}
	 
    
    /**
     *  This method should return user info for given user name
     * @param name
     * @return
     */
	   
	 public User getByUsername(String name) {
		 User user = null;
	    	try {
	    		List<User> users = entityManager.createQuery(
			    		"FROM User WHERE user_name = :name", User.class)
		          .setParameter("name", name)
		          .getResultList();
	    		user=users.get(0);
	    	}
	    	catch(Exception ex) {
	    	ex.printStackTrace();
	    	}
	    	return user;
	    	}
	 
	 /**
	  * This method check the given user name same as user table exsiting user name 
	  * @param context
	  * @param name
	  * @return
	  */
	   
     public User getByName(RoutingContext context,String name)
	  {
	      try{Object result = entityManager.createQuery( "SELECT s FROM User s WHERE s.name LIKE :user_name")
	    	        .setParameter("user_name", name)
	    	        .getSingleResult();
	      System.out.print(result);

	      if (result != null) {
	          return (User) result;}}
	      catch(NoResultException nre){
	    	  sendError("Login failed", context.response(),400);
	      }
	     return null;
	  }


	  /**
	   * This method handles the post call transaction for sigup data
	   * @param user
	   */
      public void persist(User user)
    	{
	        try {
	            entityManager.getTransaction().begin();
	            entityManager.persist(user);
	            entityManager.getTransaction().commit();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            entityManager.getTransaction().rollback();
	        }
	    }
      

      /**
       * This method should return user info for given user_email
       * @param email
       * @return
       */
	   
      public User getByEmail(String email) {
 		 User user = null;
 	    	try {
 	    		List<User> users = entityManager.createQuery(
 			    		"FROM User WHERE email = :email", User.class)
 		          .setParameter("email", email)
 		          .getResultList();
 	    		user=users.get(0);
 	    	}
 	    	catch(Exception ex) {
 	    	ex.printStackTrace();
 	    	}
 	    	return user;
 	    	}
      
      /**
       * This method used to update a password
       * @param context
       * @param email
       * @param password
       */
      public void forgotPassword(RoutingContext context,String email,String password)
  		{   
    	  try {
    		  
	            entityManager.getTransaction().begin();
	            Query user = entityManager.createQuery("UPDATE User set password='"+password+"'  WHERE email='"+email+"'");
	            user.executeUpdate();
        		entityManager.getTransaction().commit();
        		sendSuccess(" Password Updated", context.response(),200);   		 
    	  }  catch (Exception ex) {
		            ex.printStackTrace();
		            entityManager.getTransaction().rollback();       
		        }
	    }

      /**
       * This method send Error message
       * @param errorMessage
       * @param response
       * @param code
       */
      private static void sendError(String errorMessage, HttpServerResponse response,int code) {
          JsonObject jo = new JsonObject();
          jo.put("errorMessage", errorMessage);

          response
                  .setStatusCode(400)
                  .setStatusCode(code)
                  .putHeader("content-type", "application/json; charset=utf-8")
                  .end(Json.encodePrettily(jo));
      }
      
      /**
       * This method send Success message
       * @param errorMessage
       * @param response
       * @param code
       */
      private void sendSuccess(String successMessage,HttpServerResponse response,int code) {
    	  JsonObject jo = new JsonObject();
          jo.put("successMessage", successMessage);
    	  response
	              .setStatusCode(200)
	              .setStatusCode(code)
	              .putHeader("content-type", "application/json; charset=utf-8")
	              .end(Json.encodePrettily(jo));
	  }
}

