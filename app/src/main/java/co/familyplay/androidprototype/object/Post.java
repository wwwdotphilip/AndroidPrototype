package co.familyplay.androidprototype.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/*
This class extends the ParseObject class. It's main purpose is to ack as intermediate for the Post class
in Parse server.

Visit the offical documentation of parse for more details.
https://www.parse.com/docs/android/guide
 */

@ParseClassName("Post")
public class Post extends ParseObject implements Serializable {
    private String voteType;
    private String voteId;
    private int votes;
    private int comments;
    private float width;
    private float height;

    public String getContent(){
        return getString("content");
    }

    public void setContent(String content){
        put("content", content);
    }

    public Bitmap getPhoto(){
        Bitmap bmp = null;
        byte[] data = null;
        try {
            data = getParseFile("photo").getData();
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public byte[] getGif(){
        byte[] data = null;
        try {
            data = getParseFile("photo").getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setPhoto(Bitmap photo){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile file = new ParseFile("photo.png", byteArray);
        put("photo", file);
    }

    public void setGif(byte[] byteArray){
        ParseFile file = new ParseFile("image.gif", byteArray);
        put("photo", file);
    }

    public String getUserName(){
        return getString("userName");
    }

    public void setUserName(String userName){
        put("userName", userName);
    }

    public String getUserId(){
        return getString("userId");
    }

    public void setUserId(String userId){
        put("userId", userId);
    }

    public Boolean hasPhoto(){
        return getBoolean("hasPhoto");
    }

    public void hasPhoto(Boolean hasPhoto){
        put("hasPhoto", hasPhoto);
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint point){
        put("location", point);
    }

    public String getArea(){
        return getString("area");
    }

    public void setArea(String area){
        put("area", area);
    }

    public String getCity(){
        return getString("city");
    }

    public void setCity(String city){
        put("city", city);
    }

    public String getVoteType() {
        return voteType;
    }

    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String vote) {
        this.voteId = vote;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getPostType(){
        return getInt("postType");
    }

    public void setPostType(String type){
        put("postType", type);
    }

    public int getSchool(){
        return getInt("school");
    }

    public void setSchool(String type){
        put("school", type);
    }

    public Boolean isGroup(){ return getBoolean("isGroup"); }

    public void isGroup(Boolean isGroup){
        put("isGroup", isGroup);
    }

    public Boolean isSchool(){ return getBoolean("isSchool"); }

    public void isSchool(Boolean isSchool){
        put("isSchool", isSchool);
    }

    public int getGroupId(){
        return getInt("groupId");
    }

    public void setGroupId(String id){
        put("groupId", id);
    }

    public Boolean isGif(){
        return getBoolean("isGif");
    }

    public void isGif(Boolean value){
        put("isGif", value);
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
