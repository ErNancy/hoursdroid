/*
 *     Copyright 2016 Makoto Consulting Group, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.makotogo.mobile.hoursdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.makotogo.mobile.framework.AbstractArrayAdapter;
import com.makotogo.mobile.framework.AbstractFragment;
import com.makotogo.mobile.framework.ViewBinder;
import com.makotogo.mobile.hoursdroid.model.DataStore;
import com.makotogo.mobile.hoursdroid.model.Job;
import com.makotogo.mobile.hoursdroid.model.Project;

import java.util.List;

/**
 * Created by sperry on 1/30/16.
 */
public class ProjectListFragment extends AbstractFragment {

    private static final String TAG = ProjectListFragment.class.getSimpleName();

    private static final int REQUEST_CODE_PROJECT_DETAIL = 100;

    private static final boolean IN_ACTION_MODE = true;
    private static final boolean NOT_IN_ACTION_MODE = false;
    private transient boolean mInActionMode;

    /**
     * This is the one and only piece of state info we care
     * about managing. Everything else can either be retrieved,
     * or recreated.
     */
    private transient Job mJob;

    /**
     * Convenience, pure and simple.
     */
    private transient Project mProject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // If this Fragment is being recreated, we want to rematerialize
        /// its state
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_project_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle New Job
        if (id == R.id.menu_item_project_new) {
            // Fire off Job Edit Activity
            // Create an empty Job object to be used by JobDetailActivity
            Project project = new Project();
            project.setJob(mJob);
            Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
            intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT, project);
            // Start the activity
            startActivityForResult(intent, REQUEST_CODE_PROJECT_DETAIL);
            // Handled
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String METHOD = "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + "): ";
        Log.d(TAG, METHOD);
        super.onActivityResult(requestCode, resultCode, data);
        // The resulting Intent should contain the user's choice of Project.
        /// If not, do nothing. Otherwise, set the choice, and call updateUI();
        if (requestCode == REQUEST_CODE_PROJECT_DETAIL && resultCode == Activity.RESULT_OK) {
            if (data.getSerializableExtra(ProjectDetailActivity.RESULT_PROJECT) != null) {
                Log.d(TAG, METHOD + "Processing result...");
                mProject = (Project) data.getSerializableExtra(ProjectDetailActivity.RESULT_PROJECT);
                updateUI();
            }
        } else {
            String message = "Cannot handle requestCode (" + requestCode + ") and/or resultCode (" + resultCode + "). User pressed the Back button, maybe?";
            Log.w(TAG, METHOD + message);
        }
        Log.d(TAG, METHOD + "DONE.");
    }

    @Override
    protected void processFragmentArguments() {
        mJob = (Job) getArguments().get(FragmentFactory.FRAG_ARG_JOB);
        // Sanity check
        if (mJob == null) {
            // Complain. Loudly.
            throw new RuntimeException("Fragment (" + TAG + ") argument (" + FragmentFactory.FRAG_ARG_JOB + ") cannot be null!");
        }
    }

    @Override
    protected View configureUI(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        final String METHOD = "configureUI(...): ";
        Log.d(TAG, METHOD + "BEGIN");
        // Inflate the view
        View view = layoutInflater.inflate(R.layout.fragment_project_list, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listview_project_list);
        configureListView(listView);
        configureDoneButton(view);

        Log.d(TAG, METHOD + "END");
        return view;
    }

    @Override
    protected void updateUI() {
        final String METHOD = "updateUI(): ";
        Log.d(TAG, METHOD + "Starting AsyncTask...");
        // Now refresh the view - use AsyncTask
        new AsyncTask<Void, Void, List<Project>>() {

            @Override
            protected List<Project> doInBackground(Void... params) {
                final String METHOD = "doInBackground(): ";
                DataStore dataStore = DataStore.instance(getActivity());
                Log.d(TAG, METHOD);
                return dataStore.getProjects(mJob);
            }

            @Override
            protected void onPostExecute(List<Project> projects) {
                final String METHOD = "onPostExecute(" + projects + "): ";
                Log.d(TAG, METHOD + "Adding " + projects.size() + " to ListView adapter...");
                if (getListViewAdapter() != null) {
                    getListViewAdapter().clear();
                    getListViewAdapter().addAll(projects);
                    getListViewAdapter().notifyDataSetChanged();
                    if (mProject != null) {
                        int selectedIndex = 0;
                        for (int aa = 0; aa < getListViewAdapter().getCount(); aa++) {
                            if (getListViewAdapter().getItem(aa).equals(mProject)) {
                                selectedIndex = aa;
                                break;
                            }
                        }
                        getListView().setSelection(selectedIndex);
                    }
                }
                Log.d(TAG, METHOD + "Done.");
            }
        }.execute();
        Log.d(TAG, "updateUI()... DONE");
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        // Nothing to do
    }

    @Override
    protected void restoreInstanceState(Bundle savedInstanceState) {
        // Nothing to do
    }

    @Override
    protected boolean validate(View view) {
        return true;
    }

    // Private class stuff

    /**
     * I really hate stuff like this, but I don't see any better way around it. I want to be
     * able to handle short (regular) and long press on the list view. Unfortunately, both
     * are triggered on a long press, so it's impossible to tell which is which. Except that
     * it *appears* that the Long press is always handled first, which gives us the chance to
     * set some bullshit state variables like this one (yuck yuck yuck).
     * <p/>
     * If the "InActionMode" attribute is true, it indicates that the Activity is currently
     * in Action Mode, which will cause the List View to ignore short/regular press (by checking
     * this state value (again yuck yuck yuck)).
     *
     * @param value
     */
    private void setInActionMode(boolean value) {
        mInActionMode = value;
    }

    private ListView getListView() {
        ListView ret = null;
        View view = getView();
        if (view != null) {
            ret = (ListView) view.findViewById(R.id.listview_project_list);
        }
        return ret;
    }

    private AbstractArrayAdapter<Project> getListViewAdapter() {
        AbstractArrayAdapter<Project> ret = null;
        if (getListView() != null) {
            ret = (AbstractArrayAdapter<Project>) getListView().getAdapter();
        }
        return ret;
    }

    private void configureListView(final ListView listView) {
        // Set the Adapter property of the RecyclerView to the JobAdapter
        listView.setAdapter(new AbstractArrayAdapter<Project>(getActivity(), R.layout.project_list_row) {
            @Override
            protected ViewBinder<Project> createViewBinder() {
                return new ProjectViewBinder();
            }
        });
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Create the OnItemLongClickListener, used to bring up the contextual
        /// actions for the List View
        createOnItemLongClickListener(listView);
        // Create the
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mInActionMode) {
                    Project project = (Project) listView.getAdapter().getItem(position);
                    Project defaultProject = DataStore.instance(getActivity()).getDefaultProject(mJob);
                    if (project.equals(defaultProject)) {
                        Toast.makeText(getActivity(), "Cannot modify the Default project.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                        intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT, project);
                        startActivity(intent);
                    }
                    mProject = project;
                }
            }
        });
    }

    private void createOnItemLongClickListener(ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                boolean ret = false;
                final Project project = (Project) getListViewAdapter().getItem(position);
                // Long press... It will be handled first. Set the state variable (yuck).
                setInActionMode(IN_ACTION_MODE);
                // Disable Done button
                getActivity().startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // Inflate the menu
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.menu_project_ca, menu);
                        // Must return true here or the ART will abort creation of the menu!
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        final String METHOD = "onActionItemClicked(ActionMode, MenuItem): ";
                        boolean ret;
                        // Switch on the menu item ID
                        switch (item.getItemId()) {
                            case R.id.menu_item_project_edit:
                                // Fire off the JobDetailActivity
                                Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                                intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT, project);
                                // Start the activity
                                startActivity(intent);
                                Toast.makeText(getActivity(), "Project (" + project.getName() + ") will be edited (eventually)....", Toast.LENGTH_LONG).show();
                                ret = true;// handled
                                break;
                            case R.id.menu_item_project_delete:
                                Log.d(TAG, METHOD + "Attempting to delete a Project...");
                                DataStore dataStore = DataStore.instance(getActivity());
                                if (dataStore.hasHours(project)) {
                                    Toast.makeText(getActivity(), "Cannot delete a Project that has Hours.", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d(TAG, METHOD + "Project has no Hours objects for it... deleting...");
                                    dataStore.delete(project);
                                }
                                ret = true;// handled
                                break;
                            default:
                                // Not necessary, but included for completeness
                                ret = false;
                                break;
                        }
                        mode.finish();
                        // The UI probably needs updating.
                        updateUI();
                        return ret;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        final String METHOD = "onPrepareActionMode(" + mode + ", " + menu + "): ";
                        Log.d(TAG, METHOD + "...");
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        final String METHOD = "onDestroyActionMode(" + mode + "): ";
                        Log.d(TAG, METHOD + "...");
                        // Through with Action Mode. The list view can go back to handling
                        /// short press.
                        setInActionMode(NOT_IN_ACTION_MODE);
                    }
                });
                return ret;
            }
        });
    }

    private void configureDoneButton(View view) {
        Button button = (Button) view.findViewById(R.id.button_project_list_done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the Activity Result
                Intent intent = new Intent();
                intent.putExtra(ProjectListActivity.RESULT_PROJECT, mProject);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }
}
