package com.s71x.nota.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment{

	private DatePickerDialog.OnDateSetListener listener;

	public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener){
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setListener(listener);
		return fragment;
	}

	public void setListener(DatePickerDialog.OnDateSetListener listener){
		this.listener = listener;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(),listener,year,month,day);
	}
}
