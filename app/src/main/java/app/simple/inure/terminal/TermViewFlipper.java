/*
 * Copyright (C) 2011 Steven Luo
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

package app.simple.inure.terminal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Iterator;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import app.simple.inure.R;
import app.simple.inure.decorations.emulatorview.EmulatorView;
import app.simple.inure.decorations.emulatorview.TermSession;
import app.simple.inure.decorations.emulatorview.UpdateCallback;
import app.simple.inure.decorations.views.DecentViewFlipper;
import app.simple.inure.terminal.compat.AndroidCompat;
import app.simple.inure.terminal.util.TermSettings;

public class TermViewFlipper extends DecentViewFlipper implements Iterable <View> {
    
    private Context context;
    private Toast toast;
    private LinkedList <UpdateCallback> callbacks;
    
    private int curWidth;
    private int curHeight;
    private final Rect visibleRect = new Rect();
    private final Rect windowRect = new Rect();
    private LayoutParams childParams = null;
    private boolean redoLayout = false;
    
    /**
     * True if we must poll to discover if the view has changed size.
     * This is the only known way to detect the view changing size due to
     * the IME being shown or hidden in API level <= 7.
     */
    private final boolean mbPollForWindowSizeChange = (AndroidCompat.SDK < 8);
    private static final int SCREEN_CHECK_PERIOD = 1000;
    private final Handler handler = new Handler();
    private final Runnable checkSize = new Runnable() {
        public void run() {
            adjustChildSize();
            handler.postDelayed(this, SCREEN_CHECK_PERIOD);
        }
    };
    
    class ViewFlipperIterator implements Iterator <View> {
        int pos = 0;
        
        public boolean hasNext() {
            return (pos < getChildCount());
        }
        
        public View next() {
            return getChildAt(pos++);
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public TermViewFlipper(Context context) {
        super(context);
        commonConstructor(context);
    }
    
    public TermViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonConstructor(context);
    }
    
    private void commonConstructor(Context context) {
        this.context = context;
        callbacks = new LinkedList <>();
        updateVisibleRect();
        Rect visible = visibleRect;
        childParams = new LayoutParams(visible.width(), visible.height(), Gravity.TOP | Gravity.START);
        
    }
    
    public void updatePrefs(TermSettings settings) {
        int[] colorScheme = settings.getColorScheme();
        setBackgroundColor(colorScheme[1]);
    }
    
    @NonNull
    public Iterator <View> iterator() {
        return new ViewFlipperIterator();
    }
    
    public void addCallback(UpdateCallback callback) {
        callbacks.add(callback);
    }
    
    public void removeCallback(UpdateCallback callback) {
        callbacks.remove(callback);
    }
    
    private void notifyChange() {
        for (UpdateCallback callback : callbacks) {
            callback.onUpdate();
        }
    }
    
    public void onPause() {
        if (mbPollForWindowSizeChange) {
            handler.removeCallbacks(checkSize);
        }
        pauseCurrentView();
    }
    
    public void onResume() {
        if (mbPollForWindowSizeChange) {
            checkSize.run();
        }
        resumeCurrentView();
    }
    
    public void pauseCurrentView() {
        EmulatorView view = (EmulatorView) getCurrentView();
        if (view == null) {
            return;
        }
        view.onPause();
    }
    
    public void resumeCurrentView() {
        EmulatorView view = (EmulatorView) getCurrentView();
        if (view == null) {
            return;
        }
        view.onResume();
        view.requestFocus();
    }
    
    private void showTitle() {
        if (getChildCount() == 0) {
            return;
        }
        
        EmulatorView view = (EmulatorView) getCurrentView();
        if (view == null) {
            return;
        }
        TermSession session = view.getTermSession();
        if (session == null) {
            return;
        }
        
        String title = context.getString(R.string.window_title, getDisplayedChild() + 1);
        if (session instanceof GenericTermSession) {
            title = ((GenericTermSession) session).getTitle(title);
        }
        
        if (toast == null) {
            toast = Toast.makeText(context, title, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(title);
        }
        
        // toast.show(); // We hate toasts
    }
    
    @Override
    public void showPrevious() {
        pauseCurrentView();
        super.showPrevious();
        showTitle();
        resumeCurrentView();
        notifyChange();
    }
    
    @Override
    public void showNext() {
        pauseCurrentView();
        super.showNext();
        showTitle();
        resumeCurrentView();
        notifyChange();
    }
    
    @Override
    public void setDisplayedChild(int position) {
        pauseCurrentView();
        super.setDisplayedChild(position);
        showTitle();
        resumeCurrentView();
        notifyChange();
    }
    
    @Override
    public void addView(View v, int index) {
        super.addView(v, index, childParams);
    }
    
    @Override
    public void addView(View v) {
        super.addView(v, childParams);
    }
    
    private void updateVisibleRect() {
        Rect visible = visibleRect;
        Rect window = windowRect;

        /* Get rectangle representing visible area of this view, as seen by
           the activity (takes other views in the layout into account, but
           not space used by the IME) */
        getGlobalVisibleRect(visible);

        /* Get rectangle representing visible area of this window (takes
           IME into account, but not other views in the layout) */
        getWindowVisibleDisplayFrame(window);
        
        // Clip visible rectangle's top to the visible portion of the window
        if (visible.width() == 0 && visible.height() == 0) {
            visible.left = window.left;
            visible.top = window.top;
        } else {
            if (visible.left < window.left) {
                visible.left = window.left;
            }
            if (visible.top < window.top) {
                visible.top = window.top;
            }
        }
        // Always set the bottom of the rectangle to the window bottom
        /* XXX This breaks with a split action bar, but if we don't do this,
           it's possible that the view won't resize correctly on IME hide */
        visible.right = window.right;
        visible.bottom = window.bottom;
    }
    
    private void adjustChildSize() {
        updateVisibleRect();
        Rect visible = visibleRect;
        int width = visible.width();
        int height = visible.height();
        
        if (curWidth != width || curHeight != height) {
            curWidth = width;
            curHeight = height;
            
            LayoutParams params = childParams;
            params.width = width;
            params.height = height;
            for (View v : this) {
                updateViewLayout(v, params);
            }
            redoLayout = true;
            
            EmulatorView currentView = (EmulatorView) getCurrentView();
            if (currentView != null) {
                currentView.updateSize(false);
            }
        }
    }
    
    /**
     * Called when the view changes size.
     * (Note: Not always called on Android < 2.2)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        adjustChildSize();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (redoLayout) {
            requestLayout();
            redoLayout = false;
        }
        super.onDraw(canvas);
    }
}
