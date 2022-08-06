package com.s71x.nota.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.s71x.nota.R;
import com.s71x.nota.model.NotaLab;
import com.s71x.nota.model.Prod;
import com.s71x.nota.model.Storage;
import com.s71x.nota.viewModel.ProdsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SelectStorageDialogFragment extends DialogFragment {
	private RecyclerView mRecyclerView;
	private StorageAdapter mAdapter;
	private String[] mUuidProds;
	private List<Prod> mProds;

	private ProdsViewModel viewModel;

	public static SelectStorageDialogFragment newFragmentDialog(String[] uuidProds){

		SelectStorageDialogFragment fragment = new SelectStorageDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putStringArray("prods", uuidProds);
		fragment.setArguments(bundle);
		return fragment;
	}

	public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.StorageHolder>{

		private List<Storage> mStorages;

		private class StorageHolder extends RecyclerView.ViewHolder{
			private ImageView mImageView;
			private TextView mTypeTextView;
			private TextView mNameTextView;
			private TextView mNumProdsTextView;
			private TextView mNumProdsTimedTextView;
			private Storage mStorage;
			private StorageHolder(LayoutInflater inflater, ViewGroup parent) {
				super(inflater.inflate(R.layout.list_item_storage_list_edit,parent,false));
				mImageView = (ImageView) itemView.findViewById(R.id.image_storage);
				mTypeTextView = (TextView) itemView.findViewById(R.id.type_storage);
				mNameTextView = (TextView) itemView.findViewById(R.id.name_storage);
				mNumProdsTextView = (TextView) itemView.findViewById(R.id.num_prods);
				mNumProdsTimedTextView = (TextView) itemView.findViewById(R.id.timed_prods);
			}
		}

		public StorageAdapter(List<Storage> storages){
			mStorages = storages;
		}

		@NonNull
		@Override
		public StorageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			return new StorageHolder(layoutInflater,parent);
		}

		@Override
		public void onBindViewHolder(@NonNull StorageHolder holder, int position) {
			Storage mStorage = mStorages.get(position);

			//obtiene el par de la imagen y el tipo en texto pasandole el tipo int
			Pair<Integer,Integer> pair = NotaLab.imageAndType(mStorage.getmType());

			//setea la imagen en tipo y el nombre
			holder.mImageView.setImageResource(pair.first);
			holder.mTypeTextView.setText(pair.second);
			holder.mNameTextView.setText(mStorage.getmName().toString());
			//obtiene los productos del almacen
			List<Prod> prods = NotaLab.get(getActivity()).getProdsByUuidStorage(mStorage.getmId().toString());
			//setea el numero de productos que tiene el almacen
			holder.mNumProdsTextView.setText(Integer.toString(prods.size()));
			int caducada = 0;
			//bucle para obtener el total de comida caducada
			for(Prod prod : prods){
				if(ProdListFragment.diffDate(prod.getmDate().getTime()) <= 0){
					caducada++;
				}
			}
			//setea el numero de comida caducada
			holder.mNumProdsTimedTextView.setText(Integer.toString(caducada));


			//implementa un set on click listener en la vista
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					for(Prod prod: mProds){
						prod.setmUuidStorage(mStorage.getmId());
						NotaLab.get(requireActivity()).updateProd(prod);
					}
					mProds.clear();
					Toast.makeText(requireActivity(), getResources().getString(R.string.movedProds), Toast.LENGTH_LONG).show();
					viewModel.finishActionMode();
					dismiss();
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
		mUuidProds = getArguments().getStringArray("prods");
		mProds = new ArrayList<>();
		viewModel = new ViewModelProvider(requireActivity()).get(ProdsViewModel.class);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		return inflater.inflate(R.layout.select_storage_dialog, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		mRecyclerView = view.findViewById(R.id.select_storage_dialog_fragment);
		for(int i = 0; i < mUuidProds.length; i++){
			mProds.add(NotaLab.get(requireContext()).getProdByUuid(mUuidProds[i]));
			System.out.println(mProds.get(i).getmName().toString());
		}

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		mAdapter = new StorageAdapter(NotaLab.get(getActivity()).getStorages());
		mRecyclerView.setAdapter(mAdapter);


	}
}
