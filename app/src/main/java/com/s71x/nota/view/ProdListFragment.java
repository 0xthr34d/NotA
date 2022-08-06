package com.s71x.nota.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.s71x.nota.model.Storage;
import com.s71x.nota.model.NotaLab;
import com.s71x.nota.model.Prod;
import com.s71x.nota.R;
import com.s71x.nota.viewModel.ProdsViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ProdListFragment extends Fragment implements SearchView.OnQueryTextListener{

	public static final String EDIT_PROD_KEY = "uuid";
	public static final String NEW_PROD_KEY = "uuidStorage";

	//propiedades de la IU
	private RecyclerView mRecyclerView;
	private ProdsAdapter mAdapter;
	private Toolbar mToolbar;
	private SearchView mSearchProds;
	private FloatingActionButton fab;

	//viewmodel
	private ProdsViewModel viewModel;

	//propiedades para almacenar el almacen
	private UUID mUuidStorage;
	private Storage mStorage;

	//propiedades para gestionar el deslizamiento de los productos en el recyclerview
	private ItemTouchHelper.SimpleCallback simpleCallback;
	private ItemTouchHelper itemTouchHelper;

	//vista de la recyclerview que se está mostrando
	private int mItemView;

	//productos que se van a mover a otro almacen
	private List<Prod> mMoveProds;

	//propiedades para la toolbar contextual
	private ActionMode actionMode;
	private ActionMode.Callback callback;

	private class ProdsAdapter extends RecyclerView.Adapter<ProdsAdapter.ProdsHolder>{
		private List<Prod> mProds;
		private List<Prod> mProdsSearch;

		private class ProdsHolder extends RecyclerView.ViewHolder{
			//propiedades de cada elemento del recyclerview
			private ImageView mImageView;
			private TextView mNameTextView;
			private TextView mDateTextView;
			private TextView mRemainDays;
			private ConstraintLayout mProdCardView;
			private TextView mAmountTextView;

			public ProdsHolder(LayoutInflater inflater, ViewGroup parent){
				super(inflater.inflate(mItemView,parent,false));
				mImageView = (ImageView) itemView.findViewById(R.id.img_prod_list);
				mNameTextView = (TextView) itemView.findViewById(R.id.name_prod_list);
				mDateTextView = (TextView) itemView.findViewById(R.id.date_prod_list);
				mProdCardView = (ConstraintLayout) itemView.findViewById(R.id.prod_card_view);
				mRemainDays = (TextView) itemView.findViewById(R.id.remain_days_prod_list);
				mAmountTextView = (TextView) itemView.findViewById(R.id.cantidad_prod_list);
			}
		}

		public ProdsAdapter(List<Prod> prods){
			mProdsSearch = new ArrayList<>();
			mProds = prods;
			mProdsSearch.addAll(prods);
		}

		@NonNull
		@Override
		public ProdsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			return new ProdsHolder(layoutInflater,parent);
		}

		@Override
		public void onBindViewHolder(@NonNull ProdsHolder holder, int position) {
			//obtiene el producto
			Prod prod = mProds.get(position);
			//obtiene la diferencia de dias de caducidad del producto
			long days = diffDate(prod.getmDate().getTime());
			//obtiene el drawable de la imagen por defecto
			Drawable drawable = requireActivity().getResources().getDrawable(R.drawable.ic_no_camera);
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			//si el producto tiene imagen la muestra, si no, muestra una por defecto
			holder.mImageView.setImageBitmap(prod.getmImg() != null ? NotaLab.transformByteArrayToBitmap(prod.getmImg()) : bitmap);
			//setea el nombre
			holder.mNameTextView.setText(prod.getmName());
			//formatea la fecha de caducidad
			String dateString = DateFormat.format("dd/MM/yy",prod.getmDate()).toString();
			//setea la fecha
			holder.mDateTextView.setText(dateString);

			if(mItemView != R.layout.list_item_prod_list){//si está en vista detalle
				//setea la cantidad y los dias restantes de caducidad
				holder.mAmountTextView.setText(Integer.toString(prod.getmAmount()));
				holder.mRemainDays.setText(days > 0 ? getResources().getString(R.string.daysProdRemaining) + " " + Long.toString(days) : getResources().getString(R.string.daysProdLapsed) + " " + Long.toString(Math.abs(days)));
			}

			int color = 0;
			if(days >= 7){//si le queda mas de una semana
				color = Color.rgb(120,255,175); //verde
			}else if(days > 0 && days < 7){//si le queda menos de una semana
				color = Color.rgb(255,200,100); //naranja
			}else{
				color = Color.rgb(255,120,120); //rojo
			}
			holder.mProdCardView.setBackgroundColor(color);//setea el color

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(viewModel.getActionMode() != null){
						viewModel.finishActionMode();
					}

					Bundle bundle = new Bundle();
					bundle.putString(EDIT_PROD_KEY,prod.getmUuid().toString());

					Fragment fragment = new DetailProdFragment();
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
				}
			});
		}

		@Override
		public int getItemCount() {
			return mProds.size();
		}

		public void search(String s){
			int length = s.length();
			if(length == 0){
				mProds.clear();
				mProds.addAll(mProdsSearch);
			}else{
				mProds.clear();
				for(Prod prod: mProdsSearch) {
					if(prod.getmName().toString().toLowerCase().contains(s.toLowerCase())){
						mProds.add(prod);
					}
				}
			}
			notifyDataSetChanged();
		}

		public void setProds(List<Prod> prods){
			mProds = prods;
			mProdsSearch.clear();
			mProdsSearch.addAll(prods);
		}

		public void setProd(Prod prod, int position){
			mProds.add(position,prod);
			mProdsSearch.add(position,prod);
		}

		public void deleteProd(int position){
			mProds.remove(position);
			mProdsSearch.remove(position);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//almacena el valor de la vista del recyclerview
		 mItemView = R.layout.list_item_prod_list;

		//instancia del viewmodel
		viewModel = new ViewModelProvider(requireActivity()).get(ProdsViewModel.class);

		//instancia el array de lo productos que se van a mover
		mMoveProds = new ArrayList<>();

		//obtiene el uuid de del almacen seleccionado anteriormente
		mUuidStorage = UUID.fromString(getArguments().getString(StorageListFragment.STORAGE_KEY));

		//obtiene el almacen
		mStorage = NotaLab.get(getActivity()).getStorageByUuid(mUuidStorage.toString());

		//instancia el objeto simplecallback
		simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				int position = viewHolder.getAdapterPosition();
				//guarda el objeto que se va a eliminar por si se borra sin querer se vuelva a añadir
				Prod mProdUndo = NotaLab.get(getActivity()).getProdByUuid(mAdapter.mProds.get(position).getmUuid().toString());

				switch(direction){
					case ItemTouchHelper.RIGHT:
						if(viewModel.getActionMode() == null) {
							viewModel.setActionMode(requireActivity().startActionMode(callback));
						}
						mMoveProds.add(mAdapter.mProds.get(position));
						viewModel.getActionMode().setTitle(getResources().getQuantityString(R.plurals.prodsToolbar,mMoveProds.size(),mMoveProds.size()));
						mAdapter.deleteProd(position);
						mAdapter.notifyItemRemoved(position);
						break;
					case ItemTouchHelper.LEFT:
						deleteProd(position,mProdUndo);
						break;
				}
			}

			@Override
			public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
				new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
						.addSwipeRightActionIcon(R.drawable.ic_move)
						.addSwipeLeftActionIcon(R.drawable.ic_delete)
						.addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_recycler_view_item_move))
						.addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background_recycler_view_item_delete))
						.setActionIconTint(Color.BLACK)
						.create()
						.decorate();
				super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
			}
		};

		//instancia itemTouchHelper
		itemTouchHelper = new ItemTouchHelper(simpleCallback);

		//instancia del callback del actionMode
		callback = new ActionMode.Callback() {
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu){
				mode.getMenuInflater().inflate(R.menu.contextual_action_bar, menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch(item.getItemId()){
					case R.id.moveStorage:
						//crea un array de string para almacenar los id de los productos
						String[] uuidProds = new String[mMoveProds.size()];

						//almacena en el array los id de los productos que voy a mover para despues
						//pasarselo al dialogfragment
						for(int i = 0; i < mMoveProds.size(); i++) {
							uuidProds[i] = mMoveProds.get(i).getmUuid().toString();
						}
						//muestra el dialogfragment
						SelectStorageDialogFragment.newFragmentDialog(uuidProds).show(requireActivity().getSupportFragmentManager(),"selectStorage");
						return true;
				}
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				mMoveProds.clear();
				mAdapter.setProds(NotaLab.get(getContext()).getProdsByUuidStorage(mUuidStorage.toString()));
				mAdapter.notifyDataSetChanged();
				updateToolbar();
				viewModel.setActionMode(null);
			}
		};
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_prod_list,container,false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//obtiene el par de la imagen y el tipo en texto pasandole el tipo int
		Pair<Integer,Integer> pair = NotaLab.imageAndType(mStorage.getmType());

		mToolbar = view.findViewById(R.id.toolbarProdList);
		mSearchProds = (SearchView) view.findViewById(R.id.search_prods);
		mRecyclerView = (RecyclerView) view.findViewById(R.id.prod_recycler_view);
		fab = view.findViewById(R.id.fab_Prods);

		//setea un listener en el searchview
		mSearchProds.setOnQueryTextListener(this);

		//setea el titulo de la toolbar
		mToolbar.setTitle(getResources().getString(pair.second) + " " + mStorage.getmName());

		mToolbar.setNavigationIcon(R.drawable.ic_back);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				requireActivity().getSupportFragmentManager().popBackStack();
			}
		});

		mToolbar.setOnMenuItemClickListener(item -> {
			switch(item.getItemId()){
				case R.id.edit:

					return true;
				case R.id.view:
					if(mItemView != R.layout.list_item_prod_list_detail) {
						item.setIcon(R.drawable.ic_list_black);
						mItemView = R.layout.list_item_prod_list_detail;
						mRecyclerView.setAdapter(mAdapter);

					}else if(mItemView != R.layout.list_item_prod_list) {
						item.setIcon(R.drawable.ic_detail_prod);
						mItemView = R.layout.list_item_prod_list;
						mRecyclerView.setAdapter(mAdapter);
					}
					return true;
				default:
					return false;

			}
		});

		//setea un layout en el recyclerview
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		//instancia el adaptador
		mAdapter = new ProdsAdapter(NotaLab.get(getContext()).getProdsByUuidStorage(mUuidStorage.toString()));
		//setea el adaptador a la recyclerview
		mRecyclerView.setAdapter(mAdapter);
		itemTouchHelper.attachToRecyclerView(mRecyclerView);

		updateToolbar();

		//setea un listener en el boton flotante que muestra el fragment detalle de un producto
		//nuevo pasandole el id del almacen
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(NEW_PROD_KEY, mStorage.getmId().toString());
				Fragment fragment = new DetailProdFragment();
				fragment.setArguments(bundle);
				requireActivity().getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(
							R.anim.slide_in,
							R.anim.fade_out,
							R.anim.fade_in,
							R.anim.slide_out
					)
					.replace(R.id.fragment_main,fragment)
					.addToBackStack(null)
					.commit();
			}
		});
	}

	//actualiza el texto de la toolbar
	private void updateToolbar(){
		mToolbar.setSubtitle(getResources().getQuantityString(R.plurals.prodsToolbar,mAdapter.getItemCount(),mAdapter.getItemCount()));
	}

	//elimina un producto de la lista y de la base de datos, y avisa al usuario
	private void deleteProd(int position, Prod prodUndo){
		//elimina el producto de la base de datos
		NotaLab.get(getActivity()).deleteProdByUuid(prodUndo.getmUuid().toString());
		//elimina el producto del adaptador
		mAdapter.deleteProd(position);
		//actualiza el subtitulo de la toolbar
		updateToolbar();
		//refresca los elementos y muestra la animacion
		mAdapter.notifyItemRemoved(position);
		//muestra una snackbar avisando al usuario que se ha eliminado un elemento
		Snackbar.make(mRecyclerView, getResources().getString(R.string.deletedProd),Snackbar.LENGTH_LONG).setAction(R.string.undoProd, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NotaLab.get(getActivity()).addProd(prodUndo);// añade el producto eliminado a la base de datos
				mAdapter.setProd(prodUndo,position);//inserta el producto en el adaptador
				updateToolbar();//actualiza la toolbar
				mAdapter.notifyItemInserted(position);//refresca los elementos y muestra la animacion
			}
		}).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		//inserta los elementos de la base de datos
		mAdapter.setProds(NotaLab.get(getContext()).getProdsByUuidStorage(mUuidStorage.toString()));
		//cambia el subtitulo de la toolbar
		updateToolbar();
		//refresca los elementos del adaptador
		mAdapter.notifyDataSetChanged();
	}

	//devuelve la diferencia de dias que hay entre la fecha actual y la fecha de caducidad del producto
	public static long diffDate(long fechaCaducidad){
		long diferencia = fechaCaducidad - Calendar.getInstance().getTime().getTime();

		long segsMilli = 1000;
		long minsMilli = segsMilli * 60;
		long horasMilli = minsMilli * 60;
		long diasMilli = horasMilli * 24;

		long days = diferencia / diasMilli;

		return days;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		mAdapter.search(newText);
		return false;
	}
}
