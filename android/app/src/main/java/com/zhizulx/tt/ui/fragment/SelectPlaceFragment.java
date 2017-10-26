package com.zhizulx.tt.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhizulx.tt.DB.entity.CityEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.activity.IntroduceCityActivity;
import com.zhizulx.tt.ui.adapter.CityAdapter;
import com.zhizulx.tt.ui.adapter.ProvinceAdapter;
import com.zhizulx.tt.ui.adapter.SelectCityResultAdapter;
import com.zhizulx.tt.ui.adapter.TravelHotAdapter;
import com.zhizulx.tt.ui.adapter.TravelHotNameAdapter;
import com.zhizulx.tt.ui.base.TTBaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 设置页面
 */
public class SelectPlaceFragment extends TTBaseFragment{
	private View curView = null;
    private Intent intent;

    private TextView selectHot;
    private TextView selectNation;

    private RecyclerView rvHot;
    private TravelHotAdapter travelHotAdapter;
    private RecyclerView rvName;
    private TravelHotNameAdapter travelHotNameAdapter;

    private List<CityEntity> citySelectedList = new ArrayList<>();
    List<CityEntity> hotlist = new ArrayList<>();

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {
                if (imService.getTravelManager().getConfigEntity().getDestination() != null &&
                        !imService.getTravelManager().getConfigEntity().getDestination().equals("")) {
                    CityEntity cityEntity = new CityEntity();
                    cityEntity.setName(imService.getTravelManager().getConfigEntity().getDestination());
                    cityEntity.setSelect(1);
                    citySelectedList.add(cityEntity);
                }
                hotlist.clear();
                hotlist.addAll(imService.getTravelManager().getCityEntityList());
                travelHotAdapter.notifyDataSetChanged();
                travelHotNameAdapter.notifyDataSetChanged();
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
		if (null != curView) {
			((ViewGroup) curView.getParent()).removeView(curView);
			return curView;
		}
		curView = inflater.inflate(R.layout.travel_fragment_select_place, topContentView);
        intent = getActivity().getIntent();
		initRes();
        initHot();
        initName();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Activity.RESULT_FIRST_USER){
            switch (resultCode) {
                case 100:
                    String city = data.getStringExtra("cityCode");
                    if (data.getBooleanExtra("selectFlag", true)) {
                        citySelectedList.clear();
                        CityEntity cityEntity = getCityEntityByCityCode(city);
                        if (cityEntity != null) {
                            citySelectedList.add(cityEntity);
                        }
                        backProcess();
                    }
                    break;
            }
        }
    }

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * @Description 初始化资源
	 */
    private void backProcess() {
/*        if (citySelectedList.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.create_travel_not_select_destination), Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            if (citySelectedList.isEmpty()) {
                intent.putExtra("city", "");
            } else {
                intent.putExtra("city", citySelectedList.get(0).getName());
            }

            getActivity().setResult(101, intent);
            getActivity().finish();
            return;
        }
        getFragmentManager().popBackStack();
    }
	private void initRes() {
		// 设置标题栏
		setTopTitle(getString(R.string.select_destination));
		setTopLeftButton(R.drawable.tt_top_back);
        View.OnClickListener chooseListener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                backProcess();
            }
        };
		topLeftContainerLayout.setOnClickListener(chooseListener);

        selectHot = (TextView)curView.findViewById(R.id.select_place_hot);
        selectNation = (TextView)curView.findViewById(R.id.select_place_nation);

        rvHot = (RecyclerView)curView.findViewById(R.id.rv_hot);
        rvName = (RecyclerView)curView.findViewById(R.id.rv_name);
	}

	@Override
	protected void initHandler() {
	}

    private void initBtn() {
        View.OnClickListener selectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.select_place_hot:
                        if (rvHot.getVisibility() == View.GONE) {
                            rvHot.setVisibility(View.VISIBLE);
                            rvName.setVisibility(View.GONE);
                            selectHot.setBackground(getResources().getDrawable(R.drawable.shape_corner_select_city_left_click));
                            selectHot.setTextColor(getResources().getColor(R.color.white));
                            selectNation.setBackground(getResources().getDrawable(R.drawable.shape_corner_select_city_right_not_click));
                            selectNation.setTextColor(getResources().getColor(R.color.default_top_bk));
                        }
                        break;
                    case R.id.select_place_nation:
                        if (rvName.getVisibility() == View.GONE) {
                            rvName.setVisibility(View.VISIBLE);
                            rvHot.setVisibility(View.GONE);
                            selectHot.setBackground(getResources().getDrawable(R.drawable.shape_corner_select_city_left_not_click));
                            selectHot.setTextColor(getResources().getColor(R.color.default_top_bk));
                            selectNation.setBackground(getResources().getDrawable(R.drawable.shape_corner_select_city_right_click));
                            selectNation.setTextColor(getResources().getColor(R.color.white));
                        }
                        break;
                }
            }
        };
        selectHot.setOnClickListener(selectListener);
        selectNation.setOnClickListener(selectListener);
    }

    private void jump2CityIntroduction(CityEntity city) {
        Intent citySelect = new Intent(getActivity(), IntroduceCityActivity.class);
        citySelect.putExtra("cityCode", city.getCityCode());
        if (hasSelected(city)) {
            citySelect.putExtra("selectFlag", true);
        } else {
            citySelect.putExtra("selectFlag", false);
        }
        startActivityForResult(citySelect, Activity.RESULT_FIRST_USER);
    }

    private void initHot() {
        rvHot.setHasFixedSize(true);
        GridLayoutManager layoutManagerHot = new GridLayoutManager(getActivity(), 1);
        layoutManagerHot.setOrientation(LinearLayoutManager.VERTICAL);
        rvHot.setLayoutManager(layoutManagerHot);

        TravelHotAdapter.OnRecyclerViewListener hotRVListener = new TravelHotAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                jump2CityIntroduction(hotlist.get(position));
            }

            @Override
            public void onAddClick(int position) {
                citySelectedList.clear();
                citySelectedList.add(hotlist.get(position));
                backProcess();
            }
        };
        travelHotAdapter = new TravelHotAdapter(getActivity(), hotlist);
        travelHotAdapter.setOnRecyclerViewListener(hotRVListener);
        rvHot.setAdapter(travelHotAdapter);
    }

    private void initName() {
        rvName.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvName.setLayoutManager(layoutManager);
        TravelHotNameAdapter.OnRecyclerViewListener dayRVListener1 = new TravelHotNameAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                citySelectedList.clear();
                citySelectedList.add(hotlist.get(position));
                backProcess();
            }
        };
        travelHotNameAdapter = new TravelHotNameAdapter(getActivity(), hotlist);
        travelHotNameAdapter.setOnRecyclerViewListener(dayRVListener1);
        rvName.setAdapter(travelHotNameAdapter);
    }

    private boolean hasSelected(CityEntity cityEntity) {
        for (CityEntity index : citySelectedList) {
            if (cityEntity.getName().equals(index.getName())) {
                return true;
            }
        }
        return false;
    }

    private CityEntity getCityEntityByCityCode(String code) {
        for (CityEntity city : hotlist) {
            if (code.equals(city.getCityCode())) {
                return city;
            }
        }
        return null;
    }
}
