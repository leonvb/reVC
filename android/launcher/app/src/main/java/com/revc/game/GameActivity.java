package com.revc.game;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import org.libsdl.app.SDLActivity;

import java.io.File;

/** Starts the native reVC SDL entry point using the user's game-data directory. */
public class GameActivity extends SDLActivity {
    private static final String GAME_FOLDER_NAME = "reVC";
    private static final int STORAGE_PERMISSION_REQUEST = 1001;

    private static File getGameDataDirectory() {
        return new File(Environment.getExternalStorageDirectory(), GAME_FOLDER_NAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!hasStorageAccess()) {
            requestStorageAccess();
            // Reopen the app after granting access. Finish before onResume so
            // SDLActivity never starts the native game without its data path.
            finish();
            return;
        }

        File gameDataDirectory = getGameDataDirectory();
        if (!gameDataDirectory.exists() && !gameDataDirectory.mkdirs()) {
            Toast.makeText(this, "Unable to create /reVC game-data directory", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected String[] getLibraries() {
        // reVC must remain last: SDLActivity treats the final entry as the
        // library exporting SDL_main.
        return new String[] { "SDL2", "openal", "mpg123", "reVC" };
    }

    @Override
    protected String[] getArguments() {
        String path = getGameDataDirectory().getAbsolutePath();
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        return new String[] { "--dir", path };
    }

    private boolean hasStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStorageAccess() {
        Toast.makeText(this,
                "Allow file access for reVC, then open it again. Put your game data in /reVC.",
                Toast.LENGTH_LONG).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            }
        } else {
            requestPermissions(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_REQUEST);
        }
    }
}
