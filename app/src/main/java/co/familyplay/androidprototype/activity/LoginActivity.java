package co.familyplay.androidprototype.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.dialog.Msg;

//Visit the wiki for more info
public class LoginActivity extends ActionBarActivity {
    private Context context;
    private EditText userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.etUserName);
        password = (EditText) findViewById(R.id.etPassword);
        context = LoginActivity.this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(LoginActivity.this, DashActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void login(View v) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Logging In...");
        if (userName.getText().toString().trim().length() > 0 &&
                password.getText().toString().trim().length() > 0) {
            pd.show();
            ParseUser.logInInBackground(userName.getText().toString(),
                    password.getText().toString(), new LogInCallback() {

                        @Override
                        public void done(ParseUser parseUser, com.parse.ParseException e) {
                            if (pd.isShowing())
                                pd.hide();
                            if (parseUser != null) {
                                Intent i = new Intent(LoginActivity.this, DashActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Msg.AlertDialog(context, "Login Failed", e.getMessage());
                            }
                        }
                    });
        } else {
            Msg.Toast("Username/Password is empty", context);
        }
    }

    public void facebookLogin(View v) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Logging in...");
        pd.show();
        ParseFacebookUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                pd.hide();
                if (err != null) {
                    Msg.AlertDialog(context, "Error", err.getMessage());
                } else {
                    if (user == null) {
                        Msg.AlertDialog(context, "Failed", "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {

                        final Session session = Session.getActiveSession();
                        Request request = Request.newMeRequest(session,
                                new Request.GraphUserCallback() {

                                    @Override
                                    public void onCompleted(GraphUser user, Response response) {
                                        if (session == Session.getActiveSession()) {
                                            if (user != null) {
                                                ParseUser parseUser = ParseUser.getCurrentUser();
                                                parseUser.put("name", user.getName());
                                                parseUser.put("facebookId", user.getId());
                                                parseUser.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            Intent i = new Intent(LoginActivity.this, DashActivity.class);
                                                            startActivity(i);
                                                            finish();
                                                        } else {
                                                            Msg.Toast(e.getMessage(), context);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                        request.executeAsync();
                    } else {
                        Intent i = new Intent(LoginActivity.this, DashActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });
    }

    public void twitterLogin(View v) {
        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (err != null) {
                    Msg.AlertDialog(context, "Error", err.getMessage());
                } else {
                    if (user == null) {
                        Msg.AlertDialog(context, "Failed", "Uh oh. The user cancelled the twitter login.");
                    } else if (user.isNew()) {
                        Intent i = new Intent(LoginActivity.this, DashActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(LoginActivity.this, DashActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });
    }

    public void signUp(View v) {
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(i);
    }

    public void forgotPassword(View v) {
        Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(i);
    }
}
