package com.activeandroid;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.util.AALog;

public final class ActiveAndroid {
	//////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	//////////////////////////////////////////////////////////////////////////////////////

	public static void initialize(Context context, DatabaseHelperListener databaseHelperListener) {
        //add app package name for searching by default
        ModelPathManager.addPath(context.getPackageName());

		initialize(new Configuration.Builder(context).create(), databaseHelperListener);
    }

    public static void initialize(Context context) {
        initialize(context, null);
    }

	public static void initialize(Configuration configuration, DatabaseHelperListener databaseHelperListener) {
		initialize(configuration, false, databaseHelperListener);
	}

    public static void initialize(Configuration configuration) {
        initialize(configuration, null);
    }

	public static void initialize(Context context, boolean loggingEnabled, DatabaseHelperListener databaseHelperListener) {
		initialize(new Configuration.Builder(context).create(), loggingEnabled,databaseHelperListener);
	}

    public static void initialize(Context context, boolean loggingEnabled) {
        initialize(context, loggingEnabled,null);
    }

	public static void initialize(Configuration configuration, boolean loggingEnabled, DatabaseHelperListener databaseHelperListener) {
		// Set logging enabled first
		setLoggingEnabled(loggingEnabled);
		Cache.initialize(configuration, databaseHelperListener);
	}

    public static void initialize(Configuration configuration, boolean loggingEnabled) {
        initialize(configuration, loggingEnabled,null);
    }

	public static void clearCache() {
		Cache.clear();
	}

	public static void dispose() {
		Cache.dispose();
	}

    public static void reset(Context context){
        Cache.reset(context);
    }

	public static void setLoggingEnabled(boolean enabled) {
		AALog.setEnabled(enabled);
	}

	public static SQLiteDatabase getDatabase() {
		return Cache.openDatabase();
	}

	public static void beginTransaction() {
		Cache.openDatabase().beginTransaction();
	}

	public static void endTransaction() {
		Cache.openDatabase().endTransaction();
	}

	public static void setTransactionSuccessful() {
		Cache.openDatabase().setTransactionSuccessful();
	}

	public static boolean inTransaction() {
		return Cache.openDatabase().inTransaction();
	}

	public static void execSQL(String sql) {
		Cache.openDatabase().execSQL(sql);
	}

	public static void execSQL(String sql, Object[] bindArgs) {
		Cache.openDatabase().execSQL(sql, bindArgs);
	}
}
