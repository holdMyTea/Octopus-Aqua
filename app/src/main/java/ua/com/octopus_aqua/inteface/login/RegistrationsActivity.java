package ua.com.octopus_aqua.inteface.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ua.com.octopus_aqua.inteface.R;

public class RegistrationsActivity extends AppCompatActivity {

    EditText editRegLogin, editRegEmail, editRegPass, editRegPass2;
    Button buttonRegFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrations);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Registration");
        setSupportActionBar(toolbar);

        editRegLogin = (EditText) findViewById(R.id.editRegLogin);
        editRegEmail = (EditText) findViewById(R.id.editRegEmail);
        editRegPass = (EditText) findViewById(R.id.editRegPass);
        editRegPass2 = (EditText) findViewById(R.id.editRegPass2);

        editRegLogin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    setFocus(editRegEmail);
                    return true;
                }
                return false;
            }
        });

        editRegEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    setFocus(editRegPass);
                    return true;
                }
                return false;
            }
        });

        editRegPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    setFocus(editRegPass2);
                    return true;
                }
                return false;
            }
        });

        editRegPass2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    registrate();
                    return true;
                }
                return false;
            }
        });

        buttonRegFinish = (Button) findViewById(R.id.buttonRegFinish);

        buttonRegFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrate();
            }
        });
    }

    private void registrate(){
        String login, email, pass, pass2;
        login = editRegLogin.getText().toString();
        email = editRegEmail.getText().toString();
        pass = editRegPass.getText().toString();
        pass2 = editRegPass2.getText().toString();

        //TODO: login, mail cheq
        if(login.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty()){
            Toast.makeText(this,"Pls, fill all the fields",Toast.LENGTH_LONG).show();
            return;
        }
        if(!pass.equals(pass2)){
            Toast.makeText(this,"Passes do not match",Toast.LENGTH_LONG).show();
            return;
        }
        if(pass.length() < 8 || pass2.length() < 8){
            Toast.makeText(this,"Pass are too short",Toast.LENGTH_LONG).show();
            return;
        }
        //TODO: actually register
        Toast.makeText(this,"Registration successful",Toast.LENGTH_LONG).show();
        finish();
    }

    private void setFocus(EditText editText){
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
