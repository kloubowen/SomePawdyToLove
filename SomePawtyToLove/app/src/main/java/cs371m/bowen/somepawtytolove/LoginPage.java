package cs371m.bowen.somepawtytolove;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {
    EditText email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.password_text);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(View theView){
        if (!validate()){
            return;
        }

        String enteredEmail = email.getText().toString();
        String enteredPassword = password.getText().toString();
        mAuth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            success();
                        } else {
                            Toast.makeText(LoginPage.this,
                                    "Invalid email/password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signUp(View theView){
        if (!validate()){
            return;
        }
        String enteredEmail = email.getText().toString();
        String enteredPassword = password.getText().toString();
        mAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            success();
                        } else {
                            Toast.makeText(LoginPage.this,
                                    "Invalid email/password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validate(){
        return !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty();
    }

    public void success(){
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        setResult(MainActivity.EXITED, i);
        finish();
    }
}
