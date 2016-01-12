package co.familyplay.androidprototype.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.parse.SaveCallback;

import java.io.IOException;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.graphics.Draw;
import co.familyplay.androidprototype.object.Post;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/*
Holds all of the view inside the new post
To instaltiate

NewPostAdapter postAdaptor = new NewPostAdapter(newPost, isImage, null,
                    byteArray, context);

Then call postAdaptor.show(); to display the new post interface

create a listener to listen to post response from parse.

postAdaptor.setOnPostCompleteListener(new NewPostAdapter.OnPostComplete() {
                @Override
                public void onComplete(Post post, String error) {
                    if (error == null) {
                        do something here
                    } else {
                        do something here
                    }
                }

*/

public class NewPostAdapter {
    private OnPostComplete mListener;
    private Context context;
    private Boolean isImage;
    private Bitmap photoContent;
    private ProgressDialog pd;
    private byte[] photoByteArray;
    private Post post;

    // Parameters for the class
    public NewPostAdapter(Post post, Boolean isImage, Bitmap photoContent,
                          byte[] photoByteArray, Context context) {
        this.post = post;
        this.context = context;
        this.isImage = isImage;
        this.photoContent = photoContent;
        this.photoByteArray = photoByteArray;
        pd = new ProgressDialog(context);
        pd.setMessage("Posting...");
    }

    //method for setting listener
    public void setOnPostCompleteListener(OnPostComplete listener) {
        mListener = listener;
    }

    // interface method.
    public interface OnPostComplete {
        void onComplete(Post post, String error);
    }

    //Displaying the UI
    public void show() {
        // Initialize the variables
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView;
        final GifImageView gif;
        final ImageView image;
        final LinearLayout pickerHoler;
        final Button close, edit, editPhoto, done, send, showPicker;
        final RelativeLayout drawContent;
        final EditText post;
        final ColorPicker picker;
        final int[] brushColor = {Color.BLACK};
        final Draw[] draw = {null};
        SVBar svBar;
        if (isImage) {
            customView = inflater.inflate(R.layout.new_image_post, null);
            image = (ImageView) customView.findViewById(R.id.ivPhoto);
            image.setImageBitmap(photoContent);
            gif = (GifImageView) customView.findViewById(R.id.gifPhoto);
            try {
                if (photoByteArray != null){
                    GifDrawable gifFromBytes = new GifDrawable(photoByteArray);
                    gif.setImageDrawable(gifFromBytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            editPhoto = (Button) customView.findViewById(R.id.btnEditPhoto);
            done = (Button) customView.findViewById(R.id.btnDone);
            showPicker = (Button) customView.findViewById(R.id.btnShowPicker);
            pickerHoler = (LinearLayout) customView.findViewById(R.id.llPickerHolder);
            drawContent = (RelativeLayout) customView.findViewById(R.id.rlDrawContent);
            drawContent.setDrawingCacheEnabled(true);

            picker = (ColorPicker) customView.findViewById(R.id.picker);
            svBar = (SVBar) customView.findViewById(R.id.svbar);
            picker.addSVBar(svBar);
            picker.setColor(brushColor[0]);

            draw[0] = new Draw(context, null, Color.BLACK);
            draw[0].changeBrushColor(Color.TRANSPARENT);
            drawContent.addView(draw[0]);
            if (photoByteArray != null){
                editPhoto.setVisibility(View.GONE);
            }
        } else {
            customView = inflater.inflate(R.layout.new_post, null);
            drawContent = null;
            pickerHoler = null;
            showPicker = null;
            editPhoto = null;
            picker = null;
            done = null;
            image = null;
            gif = null;
        }
        close = (Button) customView.findViewById(R.id.btnClose);
        edit = (Button) customView.findViewById(R.id.btnEdit);
        post = (EditText) customView.findViewById(R.id.etPost);
        send = (Button) customView.findViewById(R.id.btnPost);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final AlertDialog dialog = builder.create();
        dialog.setView(customView);
        dialog.show();

        if (isImage && editPhoto != null) {
            // Enable photo editing
            editPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    draw[0].changeBrushColor(brushColor[0]);
                    editPhoto.setVisibility(View.GONE);
                    edit.setVisibility(View.GONE);
                    post.setVisibility(View.GONE);
                    send.setVisibility(View.INVISIBLE);
                    done.setVisibility(View.VISIBLE);
                    showPicker.setVisibility(View.VISIBLE);
                    showPicker.setBackgroundColor(picker.getColor());
                }
            });

            // Finish editing of photo
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pickerHoler.getVisibility() == View.VISIBLE) {
                        brushColor[0] = picker.getColor();
                        draw[0].changeBrushColor(brushColor[0]);
                        drawContent.setVisibility(View.VISIBLE);
                        pickerHoler.setVisibility(View.GONE);
                        close.setVisibility(View.VISIBLE);
                        showPicker.setVisibility(View.VISIBLE);
                        done.setVisibility(View.VISIBLE);
                        showPicker.setBackgroundColor(brushColor[0]);
                    } else {
                        draw[0].changeBrushColor(Color.TRANSPARENT);
                        editPhoto.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                        post.setVisibility(View.VISIBLE);
                        send.setVisibility(View.VISIBLE);
                        done.setVisibility(View.GONE);
                        showPicker.setVisibility(View.GONE);
                    }
                }
            });

            // show color picker
            showPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawContent.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.INVISIBLE);
                    close.setVisibility(View.GONE);
                    showPicker.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                    pickerHoler.setVisibility(View.VISIBLE);
                }
            });
        }

        //initiate the upload post sequence.
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getText().toString().trim().length() > 0 ||
                        photoContent != null || photoByteArray != null) {
                    dialog.dismiss();
                    if (image != null && photoContent != null) {
                        uploadPost(post.getText().toString(), drawContent.getDrawingCache(), null);
                    } else if(photoByteArray != null){
                        uploadPost(post.getText().toString(), null, photoByteArray);
                    }else {
                        uploadPost(post.getText().toString(), null, null);
                    }
                }
            }
        });

        // Focus on the textbox for text input
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(post, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        // Close the interface
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickerHoler != null) {
                    if (showPicker.getVisibility() == View.VISIBLE) {
                        editPhoto.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                        post.setVisibility(View.VISIBLE);
                        send.setVisibility(View.VISIBLE);
                        done.setVisibility(View.GONE);
                        showPicker.setVisibility(View.GONE);
                    } else {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    // Upload the post to parse server.
    private void uploadPost(final String postMessage, Bitmap image, byte[] photoByteArray) {
        pd.show();
        try {
            if (image != null) {
                post.hasPhoto(true);
                post.isGif(false);
                post.setPhoto(image);
            } else if(photoByteArray != null){
                post.hasPhoto(true);
                post.isGif(true);
                post.setGif(photoByteArray);
            }else {
                post.hasPhoto(false);
            }
            if (postMessage.trim().length() == 0) {
                post.setContent("");
            } else {
                post.setContent(postMessage);
            }
            post.saveInBackground(new SaveCallback() {

                @Override
                public void done(com.parse.ParseException e) {
                    if (pd.isShowing()) {
                        pd.hide();
                    }
                    if (e != null) {
                        mListener.onComplete(post, e.getMessage());
                    } else {
                        mListener.onComplete(post, null);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

