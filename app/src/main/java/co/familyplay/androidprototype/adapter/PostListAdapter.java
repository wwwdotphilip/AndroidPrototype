package co.familyplay.androidprototype.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.activity.ViewCommentActivity;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.object.Post;
import co.familyplay.androidprototype.object.Vote;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/*
This class handles the displaying of post from the server
it extends RecylerView

Declare the class
PostListAdapter adapter = new co.familyplay.androidprototype.adapter.PostListAdapter(postList,
                           new PostListDataChangedListener(), context, activity);

Set the adapter of the recyclerView
recyclerView.setAdapter(adapter);

To update the interface simply call
adapter.notifyDataSetChanged();
*/

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {
    private OnPostListDataChangedListener mListener;
    private List<Post> postList;
    private Context context;
    private Activity activity;
    private int newBmapWidth, newBmapHeight;

    // Listener interface
    public interface OnPostListDataChangedListener {
        void dataChanged(Post post, int position);
    }

    // adapter parameters.
    public PostListAdapter(List<Post> postList, OnPostListDataChangedListener listener,
                           Context context, Activity activity) {
        this.postList = postList;
        this.context = context;
        this.activity = activity;
        mListener = listener;
    }

    // initialize the layout if the view
    @Override
    public PostListAdapter.PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.post_card_layout, viewGroup, false);

        return new PostViewHolder(itemView);
    }

    // declare the variables.
    @Override
    public void onBindViewHolder(final PostListAdapter.PostViewHolder postViewHolder, final int position) {
        postViewHolder.area.setText(postList.get(position).getArea());
        postViewHolder.userName.setText(postList.get(position).getUserName());
        postViewHolder.content.setText(postList.get(position).getContent());
        postViewHolder.photoThumb.setImageBitmap(postList.get(position).getPhoto());
        try {
            resizeGif(BitmapFactory.decodeByteArray(postList.get(position).getGif(), 0,
                    postList.get(position).getGif().length));
            postViewHolder.gif.setLayoutParams(new RelativeLayout.LayoutParams(newBmapWidth, newBmapHeight));
            GifDrawable gifFromBytes = new GifDrawable(postList.get(position).getGif());
            postViewHolder.gif.setImageDrawable(gifFromBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (postList.get(position).hasPhoto()) {
            ViewGroup.LayoutParams params = postViewHolder.content.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            postViewHolder.content.setLayoutParams(params);
            postViewHolder.photoThumb.setVisibility(View.VISIBLE);
            postViewHolder.gif.setVisibility(View.VISIBLE);
            postViewHolder.content.setBackgroundResource(Color.TRANSPARENT);

            // check if a photo is present in a post and display if present
            if (postList.get(position).getPhoto() == null
                    && postList.get(position).getGif() == null) {
                postViewHolder.photoThumb.setImageResource(R.drawable.blank);
                postViewHolder.gif.setImageResource(R.drawable.blank);
                Post getAllPostData = postList.get(position);
                getAllPostData.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (postList.get(position).isGif()){
                            try {
                                resizeGif(BitmapFactory.decodeByteArray(postList.get(position).getGif(), 0,
                                        postList.get(position).getGif().length));
                                postViewHolder.gif.setLayoutParams(new RelativeLayout.LayoutParams(newBmapWidth, newBmapHeight));
                                GifDrawable gifFromBytes = new GifDrawable(postList.get(position).getGif());
                                postViewHolder.gif.setImageDrawable(gifFromBytes);
                                postViewHolder.photoThumb.setVisibility(View.GONE);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        } else{
                            postViewHolder.gif.setVisibility(View.GONE);
                            postViewHolder.photoThumb.setImageBitmap(postList.get(position).getPhoto());
                        }
                        mListener.dataChanged(postList.get(position), position);
                    }
                });
            } else {
                if (postList.get(position).isGif()){
                    try {
                        resizeGif(BitmapFactory.decodeByteArray(postList.get(position).getGif(), 0,
                                postList.get(position).getGif().length));
                        postViewHolder.gif.setLayoutParams(new RelativeLayout.LayoutParams(newBmapWidth, newBmapHeight));
                        GifDrawable gifFromBytes = new GifDrawable(postList.get(position).getGif());
                        postViewHolder.gif.setImageDrawable(gifFromBytes);
                        postViewHolder.photoThumb.setVisibility(View.GONE);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else{
                    postViewHolder.gif.setVisibility(View.GONE);
                    postViewHolder.photoThumb.setImageBitmap(postList.get(position).getPhoto());
                }
            }
        } else {
            ViewGroup.LayoutParams params = postViewHolder.content.getLayoutParams();
            params.height = 400;
            postViewHolder.content.setLayoutParams(params);
            postViewHolder.content.setText(postList.get(position).getContent());
            postViewHolder.photoThumb.setImageBitmap(null);
            postViewHolder.photoThumb.setVisibility(View.GONE);
            postViewHolder.gif.setImageResource(0);
            postViewHolder.gif.setVisibility(View.GONE);
        }

        // Display the comment activity
        postViewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewCommentActivity.class);
                i.putExtra("author", postList.get(position).getUserName());
                i.putExtra("authorId", postList.get(position).getUserId());
                i.putExtra("content", postList.get(position).getContent());
                i.putExtra("postId", postList.get(position).getObjectId());
                if (postList.get(position).hasPhoto()) {
                    i.putExtra("photo", postList.get(position).getPhoto());
                }
                context.startActivity(i);
            }
        });

        if (postList.get(position).getVoteType() != null) {
            if (postList.get(position).getVoteType().equals("cute")) {
                postViewHolder.cute.setChecked(true);
            } else if (postList.get(position).getVoteType().equals("yummy")) {
                postViewHolder.yummy.setChecked(true);
            } else if (postList.get(position).getVoteType().equals("awesome")) {
                postViewHolder.awesome.setChecked(true);
            } else if (postList.get(position).getVoteType().equals("sexy")) {
                postViewHolder.sexy.setChecked(true);
            }
        } else {
            postViewHolder.cute.setChecked(false);
            postViewHolder.yummy.setChecked(false);
            postViewHolder.awesome.setChecked(false);
            postViewHolder.sexy.setChecked(false);
        }

        // send vote data to parse server
        postViewHolder.vote.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(postViewHolder.cute.isChecked() || postViewHolder.awesome.isChecked()
                        || postViewHolder.sexy.isChecked() || postViewHolder.yummy.isChecked()) {
                    String voteName = null;
                    if (checkedId == R.id.rbCute) {
                        voteName = "cute";
                    } else if (checkedId == R.id.rbAwesome) {
                        voteName = "awesome";
                    } else if (checkedId == R.id.rbYummy) {
                        voteName = "yummy";
                    } else if (checkedId == R.id.rbSexy) {
                        voteName = "sexy";
                    }
                    ParseQuery<Vote> mainQuery = ParseQuery.getQuery("Vote");
                    mainQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                    mainQuery.whereEqualTo("postId", postList.get(position).getObjectId());
                    final String finalVoteName = voteName;
                    mainQuery.findInBackground(new FindCallback<Vote>() {
                        @Override
                        public void done(List<Vote> vote, ParseException e) {
                            if (e == null) {
                                if (vote.size() > 0) {
                                    vote.get(0).setVote(finalVoteName);
                                    vote.get(0).saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                postList.get(position).setVoteType(finalVoteName);
                                                ParseQuery<Post> query = ParseQuery.getQuery("Post");
                                                query.getInBackground(postList.get(position).getObjectId(), new GetCallback<Post>() {
                                                    public void done(Post updatePost, ParseException e) {
                                                        if (e == null) {
                                                            updatePost.increment("activity");
                                                            updatePost.saveInBackground();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    Vote newVote = new Vote();
                                    newVote.setUserId(ParseUser.getCurrentUser().getObjectId());
                                    newVote.setPostId(postList.get(position).getObjectId());
                                    newVote.setVote(finalVoteName);
                                    ParseRelation relation = newVote.getRelation("parent");
                                    relation.add(postList.get(position));
                                    newVote.put("parent", relation);
                                    newVote.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                ParseQuery<Post> query = ParseQuery.getQuery("Post");
                                                query.getInBackground(postList.get(position).getObjectId(), new GetCallback<Post>() {
                                                    public void done(Post updatePost, ParseException e) {
                                                        if (e == null) {
                                                            updatePost.increment("activity");
                                                            updatePost.saveInBackground();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Msg.Toast(e.getMessage(), context);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Instantiate and holds the views together
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        protected TextView area;
        protected RadioButton cute, awesome, sexy, yummy;
        protected RadioGroup vote;
        protected TextView userName;
        protected TextView content;
        protected ImageButton comment;
        protected ImageView photoThumb;
        protected GifImageView gif;

        public PostViewHolder(View v) {
            super(v);
            area = (TextView) v.findViewById(R.id.tvArea);
            userName = (TextView) v.findViewById(R.id.tvUserName);
            content = (TextView) v.findViewById(R.id.tvContent);
            comment = (ImageButton) v.findViewById(R.id.ibComment);
            photoThumb = (ImageView) v.findViewById(R.id.ivPhoto);
            cute = (RadioButton) v.findViewById(R.id.rbCute);
            awesome = (RadioButton) v.findViewById(R.id.rbAwesome);
            sexy = (RadioButton) v.findViewById(R.id.rbSexy);
            yummy = (RadioButton) v.findViewById(R.id.rbYummy);
            vote = (RadioGroup) v.findViewById(R.id.rgVote);
            gif = (GifImageView) v.findViewById(R.id.gifPhoto);
        }

    }

    // Resize the GIF to fit into the width of the device and scale the height accordingly
    private void resizeGif(Bitmap bmap){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        float bmapWidth = bmap.getWidth();
        float bmapHeight = bmap.getHeight();

        float wRatio = width / bmapWidth;
        float hRatio = height / bmapHeight;

        float ratioMultiplier = wRatio;
        if (hRatio < wRatio) {
            ratioMultiplier = hRatio;
        }

        newBmapWidth = (int) (bmapWidth*ratioMultiplier);
        newBmapHeight = (int) (bmapHeight*ratioMultiplier);
    }
}
