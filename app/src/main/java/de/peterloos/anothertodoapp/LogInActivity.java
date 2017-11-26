package de.peterloos.anothertodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    protected EditText edittextEmail;
    protected EditText edittextPassword;
    protected Button buttonLogIn;
    protected TextView textviewSignUp;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_log_in);

        // initialize firebase auth
        this.firebaseAuth = FirebaseAuth.getInstance();

        // retrieve references of controls
        this.edittextEmail = (EditText) this.findViewById(R.id.emailField);
        this.edittextPassword = (EditText) this.findViewById(R.id.passwordField);
        this.buttonLogIn = (Button) this.findViewById(R.id.loginButton);
        this.textviewSignUp = (TextView) this.findViewById(R.id.signUpText);

        // connect controls with event listener
        this.buttonLogIn.setOnClickListener(this);
        this.textviewSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == this.textviewSignUp) {

            Intent intent = new Intent(this, SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(intent);

        } else if (view == this.buttonLogIn) {

            String email = LogInActivity.this.edittextEmail.getText().toString();
            String password = LogInActivity.this.edittextPassword.getText().toString();
            email = email.trim();
            password = password.trim();

            if (email.isEmpty() || password.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.login_error_title)
                    .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Task<AuthResult> task = this.firebaseAuth.signInWithEmailAndPassword(email, password);
                task.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            LogInActivity.this.startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
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
