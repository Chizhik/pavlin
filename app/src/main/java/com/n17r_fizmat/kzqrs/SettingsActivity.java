package com.n17r_fizmat.kzqrs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    // Handle case when picture is not square
    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameEditText;
    private Button settingsSaveButton, settingsLogoutButton;
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
        settingsSaveButton = (Button) findViewById(R.id.settingsSaveButton);
        settingsLogoutButton = (Button) findViewById(R.id.settingsLogoutButton);

        if (currentUser.get("name")!=null && currentUser.getParseFile("avatar") != null) {
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
            name = currentUser.getString("name");
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
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);

                break;
            case R.id.settingsLogoutButton:
                ParseUser.logOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.settingsSaveButton:
                if (currentUser.getParseFile("avatar") == null && !imageChanged) {
                    Toast.makeText(this, "Please choose profile picture", Toast.LENGTH_SHORT).show();
                } else if (currentUser.get("name")==null && nameEditText.getText().toString().matches("")) {
                    Toast.makeText(this, "Please choose name", Toast.LENGTH_SHORT).show();
                } else {
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
                    // Upload to parse
                    if (imageChanged) {
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
                                                currentUser.put("name", nameEditText.getText().toString());
                                                currentUser.saveInBackground();
                                                Toast.makeText(SettingsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                                Intent intentHome = new Intent(SettingsActivity.this, MainActivity.class);
                                                pd.hide();
                                                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intentHome);
                                                finish();
                                            } else {
                                                pd.hide();
                                                Log.d("ParseException", e.toString());
                                                Toast.makeText(SettingsActivity.this, "Something went wrong while uploading avatar", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Something went wrong while uploading avatar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
//                    currentUser.put("name", nameEditText.getText().toString());
//                    currentUser.saveInBackground();
//                    Intent intentHome = new Intent(this, MainActivity.class);
//                    intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intentHome);
//                    finish();
                }
                break;
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int radius;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            radius = bitmap.getHeight()/2;
        } else {
            radius = bitmap.getWidth()/2;
        }
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
//        return Bitmap.createScaledBitmap(output, 200, 200, false);
        return output;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                bm = null;

                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                    profileImage.setImageBitmap(bm);
                    bm_small = Bitmap.createScaledBitmap(bm, 100, 100, false);
                    bm = Bitmap.createScaledBitmap(bm, 200, 200, false);
                    imageChanged = true;
                }
            }
        }
    }
}
