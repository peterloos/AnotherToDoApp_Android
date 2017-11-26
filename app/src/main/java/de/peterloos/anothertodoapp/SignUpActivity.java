package de.peterloos.anothertodoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    protected EditText edittextPassword;
    protected EditText edittextEMail;
    protected Button buttonSignUp;
    protected TextView textviewLogIn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_sign_up);

        // initialize firebase auth
        this.firebaseAuth = FirebaseAuth.getInstance();

        // retrieve references of controls
        this.edittextPassword = (EditText) this.findViewById(R.id.passwordField);
        this.edittextEMail = (EditText) this.findViewById(R.id.emailField);
        this.buttonSignUp = (Button) this.findViewById(R.id.signupButton);
        this.textviewLogIn = (TextView) this.findViewById(R.id.loginText);

        // connect controls with event listener
        this.buttonSignUp.setOnClickListener(this);
        this.textviewLogIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == this.textviewLogIn) {

            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(intent);

        } else if (view == this.buttonSignUp) {

            String password = this.edittextPassword.getText().toString();
            String email = this.edittextEMail.getText().toString();
            password = password.trim();
            email = email.trim();

            if (password.isEmpty() || email.isEmpty()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(R.string.signup_error_message)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Task<AuthResult> task = this.firebaseAuth.createUserWithEmailAndPassword(email, password);
                task.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            SignUpActivity.this.startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                .setTitle(R.string.login_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
            }
        }
    }
}
