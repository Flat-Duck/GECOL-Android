package ly.smarthive.gecol;



import static ly.smarthive.gecol.COMMON.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;

    public class SessionManager {
        private static final String TAG = SessionManager.class.getSimpleName();

        SharedPreferences pref;
        SharedPreferences.Editor editor;
        Context _context;

        @SuppressLint("CommitPrefEdits")
        public SessionManager(Context context) {
            this._context = context;
            pref = _context.getSharedPreferences(PREF_NAME, 0);
            editor = pref.edit();
        }

        public void setLogin(boolean isLoggedIn) {
            editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
            editor.commit();
            Log.d(TAG, "User login session modified!");
        }


        public void setEmailPassword(String email, String password, boolean stat) {
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_STATUS, stat);
            editor.commit();
            Log.d(TAG, "User E&P set modified!");
        }

        public void setToken(String token) {
            editor.putString(KEY_TOKEN, token);
            editor.commit();
            Log.d(TAG, "User login session modified!");
        }

        public void setStudentId(int id) {
            editor.putInt(KEY_STUDENT_ID, id);
            editor.commit();
            Log.d(TAG, "student id updated!");
        }
        public void setLastMillis(long time) {
            editor.putLong(KEY_MESSAGE_TIME_STAMP, time);
            editor.commit();
            Log.d(TAG, "message time stamp updated");
        }

        public void clear() {
            File dir = new File(_context.getFilesDir().getParent() + "/shared_prefs/");
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                // clear each preference file
                _context.getSharedPreferences(children[i].replace(".xml", ""), 0).edit().clear().commit();
                //delete the file
                new File(dir, children[i]).delete();
            }
        }

        public boolean isLoggedIn() {
            return pref.getBoolean(KEY_IS_LOGGED_IN, false);
        }

        public String getToken() {
            return pref.getString(KEY_TOKEN, null);
        }
        public int getStudentId() {
            return pref.getInt(KEY_STUDENT_ID,1);
        }


        public String getEmail() {

            return pref.getString(KEY_EMAIL, null);
        }

        public String getPassword() {
            return pref.getString(KEY_PASSWORD, null);
        }

        public long getLastMillis() {
            return pref.getLong(KEY_MESSAGE_TIME_STAMP,0);
        }
    }