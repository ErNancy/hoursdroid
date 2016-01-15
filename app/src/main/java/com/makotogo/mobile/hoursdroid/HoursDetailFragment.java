package com.makotogo.mobile.hoursdroid;

import android.app.Fragment;
import android.os.Bundle;

import com.makotogo.mobile.hoursdroid.model.Hours;

/**
 * Created by sperry on 1/13/16.
 */
public class HoursDetailFragment extends Fragment {

    public static final String ARG_HOURS = "arg." + HoursDetailFragment.class.getSimpleName();

    public static HoursDetailFragment newInstance(Hours hours) {
        HoursDetailFragment ret = new HoursDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_HOURS, hours);
        ret.setArguments(args);
        return ret;
    }
}
