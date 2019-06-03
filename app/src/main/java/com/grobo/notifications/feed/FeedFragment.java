package com.grobo.notifications.feed;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grobo.notifications.R;
import com.grobo.notifications.admin.XPortal;
import com.grobo.notifications.admin.feed.AddFeedActivity;
import com.grobo.notifications.main.MainActivity;
import com.grobo.notifications.network.GetDataService;
import com.grobo.notifications.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class FeedFragment extends Fragment {

    public FeedFragment() {
    }

    private FeedViewModel feedViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FeedRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private RadioGroup radioGroup;
    private FloatingActionButton addFab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_feed);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });

        addFab = view.findViewById(R.id.add_feed_fab);

        emptyView = view.findViewById(R.id.feed_empty_view);
        recyclerView = view.findViewById(R.id.rv_feed_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FeedRecyclerAdapter(getContext(), (FeedRecyclerAdapter.OnFeedSelectedListener) getActivity());
        recyclerView.setAdapter(adapter);

        radioGroup = view.findViewById(R.id.radio_group_feed);

        if (getContext() instanceof MainActivity) {

            addFab.hide();

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.feed_radio_all:
                            observeAll();
                            break;
                        case R.id.feed_radio_starred:
                            observeStarred();
                            break;
                    }
                }
            });
            radioGroup.check(R.id.feed_radio_all);

        } else if (getContext() instanceof XPortal){

            addFab.show();

            radioGroup.setVisibility(View.GONE);
            final String feedPoster = getArguments().getString("club", "");
            if (feedPoster.equals("gymkhana")) {
                observeAll();
            } else {
                observeMy(feedPoster);
            }
            //TODO: add button

            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddFeedActivity.class);
                    intent.putExtra("club", feedPoster);
                    startActivity(intent);
                }
            });
        }

        return view;
    }

    private void updateData() {

        long latest = feedViewModel.getMaxEventId();
        Log.e("maxid", String.valueOf(latest));

        String token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(USER_TOKEN, "0");
        Log.e("token", token);

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<FeedItem.FeedItemSuper1> call = service.getNewFeed(token, latest);
        call.enqueue(new Callback<FeedItem.FeedItemSuper1>() {
            @Override
            public void onResponse(Call<FeedItem.FeedItemSuper1> call, Response<FeedItem.FeedItemSuper1> response) {

                if (response.isSuccessful()) {
                    List<FeedItem> allItems = response.body().getLatestFeeds();

                    for (FeedItem newItem : allItems) {
                        if (feedViewModel.getFeedCount(newItem.getId()) == 0)
                            feedViewModel.insert(newItem);
                        Log.e("feed", newItem.getEventName());
                    }

                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<FeedItem.FeedItemSuper1> call, Throwable t) {
                Log.e("failure", t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void observeAll() {
        feedViewModel.loadAllFeeds().removeObservers(FeedFragment.this);
        feedViewModel.loadAllFeeds().observe(FeedFragment.this, new Observer<List<FeedItem>>() {
            @Override
            public void onChanged(List<FeedItem> feedItems) {
                adapter.setFeedItemList(feedItems);
                if (feedItems.size() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void observeStarred() {
        feedViewModel.loadAllFeeds().removeObservers(FeedFragment.this);
        feedViewModel.loadAllFeeds().observe(FeedFragment.this, new Observer<List<FeedItem>>() {
            @Override
            public void onChanged(List<FeedItem> feedItems) {

                List<FeedItem> newList = new ArrayList<>();
                for (FeedItem n : feedItems) {
                    if (n.isInterested()) newList.add(n);
                }
                adapter.setFeedItemList(newList);
                if (newList.size() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void observeMy(final String poster) {
        feedViewModel.loadAllFeeds().removeObservers(FeedFragment.this);
        feedViewModel.loadAllFeeds().observe(FeedFragment.this, new Observer<List<FeedItem>>() {
            @Override
            public void onChanged(List<FeedItem> feedItems) {

                List<FeedItem> newList = new ArrayList<>();
                for (FeedItem n : feedItems) {
                    if (n.getFeedPoster().equals(poster)) newList.add(n);
                }
                adapter.setFeedItemList(newList);
                if (newList.size() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

}
