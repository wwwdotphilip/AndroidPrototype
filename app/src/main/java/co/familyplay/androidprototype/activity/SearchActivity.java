package co.familyplay.androidprototype.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.object.Group;

//Visit the wiki for more info
public class SearchActivity extends ActionBarActivity {
    private SearchView search;
    private ListView itemList;
    private ArrayAdapter<String> adapter;
    private Context context;
    private List<ParseUser> userResults;
    private List<Group> groupResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = SearchActivity.this;
        search = (SearchView) findViewById(R.id.svSearch);
        itemList = (ListView) findViewById(R.id.lvItems);
        itemList.setOnItemClickListener(new ItemlistListener());
        setupSearchView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return false;
    }

    private void setupSearchView() {
        search.setSubmitButtonEnabled(false);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                final ProgressDialog pd = new ProgressDialog(context);
                pd.setMessage("Searching...");
                pd.show();

                ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                userQuery.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                userQuery.whereContains("search", search.getQuery().toString().toLowerCase());
                userQuery.findInBackground(new FindCallback<ParseUser>() {
                    public void done(final List<ParseUser> results, ParseException e) {
                        if (e == null) {
                            final List<String> queryList = new ArrayList<>();
                            for (int i = 0; i < results.size(); i++) {
                                if (results.get(i).getString("name") != null) {
                                    queryList.add(results.get(i).getString("name"));
                                } else {
                                    queryList.add(results.get(i).getString("username"));
                                }
                            }
                            userResults = results;

                            ParseQuery<Group> groupQuery = ParseQuery.getQuery("Group");
                            groupQuery.whereContains("search", search.getQuery().toString().toLowerCase());
                            groupQuery.findInBackground(new FindCallback<Group>() {
                                @Override
                                public void done(List<Group> list, ParseException e) {
                                    pd.hide();
                                    if (e == null) {
                                        for (Group group : list) {
                                            queryList.add(group.getGroupName());
                                        }
                                        groupResults = list;
                                        adapter = new ArrayAdapter<>(context,
                                                android.R.layout.simple_list_item_1, queryList);
                                        itemList.setAdapter(adapter);
                                    }
                                }
                            });
                        } else {
                            pd.hide();
                            Msg.Toast(e.getMessage(), context);
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (TextUtils.isEmpty(newText)) {

                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    private class ItemlistListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position >= userResults.size()){
                Intent i = new Intent(context, GroupActivity.class);
                i.putExtra("groupName", groupResults.get(position-userResults.size()).getGroupName());
                i.putExtra("groupId", groupResults.get(position-userResults.size()).getObjectId());
                startActivity(i);
            } else {
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("userId", userResults.get(position).getObjectId());
                startActivity(i);
//                Msg.Toast(userResults.get(position).getString("name"), context);
            }
        }
    }
}
