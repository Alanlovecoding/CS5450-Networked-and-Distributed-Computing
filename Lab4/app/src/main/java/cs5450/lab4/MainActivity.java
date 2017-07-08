package cs5450.lab4;

import android.content.ClipData;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private MenuItem login;
    private MenuItem logout;
    private FloatingActionButton mNewPostButton;
    private SearchView mSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b = getIntent().getExtras();
        String query = null; // or other values
        if(b != null) {
            query = b.getString("query");
        }
        final ItemsFragment pub = new ItemsFragment();
        final PrivateFragment pri = new PrivateFragment();
        pub.setQuery(query);
        pri.setQuery(query);

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {pub, pri};
            private final String[] mFragmentNames = {"PUBLIC", "PRIVATE"};
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mNewPostButton = (FloatingActionButton) findViewById(R.id.new_post_button);
        mNewPostButton.setOnClickListener(this);
        mSearchBar = (SearchView) findViewById(R.id.search_bar);
        mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("query", query); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        Log.e("onCreateOp", "User: " + u);
        login = menu.getItem(0);
        logout = menu.getItem(1);
        if (u == null) {
            login.setVisible(true);
            logout.setVisible(false);
        } else {
            login.setVisible(false);
            logout.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_login) {
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        } else if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            login.setVisible(true);
            logout.setVisible(false);
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.new_post_button) {
            FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
            if (u == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(this, NewPostActivity.class));
                finish();
            }
        }
    }
}
