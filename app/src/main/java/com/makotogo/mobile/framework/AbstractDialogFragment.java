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

package com.makotogo.mobile.framework;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by sperry on 2/6/16.
 */
public abstract class AbstractDialogFragment extends DialogFragment {

    protected abstract void processFragmentArguments();

    @Override
    public abstract Dialog onCreateDialog(Bundle savedInstanceState);

    protected abstract void saveInstanceState(Bundle outState);

    protected abstract void restoreInstanceState(Bundle savedInstanceState);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveInstanceState(outState);
    }

}
