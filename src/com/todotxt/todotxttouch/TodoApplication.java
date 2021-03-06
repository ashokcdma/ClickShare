/**
 * This file is part of Todo.txt for Android, an app for managing your todo.txt file (http://todotxt.com).
 *
 * Copyright (c) 2009-2013 Todo.txt for Android contributors (http://todotxt.com)
 *
 * LICENSE:
 *
 * Todo.txt for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Todo.txt for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with Todo.txt for Android. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Todo.txt for Android's source code is available at https://github.com/ginatrapani/todo.txt-android
 *
 * @author Todo.txt for Android contributors <todotxt@yahoogroups.com>
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2009-2013 Todo.txt for Android contributors (http://todotxt.com)
 */

package com.todotxt.todotxttouch;

import java.util.ArrayList;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.preference.PreferenceManager;

import com.debasmita.dropbox.RemoteClientManager;

public class TodoApplication extends Application {
    private final static String TAG = TodoApplication.class.getSimpleName();
    private boolean m_pulling = false;
    private boolean m_pushing = false;
    private int pushQueue = 0;
    private BroadcastReceiver m_broadcastReceiver;
    private static Context appContext;
    private RemoteClientManager remoteClientManager;
    public TodoPreferences m_prefs;
    
    // filter variables
    public ArrayList<String> m_contexts = new ArrayList<String>();
    public ArrayList<String> m_projects = new ArrayList<String>();
    public String m_search;
    public ArrayList<String> m_filters = new ArrayList<String>();

    @Override
    public void onCreate() {
        super.onCreate();

        TodoApplication.appContext = getApplicationContext();
        m_prefs = new TodoPreferences(appContext,
                PreferenceManager.getDefaultSharedPreferences(this));
        remoteClientManager = new RemoteClientManager(this, m_prefs);
    }

    @Override
    public void onTerminate() {
        if (null != m_broadcastReceiver) {
            unregisterReceiver(m_broadcastReceiver);
        }

        super.onTerminate();
    }
    public static Context getAppContetxt() {
        return appContext;
    }
    

    public RemoteClientManager getRemoteClientManager() {
        return remoteClientManager;
    }

}
