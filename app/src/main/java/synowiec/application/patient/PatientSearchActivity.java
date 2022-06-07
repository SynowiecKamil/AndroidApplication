package synowiec.application.patient;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.MyAdapter;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Physiotherapist;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;

public class PatientSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{

    //defining instance fields
    private RecyclerView rv;
    private MyAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    public ArrayList<Physiotherapist> allPagesPhysiotherapists = new ArrayList();
    private Boolean isScrolling = false;
    private int currentPhysio, totalPhysios, scrolledOutPhysios;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_search);

        initializeViews();
        this.listenToRecyclerViewScroll();
        setupRecyclerView();
        retrieveAndFillRecyclerView("GET_PAGINATED", "", "0", "7");
    }

    /**
     setup RecyclerView
     */
    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new MyAdapter(allPagesPhysiotherapists);
        rv.setAdapter(mAdapter);
        rv.setLayoutManager(layoutManager);
    }

    /**
     download data from php mysql based on supplied query string
     */
    private void retrieveAndFillRecyclerView(final String action, String queryString,
                                             final String start, String limit) {

        mAdapter.searchString = queryString;
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;

        if (action.length() > 0) {
            showProgressBar(mProgressBar);
            retrievedData = api.search(action, queryString, start, limit);
        } else {
            allPagesPhysiotherapists.clear();
            showProgressBar(mProgressBar);
            retrievedData = api.retrievePhysio();
        }
        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if(response == null || response.body() == null){
                    showInfoDialog(PatientSearchActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                List<Physiotherapist> currentPagePhysiotherapists = response.body().getResult();

                if (currentPagePhysiotherapists != null && currentPagePhysiotherapists.size() > 0) {
                    if (action.equalsIgnoreCase("GET_PAGINATED_SEARCH")) {
                        allPagesPhysiotherapists.clear();
                    }
                    allPagesPhysiotherapists.addAll(currentPagePhysiotherapists);
                } else {
                    if (action.equalsIgnoreCase("GET_PAGINATED_SEARCH")) {
                        allPagesPhysiotherapists.clear();
                    }
                    show(PatientSearchActivity.this,"Hey! Reached End. No more Found");
                }
                mAdapter.notifyDataSetChanged();
                hideProgressBar(mProgressBar);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                hideProgressBar(mProgressBar);
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PatientSearchActivity.this, "ERROR", t.getMessage());
            }
        });
    }

    /**
     Listening to scroll events. Implementing scroll to load more data pagination technique
     */
    private void listenToRecyclerViewScroll() {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //when scrolling starts
                super.onScrollStateChanged(recyclerView, newState);
                //check for scroll state
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // When the scrolling has stopped
                super.onScrolled(recyclerView, dx, dy);
                currentPhysio = layoutManager.getChildCount();
                totalPhysios = layoutManager.getItemCount();
                scrolledOutPhysios = ((LinearLayoutManager) recyclerView.getLayoutManager()).
                        findFirstVisibleItemPosition();

                if (isScrolling && (currentPhysio + scrolledOutPhysios ==
                        totalPhysios)) {
                    isScrolling = false;

                    if (dy > 0) {
                        // Scrolling up
                        retrieveAndFillRecyclerView("GET_PAGINATED",
                                mAdapter.searchString,
                                String.valueOf(totalPhysios), "7");

                    } else {
                        // Scrolling down
                    }
                }
            }
        });
    }

    /**
     inflating menu. Show SearchView inside the toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.physio_search_page_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        searchView.setQueryHint("Szukaj");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                Utils.openActivity(this, PatientDashboardActivity.class);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        retrieveAndFillRecyclerView("GET_PAGINATED_SEARCH", query, "0", "5");
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    /**
     initializing widgets
     */
    private void initializeViews() {
        mProgressBar = findViewById(R.id.mProgressBarLoad);
        mProgressBar.setIndeterminate(true);
        showProgressBar(mProgressBar);
        rv = findViewById(R.id.mRecyclerView);
    }
}
