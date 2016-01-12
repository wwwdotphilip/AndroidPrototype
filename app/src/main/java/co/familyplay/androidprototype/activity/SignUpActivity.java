package co.familyplay.androidprototype.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.validator.EmailValidator;

//Visit the wiki for more info
public class SignUpActivity extends ActionBarActivity {
    EditText userName, password, retypePassword, email;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = SignUpActivity.this;
        userName = (EditText) findViewById(R.id.etUserName);
        password = (EditText) findViewById(R.id.etPassword);
        retypePassword = (EditText) findViewById(R.id.etRetypePassword);
        email = (EditText) findViewById(R.id.etEmail);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void signUp(View v) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Registering...");
        ParseUser newUser = new ParseUser();
        if (userName.getText().toString().trim().length() > 0 || password.getText().toString().trim().length() > 0
                || retypePassword.getText().toString().trim().length() > 0|| email.getText().toString().trim().length() > 0){
            if (password.getText().toString().equals(retypePassword.getText().toString())) {
                pd.show();
                newUser.setUsername(userName.getText().toString());
                newUser.setPassword(password.getText().toString());
                newUser.setEmail(email.getText().toString());
                newUser.put("search", userName.getText().toString().toLowerCase());
                EmailValidator val = new EmailValidator();
                if (val.validate(email.getText().toString())) {
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (pd.isShowing())
                                pd.hide();
                            if (e == null) {
                                ParseUser.logOut();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setCancelable(false);
                                builder.setTitle("Sign up success");
                                builder.setMessage("You can now log in.");
                                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                builder.show();
                            } else {
                                Msg.AlertDialog(context, "Error Sign Up", e.getMessage());
                            }
                        }
                    });
                } else {
                    Msg.Toast("Not a valid email", context);
                }
            } else {
                password.setText("");
                retypePassword.setText("");
                Msg.Toast("Password did not match", context);
            }
        } else {
            Msg.Toast("All fields are required", context);
        }
    }
}
