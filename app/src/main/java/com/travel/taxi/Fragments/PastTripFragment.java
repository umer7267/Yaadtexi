package com.travel.taxi.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.taxi.Adapter.MapLocationAdapter;
import com.travel.taxi.ApiResponse.Login.Response;
import com.travel.taxi.ApiResponse.Trip.TripResponse;
import com.travel.taxi.Connection.Services;
import com.travel.taxi.Connection.Utils;
import com.travel.taxi.R;
import com.travel.taxi.Utils.LocalPersistence;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastTripFragment extends Fragment {

    protected MapLocationAdapter mListAdapter;
    protected RecyclerView mRecyclerView;

    private Services mApi;

    public PastTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past_trip, container, false);
        mApi = Utils.getApiService();

        String Token=((Response)LocalPersistence.readObjectFromFile(getActivity())).getAccessToken();

//        String Token = ((Response) LocalPersistence.readObjectFromFile(getActivity())).getAccessToken();
        mApi.Trip("Bearer " + Token).enqueue(new Callback<List<TripResponse>>() {
            @Override
            public void onResponse(Call<List<TripResponse>> call, retrofit2.Response<List<TripResponse>> response) {
                if (response.isSuccessful()) {
                    Log.e("past", "onResponse: " );
                    mRecyclerView = (RecyclerView) view.findViewById(R.id.card_list);

                    // Determine the number of columns to display, based on screen width.

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    mListAdapter = new MapLocationAdapter();
                    mListAdapter.setMapLocations(response.body());

                    if (response.body().size() == 0) {
                        view.findViewById(R.id.main_relative_layout).setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    mRecyclerView.setAdapter(mListAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<TripResponse>> call, Throwable t) {
                Log.e("response", t.getLocalizedMessage());
            }
        });

        return view;
    }

}
