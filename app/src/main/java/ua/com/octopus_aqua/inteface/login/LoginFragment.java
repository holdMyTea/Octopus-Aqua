package ua.com.octopus_aqua.inteface.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ua.com.octopus_aqua.inteface.R;


public class LoginFragment extends Fragment {

    EditText editLogin, editPassword;
    Button buttonLogin;

    //var to save change state for fragments in MainActivty
    boolean change = false;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentHolder = inflater.inflate(R.layout.fragment_login, container, false);

        editLogin = (EditText) fragmentHolder.findViewById(R.id.editLogin);
        editPassword = (EditText) fragmentHolder.findViewById(R.id.editPassword);

        buttonLogin = (Button) fragmentHolder.findViewById(R.id.buttonLogin);

        // NEXT -> editPassword to focus
        editLogin.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    editPassword.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                    return true;
                }
                return false;
            }
        });

        // launch logging on GO
        editPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    changeFragment();
                }
                return false;
            }
        });

        // launch logging on button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment();
            }
        });

        ((TextView) fragmentHolder.findViewById(R.id.textLogRegister)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RegistrationsActivity.class);
                startActivity(intent);
            }
        });

        Log.d("MY_TAG", "LoginFragment created");

        return fragmentHolder;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void changeFragment(){
        change = true;
    }

    public boolean isChange() {
        return change;
    }
}
