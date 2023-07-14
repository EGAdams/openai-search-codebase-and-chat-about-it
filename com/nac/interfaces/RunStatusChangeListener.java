package com.nac.interfaces;

import com.nac.model.RunStatus;

public interface RunStatusChangeListener {
        void onStatusChanged(RunStatus runStatus);
}