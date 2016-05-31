package ir.winep.winepbarcode.Utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by ShaisteS on 5/25/2016.
 */
public class SwipeToDismissTouchListener<SomeCollectionView extends ViewAdapter> implements
        View.OnTouchListener {

    // Cached ViewConfiguration and system-wide constant values
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;

    // Fixed properties
    private SomeCollectionView mRecyclerView;
    private DismissCallbacks<SomeCollectionView> mCallbacks;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private PendingDismissData mPendingDismiss;
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private int mDownPosition;
    private RowContainer mRowContainer;
    private boolean mPaused;

    public class RowContainer {

        final View container;
        final View dataContainer;
        final View undoContainer;
        boolean dataContainerHasBeenDismissed;

        public RowContainer(ViewGroup container) {
            this.container = container;
            dataContainer = container.getChildAt(0);
            undoContainer = container.getChildAt(1);
            dataContainerHasBeenDismissed = false;
        }

        View getCurrentSwipingView() {
            return dataContainerHasBeenDismissed ? undoContainer: dataContainer;
        }

    }

    /**
     * The callback interface used by {@link SwipeToDismissTouchListener} to inform its client
     * about a successful dismissal of one or more list item positions.
     */
    public interface DismissCallbacks<SomeCollectionView extends ViewAdapter> {
        /**
         * Called to determine whether the given position can be dismissed.
         */
        boolean canDismiss(int position);


        void onDismiss(SomeCollectionView recyclerView, int position);
    }


    public SwipeToDismissTouchListener(SomeCollectionView recyclerView,
                                       DismissCallbacks<SomeCollectionView> callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(recyclerView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = recyclerView.getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        mRecyclerView = recyclerView;
        mCallbacks = callbacks;
    }


    public void setEnabled(boolean enabled) {
        mPaused = !enabled;
    }


    public Object makeScrollListener() {
        return mRecyclerView.makeScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                processPendingDismisses();
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mViewWidth < 2) {
            mViewWidth = mRecyclerView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                if (mPaused) {
                    return false;
                }

                Rect rect = new Rect();
                int childCount = mRecyclerView.getChildCount();
                int[] listViewCoords = new int[2];
                mRecyclerView.getLocationOnScreen(listViewCoords);
                int x = (int) motionEvent.getRawX() - listViewCoords[0];
                int y = (int) motionEvent.getRawY() - listViewCoords[1];
                View child;
                for (int i = 0; i < childCount; i++) {
                    child = mRecyclerView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(x, y)) {
                        assert(child instanceof ViewGroup &&
                                ((ViewGroup) child).getChildCount() == 2) :
                                "Each child needs to extend from ViewGroup and have two children";

                        boolean dataContainerHasBeenDismissed = mPendingDismiss != null &&
                                mPendingDismiss.position == mRecyclerView.getChildPosition(child) &&
                                mPendingDismiss.rowContainer.dataContainerHasBeenDismissed;
                        mRowContainer = new RowContainer((ViewGroup) child);
                        mRowContainer.dataContainerHasBeenDismissed = dataContainerHasBeenDismissed;
                        break;
                    }
                }

                if (mRowContainer != null) {
                    mDownX = motionEvent.getRawX();
                    mDownY = motionEvent.getRawY();
                    mDownPosition = mRecyclerView.getChildPosition(mRowContainer.container);
                    if (mCallbacks.canDismiss(mDownPosition)) {
                        mVelocityTracker = VelocityTracker.obtain();
                        mVelocityTracker.addMovement(motionEvent);
                    } else {
                        mRowContainer = null;
                    }
                }
                return false;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (mVelocityTracker == null) {
                    break;
                }

                if (mRowContainer != null && mSwiping) {
                    // cancel
                    mRowContainer.getCurrentSwipingView()
                            .animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                mRowContainer = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaX = motionEvent.getRawX() - mDownX;
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
                boolean dismiss = false;
                boolean dismissRight = false;
                if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) {
                    dismiss = true;
                    dismissRight = deltaX > 0;
                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                        && absVelocityY < absVelocityX && mSwiping) {
                    dismiss = (velocityX < 0) == (deltaX < 0);
                    dismissRight = mVelocityTracker.getXVelocity() > 0;
                }
                if (dismiss && mDownPosition != ListView.INVALID_POSITION) {
                    // dismiss
                    final RowContainer downView = mRowContainer;
                    final int downPosition = mDownPosition;
                    mRowContainer.getCurrentSwipingView()
                            .animate()
                            .translationX(dismissRight ? mViewWidth : -mViewWidth)
                            .alpha(0)
                            .setDuration(mAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performDismiss(downView, downPosition);
                                }
                            });
                } else {
                    // cancel
                    mRowContainer.getCurrentSwipingView()
                            .animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownX = 0;
                mDownY = 0;
                mRowContainer = null;
                mDownPosition = ListView.INVALID_POSITION;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mVelocityTracker == null || mPaused) {
                    break;
                }

                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;
                float deltaY = motionEvent.getRawY() - mDownY;
                if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    mSwiping = true;
                    mSwipingSlop = (deltaX > 0 ? mSlop : -mSlop);
                    mRecyclerView.requestDisallowInterceptTouchEvent(true);

                    // Cancel ListView's touch (un-highlighting the item)
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex()
                                    << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mRecyclerView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (mSwiping) {
                    mRowContainer.getCurrentSwipingView().setTranslationX(deltaX - mSwipingSlop);
                    mRowContainer.getCurrentSwipingView().setAlpha(Math.max(0f, Math.min(1f,
                            1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    class PendingDismissData implements Comparable<PendingDismissData> {
        public int position;
        public RowContainer rowContainer;

        public PendingDismissData(int position, RowContainer rowContainer) {
            this.position = position;
            this.rowContainer= rowContainer;
        }

        @Override
        public int compareTo(@NonNull PendingDismissData other) {
            // Sort by descending position
            return other.position - position;
        }
    }

    private void performDismiss(RowContainer dismissView, int dismissPosition) {
        // Animate the dismissed list item to zero-height and fire the dismiss callback when
        // all dismissed list item animations have completed. This triggers layout on each animation
        // frame; in the future we may want to do something smarter and more performant.
        if (mPendingDismiss != null) {
            boolean dismissingDifferentRow = mPendingDismiss.position != dismissPosition;
            int newPosition = mPendingDismiss.position < dismissPosition ? dismissPosition-1 : dismissPosition;
            processPendingDismisses();
            if (dismissingDifferentRow) {
                addPendingDismiss(dismissView, newPosition);
            }
        } else {
            addPendingDismiss(dismissView, dismissPosition);
        }
    }

    private void addPendingDismiss(RowContainer dismissView, int dismissPosition) {
        dismissView.dataContainerHasBeenDismissed = true;
        dismissView.undoContainer.setVisibility(View.VISIBLE);
        mPendingDismiss = new PendingDismissData(dismissPosition, dismissView);
    }

    /**
     * If a view was dismissed and the undo container is showing it will proceed with the final
     * dismiss of the item.
     * @return whether there were any pending rows to be dismissed.
     */
    public boolean processPendingDismisses() {
        boolean existPendingDismisses = existPendingDismisses();
        if (existPendingDismisses) processPendingDismisses(mPendingDismiss);
        return existPendingDismisses;
    }

    /**
     * Whether a row has been dismissed and is waiting for confirmation
     * @return whether there are any pending rows to be dismissed.
     */
    public boolean existPendingDismisses() {
        return mPendingDismiss != null && mPendingDismiss.rowContainer.dataContainerHasBeenDismissed;
    }

    /**
     * If a view was dismissed and the undo container is showing it will undo and make the data
     * container reappear.
     * @return whether there were any pending rows to be dismissed.
     */
    public boolean undoPendingDismiss() {
        boolean existPendingDismisses = existPendingDismisses();
        if (existPendingDismisses) {
            mPendingDismiss.rowContainer.undoContainer.setVisibility(View.GONE);
            mPendingDismiss.rowContainer.dataContainer
                    .animate()
                    .translationX(0)
                    .alpha(1)
                    .setDuration(mAnimationTime)
                    .setListener(null);
            mPendingDismiss = null;
        }
        return existPendingDismisses;
    }


    private void processPendingDismisses(final PendingDismissData pendingDismissData) {
        mPendingDismiss = null;
        final ViewGroup.LayoutParams lp = pendingDismissData.rowContainer.container.getLayoutParams();
        final int originalHeight = pendingDismissData.rowContainer.container.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCallbacks.canDismiss(pendingDismissData.position))
                    mCallbacks.onDismiss(mRecyclerView, pendingDismissData.position);
                pendingDismissData.rowContainer.dataContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        pendingDismissData.rowContainer.dataContainer.setTranslationX(0);
                        pendingDismissData.rowContainer.dataContainer.setAlpha(1);
                        pendingDismissData.rowContainer.undoContainer.setVisibility(View.GONE);
                        pendingDismissData.rowContainer.undoContainer.setTranslationX(0);
                        pendingDismissData.rowContainer.undoContainer.setAlpha(1);

                        lp.height = originalHeight;
                        pendingDismissData.rowContainer.container.setLayoutParams(lp);
                    }
                });
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                pendingDismissData.rowContainer.container.setLayoutParams(lp);
            }
        });

        animator.start();
    }
}
