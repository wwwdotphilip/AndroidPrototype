package co.familyplay.androidprototype.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.object.Comment;
import co.familyplay.androidprototype.object.Post;
//Visit the wiki for more info
public class ViewCommentActivity extends ActionBarActivity {
    EditText comment;
    ImageButton send;
    String authorNameText, contentText, postId;
    Context context;
    ListView commentsList;
    List<Comment> postComments;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        comment = (EditText) findViewById(R.id.etComment);
        commentsList = (ListView) findViewById(R.id.lvComments);
        commentsList.setEnabled(false);
        send = (ImageButton) findViewById(R.id.ibPost);
        authorNameText = getIntent().getStringExtra("author");
        contentText = getIntent().getStringExtra("content");
        postId = getIntent().getStringExtra("postId");
        context = ViewCommentActivity.this;

        ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
        query.whereContains("postId", postId);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null){
                    Msg.AlertDialog(context, "Retrieving post error", e.getMessage());
                } else {
                    postComments = comments;
                    reloadComments();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        Intent previousScreen = new Intent(getApplicationContext(), DashActivity.class);
        previousScreen.putExtra("hey", " Blabla");
        setResult(1000, previousScreen);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return true;
    }

    public void postComment(View v){
        comment.setEnabled(false);
        send.setEnabled(false);
        String name = null;
        if (ParseUser.getCurrentUser().getString("name") != null){
            name = ParseUser.getCurrentUser().getString("name");
        } else {
            name = ParseUser.getCurrentUser().getUsername();
        }
        final Comment newComment = new Comment();
        newComment.setComment(comment.getText().toString());
        newComment.setPostId(postId);
        newComment.setUserId(ParseUser.getCurrentUser().getObjectId());
        newComment.setUserName(name);
        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Msg.Toast(e.getMessage(), context);
                } else {
                    ParseQuery<Post> query = ParseQuery.getQuery("Post");
                    query.getInBackground(postId, new GetCallback<Post>() {
                        public void done(Post updatePost, ParseException e) {
                            if (e == null) {
                                updatePost.increment("comments");
                                updatePost.increment("activity");
                                updatePost.saveInBackground();
                            }
                        }
                    });
                    comment.setText("");
                    postComments.add(0, newComment);
                    reloadComments();
                }
                comment.setEnabled(true);
                send.setEnabled(true);
            }
        });
    }

    private void reloadComments(){
        int index = 0;
        String[] list = new String[postComments.size()];
        for (Comment comment : postComments){
            list[index] = comment.getUserName() + " says: " + comment.getComment();
            index++;
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
        commentsList.setAdapter(adapter);
    }
}
