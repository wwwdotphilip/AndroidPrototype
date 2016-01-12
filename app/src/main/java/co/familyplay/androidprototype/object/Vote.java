package co.familyplay.androidprototype.object;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/*
This class extends the ParseObject class. It's main purpose is to ack as intermediate for the Vote class
in Parse server.

Visit the offical documentation of parse for more details.
https://www.parse.com/docs/android/guide
 */


@ParseClassName("Vote")
public class Vote extends ParseObject{

    public String getPostId(){
        return getString("postId");
    }

    public void setPostId(String postId){
        put("postId", postId);
    }

    public String getUserId(){
        return getString("userId");
    }

    public void setUserId(String userId){
        put("userId", userId);
    }

    public String getVote(){
        return getString("voteType");
    }

    public void setVote(String voteType){
        put("voteType", voteType);
    }

}
