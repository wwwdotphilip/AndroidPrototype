package co.familyplay.androidprototype.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.activity.CreateGIFActivity;
import co.familyplay.androidprototype.adapter.NewPostAdapter;
import co.familyplay.androidprototype.adapter.PostListAdapter;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.graphics.Bmp;
import co.familyplay.androidprototype.object.Post;
import co.familyplay.androidprototype.object.Vote;

// Visit the wiki for mroe details
public class SchoolFragment extends android.support.v4.app.Fragment {
    private EditText editTextSchool;
    private Bitmap photoContent;
    private Button setSchool;
    private TextView schoolText;
    private ProgressBar pb;
    private List<Post> postList;
    private RecyclerView post;
    private static final int PICTURE_RESULT = 1;
    private static final int SELECT_PHOTO = 2;
    private Uri imageUri;
    private PostListAdapter adapter;
    private int preLast, postLimit;
    private String area, region, country, locality;
    private Boolean downloadingPost, loading = false, isReported = false, isLocationReported = false;
    private SharedPreferences prefs;
    private Menu items;
    private ProgressDialog pd;
    private int verticalDirection;
    private int SCROLL_DIRECTION_DOWN = 1;
    private int SCROLL_DIRECTION_UP = 0;
    private LinearLayoutManager mLayoutManager;
    //    private SwipeRefreshLayout refreshLayout;
    private ActionBar actionBar;
    private double latitude, longitude;
    private RelativeLayout includeLayout;
    private RelativeLayout schoolTextLayout;
    private byte[] photoByteArray;

    public SchoolFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school, container, false);
        post = (RecyclerView) view.findViewById(R.id.lvPost);
        editTextSchool = (EditText) view.findViewById(R.id.etSchoolName);
        pb = (ProgressBar) view.findViewById(R.id.pbProgress);
        schoolText = (TextView) view.findViewById(R.id.tvSchool);
        includeLayout = (RelativeLayout) view.findViewById(R.id.layoutRecycle);
        schoolTextLayout = (RelativeLayout) view.findViewById(R.id.rlTextSchool);
        setSchool = (Button) view.findViewById(R.id.btnSet);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        postLimit = 5;
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading post...");
        prefs = getActivity().getSharedPreferences("appData", Context.MODE_PRIVATE);
        downloadingPost = false;

        post.setHasFixedSize(true);
        actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        post.setLayoutManager(mLayoutManager);
        post.setOnScrollListener(new ScrollListener());

        setSchool.setOnClickListener(new ButtonSetSchool());
        editTextSchool.setOnKeyListener(new KeySetSchool());

        if (ParseUser.getCurrentUser().get("school") == null){
            includeLayout.setVisibility(View.GONE);
            schoolText.setVisibility(View.GONE);
        } else {
            schoolTextLayout.setVisibility(View.GONE);
            initialLoad();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String search = prefs.getString("search", null);
        String from = prefs.getString("from", null);
        if (search != null && from != null){
            if (from.equals("school")) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("search", null);
                editor.putString("from", null);
                editor.apply();
                ParseQuery<Post> query = ParseQuery.getQuery("Post");
                query.selectKeys(Arrays.asList("content", "userName", "userId",
                        "hasPhoto", "localtion", "area", "school", "isGroup",
                        "isSchool", "groupId", "activity"));
                query.getInBackground(search, new GetCallback<Post>() {
                    @Override
                    public void done(Post post, ParseException e) {
                        if (e == null) {
                            addInPost(post);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem addPhoto = menu.add(0, 0, 0, "Add Photo");
        addPhoto.setIcon(R.drawable.ic_action_cam);
        addPhoto.setOnMenuItemClickListener(new MenuListener());
        addPhoto.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        MenuItem addPost = menu.add(0, 1, 1, "New Post");
        addPost.setIcon(R.drawable.ic_action_new);
        addPost.setOnMenuItemClickListener(new MenuListener());
        addPost.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        items = menu;

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case PICTURE_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String imageurl = Bmp.getPath(imageUri,
                                getActivity());
                        Bitmap bmp = Bmp
                                .decodeSampledBitmapFromResource(imageurl, 200,
                                        200);
                        if(checkifGif(imageurl)){
                            InputStream iStream =   getActivity().getContentResolver().openInputStream(data.getData());
                            photoByteArray = Bmp.getBytes(iStream);
                        } else {
                            photoContent = Bitmap.createBitmap(bmp, 0, 0,
                                    bmp.getWidth(), bmp.getHeight(),
                                    Bmp.rotateMatrix(imageurl), true);
                        }
                        displayPost(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImageUri = data.getData();

                        String imageurl = Bmp.getSelectedPath(selectedImageUri, getActivity());
                        Bitmap bmp = Bmp
                                .decodeSampledBitmapFromResource(imageurl, 200,
                                        200);
                        if(checkifGif(imageurl)){
                            InputStream iStream =   getActivity().getContentResolver().openInputStream(selectedImageUri);
                            photoByteArray = Bmp.getBytes(iStream);
                        } else {
                            photoContent = Bitmap.createBitmap(bmp, 0, 0,
                                    bmp.getWidth(), bmp.getHeight(),
                                    Bmp.rotateMatrix(imageurl), true);
                        }
                        displayPost(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            default:
                if (resultCode == Activity.RESULT_OK) {
                    Msg.Toast(data.getStringExtra("hey"), getActivity());
                }
                break;
        }
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) {
                verticalDirection = SCROLL_DIRECTION_UP;
            } else {
                verticalDirection = SCROLL_DIRECTION_DOWN;
            }

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int lastVisibleItemIndex = mLayoutManager.findLastVisibleItemPosition();
            int firstCompletelyVisibleItemPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

//            refreshLayout.setEnabled(firstVisibleItemPosition == 0 && recyclerView.getChildAt(0).getTop() == 0);

            if (totalItemCount == lastVisibleItemIndex + 1 && !downloadingPost) {
                loadPosts(postList.size());
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                if (verticalDirection == SCROLL_DIRECTION_UP && actionBar.isShowing()) {
//                    actionBar.hide();
                } else if (verticalDirection == SCROLL_DIRECTION_DOWN && !actionBar.isShowing()) {
//                    actionBar.show();
                }
            }
        }
    }

    private class ButtonSetSchool implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (editTextSchool.getText().toString().trim().length() > 0){
                SetSchool(editTextSchool.getText().toString());
            }
        }
    }

    private class KeySetSchool implements View.OnKeyListener{

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP
                    && keyCode == KeyEvent.KEYCODE_ENTER) {
                SetSchool(editTextSchool.getText().toString());
            }
            return false;
        }
    }

    private void SetSchool(String schoolName){
        ParseUser user = ParseUser.getCurrentUser();
        user.put("school", schoolName);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null){
                                schoolTextLayout.setVisibility(View.GONE);
                                includeLayout.setVisibility(View.VISIBLE);
                                schoolText.setVisibility(View.VISIBLE);
                                editTextSchool.setText("");
                                initialLoad();
                            } else {
                                Msg.Toast("Failed setting school. Please try again.", getActivity());
                            }
                        }
                    });
                } else {
                    Msg.Toast("Failed setting school. Please try again.", getActivity());
                }
            }
        });
    }

    private class MenuListener implements MenuItem.OnMenuItemClickListener{

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                    getActivity());
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.select_dialog_singlechoice);
            switch (id) {
                case 0:
                    builderSingle.setTitle("Add photo");
                    arrayAdapter.add("Take photo");
                    arrayAdapter.add("Select from gallery");
                    arrayAdapter.add("Generate GIF");
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
                                            imageUri = getActivity().getContentResolver().insert(
                                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                startActivityForResult(intent, PICTURE_RESULT);
                                            }
                                            break;
                                        case 1:
                                            intent = new Intent(Intent.ACTION_PICK);
                                            intent.setType("image/*");
                                            startActivityForResult(intent, SELECT_PHOTO);
                                            break;
                                        case 2:
                                            intent = new Intent(getActivity(), CreateGIFActivity.class);
                                            intent.putExtra("isSchool", true);
                                            intent.putExtra("from", "school");
                                            startActivity(intent);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });
                    builderSingle.show();
//                }
                    break;
                case 1:
                    displayPost(false);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void loadPosts(int skip) {
        latitude = Double.parseDouble(prefs.getString("latitude", "0"));
        longitude = Double.parseDouble(prefs.getString("longitude", "0"));
        if (latitude != 0 && longitude != 0) {
//            refreshLayout.setEnabled(true);
            downloadingPost = true;
            ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
            ParseQuery<Post> query = ParseQuery.getQuery("Post");
            query.orderByDescending("createdAt");
            query.whereEqualTo("area", area);
            query.whereEqualTo("school", ParseUser.getCurrentUser().get("school").toString());
            query.selectKeys(Arrays.asList("content", "userName", "userId",
                    "hasPhoto", "localtion", "area", "school", "isGroup",
                    "isSchool", "groupId", "activity"));
            query.setLimit(postLimit);
//            query.whereWithinKilometers("location", point, 50);
            query.setSkip(skip);
            query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> posts, ParseException e) {
                    if (e != null) {
                        Msg.Toast(e.getMessage(), getActivity());
                    } else {
                        if (posts.size() > 0) {
                            for (Post post : posts) {
                                postList.add(post);
                            }
                            postLimit = postLimit + posts.size();
                            ParseQuery<Vote> query = ParseQuery.getQuery("Vote");
                            query.include("Post");
                            query.findInBackground(new FindCallback<Vote>() {
                                @Override
                                public void done(List<Vote> list, ParseException e) {
                                    if (e == null) {
                                        for (Vote vote : list) {
                                            for (int j = 0; j < postList.size(); j++) {
                                                if (vote.getPostId().equals(postList.get(j).getObjectId())) {
                                                    postList.get(j).setVoteType(vote.getVote());
                                                }
                                            }
                                        }
                                        pb.setVisibility(View.GONE);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Msg.Toast(e.getMessage(), getActivity());
                                    }
                                }
                            });
                        }
                    }
                    downloadingPost = false;
                }
            });
//            }
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    loadPosts(0);
                }
            }, 3000);
        }
    }

    private void initialLoad() {
        area = prefs.getString("area", null);
        if (area != null) {
            schoolText.setText(ParseUser.getCurrentUser().get("school").toString());
//            ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            ParseQuery<Post> query = ParseQuery.getQuery("Post");
            query.orderByDescending("createdAt");
            query.setLimit(postLimit);
//            query.whereWithinKilometers("location", point, 50);
//            query.whereNear("location", point);
            query.whereEqualTo("school", ParseUser.getCurrentUser().get("school").toString());
            query.selectKeys(Arrays.asList("content", "userName", "userId",
                    "hasPhoto", "localtion", "area", "school", "isGroup",
                    "isSchool", "groupId", "activity"));
            query.findInBackground(new FindCallback<Post>() {

                @Override
                public void done(List<Post> posts, com.parse.ParseException e) {
                    if (e != null) {
                        Msg.Toast(e.getMessage(), getActivity());
                    } else {
                        postList = posts;
                        ParseQuery<Vote> query = ParseQuery.getQuery("Vote");
                        query.include("Post");
                        query.findInBackground(new FindCallback<Vote>() {
                            @Override
                            public void done(List<Vote> list, ParseException e) {
                                if (e == null) {
                                    for (Vote vote : list) {
                                        for (int j = 0; j < postList.size(); j++) {
                                            if (vote.getPostId().equals(postList.get(j).getObjectId())) {
                                                postList.get(j).setVoteType(vote.getVote());
                                            }
                                        }
                                    }
                                    pb.setVisibility(View.GONE);
                                    adapter = new PostListAdapter(postList, new PostListDataChangedListener(),
                                            getActivity(), getActivity());
                                    post.setAdapter(adapter);
                                } else {
                                    Msg.Toast(e.getMessage(), getActivity());
                                }
                            }
                        });
                    }
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    initialLoad();
                }
            }, 3000);
        }
    }

    private void displayPost(final Boolean isImage) {
        latitude = Double.parseDouble(prefs.getString("latitude", "0"));
        longitude = Double.parseDouble(prefs.getString("longitude", "0"));
        region = prefs.getString("region", null);
        if (latitude != 0 && longitude != 0 && area != null && region != null) {
            if (downloadingPost && postList == null) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        displayPost(isImage);
                    }
                }, 3000);
            } else {
                Post newPost = new Post();
                ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
                newPost.setUserId(ParseUser.getCurrentUser().getObjectId());
                newPost.setUserName(ParseUser.getCurrentUser().getUsername());
                newPost.hasPhoto(false);
                newPost.setLocation(point);
                newPost.setArea(area);
                newPost.isGroup(false);
                newPost.isSchool(true);
                newPost.setSchool(ParseUser.getCurrentUser().get("school").toString());
//                newPost.setCity(city);
                NewPostAdapter postAdaptor = new NewPostAdapter(newPost, isImage, photoContent,
                        photoByteArray, getActivity());
                postAdaptor.show();
                postAdaptor.setOnPostCompleteListener(new NewPostAdapter.OnPostComplete() {
                    @Override
                    public void onComplete(Post post, String error) {
                        if (error == null) {
                            preLast++;
                            postList.add(0, post);
                            adapter.notifyDataSetChanged();
                            photoContent = null;
                            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            Msg.Toast("Post success", getActivity());
                        } else {
                            Msg.AlertDialog(getActivity(), "Error posting", error);
                        }
                        downloadingPost = false;
                    }
                });
            }
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    displayPost(isImage);
                }
            }, 3000);
        }
    }

    private class PostListDataChangedListener implements PostListAdapter.OnPostListDataChangedListener{

        @Override
        public void dataChanged(Post post, int position) {
            postList.set(position, post);
        }
    }

    private Boolean checkifGif(String imageUri){
        File file = new File(imageUri);
        return Bmp.isGif(file);
    }

    private void addInPost(final Post post){
        if (postList != null){
            postList.add(0, post);
            adapter.notifyDataSetChanged();
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    addInPost(post);
                }
            }, 3000);
        }
    }
}
