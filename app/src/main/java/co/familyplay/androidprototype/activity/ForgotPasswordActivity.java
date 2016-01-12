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
import com.parse.RequestPasswordResetCallback;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.validator.EmailValidator;

//Visit the wiki for more info
public class ForgotPasswordActivity extends ActionBarActivity {
    EditText email;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        context = ForgotPasswordActivity.this;
        email = (EditText) findViewById(R.id.etEmail);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
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

    public void requestPasswordReset(View v) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Requesting...");
        if (email.getText().toString().trim().length() > 0) {
            EmailValidator val = new EmailValidator();
            if (val.validate(email.getText().toString())) {
                pd.show();
                ParseUser.requestPasswordResetInBackground(email.getText().toString(),
                        new RequestPasswordResetCallback() {

                            @Override
                            public void done(ParseException e) {
                                if (pd.isShowing())
                                    pd.hide();
                                if (e == null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setCancelable(false);
                                    builder.setTitle("Request success");
                                    builder.setMessage("Password reset request is success. Please check your email.");
                                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    builder.show();
                                } else {
                                    Msg.AlertDialog(context, "Request failed", e.getMessage());
                                }
                            }
                        });
            } else {
                Msg.Toast("Not a valid email", context);
            }
        } else {
            Msg.Toast("email is empty", context);
        }
    }
}
