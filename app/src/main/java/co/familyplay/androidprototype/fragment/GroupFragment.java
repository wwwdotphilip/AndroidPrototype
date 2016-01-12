package co.familyplay.androidprototype.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import co.familyplay.androidprototype.R;
import co.familyplay.androidprototype.activity.GroupActivity;
import co.familyplay.androidprototype.dialog.Msg;
import co.familyplay.androidprototype.object.Group;

// Visit the wiki for mroe details
public class GroupFragment extends android.support.v4.app.Fragment {
    private ListView groupList;
    private List<String> list;
    private Button createGroup;
    private List<Group> allGroups;

    public GroupFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        groupList = (ListView) view.findViewById(R.id.lvGroup);
        createGroup = (Button) view.findViewById(R.id.btnCreate);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createGroup.setOnClickListener(new CreateNewGroup());
        loadGroup();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class GroupItemSelected implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(getActivity(), GroupActivity.class);
            i.putExtra("groupName", allGroups.get(position).getGroupName());
            i.putExtra("groupId", allGroups.get(position).getObjectId());
            startActivity(i);
        }
    }

    private void loadGroup(){
        ParseQuery<Group> query = ParseQuery.getQuery("Group");
        query.whereEqualTo("isPrivate", false);
        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e == null){
                    if (groups.size() > 0){
                        allGroups = groups;
                        list = new ArrayList<>();
                         for (int i = 0; i < groups.size(); i++){
                            list.add(groups.get(i).getGroupName());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_list_item_1, list );
                        groupList.setAdapter(adapter);
                        groupList.setOnItemClickListener(new GroupItemSelected());
                    }
                } else {
                    Msg.AlertDialog(getActivity(), "Error", e.getMessage());
                }
            }
        });
    }

    private class CreateNewGroup implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater(null);
            View customView = inflater.inflate(R.layout.new_post, null);
            final EditText post = (EditText) customView.findViewById(R.id.etPost);
            post.setHint("Group name");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Add new group");
            builder.setView(customView);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final Group newGroup = new Group();
                    newGroup.setCreatorId(ParseUser.getCurrentUser().getObjectId());
                    newGroup.setCreatorName(ParseUser.getCurrentUser().getUsername());
                    newGroup.setGroupName(post.getText().toString());
                    newGroup.isPrivate(false);
                    newGroup.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                list.add(0, post.getText().toString());
                                allGroups.add(0, newGroup);
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_list_item_1, list );
                                groupList.setAdapter(adapter);
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }

}
