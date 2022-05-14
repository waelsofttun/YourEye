package com.example.youreye;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.Nullable;

public class Text_detection extends AppCompatActivity {

    //Decalartion des variables
    Button button_capture, button_copy;
    TextView textview_data;
    //image bitmap
    Bitmap bitmap ;
    //Text a prononcer
    TextToSpeech t2;
    private static final int REQUEST_CAMERA_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_text_reco);

       //initialisation des variables
        button_capture = findViewById(R.id.btn_camera);
        button_copy = findViewById(R.id.btn_copy);
         textview_data = findViewById(R.id.text_data);

        //verification des permissions sinon on les demandes
        if (ContextCompat.checkSelfPermission(Text_detection.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Text_detection.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);


        }

        //Listener de bouton camera pour obtenir une photo
        button_capture.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v){

                    CropImage.activity().setGuidelines (CropImageView.Guidelines.ON).start(Text_detection.this);

            }
            });
        //copier et prononcée le text aprés reconnaissance de texte
        button_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String scannd_text= textview_data.getText().toString();
                        copyToClipBoard(scannd_text);

            }
        });
         //instantiation objet Text to Speech et choix de langue
         t2=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {

                    t2.setLanguage(Locale.FRANCE);

                }
            }
        });

    }
//
 @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
    //aprés modification ou corp de l'images
     if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
         CropImage.ActivityResult result = CropImage.getActivityResult(data);
         if (resultCode == RESULT_OK){
             Uri resulturi = result.getUri();
             //get image uri
             try {
                 //convertir l'image en image bitmap
                 bitmap =MediaStore.Images.Media.getBitmap(this.getContentResolver(),resulturi);
//executer la fonction de reconnaissance de text provided by google ML Kit
                         getTextFromImage(bitmap);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }

     }
 }
  //fonction permet la reconnaissance de text a partir d'un image bitmap 2d
    private void getTextFromImage (Bitmap bitmap) {
        //instantiation objet TextRecognizer
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(Text_detection.this, "Error Occurred!!!", Toast.LENGTH_SHORT);
        } else {
           //reconnaissance de text par block frame
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < textBlockSparseArray.size(); i++) {
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                //ajout de retour a la ligne
                stringBuilder.append("\n");
                textview_data.setText(stringBuilder.toString());
                button_capture.setText("Retake");
                button_copy.setVisibility(View.VISIBLE);
            }


        }

    }

    //fonction permet de copier de text vers Clipboard
    private void copyToClipBoard(String text){
        ClipboardManager clipBoard= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip =ClipData.newPlainText("Copied data", text);
        clipBoard.setPrimaryClip(clip);
        t2.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        Toast.makeText(Text_detection.this,"Copied",Toast.LENGTH_SHORT);
    }

}
