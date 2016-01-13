package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.makotogo.mobile.hoursdroid.model.Hours;
import com.makotogo.mobile.hoursdroid.model.Job;

/**
 * Created by sperry on 1/12/16.
 */
public class HoursListFragment extends Fragment {

    private static final String TAG = HoursListFragment.class.getSimpleName();

    public static final String ARG_JOB_HOURS_LIST = "arg." + HoursListFragment.class.getSimpleName();

    private Job mJob;
    private ListView mListView;
    private HoursAdapter mHoursAdapter;

    public static HoursListFragment newInstance(Job parentJob) {
        HoursListFragment ret = new HoursListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_JOB_HOURS_LIST, parentJob);
        ret.setArguments(args);
        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the view
        View ret = inflater.inflate(R.layout.fragment_hours_list, container, false);
        // Process Fragment arguments.
        // 1 - Job object to be edited
        Bundle arguments = getArguments();
        mJob = (Job) arguments.getSerializable(ARG_JOB_HOURS_LIST);
        if (mJob != null) {
            // Get a list of the Hours for this Job
        } else {
            Log.e(TAG, "Job detail object cannot be null!");
        }
        return ret;
    }

    private class HoursViewHolder {

    }

    private class HoursAdapter extends ArrayAdapter<Hours> {

        public HoursAdapter(Context context, int resource, Hours[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }
}
