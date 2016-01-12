package co.familyplay.androidprototype.object;

import com.parse.ParseClassName;
import com.parse.ParseObject;
/*
This class extends the ParseObject class. It's main purpose is to ack as intermediate for the Group class
in Parse server.

Visit the offical documentation of parse for more details.
https://www.parse.com/docs/android/guide
 */
@ParseClassName("Group")
public class Group extends ParseObject {

    public void setGroupName(String name) {
        put("groupName", name);
    }

    public String getGroupName() {
        return getString("groupName");
    }

    public void setCreatorName(String name) {
        put("creatorName", name);
    }

    public String getCreatorName() {
        return getString("creatorName");
    }

    public void setCreatorId(String id) {
        put("creatorId", id);
    }

    public String getCreatorId() {
        return getString("creatorId");
    }

    public void isPrivate(Boolean value) {
        put("isPrivate", value);
    }

    public String isPrivate() {
        return getString("isPrivate");
    }
}
