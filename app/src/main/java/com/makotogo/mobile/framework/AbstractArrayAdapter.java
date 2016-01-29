package com.makotogo.mobile.framework;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Just a little tune that's been creeping and crawling around inside my head.
 * <p/>
 * BTW, the View Holder pattern is great, but sadly is mis-named. If you look closely
 * it doesn't actually HOLD the view, it BINDS to the view.
 * <p/>
 * Created by sperry on 1/24/16.
 */
public abstract class AbstractArrayAdapter<T extends ModelObject> extends ArrayAdapter<ModelObject> {
    private static final String TAG = AbstractArrayAdapter.class.getSimpleName();

    private Activity mActivity;
    private int mLayoutResourceId;

    public int getLayoutResourceId() {
        return mLayoutResourceId;
    }

    /**
     * The one-and-only way to create this class.
     *
     * @param activity
     * @param resource
     */
    public AbstractArrayAdapter(Activity activity, int resource) {
        super(activity, 0);
        final String METHOD = "<constructor>(" + activity + ", " + resource + "): ";
        Log.d(TAG, METHOD + "...");
        // Tells the superclass that we will be handling View inflation
        mActivity = activity;
        mLayoutResourceId = resource;
        Log.d(TAG, METHOD + "...DONE");
    }

    /**
     * Creates the ViewBinder<T> that is appropriate to the subclass.
     *
     * @return
     */
    protected abstract ViewBinder<T> createViewBinder();

    /**
     * Have to override this method in order to make the compiler happy.
     *
     * @param position
     * @return
     */
    @Override
    public T getItem(int position) {
        return (T) super.getItem(position);
    }

    protected LayoutInflater getLayoutInflater() {
        if (mActivity != null) {
            return mActivity.getLayoutInflater();
        }
        throw new RuntimeException(AbstractArrayAdapter.class.getName() + " is misconfigured! Activity parameter cannot be null!");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // View is not recycled. Create one.
            convertView = getLayoutInflater().inflate(getLayoutResourceId(), null);
            ViewBinder<T> viewBinder = createViewBinder();
            convertView.setTag(getLayoutResourceId(), viewBinder);
        }
        // Fill up the View with stuff
        T object = getItem(position);
        ViewBinder<T> viewBinder = (ViewBinder<T>) convertView.getTag(getLayoutResourceId());
        viewBinder.initView(convertView);
        viewBinder.bind(object, convertView);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Placeholder in case the Dropdown view needs to look different than
        /// the Passive view.
        return getView(position, convertView, parent);
    }

}