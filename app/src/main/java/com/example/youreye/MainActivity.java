package com.example.youreye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    //declaration des variables
    TextView fullName,verifyMsg;
    //declaration des point d'entrée firbase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button resendCode;
    Button   guesscolor,camera_button,list_color,obj_detect;
    //objet FirebaseUser
    FirebaseUser user;
    ImageView profileImage;
    StorageReference storageReference;

    String phone,email,name;
    //Pour broadcast RECIVER
    private ComponentName mReceiverComponentName;
    private PackageManager mPackageManager;

    AirplaneModeChangeReceiver airplaneModeChangeReceiver = new AirplaneModeChangeReceiver();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ajouter  ActionBar  avec setSupportActionBar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);







        //intialisation des variables
        fullName = findViewById(R.id.profileName);

        obj_detect = findViewById(R.id.objectpred);
        guesscolor = findViewById(R.id.guesscolor);
        camera_button=findViewById(R.id.txt_camera_button);
        profileImage = findViewById(R.id.profileImage);
        list_color = findViewById(R.id.color_list);


        //instantiation des points d'entrées
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //get current user
        FirebaseUser currentUser = fAuth.getCurrentUser();
          if (currentUser == null) {
              //si user n 'est pas connecté on retourne vers page login
              startActivity(new Intent(getApplicationContext(),Login.class));
              finish();
          }else {

              //recupération de limage de user
              StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
              profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                      Picasso.get().load(uri).into(profileImage);
                  }
              });
              //lié les objets java a xml
              resendCode = findViewById(R.id.resendCode);
              verifyMsg = findViewById(R.id.verifyMsg);


              userId = fAuth.getCurrentUser().getUid();
              user = fAuth.getCurrentUser();
//si l'email n 'est pas verifié on affiche le message et le bouton
              if (!user.isEmailVerified()) {
                  verifyMsg.setVisibility(View.VISIBLE);
                  resendCode.setVisibility(View.VISIBLE);
   //set listener de lien de vérifcation par mail
                  resendCode.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(final View v) {
 //envoi de mail de verification
                          user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  Toast.makeText(v.getContext(), "Verification Email  Sent.", Toast.LENGTH_SHORT).show();
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Log.d("tag", " Email not sent " + e.getMessage());
                              }
                          });
                      }
                  });
              }

              //récupération de phone et name et mail de user
              DocumentReference documentReference = fStore.collection("users").document(userId);
              documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                  @Override
                  public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                      if (documentSnapshot.exists()) {
                          phone =documentSnapshot.getString("phone");
                          name=documentSnapshot.getString("fName");
                          fullName.setText("Welcome "+name );
                          email=documentSnapshot.getString("email");

                      } else {
                          Log.d("tag", "onEvent: Document do not exists");
                      }
                  }
              });

//boutton vers la liste des couleurs
              list_color.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent i = new Intent(view.getContext(),List_color.class);
                      startActivity(i);

                  }
              });

//boutton vers activity pour la detections des objets
              obj_detect.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent i = new Intent(view.getContext(),Main_obj_detection.class);
                      startActivity(i);
                  }
              });




//boutton vers activty pour la reconnaissance des couleurs
              guesscolor.setOnClickListener( new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      // open gallery
                      Intent i = new Intent(v.getContext(), color_detection.class);

                      startActivity(i);
//

                  }

          });

//boutton vers activity text detection

              camera_button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      startActivity(new Intent(MainActivity.this,Text_detection.class));
                  }
              });
          }
    }
//menu inflate  vers actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
//configuration des actions de menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.editprofile:{
                //aller vers lactivity editprofile en passant des donnée a travers les extras
                Intent i = new Intent(this, EditProfile.class);
                i.putExtra("fullName", name);
                i.putExtra("email", email);
                i.putExtra("phone", phone);
                startActivity(i);

                return true;}
            case R.id.resetpass: {

//reset password a travers Alertdialog

                final EditText resetPassword = new EditText(this);

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // recuperation de mail et envoi de lien de recupération
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Password Reseted.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, " Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });
                //Afficher Alerdialog
                passwordResetDialog.create().show();
            }
                return true;
            case R.id.logout:
                //Logout
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //set Intent Filter on start Activity
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneModeChangeReceiver, filter);
    }
   //unregister Receiver on Airplane mode desactivated
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneModeChangeReceiver);
    }
    //logout et aller vers activity Login
    public void logout() {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }







}