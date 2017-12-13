package com.zhizulx.tt.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhizulx.tt.DB.entity.CollectRouteEntity;
import com.zhizulx.tt.DB.entity.RouteEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.event.TravelEvent;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.adapter.CollectionAdapter;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.ui.widget.swiprecycleview.RecyItemTouchHelperCallback;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 设置页面
 */
public class MineCollectionFragment extends TTBaseFragment{
	private View curView = null;
    private IMTravelManager travelManager;
	private RecyclerView rvCollection;
	private CollectionAdapter collectionAdapter;
    private List<CollectRouteEntity> collectRouteEntityList = new ArrayList<>();

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {
                travelManager = imService.getTravelManager();
                initCollection();
                travelManager.reqGetCollectRoute();
            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		imServiceConnector.connect(this.getActivity());
        EventBus.getDefault().register(this);
		if (null != curView) {
			((ViewGroup) curView.getParent()).removeView(curView);
			return curView;
		}
		curView = inflater.inflate(R.layout.travel_fragment_mine_collection, topContentView);
		initRes();
        initBtn();
		return curView;
	}

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        imServiceConnector.disconnect(getActivity());
        EventBus.getDefault().unregister(this);
    }

	@Override
	public void onResume() {
		super.onResume();
	}

    /**
	 * @Description 初始化资源
	 */
	private void initRes() {
		setTopTitle(getString(R.string.mine_collection));
		setTopLeftButton(R.drawable.tt_top_back);
		topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getActivity().finish();
			}
		});
        rvCollection = (RecyclerView)curView.findViewById(R.id.rv_collection_disp);
	}

	@Override
	protected void initHandler() {
	}

    private void initBtn() {

    }

    private void initCollection() {
        rvCollection.setHasFixedSize(true);
        LinearLayoutManager layoutManagerResult = new LinearLayoutManager(getActivity());
        layoutManagerResult.setOrientation(LinearLayoutManager.VERTICAL);
        rvCollection.setLayoutManager(layoutManagerResult);
        CollectionAdapter.OnRecyclerViewListener collectionListenser = new CollectionAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                Log.e("swip", "onItemClick");
                travelManager.setRouteEntity(collectRouteEntityList.get(position).getRouteEntity());
                TravelUIHelper.openDetailDispActivity(getActivity());
            }

/*            @Override
            public void onTopClick(int position) {
                Log.e("swip", "onTopClick");
                collectionAdapter.notifyItemMoved(position, 0);
            }*/

            @Override
            public void onDelClick(int position) {
                Log.e("swip", "onDelClick");
                List<Integer> idlist = new ArrayList<>();
                idlist.add(collectRouteEntityList.get(position).getDbId());
                travelManager.reqDelCollectRoute(idlist);
                collectRouteEntityList.remove(position);
                collectionAdapter.notifyItemRemoved(position);
                collectionAdapter.notifyDataSetChanged();
            }
        };
        collectionAdapter = new CollectionAdapter(getActivity(), travelManager, collectRouteEntityList);
        collectionAdapter.setOnRecyclerViewListener(collectionListenser);
        rvCollection.setAdapter(collectionAdapter);

/*        RecyItemTouchHelperCallback itemTouchHelperCallback = new RecyItemTouchHelperCallback(collectionAdapter);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(rvCollection);*/
    }

    public void onEventMainThread(TravelEvent event){
        switch (event.getEvent()){
            case QUERY_COLLECT_ROUTE_OK:
                collectRouteEntityList.clear();
                collectRouteEntityList.addAll(travelManager.getCollectRouteEntityList());
                collectionAdapter.notifyDataSetChanged();
                break;
            case QUERY_COLLECT_ROUTE_FAIL:
                Log.e("yuki", "QUERY_COLLECT_ROUTE_FAIL");
                break;
        }
    }
}
