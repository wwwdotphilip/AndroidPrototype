package co.familyplay.androidprototype;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;

import co.familyplay.androidprototype.object.Comment;
import co.familyplay.androidprototype.object.Follow;
import co.familyplay.androidprototype.object.Group;
import co.familyplay.androidprototype.object.Post;
import co.familyplay.androidprototype.object.UserPhoto;
import co.familyplay.androidprototype.object.Vote;


// This class calls all Parse class that is going to be used in the entire app.
// Visit the parse documentation for more details
// https://www.parse.com/docs/android/guide
public class AndroidPrototype extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
        ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Vote.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(UserPhoto.class);
        ParseObject.registerSubclass(Follow.class);
    }
}
