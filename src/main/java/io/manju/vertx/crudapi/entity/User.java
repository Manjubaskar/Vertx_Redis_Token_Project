package io.manju.vertx.crudapi.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import io.vertx.core.json.JsonObject;

@Entity
@Table(name = "user_table")
/**
 *Create the signup entity inside this class
 */
public class User implements Serializable{

	@Id
	@Column(name = "id")
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")	  
	private String id;
	
    @Column(name = "user_name",unique = true, nullable=false)
    private String name;
    
    @Column(name = "email",unique = true, nullable=false)
    private String email;
    
    @Column(name = "password", nullable=false)
    private String password;

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
        public void setPassword(String password) {
            this.password = password;
    }
       
    public String toJsonString(){
         return String.valueOf(JsonObject.mapFrom(this));
    }
  
}
   