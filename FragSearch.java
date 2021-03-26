package com.app.vivahmilan.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.vivahmilan.R;
import com.app.vivahmilan.adapter.AdapterSearchListProfile;
import com.app.vivahmilan.adapter.CastAdapter;
import com.app.vivahmilan.adapter.CountryAdapter;
import com.app.vivahmilan.adapter.ReligionAdapter;
import com.app.vivahmilan.adapter.StateAdapter;
import com.app.vivahmilan.adapter.pagig.PaginationAdapter;
import com.app.vivahmilan.adapter.pagig.PaginationScrollListener;
import com.app.vivahmilan.adapter.pagignation.PaginationListener;
import com.app.vivahmilan.adapter.pagignation.PostRecyclerAdapter;
import com.app.vivahmilan.api.ApiServiceProvider;
import com.app.vivahmilan.api.RetrofitListener;
import com.app.vivahmilan.model.Cast;
import com.app.vivahmilan.model.CastResponce;
import com.app.vivahmilan.model.Profile;
import com.app.vivahmilan.model.Religion;
import com.app.vivahmilan.model.SearchInfoResponce;
import com.app.vivahmilan.model.State;
import com.app.vivahmilan.model.StateResponce;
import com.app.vivahmilan.model.StaticDataResponce;
import com.app.vivahmilan.utils.Config;
import com.app.vivahmilan.utils.ErrorObject;
import com.app.vivahmilan.utils.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import static com.app.vivahmilan.adapter.pagignation.PaginationListener.PAGE_START;

import java.util.ArrayList;
import java.util.List;

public class FragSearch extends Fragment implements RetrofitListener {
    private ApiServiceProvider apiServiceProvider;
    private String strToken, strCAst = "", strReligion = "", strState = "", strMerstatus, strAgeFrom, strAgeTo;
    private Dialog dialog;
    private Spinner spnAgefrom, spnAgeTo, spnReligion, spnState, spnCast;
    private TextView tvReligion, tvCast, tvState;
    private CheckBox chNeverMerid, chDivorce, chWindow, chAnnulled, chAviating;
    private EditText edCity;
    private ReligionAdapter religionAdapter;
    private CastAdapter castAdapter;
    private CountryAdapter countriesAdapter;
    private StateAdapter stateAdapter;
    private LinearLayout layList, lsySearch;
    private RecyclerView recyclerView;
    private AdapterSearchListProfile adapterSearchList;
    private List<Profile> listProfile = new ArrayList<>();

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 20;
    private boolean isLoading = false;
    int itemCount = 1;
    LinearLayoutManager manager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initialisData(view);
        apiServiceProvider = ApiServiceProvider.getInstance(getContext());
        SessionManager sessionManager = new SessionManager(getContext());
        strToken = sessionManager.getUserDetails().get(Config.KEY_MOBILE);
        Log.i("TAG", "edxtoken: " + strToken);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.progress_dialog);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getValues();
        return view;
    }

    private void getValues() {
        dialog.show();
        apiServiceProvider.getStaticData(strToken, this);
        apiServiceProvider.getStateByID(strToken, "101", this);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initialisData(View aview) {
        getCheckBox(aview);
        spnAgefrom = aview.findViewById(R.id.spnAgeFrom);
        spnAgeTo = aview.findViewById(R.id.spnAgeTo);
        spnReligion = aview.findViewById(R.id.spnRegional);
        spnState = aview.findViewById(R.id.spnState);
        spnCast = aview.findViewById(R.id.spnCast);
        edCity = aview.findViewById(R.id.edCity);
        recyclerView = aview.findViewById(R.id.rec);
        layList = aview.findViewById(R.id.layList);
        tvReligion = aview.findViewById(R.id.tvspnRegional);
        tvCast = aview.findViewById(R.id.tvspnCast);
        tvState = aview.findViewById(R.id.tvspnState);
        lsySearch = aview.findViewById(R.id.laySearch);
        // getRecyClerBind();
        manager = new LinearLayoutManager(getContext());
        //  recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        adapterSearchList = new AdapterSearchListProfile(listProfile, getContext());
        recyclerView.setAdapter(adapterSearchList);

        recyclerView.setNestedScrollingEnabled(false);
        NestedScrollView mScrollView=aview.findViewById(R.id.mScrollView);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) mScrollView.getChildAt(mScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView
                        .getScrollY()));

                if (diff == 0) {
                    Log.i("TAG", "getSearchLIst:" + itemCount);
                    itemCount = itemCount + 1;
                    getSearchLIst(itemCount);
                }
            }
        });
      /*  recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = layoutM.getChildCount();
                totalItems = layoutM.getItemCount();
                scrollOutItems = layoutM.findFirstVisibleItemPosition();
                Log.i("TAG", totalItems + " getSearchLIst: cc " + currentItems + "  " + scrollOutItems);


                if ((currentItems + scrollOutItems) >= totalItems && scrollOutItems >= 0 && totalItems >= totalPage) {
                    isScrolling = false;
                    Log.i("TAG", "getSearchLIst:" + itemCount);
                    itemCount = itemCount + 1;
                    getSearchLIst(itemCount);
                } else {
                    Log.i("TAG", "getSearchLIst: el" + itemCount);
                }

            }
        });*/

        lsySearch.setVisibility(View.VISIBLE);
        ArrayList<String> years = new ArrayList<String>();
        for (int i = 18; i <= 50; i++) {
            years.add(Integer.toString(i));
        }
        tvReligion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvReligion.setVisibility(View.GONE);
                spnReligion.setVisibility(View.VISIBLE);
            }
        });
        tvCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvCast.setVisibility(View.GONE);
                spnCast.setVisibility(View.VISIBLE);
            }
        });
        tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvState.setVisibility(View.GONE);
                spnState.setVisibility(View.VISIBLE);
            }
        });
        ArrayAdapter<String> adapterAge = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, years);
        spnAgeTo.setAdapter(adapterAge);
        spnAgefrom.setAdapter(adapterAge);
        spnAgefrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strAgeFrom = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spnAgeTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strAgeTo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spnReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Religion myModel = (Religion) parent.getSelectedItem();
                Log.e("DATA", String.valueOf(myModel.getReligionId()));
                Log.e("DATA", myModel.getReligion());
                String relId = String.valueOf(myModel.getReligionId());
                strReligion = myModel.getReligion().toString();
                callForCastApi(relId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                State myModel = (State) parent.getSelectedItem();
                Log.e("DATA", String.valueOf(myModel.getId()));
                Log.e("DATA", myModel.getName());
                String relId = String.valueOf(myModel.getId());
                strState = myModel.getName().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnCast.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Cast myModel = (Cast) parent.getSelectedItem();
                Log.e("DATA", String.valueOf(myModel.getCastId()));
                Log.e("DATA", myModel.getCast());
                String relId = String.valueOf(myModel.getCastId());
                strCAst = myModel.getCast().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        aview.findViewById(R.id.tvSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  getCheckBoxValue();
                getSearchLIst(itemCount);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getRecyClerBind() {
        Log.i("TAG", "getSearchLIst:recc ");


        // adapterSearchList = new AdapterSearchListProfile(new ArrayList<>(), getContext());

        /*recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                getSearchLIst(currentPage);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });*/

    }

    private void getSearchLIst(int no) {

        Log.i("TAG", "getSearchLIst: " + no);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONObject paramObject = new JSONObject();
                try {

                    paramObject.put("age_from", strAgeFrom);
                    paramObject.put("age_to", strAgeTo);
                    paramObject.put("marital_status", String.valueOf(getCheckBoxValue()));
                    paramObject.put("religion", strReligion);
                    paramObject.put("cast", strCAst);
                    paramObject.put("state", strState);
                    paramObject.put("city", edCity.getText().toString().trim());

                    Log.e("TAG", "validateData: " + paramObject.toString());

                    updateDataApi(paramObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 1500);
    }

    private void updateDataApi(JSONObject paramObject) {
        dialog.show();
        apiServiceProvider.getSearchInfo(strToken, paramObject, itemCount, this);
    }

    private void callForCastApi(String relId) {
        dialog.show();
        apiServiceProvider.getCastByRelID(strToken, relId, this);

    }

    private String getCheckBoxValue() {
        String strr = "";

        if (chNeverMerid.isChecked()) {
            strr = "Never Married";
        } else if (chDivorce.isChecked()) {
            strr = "Divorced";
        } else if (chWindow.isChecked()) {
            strr = "Windowed";
        } else if (chAnnulled.isChecked()) {
            strr = "Annulled";
        } else if (chAviating.isChecked()) {
            strr = "Awaiting Divorce";
        }
        return strr;
    }

    private void getCheckBox(View aview) {
        chNeverMerid = aview.findViewById(R.id.checkNever);
        chDivorce = aview.findViewById(R.id.checkDivorced);
        chWindow = aview.findViewById(R.id.checkWindowsd);
        chAnnulled = aview.findViewById(R.id.checkAnnulled);
        chAviating = aview.findViewById(R.id.checkAwating);
        chNeverMerid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chNeverMerid.setChecked(true);
                    chDivorce.setChecked(false);
                    chWindow.setChecked(false);
                    chAnnulled.setChecked(false);
                    chAviating.setChecked(false);
                }
            }
        });
        chDivorce.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chNeverMerid.setChecked(false);
                    chDivorce.setChecked(true);
                    chWindow.setChecked(false);
                    chAnnulled.setChecked(false);
                    chAviating.setChecked(false);
                }
            }
        });
        chWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chNeverMerid.setChecked(false);
                    chDivorce.setChecked(false);
                    chWindow.setChecked(true);
                    chAnnulled.setChecked(false);
                    chAviating.setChecked(false);
                }
            }
        });
        chAnnulled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chNeverMerid.setChecked(false);
                    chDivorce.setChecked(false);
                    chWindow.setChecked(false);
                    chAnnulled.setChecked(true);
                    chAviating.setChecked(false);
                }
            }
        });
        chAviating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    chNeverMerid.setChecked(false);
                    chDivorce.setChecked(false);
                    chWindow.setChecked(false);
                    chAnnulled.setChecked(false);
                    chAviating.setChecked(true);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResponseSuccess(Object responseBody, int apiFlag) {
        dialog.dismiss();
        if (responseBody instanceof StaticDataResponce) {
            StaticDataResponce staticDataResponce = (StaticDataResponce) responseBody;
            religionAdapter = new ReligionAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, staticDataResponce.getReligions());
            spnReligion.setAdapter(religionAdapter);
        }
        if (responseBody instanceof CastResponce) {
            CastResponce castResponce = (CastResponce) responseBody;
            castAdapter = new CastAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, castResponce.getCast());
            spnCast.setAdapter(castAdapter);
        }
        if (responseBody instanceof StateResponce) {
            StateResponce stateResponce = (StateResponce) responseBody;
            stateAdapter = new StateAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, stateResponce.getStates());
            spnState.setAdapter(stateAdapter);
        }

        if (responseBody instanceof SearchInfoResponce) {
            SearchInfoResponce searchInfoResponce = (SearchInfoResponce) responseBody;
            layList.setVisibility(View.VISIBLE);
            lsySearch.setVisibility(View.GONE);
            Log.i("TAG", "searchInfoResponce: " + searchInfoResponce.getProfiles().size());
            listProfile.addAll(searchInfoResponce.getProfiles());
            adapterSearchList.notifyDataSetChanged();
            //     adapterSearchList = new AdapterSearchListProfile(listProfile, getContext());
            //   recyclerView.setAdapter(adapterSearchList);

           /* if (currentPage != PAGE_START) adapterSearchList.removeLoading();
            adapterSearchList.addItems(searchInfoResponce.getProfiles());

            // check weather is last page or not
            if (currentPage < totalPage) {
                adapterSearchList.addLoading();
            } else {
                isLastPage = true;
            }
            isLoading = false;

            Log.i("TAG", "getSearchLIst: count " + listProfile.size());
            for (Profile ff : searchInfoResponce.getProfiles()) {
                Log.i("TAG", "getProfiles: " + ff.getId());
            }
            Log.i("TAG", "getProfiles: " + searchInfoResponce.getTotalRecords());
            if (searchInfoResponce.getTotalRecords() == 0) {
                Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            } else {
                lsySearch.setVisibility(View.GONE);
                //    Toast.makeText(getContext(), "Found Some Record", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    @Override
    public void onResponseError(ErrorObject errorObject, Throwable throwable, int apiFlag) {
        dialog.dismiss();
        Log.e("TAG", "onResponseError: " + throwable.getLocalizedMessage());
        Snackbar.make(getActivity().findViewById(android.R.id.content), "Invalid username or password" + throwable.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();

    }
}
