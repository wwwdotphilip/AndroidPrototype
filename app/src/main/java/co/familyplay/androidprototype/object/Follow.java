package co.familyplay.androidprototype.object;

import com.parse.ParseClassName;
import com.parse.ParseObject;
/*
This class extends the ParseObject class. It's main purpose is to ack as intermediate for the Follow class
in Parse server.

Visit the offical documentation of parse for more details.
https://www.parse.com/docs/android/guide
 */
@ParseClassName("Follow")
public class Follow extends ParseObject {

    public void setFollowerId(String followerId){
        put("followerId", followerId);
    }

    public String getFollowerId(){
        return getString("followerId");
    }

    public void setFolloweeId(String followeeId){
        put("followeeId", followeeId);
    }

    public String getFolloweeId(){
        return getString("followeeId");
    }

}
