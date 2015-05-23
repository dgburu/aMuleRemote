package com.iukonline.amule.android.amuleremote.partfile;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.iukonline.amule.android.amuleremote.AmuleControllerApplication;
import com.iukonline.amule.android.amuleremote.R;
import com.iukonline.amule.android.amuleremote.helpers.ec.AmuleWatcher.ECPartFileWatcher;
import com.iukonline.amule.ec.ECPartFile;
import com.iukonline.amule.ec.ECPartFile.ECPartFileComment;



public class PartFileCommentsFragment extends SherlockListFragment implements ECPartFileWatcher {

    byte[] mHash;
    ECPartFile mPartFile;
    AmuleControllerApplication mApp;
    
    CommentsAdapter mCommentsAdpater;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mHash = getArguments().getByteArray(PartFileActivity.BUNDLE_PARAM_HASH);
        mApp = (AmuleControllerApplication) getActivity().getApplication();

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            //return null;
        }
        
        View v = inflater.inflate(R.layout.partfile_comments_fragment, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateECPartFile(mApp.mECHelper.registerForECPartFileUpdates(this, mHash));
    }

    @Override
    public void onPause() {
        super.onPause();
        mApp.mECHelper.unRegisterFromECPartFileUpdates(this, mHash);
    }
    
    
    // Interface ECPartFileWatcher
    
    @Override
    public String getWatcherId() {
        return this.getClass().getName();
    }

    
    @Override
    public void updateECPartFile(ECPartFile newECPartFile) {
        if (newECPartFile != null) {
            if (mPartFile == null) {
                mPartFile = newECPartFile;
                mCommentsAdpater = new CommentsAdapter(getActivity(), R.layout.partfile_sourcenames_fragment, mPartFile.getComments() );
                setListAdapter(mCommentsAdpater);
            } else {
                if (! mPartFile.getHashAsString().equals(newECPartFile.getHashAsString())) {
                    Toast.makeText(mApp, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                    if (mApp.enableLog) Log.e(AmuleControllerApplication.AC_LOGTAG, "Got a different hash in updateECPartFile!");

                    mApp.mECHelper.resetClient();
                }
            }
        }

        refreshView();
    }
    
    
    public void refreshView() {
        
        if (mCommentsAdpater != null) {
            mCommentsAdpater.notifyDataSetChanged();
        }
    }
    
    
    private class CommentsAdapter extends ArrayAdapter<ECPartFileComment> {
        private ArrayList<ECPartFileComment> items;
        
        public CommentsAdapter(Context context, int textViewResourceId, ArrayList<ECPartFileComment> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.amuledl_comments_row, null);
                }
                int ratingColor = android.R.color.primary_text_dark;
                ECPartFileComment o = items.get(position);
                if (o != null) {
                    int ratingRes = R.string.partfile_deatil_comments_rating_notrated;
                    switch (o.getRating()) {
                    case ECPartFile.RATING_INVALID:
                        ratingRes = R.string.partfile_deatil_comments_rating_invalid;
                        ratingColor = R.color.ratingInvalid;
                        break;
                    case ECPartFile.RATING_POOR:
                        ratingRes = R.string.partfile_deatil_comments_rating_poor;
                        ratingColor = R.color.ratingPoor;
                        break;
                    case ECPartFile.RATING_FAIR:
                        ratingRes = R.string.partfile_deatil_comments_rating_fair;
                        ratingColor = R.color.ratingFair;
                        break;
                    case ECPartFile.RATING_GOOD:
                        ratingRes = R.string.partfile_deatil_comments_rating_good;
                        ratingColor = R.color.ratingGood;
                        break;
                    case ECPartFile.RATING_EXCELLENT:
                        ratingRes = R.string.partfile_deatil_comments_rating_excellent;
                        ratingColor = R.color.ratingExcellent;
                        break;

                    }
                    ((TextView) v.findViewById(R.id.amuledl_comments_row_rating)).setText(ratingRes);
                    ((TextView) v.findViewById(R.id.amuledl_comments_row_rating)).setTextColor(getResources().getColor(ratingColor));
                    ((TextView) v.findViewById(R.id.amuledl_comments_row_author)).setText(o.getAuthor());
                    ((TextView) v.findViewById(R.id.amuledl_comments_row_filename)).setText(o.getSourceName());
                    ((TextView) v.findViewById(R.id.amuledl_comments_row_comment)).setText(o.getComment());
                }
                return v;
        }
        
    }
    
}
