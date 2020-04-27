package com.example.myapplication0103.ui.reg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.myapplication0103.R;

public class RegFragment extends Fragment {

    private RegViewModel regViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        regViewModel = ViewModelProviders.of(this).get(RegViewModel.class);
        View root = inflater.inflate(R.layout.fragment_reg, container, false);

        //так можно вызывать из любой активити =)
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.logIn_pref_file), Context.MODE_PRIVATE);
        final String organization = sharedPref.getString(getString(R.string.organization_key), "");
        final String drname = sharedPref.getString(getString(R.string.drname_key), "");
        final String uniq_id = sharedPref.getString("idOfActUniq", "0");

        /*final TextView textView = root.findViewById(R.id.text_reg);
        regViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        EditText org_code = (EditText) root.findViewById(R.id.editText_Code);
        org_code.setText(organization);

        EditText dr_name = (EditText) root.findViewById(R.id.editText_NameOfDr);
        dr_name.setText(drname);

        final Button save_button = root.findViewById(R.id.button_save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText org_code = getActivity().findViewById(R.id.editText_Code);
                final EditText dr_name = getActivity().findViewById(R.id.editText_NameOfDr);

                final String code = org_code.getText().toString();
                final String name = dr_name.getText().toString();

                Log.i("", "code: " + code);
                Log.i("", "name: " + name);

                Context context = getActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(
                        getString(R.string.logIn_pref_file), Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.drname_key), name);
                editor.putString(getString(R.string.organization_key), code);
                editor.commit();
            }
        });

        return root;
    }
}
