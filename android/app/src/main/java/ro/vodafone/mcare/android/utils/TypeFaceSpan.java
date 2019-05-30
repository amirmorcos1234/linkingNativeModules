package ro.vodafone.mcare.android.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Created by user1 on 06-Mar-18.
 */

public class TypeFaceSpan extends MetricAffectingSpan {

	/**
	 * An <code>LruCache</code> for previously loaded typefaces.
	 */
	private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

	private Typeface mTypeface;

	/**
	 * Load the {@link android.graphics.Typeface} and apply to a {@link android.text.Spannable}.
	 */
	public TypeFaceSpan(Context context, String typefaceName) {
		mTypeface = sTypefaceCache.get(typefaceName);

		if (mTypeface == null) {
			mTypeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), typefaceName);

			// Cache the loaded Typeface
			sTypefaceCache.put(typefaceName, mTypeface);
		}
	}

	public TypeFaceSpan(Typeface typeface) {
		mTypeface = sTypefaceCache.get(typeface.hashCode() + "");

		if (mTypeface == null) {
			mTypeface = typeface;
			// Cache the loaded Typeface
			sTypefaceCache.put(typeface.hashCode() + "", mTypeface);
		}
	}

	@Override
	public void updateMeasureState(TextPaint p) {
		p.setTypeface(mTypeface);

		// Note: This flag is required for proper typeface rendering
		p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}

	@Override
	public void updateDrawState(TextPaint tp) {
		tp.setTypeface(mTypeface);

		// Note: This flag is required for proper typeface rendering
		tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}
}