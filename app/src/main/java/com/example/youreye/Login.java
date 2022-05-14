package com.example.youreye;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//activity Login
public class Login extends AppCompatActivity {

    //déclaration des composant (textviews , buttons , progressbar)
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mCreateBtn,forgotTextLink;
    ProgressBar progressBar;
    //point de entrée de  Firebase Authentication SDK
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //relié activity login class avec fichier xml
        setContentView(R.layout.activity_login);
        //Relié les composant avec leur id dans l'interface xml
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        forgotTextLink = findViewById(R.id.forgotPassword);
        //creation de linstance de FirebaseAuth
        fAuth = FirebaseAuth.getInstance();

        //login button listener
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get les valeur de l'email et mot de pass
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
               //Controle de saisie deux champs obligatoire
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }


               //activé le progressbar
                progressBar.setVisibility(View.VISIBLE);

                // authentification avec signInWithEmailAndPassword de l'objet FirebaseAuth

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //affiché toast si le mot de pass et email sont valide
                            Toast.makeText(Login.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            //creation d'intent pour aller vers MainActivity
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            //sinon affiché toast d'erreur et desactivé le progress barre
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });

            }
        });
        //Listener pour textview  mCreateBtn d'inscription
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });
            //listener pour le textview  forgotTextLink
        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creation de AlertDialog pour récupérée l'adresse mail de récupération
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link");
                passwordResetDialog.setView(resetMail);
                 //listener on positive response to the alertdialog
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // avoir le mail et envoi de mail de récupération
                        String mail = resetMail.getText().toString();
                        //listener pour le retour aprés lenvoi de lien
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Affiché toast si le lien est envoyé
                                Toast.makeText(Login.this, "Reset Link is Sent ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //toast d'erreur si il ya une erreur
                                Toast.makeText(Login.this, "Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });
                //affiché AlertDialog
                passwordResetDialog.create().show();

            }
        });


    }
}