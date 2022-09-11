package com.kai.unogame.ui.authentication;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kai.unogame.R;
import com.kai.unogame.databinding.FragmentRegistrationBinding;
import com.kai.unogame.listener.RegistrationListener;
import com.kai.unogame.utils.FirebaseHelper;

public class RegistrationFragment extends Fragment implements RegistrationListener {

    private FragmentRegistrationBinding binding;
    AlertDialog.Builder builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        binding.buttonRegisterCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });

        binding.buttonRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClicked();
            }
        });
    }
    void onCancelClicked(){
        NavHostFragment.findNavController(this).navigate(R.id.action_RegistrationFragment_to_LoginFragment);
    }

    void onSubmitClicked(){
        String firstName = binding.editTextRegisterFirstName.getText().toString();
        String lastName = binding.editTextRegisterLastName.getText().toString();
        String email = binding.editTextRegisterEmail.getText().toString();
        String password = binding.editTextRegisterPassword.getText().toString();
        String city = binding.editTextRegisterCity.getText().toString();
        int genderID = binding.RadioGroupGender.getCheckedRadioButtonId();
        String gender = null;
        if(genderID == binding.radioButtonMale.getId()){
            gender = "male";
        }
        else{
            gender = "female";
        }
        if(!email.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !gender.isEmpty() && !city.isEmpty()){
            FirebaseHelper.register(email,password,firstName,lastName,city,gender,this);
        }
        else{
            builder.setMessage("Please enter Data");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void registered() {
        NavHostFragment.findNavController(this).navigate(R.id.action_RegistrationFragment_to_LoginFragment);
    }

    @Override
    public void registeredFailure(String message) {
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}