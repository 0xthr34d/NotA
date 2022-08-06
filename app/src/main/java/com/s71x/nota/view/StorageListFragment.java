package com.s71x.nota.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.s71x.nota.model.Storage;
import com.s71x.nota.model.NotaLab;
import com.s71x.nota.model.Prod;
import com.s71x.nota.R;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class StorageListFragment extends Fragment {

	public static final String EDIT_STORAGE_KEY = "uuid";
	public static final String STORAGE_KEY = "uuidStorage";

	private Toolbar mToolbar;
	private FloatingActionButton fab;
	private ItemTouchHelper itemTouchHelper;
	private RecyclerView mRecyclerView;
	private NotaAdapter mAdapter;
	private int mItemView;

	public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaHolder>{
		private List<Storage> mStorages;

		private class NotaHolder extends RecyclerView.ViewHolder{
			private ImageView mImageView;
			private TextView mTypeTextView;
			private TextView mNameTextView;
			private TextView mNumProdsTextView;
			private TextView mNumProdsTimedTextView;
			private Storage mStorage;

			public NotaHolder(LayoutInflater inflater, ViewGroup parent){
				super(inflater.inflate(mItemView,parent,false));
					mImageView = (ImageView) itemView.findViewById(R.id.image_storage);
					mTypeTextView = (TextView) itemView.findViewById(R.id.type_storage);
					mNameTextView = (TextView) itemView.findViewById(R.id.name_storage);
					mNumProdsTextView = (TextView) itemView.findViewById(R.id.num_prods);
					mNumProdsTimedTextView = (TextView) itemView.findViewById(R.id.timed_prods);
			}
		}

		public NotaAdapter(List<Storage> storages){
			mStorages = storages;
		}

		@NonNull
		@Override
		public NotaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			return new NotaHolder(layoutInflater,parent);
		}

		@Override
		public void onBindViewHolder(@NonNull NotaHolder holder, int position) {
			Storage mStorage = mStorages.get(position);

			//obtiene el par de la imagen y el tipo en texto pasandole el tipo int
			Pair<Integer,Integer> pair = NotaLab.imageAndType(mStorage.getmType());

			//setea la imagen en tipo y el nombre
			holder.mImageView.setImageResource(pair.first);
			holder.mTypeTextView.setText(pair.second);
			holder.mNameTextView.setText(mStorage.getmName().toString());

			//si la vista está en vista rejilla setea siguientes elementos: comida total y comida caducada
			if(mItemView != R.layout.list_item_storage_grid){
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
			}

			//implementa un set on click listener en la vista
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					//si la vista está en modo edición muestra un fragment detalle del almacen que hemos seleccionado para poder editarlo
					if(mItemView == R.layout.list_item_storage_list_edit){
						bundle.putString(EDIT_STORAGE_KEY,mStorage.getmId().toString());

						Fragment fragment = new DetailStorageFragment();
						fragment.setArguments(bundle);
						requireActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.setCustomAnimations(
									R.anim.slide_in,
									R.anim.fade_out,
									R.anim.fade_in,
									R.anim.slide_out
							)
							.replace(R.id.fragment_main, fragment)
							.addToBackStack(null)
							.commit();
					}else{//se está en vista "normal" muestra el fragment de la lista de productos del almacen
						bundle.putString(STORAGE_KEY,mStorage.getmId().toString());

						Fragment fragment = new ProdListFragment();
						fragment.setArguments(bundle);
						requireActivity()
							.getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.fragment_main, fragment)
							.addToBackStack(null)
							.commit();
					}
				}
			});
		}

		@Override
		public int getItemCount() {
			return mStorages.size();
		}

		public void setStorages(List<Storage> storages){
			mStorages = storages;
		}

		public void setStorage(Storage storage, int position){
			mStorages.add(position,storage);
		}

		public void deleteStorage(int position){
			mStorages.remove(position);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//creo el simpleCallback
		ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				int position = viewHolder.getAdapterPosition();
				String deleteUuid = mAdapter.mStorages.get(position).getmId().toString();
				Storage storageUndo = NotaLab.get(getActivity()).getStorageByUuid(deleteUuid);
				List<Prod> prods = NotaLab.get(getActivity()).getProdsByUuidStorage(storageUndo.getmId().toString());

				switch(direction){
					case ItemTouchHelper.LEFT:
					case ItemTouchHelper.RIGHT:
						deleteStorage(storageUndo, prods, position);
						break;
				}
			}

			@Override
			public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
				new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
						.addActionIcon(R.drawable.ic_delete)
						.addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_recycler_view_item_delete))
						.setActionIconTint(Color.BLACK)
						.create()
						.decorate();
				super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
			}
		};
		//instancio itemTouchHelper pasandole a la clase el objeto SimpleCallback
		itemTouchHelper = new ItemTouchHelper(simpleCallback);
		mItemView = R.layout.list_item_storage_grid;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_storage_list,container,false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//identidfica la vista
		mToolbar = view.findViewById(R.id.toolbarStorageList);
		fab = view.findViewById(R.id.fab);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.storage_recycler_view);

		mToolbar.setOnMenuItemClickListener(item -> {
			switch(item.getItemId()) {
				case R.id.edit:
					if(mItemView != R.layout.list_item_storage_list_edit){
						mItemView = R.layout.list_item_storage_list_edit;
						mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
						mRecyclerView.setAdapter(mAdapter);
						itemTouchHelper.attachToRecyclerView(mRecyclerView);
					}
					return true;
				case R.id.view:
					itemTouchHelper.attachToRecyclerView(null);
					if(mItemView != R.layout.list_item_storage_grid) {
						item.setIcon(R.drawable.ic_list_black);
						mItemView = R.layout.list_item_storage_grid;
						mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
						mRecyclerView.setAdapter(mAdapter);
					}else if(mItemView != R.layout.list_item_storage_list) {
						item.setIcon(R.drawable.ic_grid);
						mItemView = R.layout.list_item_storage_list;
						mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
						mRecyclerView.setAdapter(mAdapter);
					}
					return true;
				default:
					return false;
			}
		});

		//boton que muestra un fragment para añadir un almacen nuevo
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = new DetailStorageFragment();
				requireActivity()
					 	.getSupportFragmentManager()
						.beginTransaction()
						.setCustomAnimations(
								R.anim.slide_in,
								R.anim.fade_out,
								R.anim.fade_in,
								R.anim.slide_out
						)
						.replace(R.id.fragment_main, fragment)
						.addToBackStack(null)
						.commit();
			}
		});

		//obtiene los almacenes para pasarselos al adaptador
		List<Storage> storages = NotaLab.get(getContext()).getStorages();

		//setea un layout tipo gridlayout
		mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

		//crea el adaptador y lo setea a la recyclerview
		mAdapter = new NotaAdapter(storages);
		mRecyclerView.setAdapter(mAdapter);
	}

	private void deleteStorage(Storage storageUndo, List<Prod> prodsUndo, int position){
		//elimina el amacen
		NotaLab.get(getActivity()).deleteStoragebyUuid(storageUndo.getmId().toString());
		//elimina los productos de ese almacen
		NotaLab.get(getActivity()).deleteProdbyStorageUuid(storageUndo.getmId().toString());
		mAdapter.deleteStorage(position);
		mAdapter.notifyItemRemoved(position);
		Snackbar.make(mRecyclerView, getResources().getString(R.string.deletedStorage),Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.undoStorage), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//añade el almacen
				NotaLab.get(getActivity()).addStorage(storageUndo);
				//añade los productos del almacen
				NotaLab.get(getActivity()).addProds(prodsUndo);
				mAdapter.setStorage(storageUndo, position);
				mAdapter.notifyItemInserted(position);
			}
		}).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		mItemView = R.layout.list_item_storage_grid;
		itemTouchHelper.attachToRecyclerView(null);
		mAdapter.setStorages(NotaLab.get(getContext()).getStorages());
		mAdapter.notifyDataSetChanged();
	}
}
