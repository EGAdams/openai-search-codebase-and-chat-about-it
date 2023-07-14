package com.nac.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nac.R;
import com.nac.database.ProgramDataSource;

public class ProgramFragment extends Fragment {
    public static final String COMPRESS_MODE_EXTRAS = "compress_mode";
    private Bundle bundle;

    public static ProgramFragment create(Bundle bundle) {
        ProgramFragment fragment = new ProgramFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public Bundle getBundle() {
        return bundle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_program, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = getArguments();
        setText(view, R.id.txt_airport_name, bundle.getString(ProgramDataSource.PROGRAM_TABLE_SELECTION[1]));
        setText(view, R.id.txt_location, bundle.getString(ProgramDataSource.PROGRAM_TABLE_SELECTION[2]));
        setText(view, R.id.txt_comment, bundle.getString(ProgramDataSource.PROGRAM_TABLE_SELECTION[3]));
        boolean compressMode = bundle.getBoolean(COMPRESS_MODE_EXTRAS, false);
        if (compressMode) {
            view.findViewById(R.id.label_airport_name).setVisibility(View.GONE);
            view.findViewById(R.id.label_location).setVisibility(View.GONE);
            view.findViewById(R.id.label_comment).setVisibility(View.GONE);
        }
    }

    private void setText(View rootView, int textViewId, String text) {
        ((TextView) rootView.findViewById(textViewId)).setText(text);
    }
}