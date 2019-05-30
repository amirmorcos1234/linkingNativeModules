package ro.vodafone.mcare.android.card.offers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ro.vodafone.mcare.android.R;
import ro.vodafone.mcare.android.card.VodafoneAbstractCard;
import ro.vodafone.mcare.android.client.model.shop.ProductColor;
import ro.vodafone.mcare.android.client.model.shop.ProductMemory;
import ro.vodafone.mcare.android.ui.utils.ScreenMeasure;
import ro.vodafone.mcare.android.ui.views.images.CheckableImageView;
import ro.vodafone.mcare.android.ui.views.textviews.VodafoneTextView;

/**
 * Created by Victor Radulescu on 3/30/2017.
 */

public class CustomizePhoneCard extends VodafoneAbstractCard {

    @BindView(R.id.color_gridlayout)
    GridLayout colorGridLayout;
    @BindView(R.id.storage_gridlayout)
    GridLayout storageGridLayout;

    @BindView(R.id.info_imageView)
    AppCompatImageView infoImageView;

    @BindView(R.id.info_tv)
    VodafoneTextView infoTextView;

    @BindView(R.id.chooseColor_tv)
    VodafoneTextView chooseColorTv;
    @BindView(R.id.chooseStorage_tv)
    VodafoneTextView chooseStorageTv;

    ArrayList<CheckableImageView> colorViews;

    ArrayList<AppCompatCheckBox> memoryViews;

    List<ProductColor> productColors;

    List<ProductMemory> productMemories;

    final RoundedCornersTransformation transformation;
    int stockNumber;

    int selectedColor=0;

    int selectedModel=0;

    private OnUserSelectionListener userSelectionListener;
    @OnClick(R.id.card_group_view)
    public void open(){
       // PhoneDetailsFragment.newInstance(null);
    }


    public CustomizePhoneCard(Context context) {
        super(context);
        init(null);
        transformation =  new RoundedCornersTransformation(getContext(), ScreenMeasure.dpToPx(10),0, RoundedCornersTransformation.CornerType.ALL);
    }

    public CustomizePhoneCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        transformation =  new RoundedCornersTransformation(getContext(), ScreenMeasure.dpToPx(10),0, RoundedCornersTransformation.CornerType.ALL);
    }

    public CustomizePhoneCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        transformation =  new RoundedCornersTransformation(getContext(), ScreenMeasure.dpToPx(10),0, RoundedCornersTransformation.CornerType.ALL);
    }

    private void init(AttributeSet attributeSet){
        ButterKnife.bind(this);
        setAttributes(attributeSet);
        addCardTitle("Customizează-ți telefonul", R.color.purple_title_color, null);
        // setRedirectClickListener();
       // addStorages();
       // addColors();
    }



    private void setAttributes(AttributeSet attributeSet) {
    }

    @Override
    protected int setContent() {
        return R.layout.card_details_phone;
    }

    public CustomizePhoneCard setupMemories(List<ProductMemory> storageList){
        this.productMemories = storageList;
        return this;
    }
    public CustomizePhoneCard setupColors(List<ProductColor> colorList){
        this.productColors = colorList;
        return this;
    }

    public void build(OnUserSelectionListener onUserSelectionListener){
        this.userSelectionListener = onUserSelectionListener;
        computeCellHeight();
        setInfo();

    }


    private void setRedirectClickListener(){
        //this.setOnClickListener( new RedirectFragmentListener(getContext(),PhoneDetailsFragment.newInstance(null)));
    }

    public CustomizePhoneCard setStockNumber(int stockNumber) {
        this.stockNumber = stockNumber;
        return this;
    }

    public void addStorages(int width, int height){
        memoryViews = new ArrayList<>();
        if(productMemories!=null && !productMemories.isEmpty()){
            for(int i=0;i<productMemories.size();i++){
                storageGridLayout.addView(createStorageButton(i,productMemories.get(i).getMemory(), width, height,false),i);

            }
            addEmptyStorageViewsToFill(width, height);
            if(!memoryViews.isEmpty()){
                memoryViews.get(selectedModel).setChecked(true);
            }
            showMemories(true);
        }else{
            showMemories(false);
        }
    }


    public void addColors(int width){
        colorViews = new ArrayList<>();
        if(productColors!=null && !productColors.isEmpty()){

            for(int i=0;i<productColors.size();i++){
                Log.d(TAG, "phone color:" + productColors.get(i).getColorImg());
                colorGridLayout.addView(createColorImageView(i,productColors.get(i).getColorImg(),width,false),i);
            }
            addEmptyColorViewsToFill(width);
            showColors(true);
            if(!colorViews.isEmpty()){
                colorViews.get(selectedColor).setChecked(true);
            }
        }else{
            showColors(false);
        }

    }

    private void addEmptyColorViewsToFill(int width) {

       if(colorGridLayout.getChildCount() %4 ==0){
           return;
       }
        for(int i=colorGridLayout.getChildCount()%4; i < 4;i++){
            colorGridLayout.addView(createColorImageView(colorGridLayout.getChildCount(),
                    "http://portal-pet.vodafone.ro/images/v117664.jpg",width,true),
                    colorGridLayout.getChildCount());
        }
    }
    private void addEmptyStorageViewsToFill(int width, int height) {

       if(productMemories.size() %3 ==0){
           return;
       }
        for(int i = storageGridLayout.getChildCount()%3; i < 3;i++){
            storageGridLayout.addView(createStorageButton(storageGridLayout.getChildCount(),"", width, height, true),storageGridLayout.getChildCount());
        }
    }

    private AppCompatCheckBox createStorageButton(final int index, String text, int width, int height, boolean dummy){
        //new ContextThemeWrapper(getContext(),R.style.StorageSelectionCheckBox)
        AppCompatCheckBox storageButton = new AppCompatCheckBox(new ContextThemeWrapper(getContext(),R.style.StorageSelectionCheckBox),null,0);
        //storageButton.setBackgroundResource(R.drawable.shape_blue_white_rectangle);
        storageButton.setText(text);
        storageButton.setTextColor(Color.WHITE);
        int margins = ScreenMeasure.dpToPx(5);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

        Double calculatedWidth = Double.valueOf(width);
        calculatedWidth = calculatedWidth * (0.8);
        Log.d("Grid", calculatedWidth + " - " + height);

        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.setMargins(0,margins,index % 3!=2? margins:0,margins);

        layoutParams.rowSpec = GridLayout.spec(index/3);
        layoutParams.columnSpec =  GridLayout.spec(index % 3);

        storageButton.setLayoutParams(layoutParams);
        storageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStorage(index);
            }
        });

        if(dummy){
            storageButton.setVisibility(INVISIBLE);
            storageButton.setClickable(false);
        }else{
            memoryViews.add(storageButton);
        }
        // memoryViews.add(storageButton);
        //radioButton.setBackground(ContextCompat.getDrawable(R.drawable));
        return storageButton;
    }

    void computeCellHeight(){
        ViewTreeObserver vto = colorGridLayout.getViewTreeObserver();

        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                colorGridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int imageW = (colorGridLayout.getMeasuredWidth() - colorGridLayout.getPaddingLeft() - colorGridLayout.getPaddingRight() - ScreenMeasure.dpToPx(45)) / 4;
                int storageW = (colorGridLayout.getMeasuredWidth() - colorGridLayout.getPaddingLeft() - colorGridLayout.getPaddingRight() - ScreenMeasure.dpToPx(10)) / 3;

                addColors(imageW);
                addStorages(storageW, imageW);

            }
        });
    }

    private AppCompatImageView createColorImageView(final int index, final String url, final int width, boolean dummyColor){

        final CheckableImageView colorImageView = new CheckableImageView(getContext(), width);
        colorImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        colorImageView.setAdjustViewBounds(false);
        colorImageView.setBackgroundResource(R.drawable.selector_checkbox_selection_color_background);
        colorImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectColor(index);
            }
        });
        int margin = ScreenMeasure.dpToPx(15);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.setGravity(Gravity.CENTER);


        //colorImageView.setMinimumHeight(width);
        //colorImageView.setMaxHeight(width);
        //colorImageView.setMinimumWidth(width);
        //colorImageView.setMaxWidth(width);

        layoutParams.columnSpec =  GridLayout.spec(index % 4);
        layoutParams.rowSpec = GridLayout.spec(index/4);
        layoutParams.width = width;
        layoutParams.height = width;
        layoutParams.setMargins(0,margin,index % 4!=3? margin:0,margin);
        Log.d("GridLayout", "color index "+index+" column spec "+index % 4+" row spec "+index/4);

        colorImageView.setPadding(0,0,0,0);
        colorImageView.setLayoutParams(layoutParams);
        Log.d(TAG, url);

        Glide.with(getContext()).load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.error_message_border)
                .bitmapTransform(new BitmapTransformation(getContext()) {
                    @Override
                    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
						return maskRoundedCorners(pool, toTransform, width);
                    }

                    @Override
                    public String getId() {
                        return CustomizePhoneCard.class.getCanonicalName();
                    }
                })
                .into(colorImageView);
        if(dummyColor){
            colorImageView.setVisibility(INVISIBLE);
            colorImageView.setClickable(false);
        }else{
            colorViews.add(colorImageView);
        }

        return colorImageView;
    }

	private static Bitmap maskRoundedCorners(BitmapPool pool, Bitmap source, int width) {
		if (source == null) return null;

		final int size = width;
		final int radius = ScreenMeasure.dpToPx(8);
		final RectF rect = new RectF(0, 0, width, width);

		Bitmap squared = Bitmap.createBitmap(source);
		if (squared != source) {
			source.recycle();
		}

		Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		canvas.drawRect(rect, paint);

		final Path path = new Path();
		path.addRoundRect(rect, radius, radius, Path.Direction.CW);
		canvas.clipPath(path);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
		canvas.drawBitmap(squared, 0, 0, paint);
		canvas.drawBitmap(squared,
				new Rect(0, 0, squared.getWidth(), squared.getHeight()),
				rect,
				paint);

		squared.recycle();

		return result;
	}

    private void selectColor(int index) {
        if(colorViews==null || colorViews.isEmpty()){
            return;
        }
        for (int i = 0; i < colorViews.size(); i++) {
            if(i!=index){
                colorViews.get(i).setChecked(false);
            }else{

                colorViews.get(i).setChecked(true);
            }
        }
        selectedColor = index;
        Log.d(TAG, "selectedColor " + selectedColor);
        userSelectionListener.onColorSelect(selectedColor,productColors.get(index).getPhoneSkuId(),
                productColors.get(index).getPricePlanSkuId());

    }
    private void selectStorage(int index) {
        if(memoryViews == null || memoryViews.isEmpty()){
            return;
        }
        for (int i = 0; i < memoryViews.size(); i++) {
            if(i!=index){
                memoryViews.get(i).setChecked(false);
            }else{
                memoryViews.get(i).setChecked(true);
            }
        }
        selectedModel = index;
        Log.d(TAG, "selectedModel " + selectedModel);
        userSelectionListener.onMemorySelect(selectedModel,productMemories.get(index).getPhoneSkuId(),
                                            productMemories.get(index).getPricePlanSkuId());
    }
    public void setInfo() {
        if(stockNumber > 5){
            setHasStockInfo();
        }else if(stockNumber <= 0){
            setOutOfStockInfo();
        }else{
            setHasStockLimitedInfo(stockNumber);
        }
    }

    private void setOutOfStockInfo(){
        infoImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.error_icon));
        String textInfo="Nu e disponibil în stoc";
        infoTextView.setText(textInfo);
    }
    private void setHasStockInfo(){
        infoImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_check_circle_greeen_24dp));
        String textInfo="În stoc";
        infoTextView.setText(textInfo);
    }

    private void setHasStockLimitedInfo(int stockNumber){
        infoImageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.toast_warning));
        String textInfo="Ultimele "+stockNumber+" bucăți rămase";
        infoTextView.setText(textInfo);
    }

    private void showMemories(boolean show){
        if(storageGridLayout==null || chooseStorageTv==null){
            return;
        }
        if(show){
            storageGridLayout.setVisibility(VISIBLE);
            chooseStorageTv.setVisibility(VISIBLE);
        }else{
            storageGridLayout.setVisibility(GONE);
            chooseStorageTv.setVisibility(GONE);
        }
    }
    private void showColors(boolean show){
        if(colorGridLayout==null || chooseColorTv==null){
            return;
        }
        if(show){
            colorGridLayout.setVisibility(VISIBLE);
            chooseColorTv.setVisibility(VISIBLE);
        }else{
            colorGridLayout.setVisibility(GONE);
            chooseColorTv.setVisibility(GONE);
        }
    }

    public CustomizePhoneCard setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
        //selectColor(selectedColor);
        Log.d(TAG, "selectedColor" + selectedColor);
        return this;
    }

    public CustomizePhoneCard setSelectedModel(int selectedModel) {
        this.selectedModel = selectedModel;
        Log.d(TAG, "selectedModel" + selectedModel);
        //selectStorage(selectedModel);
        return this;

    }

    public interface OnUserSelectionListener {
        void onMemorySelect(int selectedModel, String phoneSkuId, String pricePlanSkuId);
        void onColorSelect(int selectedColor, String phoneSkuId, String pricePlanSkuId);
    }



}
