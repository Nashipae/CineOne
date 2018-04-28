package com.redevstudios.cineone.cineone.ui.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoriteProvider extends ContentProvider {
    private FavoriteHelper mFavoriteHelper;

    public static final int CODE_FAVORITE = 100;
    public static final int CODE_FAVORITE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoriteContract.FavoriteEntry.TABLE_NAME, CODE_FAVORITE);

        matcher.addURI(authority, FavoriteContract.FavoriteEntry.TABLE_NAME + "/#", CODE_FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mFavoriteHelper = new FavoriteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_WITH_ID: {
                String _ID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{_ID};
                cursor = mFavoriteHelper.getReadableDatabase().query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        FavoriteContract.FavoriteEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_FAVORITE: {
                cursor = mFavoriteHelper.getReadableDatabase().query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavoriteHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE:

                long _id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return FavoriteContract.FavoriteEntry.buildFavoriteUriWithId(_id);

            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
