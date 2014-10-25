package com.xchanging.slidemenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.debasmita.clickshare.R;

public class TestFragment extends Fragment {

    @Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, 
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.alpha_list_view, container, false);
		return view;
    }
}