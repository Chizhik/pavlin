package com.n17r_fizmat.kzqrs;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;
    private static final int PICK_IMAGE = 1;
    ImageView upload, profileImage;
    EditText username, email, password, confirm_password;
    Button get_started;
    Bitmap bm, bm_small;
    Boolean imageChanged = false;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_new);

        upload = (ImageView) findViewById(R.id.reg_upload_photo);
        profileImage = (ImageView) findViewById(R.id.reg_profile_image);
        username = (EditText) findViewById(R.id.reg_user_name);
        email = (EditText) findViewById(R.id.reg_email);
        password = (EditText) findViewById(R.id.reg_password);
        confirm_password = (EditText) findViewById(R.id.reg_confirm_password);
        get_started = (Button) findViewById(R.id.reg_get_started);

        upload.setOnClickListener(this);
        get_started.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg_upload_photo:
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
            case R.id.reg_get_started:
                final String username_str = username.getText().toString().trim();
                final String email_str = email.getText().toString().trim();
                final String password_str = password.getText().toString().trim();
                String confirm_password_str = confirm_password.getText().toString().trim();
                if (!imageChanged) {
                    Toast.makeText(this, "Пожалуйста выберите фото профиля", Toast.LENGTH_SHORT).show();
                } else if (username_str.matches("") || email_str.matches("") || password_str.matches("") || confirm_password_str.matches("")) {
                    Toast.makeText(this, "Пожалуйста заполните все поля", Toast.LENGTH_SHORT).show();
                } else if (!password_str.equals(confirm_password_str)) {
                    Toast.makeText(this, "Введенные пароли не совпадают", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog pd = new ProgressDialog(this);
                    pd.setTitle("Загрузка");
                    pd.setMessage("Пожалуйста подождите");
                    pd.show();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    final byte[] profilePic = stream.toByteArray();
                    ByteArrayOutputStream stream_small = new ByteArrayOutputStream();
                    bm_small.compress(Bitmap.CompressFormat.PNG, 100, stream_small);
                    final byte[] profilePic_small = stream_small.toByteArray();
                    user = new ParseUser();
                    user.setUsername(username_str);
                    user.setPassword(password_str);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                final ParseFile file_small = new ParseFile(username_str+"_avatar_small", profilePic_small);
                                file_small.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            final ParseFile file = new ParseFile(username_str + "_avatar", profilePic);
                                            file.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        user.setUsername(username_str);
                                                        user.setPassword(password_str);
                                                        user.setEmail(email_str);
                                                        user.put("avatar", file);
                                                        user.put("avatar_small", file_small);
                                                        user.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    Toast.makeText(RegistrationActivity.this, "Сохранено!", Toast.LENGTH_SHORT).show();
                                                                    Intent intentHome = new Intent(RegistrationActivity.this, MainActivity.class);
                                                                    intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    startActivity(intentHome);
                                                                    finish();
                                                                    pd.dismiss();
                                                                } else {
                                                                    user.logOutInBackground();
                                                                    user.deleteInBackground();
                                                                    pd.dismiss();
                                                                    Log.d("ParseException", e.toString());
                                                                    Toast.makeText(RegistrationActivity.this, "Что-то пошло не так. Попробуйте еще раз", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        user.logOutInBackground();
                                                        user.deleteInBackground();
                                                        pd.dismiss();
                                                        Log.d("ParseException", e.toString());
                                                        Toast.makeText(RegistrationActivity.this, "Проблема с загрузкой фото профиля. Попробуйте еще раз", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            user.logOutInBackground();
                                            user.deleteInBackground();
                                            pd.dismiss();
                                            Log.d("ParseException", e.toString());
                                            Toast.makeText(RegistrationActivity.this, "Проблема с загрузкой фото профиля. Попробуйте еще раз", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                user.logOutInBackground();
                                user.deleteInBackground();
                                pd.dismiss();
                                Log.d("ParseException", e.toString());
                                Toast.makeText(RegistrationActivity.this, "Выбранное вами имя пользователя или email уже занят", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
        }
    }

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
                bm = Bitmap.createScaledBitmap(selectedBitmap, 300, 300, false);
                profileImage.setImageBitmap(bm);
//                profileImage.setScaleType(ImageView.ScaleType.FIT_XY);
                imageChanged = true;
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
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

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
