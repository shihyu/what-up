package cn.wehax.whatup.support.imageviewtouch;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Detects transformation gestures involving more than one pointer
 * ("multitouch") using the supplied {@link android.view.MotionEvent}s. The
 * {@link cn.wehax.imageviewtouch.ScaleGestureDetector.OnScaleGestureListener} callback will notify users when a particular
 * gesture event has occurred. This class should only be used with
 * {@link android.view.MotionEvent}s reported via touch.
 * 
 * To use this class:
 * <ul>
 * <li>Create an instance of the {@code ScaleGestureDetector} for your
 * {@link android.view.View}
 * <li>In the {@link android.view.View#onTouchEvent(android.view.MotionEvent)} method ensure you call
 * {@link #onTouchEvent(android.view.MotionEvent)}. The methods defined in your callback will
 * be executed when the events occur.
 * </ul>
 */
public class ScaleGestureDetector {
	/**
	 * This value is the threshold ratio between our previous combined pressure
	 * and the current combined pressure. We will only fire an onScale event if
	 * the computed ratio between the current and previous event pressures is
	 * greater than this value. When pressure decreases rapidly between events
	 * the position values can often be imprecise, as it usually indicates that
	 * the user is in the process of lifting a pointer off of the device. Its
	 * value was tuned experimentally.
	 */
	private static final float PRESSURE_THRESHOLD = 0.67f;

	private final Context mContext;

	private final OnScaleGestureListener mListener;

	private boolean mGestureInProgress;
	private MotionEvent mPrevEvent;
	private MotionEvent mCurrEvent;

	private float mFocusX;
	private float mFocusY;

	private float mPrevFingerDiffX;
	private float mPrevFingerDiffY;
	private float mCurrFingerDiffX;
	private float mCurrFingerDiffY;
	private float mCurrLen;
	private float mPrevLen;
	private float mScaleFactor;
	private float mCurrPressure;
	private float mPrevPressure;
	private long mTimeDelta;
	private final float mEdgeSlop;
	private float mRightSlopEdge;

	private float mBottomSlopEdge;
	private boolean mSloppyGesture;

	public ScaleGestureDetector(final Context context, final OnScaleGestureListener listener) {
		final ViewConfiguration config = ViewConfiguration.get(context);
		mContext = context;
		mListener = listener;
		mEdgeSlop = config.getScaledEdgeSlop();
	}

	/**
	 * Return the current distance between the two pointers forming the gesture
	 * in progress.
	 * 
	 * @return Distance between pointers in pixels.
	 */
	public float getCurrentSpan() {
		if (mCurrLen == -1) {
			final float cvx = mCurrFingerDiffX;
			final float cvy = mCurrFingerDiffY;
			mCurrLen = FloatMath.sqrt(cvx * cvx + cvy * cvy);
		}
		return mCurrLen;
	}

	/**
	 * Return the event time of the current event being processed.
	 * 
	 * @return Current event time in milliseconds.
	 */
	public long getEventTime() {
		return mCurrEvent.getEventTime();
	}

	/**
	 * Get the X coordinate of the current gesture's focal point. If a gesture
	 * is in progress, the focal point is directly between the two pointers
	 * forming the gesture. If a gesture is ending, the focal point is the
	 * location of the remaining pointer on the screen. If
	 * {@link #isInProgress()} would return false, the result of this function
	 * is undefined.
	 * 
	 * @return X coordinate of the focal point in pixels.
	 */
	public float getFocusX() {
		return mFocusX;
	}

	/**
	 * Get the Y coordinate of the current gesture's focal point. If a gesture
	 * is in progress, the focal point is directly between the two pointers
	 * forming the gesture. If a gesture is ending, the focal point is the
	 * location of the remaining pointer on the screen. If
	 * {@link #isInProgress()} would return false, the result of this function
	 * is undefined.
	 * 
	 * @return Y coordinate of the focal point in pixels.
	 */
	public float getFocusY() {
		return mFocusY;
	}

	/**
	 * Return the previous distance between the two pointers forming the gesture
	 * in progress.
	 * 
	 * @return Previous distance between pointers in pixels.
	 */
	public float getPreviousSpan() {
		if (mPrevLen == -1) {
			final float pvx = mPrevFingerDiffX;
			final float pvy = mPrevFingerDiffY;
			mPrevLen = FloatMath.sqrt(pvx * pvx + pvy * pvy);
		}
		return mPrevLen;
	}

	/**
	 * Return the scaling factor from the previous scale event to the current
	 * event. This value is defined as ({@link #getCurrentSpan()} /
	 * {@link #getPreviousSpan()}).
	 * 
	 * @return The current scaling factor.
	 */
	public float getScaleFactor() {
		if (mScaleFactor == -1) {
			mScaleFactor = getCurrentSpan() / getPreviousSpan();
		}
		return mScaleFactor;
	}

	/**
	 * Return the time difference in milliseconds between the previous accepted
	 * scaling event and the current scaling event.
	 * 
	 * @return Time difference since the last scaling event in milliseconds.
	 */
	public long getTimeDelta() {
		return mTimeDelta;
	}

	/**
	 * Returns {@code true} if a two-finger scale gesture is in progress.
	 * 
	 * @return {@code true} if a scale gesture is in progress, {@code false}
	 *         otherwise.
	 */
	public boolean isInProgress() {
		return mGestureInProgress;
	}

	public boolean onTouchEvent(final MotionEvent event) {
		final int action = event.getAction();
		final boolean handled = true;

		if (!mGestureInProgress) {
			switch (action & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_DOWN: {
					// We have a new multi-finger gesture

					// as orientation can change, query the metrics in touch
					// down
					final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
					mRightSlopEdge = metrics.widthPixels - mEdgeSlop;
					mBottomSlopEdge = metrics.heightPixels - mEdgeSlop;

					// Be paranoid in case we missed an event
					reset();

					mPrevEvent = MotionEvent.obtain(event);
					mTimeDelta = 0;

					setContext(event);

					// Check if we have a sloppy gesture. If so, delay
					// the beginning of the gesture until we're sure that's
					// what the user wanted. Sloppy gestures can happen if the
					// edge of the user's hand is touching the screen, for
					// example.
					final float edgeSlop = mEdgeSlop;
					final float rightSlop = mRightSlopEdge;
					final float bottomSlop = mBottomSlopEdge;
					final float x0 = event.getRawX();
					final float y0 = event.getRawY();
					final float x1 = getRawX(event, 1);
					final float y1 = getRawY(event, 1);

					final boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop || x0 > rightSlop || y0 > bottomSlop;
					final boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop || x1 > rightSlop || y1 > bottomSlop;

					if (p0sloppy && p1sloppy) {
						mFocusX = -1;
						mFocusY = -1;
						mSloppyGesture = true;
					} else if (p0sloppy) {
						mFocusX = MotionEventCompat.getX(event, 1);
						mFocusY = MotionEventCompat.getY(event, 1);
						mSloppyGesture = true;
					} else if (p1sloppy) {
						mFocusX = MotionEventCompat.getX(event, 0);
						mFocusY = MotionEventCompat.getY(event, 0);
						mSloppyGesture = true;
					} else {
						mGestureInProgress = mListener.onScaleBegin(this);
					}
				}
					break;

				case MotionEvent.ACTION_MOVE:
					if (mSloppyGesture) {
						// Initiate sloppy gestures if we've moved outside of
						// the slop area.
						final float edgeSlop = mEdgeSlop;
						final float rightSlop = mRightSlopEdge;
						final float bottomSlop = mBottomSlopEdge;
						final float x0 = event.getRawX();
						final float y0 = event.getRawY();
						final float x1 = getRawX(event, 1);
						final float y1 = getRawY(event, 1);

						final boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop || x0 > rightSlop || y0 > bottomSlop;
						final boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop || x1 > rightSlop || y1 > bottomSlop;

						if (p0sloppy && p1sloppy) {
							mFocusX = -1;
							mFocusY = -1;
						} else if (p0sloppy) {
							mFocusX = MotionEventCompat.getX(event, 1);
							mFocusY = MotionEventCompat.getY(event, 1);
						} else if (p1sloppy) {
							mFocusX = MotionEventCompat.getX(event, 0);
							mFocusY = MotionEventCompat.getY(event, 0);
						} else {
							mSloppyGesture = false;
							mGestureInProgress = mListener.onScaleBegin(this);
						}
					}
					break;

				case MotionEvent.ACTION_POINTER_UP:
					if (mSloppyGesture) {
						// Set focus point to the remaining finger
						final int id = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT == 0 ? 1
								: 0;
						mFocusX = event.getX(id);
						mFocusY = event.getY(id);
					}
					break;
			}
		} else {
			// Transform gesture in progress - attempt to handle it
			switch (action & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_UP:
					// Gesture ended
					setContext(event);

					// Set focus point to the remaining finger
					final int id = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT == 0 ? 1
							: 0;
					mFocusX = event.getX(id);
					mFocusY = event.getY(id);

					if (!mSloppyGesture) {
						mListener.onScaleEnd(this);
					}

					reset();
					break;

				case MotionEvent.ACTION_CANCEL:
					if (!mSloppyGesture) {
						mListener.onScaleEnd(this);
					}

					reset();
					break;

				case MotionEvent.ACTION_MOVE:
					setContext(event);

					// Only accept the event if our relative pressure is within
					// a certain limit - this can help filter shaky data as a
					// finger is lifted.
					if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
						final boolean updatePrevious = mListener.onScale(this);

						if (updatePrevious) {
							mPrevEvent.recycle();
							mPrevEvent = MotionEvent.obtain(event);
						}
					}
					break;
			}
		}
		return handled;
	}

	private void reset() {
		if (mPrevEvent != null) {
			mPrevEvent.recycle();
			mPrevEvent = null;
		}
		if (mCurrEvent != null) {
			mCurrEvent.recycle();
			mCurrEvent = null;
		}
		mSloppyGesture = false;
		mGestureInProgress = false;
	}

	private void setContext(final MotionEvent curr) {
		if (mCurrEvent != null) {
			mCurrEvent.recycle();
		}
		mCurrEvent = MotionEvent.obtain(curr);

		mCurrLen = -1;
		mPrevLen = -1;
		mScaleFactor = -1;

		final MotionEvent prev = mPrevEvent;

		final float px0 = prev.getX(0);
		final float py0 = prev.getY(0);
		final float px1 = prev.getX(1);
		final float py1 = prev.getY(1);
		final float cx0 = curr.getX(0);
		final float cy0 = curr.getY(0);
		final float cx1 = curr.getX(1);
		final float cy1 = curr.getY(1);

		final float pvx = px1 - px0;
		final float pvy = py1 - py0;
		final float cvx = cx1 - cx0;
		final float cvy = cy1 - cy0;
		mPrevFingerDiffX = pvx;
		mPrevFingerDiffY = pvy;
		mCurrFingerDiffX = cvx;
		mCurrFingerDiffY = cvy;

		mFocusX = cx0 + cvx * 0.5f;
		mFocusY = cy0 + cvy * 0.5f;
		mTimeDelta = curr.getEventTime() - prev.getEventTime();
		mCurrPressure = curr.getPressure(0) + curr.getPressure(1);
		mPrevPressure = prev.getPressure(0) + prev.getPressure(1);
	}

	/**
	 * MotionEvent has no getRawX(int) method; simulate it pending future API
	 * approval.
	 */
	private static float getRawX(final MotionEvent event, final int pointerIndex) {
		final float offset = event.getX() - event.getRawX();
		return event.getX(pointerIndex) + offset;
	}

	/**
	 * MotionEvent has no getRawY(int) method; simulate it pending future API
	 * approval.
	 */
	private static float getRawY(final MotionEvent event, final int pointerIndex) {
		final float offset = event.getY() - event.getRawY();
		return event.getY(pointerIndex) + offset;
	}

	/**
	 * The listener for receiving notifications when gestures occur. If you want
	 * to listen for all the different gestures then implement this interface.
	 * If you only want to listen for a subset it might be easier to extend
	 * {@link cn.wehax.imageviewtouch.ScaleGestureDetector.SimpleOnScaleGestureListener}.
	 * 
	 * An application will receive events in the following order:
	 * <ul>
	 * <li>One {@link cn.wehax.imageviewtouch.ScaleGestureDetector.OnScaleGestureListener#onScaleBegin(cn.wehax.imageviewtouch.ScaleGestureDetector)}
	 * <li>Zero or more
	 * {@link cn.wehax.imageviewtouch.ScaleGestureDetector.OnScaleGestureListener#onScale(cn.wehax.imageviewtouch.ScaleGestureDetector)}
	 * <li>One {@link cn.wehax.imageviewtouch.ScaleGestureDetector.OnScaleGestureListener#onScaleEnd(cn.wehax.imageviewtouch.ScaleGestureDetector)}
	 * </ul>
	 */
	public static interface OnScaleGestureListener {
		/**
		 * Responds to scaling events for a gesture in progress. Reported by
		 * pointer motion.
		 * 
		 * @param detector The detector reporting the event - use this to
		 *            retrieve extended info about event state.
		 * @return Whether or not the detector should consider this event as
		 *         handled. If an event was not handled, the detector will
		 *         continue to accumulate movement until an event is handled.
		 *         This can be useful if an application, for example, only wants
		 *         to update scaling factors if the change is greater than 0.01.
		 */
		public boolean onScale(ScaleGestureDetector detector);

		/**
		 * Responds to the beginning of a scaling gesture. Reported by new
		 * pointers going down.
		 * 
		 * @param detector The detector reporting the event - use this to
		 *            retrieve extended info about event state.
		 * @return Whether or not the detector should continue recognizing this
		 *         gesture. For example, if a gesture is beginning with a focal
		 *         point outside of a region where it makes sense,
		 *         onScaleBegin() may return false to ignore the rest of the
		 *         gesture.
		 */
		public boolean onScaleBegin(ScaleGestureDetector detector);

		/**
		 * Responds to the end of a scale gesture. Reported by existing pointers
		 * going up.
		 * 
		 * Once a scale has ended, {@link cn.wehax.imageviewtouch.ScaleGestureDetector#getFocusX()} and
		 * {@link cn.wehax.imageviewtouch.ScaleGestureDetector#getFocusY()} will return the location of
		 * the pointer remaining on the screen.
		 * 
		 * @param detector The detector reporting the event - use this to
		 *            retrieve extended info about event state.
		 */
		public void onScaleEnd(ScaleGestureDetector detector);
	}

	/**
	 * A convenience class to extend when you only want to listen for a subset
	 * of scaling-related events. This implements all methods in
	 * {@link cn.wehax.imageviewtouch.ScaleGestureDetector.OnScaleGestureListener} but does nothing.
	 * {@link cn.wehax.imageviewtouch.ScaleGestureDetector.OnScaleGestureListener#onScale(cn.wehax.imageviewtouch.ScaleGestureDetector)} returns
	 * {@code false} so that a subclass can retrieve the accumulated scale
	 * factor in an overridden onScaleEnd.
	 * {@link cn.wehax.imageviewtouch.ScaleGestureDetector.OnScaleGestureListener#onScaleBegin(cn.wehax.imageviewtouch.ScaleGestureDetector)} returns
	 * {@code true}.
	 */
	public static class SimpleOnScaleGestureListener implements OnScaleGestureListener {

		@Override
		public boolean onScale(final ScaleGestureDetector detector) {
			return false;
		}

		@Override
		public boolean onScaleBegin(final ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(final ScaleGestureDetector detector) {
			// Intentionally empty
		}
	}
}