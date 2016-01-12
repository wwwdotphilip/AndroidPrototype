package co.familyplay.androidprototype.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.graphics.Bmp;
import co.familyplay.androidprototype.object.Follow;
import co.familyplay.androidprototype.object.UserPhoto;
import co.familyplay.androidprototype.storage.Data;

//Visit the wiki for more info
public class ProfileActivity extends ActionBarActivity {
    private EditText name, age, email, userName;
    private RadioButton male, female;
    private ImageView photo;
    private Context context;
    private ParseUser user;
    private Button fbButton, twitterButton, followButton;
    private Bitmap profilePhoto;
    private Menu items;
    private static final int PICTURE_RESULT = 1;
    private static final int SELECT_PHOTO = 2;
    private Uri imageUri;
    private ProgressBar photoProgress;
    private Boolean isMainUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = ProfileActivity.this;
        name = (EditText) findViewById(R.id.etName);
        age = (EditText) findViewById(R.id.etAge);
        email = (EditText) findViewById(R.id.etEmail);
        userName = (EditText) findViewById(R.id.etUserName);
        photo = (ImageView) findViewById(R.id.ivUserPhoto);
        followButton = (Button) findViewById(R.id.btnFollow);
        fbButton = (Button) findViewById(R.id.btnConnectFb);
        twitterButton = (Button) findViewById(R.id.btnConnectTweet);
        male = (RadioButton) findViewById(R.id.rbMale);
        female = (RadioButton) findViewById(R.id.rbFemale);
        photoProgress = (ProgressBar) findViewById(R.id.pbPhotoProgress);
        followButton.setVisibility(View.GONE);
        if (getIntent().getStringExtra("userId") != null){
            isMainUser = false;
            fbButton.setVisibility(View.GONE);
            twitterButton.setVisibility(View.GONE);
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getInBackground(getIntent().getStringExtra("userId"), new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    user = parseUser;
                    checkIfFollowing();
                    loadProfileData();
                    loadProfilePhoto();
                }
            });
        } else {
            user = ParseUser.getCurrentUser();
            loadProfileData();
            loadProfilePhoto();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case PICTURE_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    photoProgress.setVisibility(View.VISIBLE);
                    try {
                        String imageurl = Bmp.getPath(imageUri,
                                ProfileActivity.this);
                        Bitmap bmp = Bmp
                                .decodeSampledBitmapFromResource(imageurl, 200,
                                        200);
                        profilePhoto = Bitmap.createBitmap(bmp, 0, 0,
                                bmp.getWidth(), bmp.getHeight(),
                                Bmp.rotateMatrix(imageurl), true);
                        profilePhoto = Bmp.cutImage(profilePhoto);
                        updatePhoto();
                    } catch (Exception e) {
                        Msg.Toast("Error collecting photo. Please try again", context);
                        photoProgress.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    photoProgress.setVisibility(View.VISIBLE);
                    try {
                        Uri selectedImageUri = data.getData();

                        String imageurl = Bmp.getSelectedPath(selectedImageUri, ProfileActivity.this);
                        Bitmap bmp = Bmp
                                .decodeSampledBitmapFromResource(imageurl, 200,
                                        200);
                        profilePhoto = Bitmap.createBitmap(bmp, 0, 0,
                                bmp.getWidth(), bmp.getHeight(),
                                Bmp.rotateMatrix(imageurl), true);
                        profilePhoto = Bmp.cutImage(profilePhoto);
                        updatePhoto();
                    } catch (Exception e) {
                        Msg.Toast("Error collecting photo. Please try again", context);
                        photoProgress.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        items = menu;
        if (!isMainUser){
            menu.clear();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_edit:
                editableItems(true);
                break;
            case R.id.action_done:
                updateProfile();
                break;
            case R.id.action_cancel:
                name.setText(user.getString("name"));
                age.setText(String.format("%d", user.getInt("age")));
                email.setText(user.getEmail());
                userName.setText(user.getUsername());
                if (user.getString("gender") != null) {
                    if (user.getString("gender").equals("male")) {
                        male.setChecked(true);
                    } else if (user.getString("gender").equals("female")) {
                        female.setChecked(true);
                    }
                } else {
                    male.setChecked(false);
                    female.setChecked(false);
                }
                editableItems(false);
                break;
            default:
                break;
        }

        return true;
    }

    private class GetFBPhoto extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            ParseUser userPhoto = user;
            String url = "https://graph.facebook.com/" + userPhoto.get("facebookId") + "/picture?type=large";
            Msg.Print(userPhoto.getUsername());
            Msg.Print(url);
            try {
                HttpsURLConnection conn = (HttpsURLConnection) new URL(url)
                        .openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bmp = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
                return bmp;
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return null;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            if (bmp != null) {
                final UserPhoto uPhoto = new UserPhoto();
                uPhoto.setUserId(user.getObjectId());
                uPhoto.setPhoto(bmp);
                uPhoto.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            user.put("photoId", uPhoto.getObjectId());
                            user.saveEventually();
                        }
                    }
                });

                Data.storeImage(bmp, user.getObjectId() + ".png", "profile_photo", context);
                photo.setImageBitmap(bmp);
            }
        }
    }

    public void changePhoto(View v) {
        if (isMainUser){
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                    context);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.select_dialog_singlechoice);
            builderSingle.setTitle("Change photo");
            arrayAdapter.add("Take photo");
            arrayAdapter.add("Select from gallery");
            builderSingle.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            Intent intent;
                            switch (position) {
                                case 0:
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                                    imageUri = context.getContentResolver().insert(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                                        startActivityForResult(intent, PICTURE_RESULT);
                                    }
                                    break;

                                case 1:
                                    intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, SELECT_PHOTO);
                                    break;

                                default:

                                    break;
                            }
                        }
                    });
            builderSingle.show();
        }
    }

    public void follow(View v){
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Following...");
        pd.show();
        Follow follow = new Follow();
        follow.setFolloweeId(user.getObjectId());
        follow.setFollowerId(ParseUser.getCurrentUser().getObjectId());
        follow.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                pd.hide();
                if (e == null){
                    followButton.setVisibility(View.GONE);
                } else {
                    Msg.Toast(e.getMessage(), context);
                }
            }
        });
    }

    private void updateProfile() {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Updating profile...");
        pd.show();
        String g = "male";
        user.put("age", Integer.parseInt(age.getText().toString()));
        user.setEmail(email.getText().toString().trim());
        user.setUsername(userName.getText().toString().trim());
        user.put("name", name.getText().toString());
        user.put("search", user.getUsername().toLowerCase() + " " + name.getText().toString().toLowerCase());
        if (female.isChecked()) {
            g = "female";
        }
        user.put("gender", g);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    user.fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            editableItems(false);
                            pd.hide();
                            if (e != null) {
                                Msg.Toast(e.getMessage(), context);
                            }
                        }
                    });
                } else {
                    pd.hide();
                    Msg.Toast(e.getMessage(), context);
                }
            }
        });
    }

    private void editableItems(Boolean value) {
        if (value) {
            name.setEnabled(true);
            age.setEnabled(true);
            email.setEnabled(true);
            userName.setEnabled(true);
            male.setEnabled(true);
            female.setEnabled(true);
            fbButton.setVisibility(View.GONE);
            twitterButton.setVisibility(View.GONE);
            items.getItem(0).setVisible(false);
            items.getItem(1).setVisible(true);
            items.getItem(2).setVisible(true);
        } else {
            name.setEnabled(false);
            age.setEnabled(false);
            email.setEnabled(false);
            userName.setEnabled(false);
            male.setEnabled(false);
            female.setEnabled(false);
            if (user.getString("facebookId") == null) {
                fbButton.setVisibility(View.VISIBLE);
            }
            if (user.getString("twitterId") == null) {
                twitterButton.setVisibility(View.VISIBLE);
            }
            items.getItem(0).setVisible(true);
            items.getItem(1).setVisible(false);
            items.getItem(2).setVisible(false);
        }
    }

    private void loadProfilePhoto() {
        profilePhoto = Data.getImage(user.getObjectId() + ".png", "profile_photo", context);
        if (profilePhoto != null) {
            photo.setImageBitmap(profilePhoto);
        } else {
            ParseQuery<UserPhoto> query = ParseQuery.getQuery("UserPhoto");
            query.whereEqualTo("userId", user.getObjectId());
            query.findInBackground(new FindCallback<UserPhoto>() {
                @Override
                public void done(List<UserPhoto> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            photo.setImageBitmap(list.get(0).getPhoto());
                        } else {
                            new GetFBPhoto().execute();
                        }
                    } else {
                        new GetFBPhoto().execute();
                    }
                }
            });
        }
    }

    private void updatePhoto(){
        ParseQuery<UserPhoto> query = ParseQuery.getQuery("UserPhoto");
        query.getInBackground(user.getString("photoId"), new GetCallback<UserPhoto>() {
            @Override
            public void done(UserPhoto userPhoto, ParseException e) {
                if (e == null) {
                    userPhoto.setPhoto(profilePhoto);
                        userPhoto.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                photoProgress.setVisibility(View.GONE);
                                if (e == null){
                                    photo.setImageBitmap(profilePhoto);
                                    Data.storeImage(profilePhoto, user.getObjectId() + ".png", "profile_photo", context);
                                } else {
                                    Msg.Toast(e.getMessage(), context);
                                }
                            }
                        });
                } else {
                    final UserPhoto uPhoto = new UserPhoto();
                    uPhoto.setUserId(user.getObjectId());
                    uPhoto.setPhoto(profilePhoto);
                    uPhoto.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            photoProgress.setVisibility(View.GONE);
                            if (e == null) {
                                user.put("photoId", uPhoto.getObjectId());
                                user.saveEventually();
                                photo.setImageBitmap(profilePhoto);
                                Data.storeImage(profilePhoto, user.getObjectId() + ".png", "profile_photo", context);
                            } else {
                                Msg.Toast(e.getMessage(), context);
                            }
                        }
                    });
                }
            }
        });
    }

    private void loadProfileData(){

        name.setText(user.getString("name"));
        age.setText(String.format("%d", user.getInt("age")));
        email.setText(user.getEmail());
        userName.setText(user.getUsername());

        if (user.getString("facebookId") != null) {
            fbButton.setVisibility(View.GONE);
        }

        if (user.getString("gender") != null) {
            if (user.getString("gender").equals("male")) {
                male.setChecked(true);
            } else if (user.getString("gender").equals("female")) {
                female.setChecked(true);
            }
        }
    }

    private void checkIfFollowing(){
        ParseQuery<Follow> follow = ParseQuery.getQuery("Follow");
        follow.whereEqualTo("followerId", ParseUser.getCurrentUser().getObjectId());
        follow.whereEqualTo("followeeId", user.getObjectId());
        follow.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> list, ParseException e) {
                if (e == null){
                    if (list.size() > 0){
                        followButton.setVisibility(View.GONE);
                    } else {
                        followButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
