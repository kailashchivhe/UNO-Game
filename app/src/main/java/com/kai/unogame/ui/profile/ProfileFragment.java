package com.kai.unogame.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kai.unogame.R;
import com.kai.unogame.databinding.FragmentProfileBinding;
import com.kai.unogame.listener.ProfileListener;
import com.kai.unogame.model.User;
import com.kai.unogame.utils.FirebaseHelper;

public class ProfileFragment extends Fragment implements ProfileListener {

    FragmentProfileBinding binding;
    AlertDialog.Builder builder;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
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

        binding.buttonProfileCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });
        binding.buttonProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileClicked();
            }
        });
    }
    void onCancelClicked(){
        NavHostFragment.findNavController(this).navigate(R.id.action_ProfileFragment_to_HomeFragment);
    }
    void onProfileClicked(){
        String firstname = binding.editTextProfileFirstName.getText().toString();
        String lastname = binding.editTextProfileLastName.getText().toString();
        String city = binding.editTextProfileCity.getText().toString();
        String gender;
        int genderID = binding.radioGroup.getCheckedRadioButtonId();
        if(genderID == binding.radioButtonProfileMale.getId()){
            gender = "male";
        }
        else{
            gender = "female";
        }
        User user = new User(firstname,lastname,gender,city, FirebaseHelper.getUser().getUid());
        FirebaseHelper.profileUpdate(user,this);
    }

    @Override
    public void profileUpdate() {
        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_LONG).show();
        NavHostFragment.findNavController(this).navigate(R.id.action_ProfileFragment_to_HomeFragment);
    }

    @Override
    public void profileUpdateFailure(String message) {
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}