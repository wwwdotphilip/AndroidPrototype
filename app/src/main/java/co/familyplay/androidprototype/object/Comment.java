package co.familyplay.androidprototype.object;

import com.parse.ParseClassName;
import com.parse.ParseObject;
/*
This class extends the ParseObject class. It's main purpose is to ack as intermediate for the Comment class
in Parse server.

Visit the offical documentation of parse for more details.
https://www.parse.com/docs/android/guide
 */
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public String getComment(){
        return getString("content");
    }

    public void setComment(String comment){
        put("content", comment);
    }

    public String getPostId(){
        return getString("postId");
    }

    public void setPostId(String id){
        put("postId", id);
    }

    public String getUserId(){
        return getString("userId");
    }

    public void setUserId(String userId){
        put("userId", userId);
    }

    public String getUserName(){
        return getString("userName");
    }

    public void setUserName(String userName){
        put("userName", userName);
    }

    public void setPost(ParseObject post){
        put("post", post);
    }

}
