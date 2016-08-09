package com.n17r_fizmat.kzqrs;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    // Handle case when picture is not square
    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;
//    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameEditText;
    private Bitmap bm;
    private Bitmap bm_small;
    private String name;
    private boolean imageChanged = false;
    private ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImage = (ImageView) findViewById(R.id.profileImageView);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        ImageView settingsSaveButton = (ImageView) findViewById(R.id.settingsSaveButton);
        Button settingsLogoutButton = (Button) findViewById(R.id.settingsLogoutButton);

        if (currentUser.getParseFile("avatar") != null) {
            ParseFile avatar = (ParseFile) currentUser.get("avatar");
            avatar.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        bm = BitmapFactory.decodeByteArray(data , 0, data .length);
                        profileImage.setImageBitmap(bm);
                    } else {
                        Log.d("ParseException", e.toString());
                        Toast.makeText(SettingsActivity.this, "Something went wrong while downloading avatar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            name = currentUser.getUsername();
            nameEditText.setText(name);

        }


        profileImage.setOnClickListener(this);
        settingsSaveButton.setOnClickListener(this);
        settingsLogoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profileImageView:
                Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
//                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                getIntent.setType("image/*");
//
//                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                pickIntent.setType("image/*");
//
//                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
//
//                startActivityForResult(chooserIntent, PICK_IMAGE);

                break;
            case R.id.settingsLogoutButton:
                ParseUser.logOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.settingsSaveButton:
                if (nameEditText.getText().toString().trim().matches("")) {
                    Toast.makeText(this, "Введите новый user name", Toast.LENGTH_SHORT).show();
                } else if (nameEditText.getText().toString().trim().equals(currentUser.getUsername())) {
                    Toast.makeText(this, "Введите новый user name", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog pd = new ProgressDialog(this);
                    pd.setTitle("Загрузка картинки");
                    pd.setMessage("Пожалуйста подождите");
                    pd.show();
                    currentUser.setUsername(nameEditText.getText().toString());
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                pd.dismiss();
                                Toast.makeText(SettingsActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                            } else {
                                pd.dismiss();
                                Toast.makeText(SettingsActivity.this, "Этот user name уже занят", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }

//    public Bitmap getCroppedBitmap(Bitmap bitmap) {
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
//                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        int radius;
//        if (bitmap.getWidth() > bitmap.getHeight()) {
//            radius = bitmap.getHeight()/2;
//        } else {
//            radius = bitmap.getWidth()/2;
//        }
//        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
//                radius, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
////        return Bitmap.createScaledBitmap(output, 200, 200, false);
//        return output;
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                bm = null;
//
//                if (data != null) {
//                    try {
//                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
//                    } catch(IOException e) {
//                        e.printStackTrace();
//                    }
//                    profileImage.setImageBitmap(bm);
//                    bm_small = Bitmap.createScaledBitmap(bm, 100, 100, false);
//                    bm = Bitmap.createScaledBitmap(bm, 200, 200, false);
//                    imageChanged = true;
//                }
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if(resultCode == RESULT_OK){
                String picturePath = data.getStringExtra("picturePath");
                //perform Crop on the Image Selected from Gallery
                performCrop(picturePath);
            }
        }

        if (requestCode == RESULT_CROP ) {
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                // Set The Bitmap Data To ImageView
                bm_small = Bitmap.createScaledBitmap(selectedBitmap, 100, 100, false);
                bm = Bitmap.createScaledBitmap(selectedBitmap, 350, 350, false);
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setTitle("Загрузка картинки");
                pd.setMessage("Пожалуйста подождите");
                pd.show();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                final byte[] profilePic = stream.toByteArray();
                ByteArrayOutputStream stream_small = new ByteArrayOutputStream();
                bm_small.compress(Bitmap.CompressFormat.PNG, 100, stream_small);
                byte[] profilePic_small = stream_small.toByteArray();
                final ParseFile file_small = new ParseFile(currentUser.getUsername()+"_avatar_small", profilePic_small);
                file_small.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            final ParseFile file = new ParseFile(currentUser.getUsername() + "_avatar", profilePic);
                            file.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        currentUser.put("avatar", file);
                                        currentUser.put("avatar_small", file_small);
                                        currentUser.saveInBackground();
                                        pd.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        pd.dismiss();
                                        Log.d("ParseException", e.toString());
                                        Toast.makeText(SettingsActivity.this, "Something went wrong while uploading avatar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(SettingsActivity.this, "Something went wrong while uploading avatar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                profileImage.setImageBitmap(bm);
//                profileImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
//            cropIntent.putExtra("outputX", 350);
//            cropIntent.putExtra("outputY", 350);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
