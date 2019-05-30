package ro.vodafone.mcare.android.widget.SwitchButton;


import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Generate thumb and background color state list use tintColor
 * Created by kyle on 15/11/4.
 */
public class ColorUtils {
	private static final int ENABLE_ATTR = android.R.attr.state_enabled;
	private static final int CHECKED_ATTR = android.R.attr.state_checked;
	private static final int PRESSED_ATTR = android.R.attr.state_pressed;

	public static ColorStateList generateThumbColorWithTintColor(final int tintColor) {
		int[][] states = new int[][]{
				{-ENABLE_ATTR, CHECKED_ATTR},
				{-ENABLE_ATTR},
				{PRESSED_ATTR, -CHECKED_ATTR},
				{PRESSED_ATTR, CHECKED_ATTR},
				{CHECKED_ATTR},
				{-CHECKED_ATTR}
		};

		int[] colors = new int[] {
				tintColor - 0xAA000000,
				0xFFBABABA,
				tintColor - 0x99000000,
				tintColor - 0x99000000,
				tintColor | 0xFF000000,
				0xFFEEEEEE
		};
		return new ColorStateList(states, colors);
	}

	public static ColorStateList generateBackColorWithTintColor(final int tintColor) {
		int[][] states = new int[][]{
				{-ENABLE_ATTR, CHECKED_ATTR},
				{-ENABLE_ATTR},
				{CHECKED_ATTR, PRESSED_ATTR},
				{-CHECKED_ATTR, PRESSED_ATTR},
				{CHECKED_ATTR},
				{-CHECKED_ATTR}
		};

		int[] colors = new int[] {
				tintColor - 0xE1000000,
				0x10000000,
				tintColor - 0xD0000000,
				0x20000000,
				tintColor - 0xD0000000,
				0x20000000
		};
		return new ColorStateList(states, colors);
	}

	public static void setColorHandles(EditText view, int color) {
		try {
			Field editorField = TextView.class.getDeclaredField("mEditor");
			if (!editorField.isAccessible()) {
				editorField.setAccessible(true);
			}

			Object editor = editorField.get(view);
			Class<?> editorClass = editor.getClass();

			String[] handleNames = {"mSelectHandleLeft", "mSelectHandleRight", "mSelectHandleCenter"};
			String[] resNames = {"mTextSelectHandleLeftRes", "mTextSelectHandleRightRes", "mTextSelectHandleRes"};

			for (int i = 0; i < handleNames.length; i++) {
				Field handleField = editorClass.getDeclaredField(handleNames[i]);
				if (!handleField.isAccessible()) {
					handleField.setAccessible(true);
				}

				Drawable handleDrawable = (Drawable) handleField.get(editor);

				if (handleDrawable == null) {
					Field resField = TextView.class.getDeclaredField(resNames[i]);
					if (!resField.isAccessible()) {
						resField.setAccessible(true);
					}
					int resId = resField.getInt(view);
					if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
						handleDrawable = view.getContext().getDrawable(resId);
					else
						handleDrawable = view.getResources().getDrawable(resId);
				}

				if (handleDrawable != null) {
					Drawable drawable = handleDrawable.mutate();
					drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
					handleField.set(editor, drawable);
				}
			}
			view.setHighlightColor(android.support.v4.graphics.ColorUtils.setAlphaComponent(color, 128));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
