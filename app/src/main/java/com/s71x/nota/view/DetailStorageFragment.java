package com.s71x.nota.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.s71x.nota.model.Storage;
import com.s71x.nota.model.StoragesView;
import com.s71x.nota.model.NotaLab;
import com.s71x.nota.R;

import java.util.List;
import java.util.UUID;

public class DetailStorageFragment extends Fragment {
	private Storage mStorageBase;
	private RecyclerView mRecyclerView;
	private EditText mNameEditText;
	private TextView mSaveTextView;
	private StoragesAdapter mAdapter;
	private Toolbar mToolbar;
	private TextInputLayout mNameLayout;
	private UUID id;
	private boolean first = true;

	private class StoragesAdapter extends RecyclerView.Adapter<StoragesAdapter.StoragesHolder>{
		private List<StoragesView> mStorages;

		private class StoragesHolder extends RecyclerView.ViewHolder{
			private ImageView mImageView;
			private CardView mLinearLayout;
			private TextView mTypeTextView;

			public StoragesHolder(LayoutInflater inflater, ViewGroup parent){
				super(inflater.inflate(R.layout.list_item_add_storage,parent,false));
				mImageView = (ImageView) itemView.findViewById(R.id.image_add_storage);
				mLinearLayout = (CardView) itemView.findViewById(R.id.focus_linear_recyclerview);
				mTypeTextView = (TextView) itemView.findViewById(R.id.type_add_storage);
			}

		}

		public StoragesAdapter(List<StoragesView> storages){
			mStorages = storages;
		}

		@NonNull
		@Override
		public StoragesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			return new StoragesHolder(layoutInflater,parent);
		}

		@Override
		public void onBindViewHolder(@NonNull StoragesHolder holder, int position) {
			StoragesView mStorage = mStorages.get(position);

			if(mStorageBase.getmType() == position && first == true){
				mStorage.setmChecked(true);
				first = false;
			}

			if(mStorage.ismChecked()){
				((CardView) holder.mLinearLayout).setCardElevation(20);
				((CardView) holder.mLinearLayout).setCardBackgroundColor(Color.rgb(199,255,255));
				mStorageBase.setmType(position);
			}else{
				((CardView) holder.mLinearLayout).setCardElevation(0);
				((CardView) holder.mLinearLayout).setCardBackgroundColor(Color.rgb(255,255,255));
			}

			holder.mImageView.setImageResource(mStorage.getmImage());
			holder.mTypeTextView.setText(mStorage.getTypeText());

			mStorage.setmChecked(false);
			holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mStorage.setmChecked(true);
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public int getItemCount() {
			return mStorages.size();
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//si hay argumentos obtiene el almacen a partir del id
		if(getArguments() != null){
			id = UUID.fromString(getArguments().getString(StorageListFragment.EDIT_STORAGE_KEY));
			mStorageBase = NotaLab.get(getContext()).getStorageByUuid(id.toString());
		}else{//si no, crea un almacén vacio
			mStorageBase = new Storage();
		}

	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_detail_storage,container,false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mToolbar = (Toolbar) view.findViewById(R.id.toolbarAddStorage);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.add_storage_recyclerview);
		mNameEditText = (EditText) view.findViewById(R.id.name_storage_add_edittext);
		mNameLayout = (TextInputLayout) view.findViewById(R.id.name_storage_add_edittext_layout);

		mToolbar.setNavigationIcon(R.drawable.ic_close);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requireActivity().getSupportFragmentManager().popBackStack();
			}
		});

		mToolbar.setOnMenuItemClickListener(item -> {
			switch(item.getItemId()){
				case R.id.save:
					if(mNameEditText.getText().toString().isEmpty()){
						mNameLayout.setError(getString(R.string.errorNameStorageDetail));
						return false;
					}else if(mNameEditText.getText().toString().length() > mNameLayout.getCounterMaxLength()){
						return false;
					}
					mStorageBase.setmName(mNameEditText.getText().toString());
					if(id != null){//si existe el id en los argumentos entonces actualiza el almacen en la base de datos
						NotaLab.get(getActivity()).updateStorage(mStorageBase);
					}else{//si no crea el almacen
						NotaLab.get(getActivity()).addStorage(mStorageBase);
					}
					requireActivity().getSupportFragmentManager().popBackStack();
					return true;
				default:
					return false;
			}
		});

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false));
		List<StoragesView> storages = StoragesView.getSelectStorage();
		mAdapter = new StoragesAdapter(storages);
		mRecyclerView.setAdapter(mAdapter);

		mNameEditText.setText(mStorageBase.getmName());

	}
}
