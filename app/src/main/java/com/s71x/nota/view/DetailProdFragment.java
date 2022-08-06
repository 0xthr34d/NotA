package com.s71x.nota.view;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.s71x.nota.R;
import com.s71x.nota.model.NotaLab;
import com.s71x.nota.model.Prod;

import java.util.Calendar;
import java.util.UUID;

public class DetailProdFragment extends Fragment {
	private Toolbar mToolbar;
	private ImageView mImgImageView;
	private TextInputLayout mNameLayout;
	private TextInputLayout mDescriptionLayout;
	private EditText mNameEditText;
	private EditText mDescriptionEditText;
	private EditText mDateEditText;
	private FloatingActionButton mPhotoProdfab;
	private FloatingActionButton mDeletePhotoProdfab;
	private ImageButton mAddButton;
	private TextView mCounter;
	private ImageButton mSubtractButton;
	private Prod mProd;
	private UUID uuid;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uuid = null;
		if(getArguments().getString(ProdListFragment.EDIT_PROD_KEY) != null){//si existe el producto en la base de datos
			uuid = UUID.fromString(getArguments().getString(ProdListFragment.EDIT_PROD_KEY));
			mProd = NotaLab.get(getActivity()).getProdByUuid(uuid.toString());
		}else{//si no, obtengo el id del almacen para crear un producto nuevo
			mProd = new Prod(Calendar.getInstance().getTime(),UUID.fromString(getArguments().getString(ProdListFragment.NEW_PROD_KEY)));
		}
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_detail_prod,container,false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//obtengo todas las vistas
		mImgImageView = view.findViewById(R.id.img_prod_imageview);
		mToolbar = view.findViewById(R.id.toolbarDetailProd);
		mPhotoProdfab = view.findViewById(R.id.photo_prod_fab);
		mDeletePhotoProdfab = view.findViewById(R.id.delete_photo_prod_fab);
		mNameEditText = view.findViewById(R.id.name_prod_edittext);
		mDateEditText = view.findViewById(R.id.date_prod_edittext);
		mDescriptionEditText = view.findViewById(R.id.description_prod_edittext);
		mCounter = view.findViewById(R.id.counter_textView);
		mAddButton = view.findViewById(R.id.button_add);
		mSubtractButton = view.findViewById(R.id.button_subtract);

		mNameLayout = view.findViewById(R.id.name_prod_edittext_layout);
		mDescriptionLayout = view.findViewById(R.id.description_prod_edittext_layout);


		mToolbar.setNavigationIcon(R.drawable.ic_close);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				requireActivity().getSupportFragmentManager().popBackStack();
			}
		});
		mToolbar.setOnMenuItemClickListener(item -> {
			switch(item.getItemId()){
				case R.id.save:
					if(mNameEditText.getText().toString().isEmpty()){
						mNameLayout.setError(getString(R.string.errorNameProdDetail));
						return false;
					}else if(mNameEditText.getText().toString().length() > mNameLayout.getCounterMaxLength()
					|| mDescriptionEditText.getText().toString().length() > mDescriptionLayout.getCounterMaxLength()){
						return false;
					}
					if(uuid == null){//si el argumento uuid no existe añade el producto a la basede datos
						mProd.setmName(mNameEditText.getText().toString());
						mProd.setmDescription(mDescriptionEditText.getText().toString());
						mProd.setmAmount(Integer.parseInt(mCounter.getText().toString()));
						NotaLab.get(getActivity()).addProd(mProd);
					}else{//si el argumento uuid existe actualiza el producto en la base de datos
						mProd.setmName(mNameEditText.getText().toString());
						mProd.setmDescription(mDescriptionEditText.getText().toString());
						mProd.setmAmount(Integer.parseInt(mCounter.getText().toString()));
						NotaLab.get(getActivity()).updateProd(mProd);
					}
					requireActivity().getSupportFragmentManager().popBackStack();
					return true;
				default:
					return false;

			}
		});


		Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_no_camera,null);
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		mImgImageView.setImageBitmap(mProd.getmImg() != null ? NotaLab.transformByteArrayToBitmap(mProd.getmImg()) : bitmap);

		ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
				new ActivityResultContracts.StartActivityForResult(),
				new ActivityResultCallback<ActivityResult>() {
					@Override
					public void onActivityResult(ActivityResult result) {
						if (result.getResultCode() == RESULT_OK && result.getData() != null) {
							Bundle bundle = result.getData().getExtras();
							Bitmap extras = (Bitmap) bundle.get("data");
							mImgImageView.setImageBitmap(extras);
							mProd.setmImg(NotaLab.transformBitmapToArray(((BitmapDrawable) mImgImageView.getDrawable()).getBitmap()));
						}
					}
				}
		);

		//prod fab photo
		mPhotoProdfab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				activityResultLauncher.launch(intent);

			}
		});

		mDeletePhotoProdfab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Drawable drawable = ResourcesCompat.getDrawable(requireActivity().getResources(),R.drawable.ic_no_camera,null);
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				mImgImageView.setImageBitmap(bitmap);
				mProd.setmImg(null);
			}
		});

		mNameEditText.setText(mProd.getmName());

		mDescriptionEditText.setText(mProd.getmDescription());

		mDateEditText.setText(DateFormat.format("dd/MM/yy",mProd.getmDate()));
		mDateEditText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DatePickerFragment pickerFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
						Calendar c = Calendar.getInstance();
						c.set(Calendar.YEAR, year);
						c.set(Calendar.MONTH, month);
						c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						mProd.setmDate(c.getTime());
						mDateEditText.setText(DateFormat.format("dd/MM/yy",c.getTime()));
					}
				});
				pickerFragment.show(getActivity().getSupportFragmentManager(),"datePicker");

			}
		});

		mCounter.setText(String.format("%s",mProd.getmAmount()));

		mAddButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCounter.setText(String.format("%s",Integer.parseInt(mCounter.getText().toString()) + 1));
				if(Integer.parseInt(mCounter.getText().toString()) > 1){
					mSubtractButton.setEnabled(true);
				}
			}
		});

		mSubtractButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCounter.setText(String.format("%s",Integer.parseInt(mCounter.getText().toString()) - 1));
				if(Integer.parseInt(mCounter.getText().toString()) == 1){
					mSubtractButton.setEnabled(false);
				}
			}
		});
	}
}
