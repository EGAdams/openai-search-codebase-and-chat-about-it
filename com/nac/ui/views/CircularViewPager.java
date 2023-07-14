package com.nac.ui.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.nac.adapters.CursorPagerAdapter;

/**
 * Created by andreikaralkou on 2/26/14.
 */
public class CircularViewPager extends ViewPager {

    public CircularViewPager(Context context) {
        super(context);
    }

    public CircularViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item) {
        if (getAdapter() instanceof CursorPagerAdapter) {
            CursorPagerAdapter cursorPagerAdapter = (CursorPagerAdapter) getAdapter();
            // offset the current item to ensure there is space to scroll
            int count = cursorPagerAdapter.getRealCount();
            if (count != 0) {
                item = getOffsetAmount() + (item);
            } else {
                item = 0;
            }
            super.setCurrentItem(item);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (getAdapter() instanceof CursorPagerAdapter) {
            CursorPagerAdapter cursorPagerAdapter = (CursorPagerAdapter) getAdapter();
            // offset the current item to ensure there is space to scroll
            int count = cursorPagerAdapter.getRealCount();
            if (count != 0) {
                item = getOffsetAmount() + (item);
            } else {
                item = 0;
            }
            super.setCurrentItem(item, smoothScroll);
        }
    }

    public void forceSetCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    public int getCurrentRealItem() {
        if (getAdapter() instanceof CursorPagerAdapter) {
            CursorPagerAdapter cursorPagerAdapter = (CursorPagerAdapter) getAdapter();
            // offset the current item to ensure there is space to scroll
            if (cursorPagerAdapter != null) {
                int count = cursorPagerAdapter.getRealCount();
                int offsetAmount = count * 100;
                if (count != 0) {
                    int diff = super.getCurrentItem() - offsetAmount;
                    return ((diff % count) + count) % count;
                } else {
                    return 0;
                }
            }

        }
        return 0;
//        int count = getAdapter().getCount();
//        if (count != 0) {
//            return (super.getCurrentItem() - getOffsetAmount());
//        } else {
//            return 0;
//        }
    }

    private int getOffsetAmount() {
        if (getAdapter() instanceof CursorPagerAdapter) {
            CursorPagerAdapter cursorPagerAdapter = (CursorPagerAdapter) getAdapter();
            // allow for 100 back cycles from the beginning
            // should be enough to create an illusion of infinity
            // warning: scrolling to very high values (1,000,000+) results in
            // strange drawing behaviour
            return cursorPagerAdapter.getRealCount() * 100;
        } else {
            return 0;
        }
    }
}
