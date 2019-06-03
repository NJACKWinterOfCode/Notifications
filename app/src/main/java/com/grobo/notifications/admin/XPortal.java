package com.grobo.notifications.admin;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.grobo.notifications.R;
import com.grobo.notifications.feed.FeedDetailFragment;
import com.grobo.notifications.feed.FeedRecyclerAdapter;

import static com.grobo.notifications.utils.Constants.COORDINATOR;
import static com.grobo.notifications.utils.Constants.CR;
import static com.grobo.notifications.utils.Constants.LEAD;
import static com.grobo.notifications.utils.Constants.SECRETARY;
import static com.grobo.notifications.utils.Constants.SUB_COORDINATOR;
import static com.grobo.notifications.utils.Constants.VP;

public class XPortal extends AppCompatActivity implements XPortalFragment.OnPORSelectedListener, FeedRecyclerAdapter.OnFeedSelectedListener {

    FragmentManager manager;
    Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xportal);

        getSupportActionBar().setTitle("Admin Portal");

        manager = getSupportFragmentManager();

        setBaseFragment(savedInstanceState);
    }

    private void setBaseFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.frame_layout_admin) != null) {

            if (savedInstanceState != null) {
                return;
            }
            XPortalFragment firstFragment = new XPortalFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_admin, firstFragment).commit();
        }
    }

    private void transactFragment(Fragment frag){
        activeFragment = frag;
        manager.beginTransaction()
                .setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
                .replace(R.id.frame_layout_admin, frag)
                .addToBackStack("later_fragment")
                .commit();
    }

    @Override
    public void onPORSelected(String club, String power) {
        Fragment next;
        Bundle bundle = new Bundle();
        bundle.putString("club", club);
        bundle.putString("power", power);
        switch (power) {
            case SUB_COORDINATOR:
                next = new CoordinatorFragment();
                break;
            case LEAD:
                next = new CoordinatorFragment();
                break;
            case COORDINATOR:
                next = new CoordinatorFragment();
                break;
            case SECRETARY:
                next = new SecretaryFragment();
                break;
            case CR:
                next = new CRFragment();
                break;
            case VP:
                next = new VPFragment();
                break;
            default:
                next = new CoordinatorFragment();
        }
        next.setArguments(bundle);
        transactFragment(next);
    }

    @Override
    public void onFeedSelected(String id, View view, int position) {
        Fragment current = manager.findFragmentById(R.id.frame_layout_main);

        Fragment newFragment = new FeedDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("transitionName", "transition" + position);
        bundle.putString("id", id);
        newFragment.setArguments(bundle);

        current.setSharedElementReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
        current.setExitTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));

        newFragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
        newFragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom));

        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_main, newFragment);
        fragmentTransaction.addToBackStack("later_fragment");
        fragmentTransaction.addSharedElement(view, "transition" + position);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}