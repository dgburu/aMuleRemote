/*
 * Copyright (c) 2015. Gianluca Vegetti
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.iukonline.amule.android.amuleremote.helpers.ec.tasks;

import com.iukonline.amule.android.amuleremote.R;
import com.iukonline.amule.ec.ECCodes;
import com.iukonline.amule.ec.ECPartFile;
import com.iukonline.amule.ec.exceptions.ECClientException;
import com.iukonline.amule.ec.exceptions.ECPacketParsingException;
import com.iukonline.amule.ec.exceptions.ECServerException;

import java.io.IOException;


public class ECPartFileActionAsyncTask extends AmuleAsyncTask {
    
    public enum ECPartFileAction {
        PAUSE, RESUME, DELETE, 
        A4AF_NOW, A4AF_AUTO, A4AF_AWAY, 
        PRIO_LOW, PRIO_NORMAL, PRIO_HIGH, PRIO_AUTO,
        RENAME, SET_CATEGORY
    }

    ECPartFile mECPartFile;
    ECPartFileAction mAction;
    
    String mStringParam = null;

    public ECPartFileActionAsyncTask setECPartFile(ECPartFile file) {
        mECPartFile = file;
        return this;
    }
    
    public ECPartFileActionAsyncTask setAction(ECPartFileAction action) {
        mAction = action;
        return this;
    }

    public ECPartFileActionAsyncTask setStringParam(String param) {
        mStringParam = param;
        return this;
    }


    @Override
    protected void backgroundTask() throws IOException, AmuleAsyncTaskException, ECClientException, ECPacketParsingException, ECServerException {
        
        if (isCancelled()) return;
        switch (mAction) {
        case DELETE:
            mECClient.changeDownloadStatus(mECPartFile.getHash(), ECCodes.EC_OP_PARTFILE_DELETE);
            break;
        case PAUSE:
            mECClient.changeDownloadStatus(mECPartFile.getHash(), ECCodes.EC_OP_PARTFILE_PAUSE);
            break;
        case RESUME:
            mECClient.changeDownloadStatus(mECPartFile.getHash(), ECCodes.EC_OP_PARTFILE_RESUME);
            break;
        case A4AF_NOW:
            mECClient.changeDownloadStatus(mECPartFile.getHash(), ECCodes.EC_OP_PARTFILE_SWAP_A4AF_THIS);
            break;
        case A4AF_AUTO:
            mECClient.changeDownloadStatus(mECPartFile.getHash(), ECCodes.EC_OP_PARTFILE_SWAP_A4AF_THIS_AUTO);
            break;
        case A4AF_AWAY:
            mECClient.changeDownloadStatus(mECPartFile.getHash(), ECCodes.EC_OP_PARTFILE_SWAP_A4AF_OTHERS);
            break;
        case PRIO_LOW:
            mECClient.setPartFilePriority(mECPartFile.getHash(), ECPartFile.PR_LOW);
            break;
        case PRIO_NORMAL:
            mECClient.setPartFilePriority(mECPartFile.getHash(), ECPartFile.PR_NORMAL);
            break;
        case PRIO_HIGH:
            mECClient.setPartFilePriority(mECPartFile.getHash(), ECPartFile.PR_HIGH);
            break;
        case PRIO_AUTO:
            mECClient.setPartFilePriority(mECPartFile.getHash(), ECPartFile.PR_AUTO);
            break;
        case RENAME:
            if (mStringParam == null) throw new AmuleAsyncTaskException(mECHelper.mApp.getResources().getString(R.string.partfile_task_rename_noname));
            mECClient.renamePartFile(mECPartFile.getHash(), mStringParam);
            break;
        case SET_CATEGORY:
            if (mStringParam == null) throw new AmuleAsyncTaskException(mECHelper.mApp.getResources().getString(R.string.partfile_task_set_cat_nocat));
            mECClient.setPartFileCategory(mECPartFile.getHash(), Long.parseLong(mStringParam));
        }
    }

    @Override
    protected void notifyResult() {
        mECHelper.notifyECPartFileActionWatchers(mECPartFile, mAction);
    }

}
