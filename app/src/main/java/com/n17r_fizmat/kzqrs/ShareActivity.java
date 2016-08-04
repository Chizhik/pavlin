package com.n17r_fizmat.kzqrs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout lin_share;
    private Button share_button;
    private ImageView profile;
    private TextView username;
    private ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        lin_share = (LinearLayout) findViewById(R.id.linear_share);
        share_button = (Button) findViewById(R.id.share_insta);
        profile = (ImageView) findViewById(R.id.share_profile);
        username = (TextView) findViewById(R.id.share_username);

        if (user.getParseFile("avatar") != null) {
            ParseFile avatar = (ParseFile) user.get("avatar");
            avatar.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profile.setImageBitmap(bm);
                    } else {
                        Log.d("ParseException", e.toString());
                        Toast.makeText(ShareActivity.this, "Ошибка при загрузке аватара", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            try {
                Object name = user.fetchIfNeeded().getUsername();
                if (name != null) {
                    username.setText(name.toString());
                }
            } catch (ParseException e) {
                Log.v("Parse", e.toString());
                e.printStackTrace();
            }

//            username.setText(user.getUsername());
            share_button.setOnClickListener(this);
        } else {
            Toast.makeText(this, "Ошибка при загрузке аватара", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.share_insta:
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setTitle("Загрузка");
                pd.setMessage("Пожалуйста подождите");
                pd.show();
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        File file = saveBitMap(ShareActivity.this, lin_share);
                        if (file != null) {
                            Log.i("TAG", "Drawing saved to the gallery!");
                            String type = "image/*";
                            String mediaPath = file.getAbsolutePath();
                            createInstagramIntent(type, mediaPath);

                        } else {
                            Log.i("TAG", "Oops! Image could not be saved.");
                        }
                        pd.dismiss();
                    }
                };
                thread.start();
                Intent intent = new Intent(ShareActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
//                File file = saveBitMap(this, lin_share);    //which view you want to pass that view as parameter
//                if (file != null) {
//                    Log.i("TAG", "Drawing saved to the gallery!");
//                    String type = "image/*";
//                    String mediaPath = file.getAbsolutePath();
//                    createInstagramIntent(type, mediaPath);
//                } else {
//                    Log.i("TAG", "Oops! Image could not be saved.");
//                }
                break;
            default:
                break;
        }
    }

    private File saveBitMap(Context context, View drawView){
        String appDirectoryName = "pavlin";
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ File.separator + appDirectoryName + File.separator);
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ "share.png";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
//            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createInstagramIntent(String type, String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }
}
