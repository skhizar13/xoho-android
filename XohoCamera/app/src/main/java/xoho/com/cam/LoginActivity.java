package xoho.com.cam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
Custom LogIn/SignUp Form

Using Firebase Authentication UI instead
 */
public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPref;
    EditText email;
    EditText password;
    Button signUp;
    Button logIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);
    }

    public void onClick(View view){
        if(email.getText().length()>0 && password.length()>0) {
            switch (view.getId()) {
                case R.id.buttonSignUp:
                    singUp(email.getText().toString(),password.getText().toString());
                    break;
                case R.id.buttonLogIn:
                    logIn(email.getText().toString(),password.getText().toString());
                    break;
            }
        }else{
            logIn("admin","admin");
            //Toast.makeText(this,"Please Enter both Email and Password",Toast.LENGTH_LONG).show();
        }
    }

    private void singUp(String user, String password) {
        if(!userExists(user)){
            if(password.length()>0){
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(user, password);
                editor.commit();
                Toast.makeText(this,"Sign Up complete with User:("+user+") and Password: ("+password+")",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this,"Please enter a Password",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this,"Username already exists",Toast.LENGTH_LONG).show();
        }
    }

    private void logIn(String user, String password){
        if(userExists(user)){
            if(password.length()>0){
                if(sharedPref.getString(user,"").equals(password)){
                    Toast.makeText(this,"Log In Successful with User:("+user+") and Password: ("+password+")\"",Toast.LENGTH_LONG).show();
                    Intent logInIntent =  new Intent(this,UploadsActivity.class);
                    logInIntent.putExtra("user",user);
                    startActivity(logInIntent);
                    finish();
                }
                else{
                    Toast.makeText(this,"Invalid Password",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(this,"Please enter Password",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this,"Username does not exist",Toast.LENGTH_LONG).show();
        }
    }

    private boolean userExists(String user){
        String value = sharedPref.getString(user,"");
        return value.length()>0 ? true:false;
    }
}