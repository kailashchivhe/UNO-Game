package com.kai.unogame.ui.authentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kai.unogame.R;
import com.kai.unogame.databinding.FragmentLoginBinding;
import com.kai.unogame.listener.LoginListener;
import com.kai.unogame.utils.FirebaseHelper;

public class LoginFragment extends Fragment implements LoginListener {

    private FragmentLoginBinding binding;
    AlertDialog.Builder builder;
    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(FirebaseHelper.getUser()!=null){
            NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_HomeFragment);
        }
        getActivity().setTitle("Login");
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked();
            }
        });
        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegistrationButtonClicked();
            }
        });
    }
    void onLoginButtonClicked(){
        String email = binding.editTextLoginEmail.getText().toString();
        String password = binding.editTextLoginPassword.getText().toString();
        if(!email.isEmpty() && !password.isEmpty()){
            FirebaseHelper.login(email,password,this);
        }
        else{
            builder.setMessage("Please enter Data");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
    void onRegistrationButtonClicked(){
        NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_RegistrationFragment);
    }

    @Override
    public void loggedIn() {
        NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_HomeFragment);
    }

    @Override
    public void loggedInFailure(String message) {
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}