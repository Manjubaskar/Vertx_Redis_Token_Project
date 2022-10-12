package io.manju.vertx.crudapi.service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.manju.vertx.crudapi.entity.User;
import io.manju.vertx.crudapi.repository.UserDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class UserService {
	private UserDao userDao = UserDao.getInstance();

	/**
	 * This method used to save the signup data
	 * @param context
	 * @param newSignup
	 * @param handler
	 */
	 
	public void signup(RoutingContext context,User newSignup, Handler<AsyncResult<User>> handler) {
		     Future<User> future = Future.future();
		     future.setHandler(handler);
		  try {
			  // This condition check the manditary fields must filled
				if(newSignup.getName().isEmpty() || newSignup.getPassword().isEmpty()||newSignup.getEmail().isEmpty()) {
		     		 System.out.print("Please filled the manditatory Fields");
		             sendError("Please filled the manditatory Fields", context.response(),400);}
				else{
				  //This condition check user name already exit
				  User user = userDao.getByUsername(newSignup.getName());
				  if(user!=null) {
					  sendError("User exist", context.response(),400);}
				  else {
						 //This condition check Password validation
				    	 String regex = "^(?=.*[0-9])"
			                     + "(?=.*[a-z])(?=.*[A-Z])"
			                     + "(?=.*[@#$%^&+=*!])"
			                     + "(?=\\S+$).{8,20}$";
				    	 
				    	 Pattern p = Pattern.compile(regex);
				    	 Matcher m = p.matcher(newSignup.getPassword());
				    	 if(m.matches()){	
				    		  //This condition check email validation
				    		  String regex1 = "^(.+)@(.+)$";  
				    		  Pattern pattern = Pattern.compile(regex1);
				    		  Matcher matcher = pattern.matcher(newSignup.getEmail());
				    		  if(matcher.matches()){ 
							    System.out.print("success");
							    userDao.persist(newSignup);	}
						      else {
						    	sendError("Please give a valid Email", context.response(),400);} 	 
				    	 	}
				    	 else{
							  sendError("Password must have length 8 characters,one Uppercase,"
							  		+ "one special character and one digit", context.response(),400);}
				  	}
				  	future.complete();}
			 	  }catch (Throwable ex) {
				            future.fail(ex);}
			}
		
	
	
	/**
	 * This method used to check signup data while login time
	 * @param context
	 * @param newSignup
	 * @param handler
	 */
	 
	public void login(RoutingContext context,User newSignup, Handler<AsyncResult<User>> handler) {
	     Future<User> future = Future.future();
	     future.setHandler(handler);
	     try {
	     	 //This conditon is used check the given user name and password is empty or not 
	     	if(newSignup.getName().isEmpty() || newSignup.getPassword().isEmpty()) {
	     		 System.out.print("invalid");
	             sendError("Invalid username and password", context.response(),400);}
	     	else {
	     		//This conditon is used check the given user name and password is equals to signup user name and password 
	     		 if(newSignup.getName().equals(userDao.getByName(context,newSignup.getName()).getName())&&
	     				newSignup.getPassword().equals(userDao.getByName(context,newSignup.getName()).getPassword()))
	     		 	{	
			     		 JedisPool jedisPool = new JedisPool("localhost", 6379);
				  		  // Get the pool and use the database
				  		  try (Jedis jedis = jedisPool.getResource()) {
			  			  //Generates random UUID  
			  			  UUID uuid=UUID.randomUUID(); 
			  			  //Convert UUID to string
			  			  String uuidAsString = uuid.toString();
			  			  System.out.println(uuidAsString);  
			  			  
			  			   User user = userDao.getByUsername(newSignup.getName());
			  			
				  			//Generate the jsonObject
				  			JsonObject jsonObject = new JsonObject();
							jsonObject.put("id", user.getId());
							jsonObject.put("name",user.getName());
							jsonObject.put("email",user.getEmail());
							jsonObject.put("password",user.getPassword());
							
						  //Convert jsonObject to string
						  String jsonObject1 = jsonObject.toString();
			  			  System.out.println(jsonObject1);  
			  			  //Set a key in redis
				  		  jedis.set(uuidAsString, jsonObject1);
				  		  jedis.expire(uuidAsString,60*60*24);
				  		  // Get key from redis
				  		  jedis.get(uuidAsString);
				  		  sendToken("Login Success",uuidAsString,"Token Expire on 24hrs after Login", context.response(),200);
				  		  System.out.println( jsonObject1 );
			  		  }
				  		  // close the connection pool
				  		  jedisPool.close();	
		             }
		     		 else {
		     			sendError("Login failed", context.response(),400);
		     		 }

	     		  future.complete();}
		   } catch (Throwable ex) {
		            future.fail(ex);}
	 	}

	/**
	 * This method used to update a (Forgot) Password 
	 * @param context
	 * @param user
	 * @param handler
	 */
	 
    public void passwordUpdate(RoutingContext context,User user, Handler<AsyncResult<User>> handler) {
        Future<User> future = Future.future();
        future.setHandler(handler);
        try {
        	 User name = userDao.getByEmail(user.getEmail());
        	 //This condition check Password validation
			  if(name!=null) {
				  String regex = "^(?=.*[0-9])"
		                     + "(?=.*[a-z])(?=.*[A-Z])"
		                     + "(?=.*[@#$%^&+=*])"
		                     + "(?=\\S+$).{8,20}$";
			    	 
			    	 Pattern p = Pattern.compile(regex);
			    	 Matcher m = p.matcher(user.getPassword());
			    	  if(m.matches()){	    		 
			    		  	System.out.print("success");  
							userDao.forgotPassword(context,user.getEmail(), user.getPassword());
				            future.complete();
				            sendSuccess("Password Updated", context.response(),200);
			    	  }else {
			    		  sendError("Password must have length 8 characters,one Uppercase,one special character and one digit", context.response(),400);} 
			    	  }
			  else {
				  sendError("Email not Exist", context.response(),400);}
        	} catch (Throwable ex) {
	        	 sendSuccess("fail ", context.response(),400);
	             future.fail(ex);}
    	}
    
    /**
     * Get User from Redis using token method
     * @param context
     * @param Authorization
     * @param userToken
     * @param handler
     */
    public void getUserName(RoutingContext context,String Authorization,String userToken,Handler<AsyncResult<User>> handler) {
    Future<User> future = Future.future();
    future.setHandler(handler);
        try {
        	String value=context.request().getHeader("Authorization");
        	 System.out.print(value+"\n");
        	 System.out.print(userToken+"\n");
        	 
        	 //This condition used to check the token param equals to auth header value
        	 if( value .equals(userToken)) {
				          JedisPool jedisPool = new JedisPool("localhost", 6379);
						  // Get the pool and use the database
						  try (Jedis jedis = jedisPool.getResource()) {
						  //Get token value from redis
						  String result =  jedis.get(userToken);
						  //Convert String to Json object
						  JsonObject jsonObject = new JsonObject(result);
						  System.out.print(jsonObject);
						  sendMessage(userToken,jsonObject, context.response(),200);
						  future.complete();
						  }
					  jedisPool.close();	
					  future.complete();
        		 } else {
        			 sendError("Unauthorized", context.response(),401);}    	 
          } catch (Throwable ex) {
       	 sendError("Unauthorized", context.response(),401);
         future.fail(ex);}
	 }
    
    /**
     *  This method used to send a error message
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
      * This method used to send a success message
      * @param successMessage
      * @param response
      * @param code
      */
     private void sendSuccess(String successMessage, HttpServerResponse response,int code) {
         JsonObject jo = new JsonObject();
         jo.put("successMessage", successMessage);
         response
                 .setStatusCode(200)
                 .setStatusCode(code)
                 .putHeader("content-type", "application/json; charset=utf-8")
                 .end(Json.encodePrettily(jo));
     	}
     
     /**
      * This method used to send a success message,token,expireMessage
      * @param successMessage
      * @param token
      * @param expireMessage
      * @param response
      * @param code
      */
     private void sendToken(String successMessage,String token,String expireMessage, HttpServerResponse response,int code) {
         JsonObject jo = new JsonObject();
         
         jo.put("successMessage", successMessage);
         jo.put("token", token);
         jo.put("expireMessage", expireMessage);
         response
                 .setStatusCode(200)
                 .setStatusCode(code)
                 .putHeader("content-type", "application/json; charset=utf-8")
                 .putHeader("Authorization", token)
                 .end(Json.encodePrettily(jo));
     	}
     
     /**
      * This method used to send a token
      * @param userToken
      * @param token
      * @param response
      * @param code
      */
     private void sendMessage( String userToken,Object token,HttpServerResponse response,int code) {
         JsonObject jo = new JsonObject();
         jo.put(userToken, token);
         response
                 .setStatusCode(200)
                 .setStatusCode(code)
                 .putHeader("content-type", "application/json; charset=utf-8")              
                 .end(Json.encodePrettily(jo));
     	}
	}