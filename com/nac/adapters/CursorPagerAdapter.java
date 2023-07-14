package com.nac.adapters;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.nac.ui.fragments.ProgramFragment;

public class CursorPagerAdapter<F extends Fragment> extends FragmentStatePagerAdapter {
    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE / 2;
    public int MIDDLE;
    private int SIZE;

    private final Class<F> fragmentClass;
    private final String[] projection;
    private Cursor cursor;
    private boolean isCompressMode;


    public CursorPagerAdapter(FragmentManager fm, Class<F> fragmentClass, String[] projection, Cursor cursor) {
        super(fm);
        this.fragmentClass = fragmentClass;
        this.projection = projection;
        this.cursor = cursor;
        SIZE = cursor.getCount();
        if (SIZE != 0) {
            MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % SIZE;
        }
    }

    public CursorPagerAdapter(FragmentManager fm, Class<F> fragmentClass, String[] projection, Cursor cursor, boolean isCompressMode) {
        this(fm, fragmentClass, projection, cursor);
        this.isCompressMode = isCompressMode;
    }

    @Override
    public F getItem(int position) {
        if (cursor == null) {// shouldn't happen
            return null;
        }
        F frag;
        Bundle args = getBundleForPosition(position);
        try {
            frag = fragmentClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        frag.setArguments(args);
        return frag;
    }

    public int getProgramIdForPosition(int position) {
        if (cursor == null || SIZE == 0) {
            return -1;
        }
        position = position % SIZE;
        cursor.moveToPosition(position);
        return cursor.getInt(0);
    }

    public int getProgramIdForCursorPosition(int position) {
        if (cursor == null || SIZE == 0) {
            return -1;
        }
        cursor.moveToPosition(position);
        return cursor.getInt(0);
    }

    public Bundle getBundleForPosition(int position) {
        if (cursor == null || SIZE == 0) { // shouldn't happen
            return null;
        }
        position = position % SIZE;
        cursor.moveToPosition(position);
        Bundle args = new Bundle();
        for (int i = 1; i < projection.length; i++) {
            args.putString(projection[i], cursor.getString(i));
        }
        args.putInt(projection[0], cursor.getInt(0));
        args.putBoolean(ProgramFragment.COMPRESS_MODE_EXTRAS, isCompressMode);
        return args;
    }

    public Bundle getBundleForCursorPosition(int position) {
        if (cursor == null || SIZE == 0) { // shouldn't happen
            return null;
        }
        cursor.moveToPosition(position);
        Bundle args = new Bundle();
        for (int i = 1; i < projection.length; i++) {
            args.putString(projection[i], cursor.getString(i));
        }
        args.putInt(projection[0], cursor.getInt(0));
        args.putBoolean(ProgramFragment.COMPRESS_MODE_EXTRAS, isCompressMode);
        return args;
    }

    @Override
    public int getCount() {
        if (cursor == null || cursor.getCount() == 0)
            return 0;
        else
            return Integer.MAX_VALUE;
    }

    public int getRealCount() {
        if (cursor == null || cursor.getCount() == 0)
            return 0;
        else
            return cursor.getCount();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void swapCursor(Cursor c) {
        if (cursor == c) {
            return;
        }
        this.cursor = c;
        SIZE = cursor.getCount();
        if (SIZE != 0) {
            MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % SIZE;
        }
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
}