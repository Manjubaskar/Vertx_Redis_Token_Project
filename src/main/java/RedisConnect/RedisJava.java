package RedisConnect;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;
public class RedisJava  {
	 public static void main(String[] args) {
	 
//		  JedisPool jedisPool = new JedisPool("localhost", 6379);
//		  // Get the pool and use the database
//		  try (Jedis jedis = jedisPool.getResource()) {
//			  
//		  jedis.set("mykey", "Hello from Jedis");
//		  String value = jedis.get("mykey");
//		  System.out.println( value );
//		  }
//
//		  // close the connection pool
//		  jedisPool.close();
		 
		 
		  JedisPool jedisPool = new JedisPool("localhost", 6379);
		  // Get the pool and use the database
		  try (Jedis jedis = jedisPool.getResource()) {
			  
			  UUID uuid=UUID.randomUUID(); 
			  //Generates random UUID  
//				 SetParams params = new SetParams();
//		  		  int expireTime = 1;
//				params.px(expireTime *1000 );
			  String uuidAsString = uuid.toString();
			  System.out.println(uuidAsString);  

		  jedis.set("manju", uuidAsString);
		  jedis.expire("manju",60);
		  String manju = jedis.get("manju");
		  System.out.println( manju );
		  }

		  // close the connection pool
		  jedisPool.close();		 
		 
		    
		  
	 }
	 
}