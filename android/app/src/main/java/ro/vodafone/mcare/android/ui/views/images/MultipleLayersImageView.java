package ro.vodafone.mcare.android.ui.views.images;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by Victor Radulescu on 4/3/2017.
 */

public class MultipleLayersImageView extends RelativeLayout {

    private Drawable mainDrawable;

    private List<Drawable> layers;


    public MultipleLayersImageView(Context context) {
        super(context);
    }

    public MultipleLayersImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleLayersImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){
    }
    private void buildImageView(){

    }
    public MultipleLayersImageView setSource(Drawable drawable){
        this.mainDrawable = drawable;
        return this;
    }
    public void build(){

    }
}
