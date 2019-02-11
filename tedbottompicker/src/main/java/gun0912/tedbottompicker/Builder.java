package gun0912.tedbottompicker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class Builder implements Parcelable {

    public static final String BUILDER_KEY = "BUILDER_KEY";

    public static final String URL_KEY = "URL_KEY";

    public static final int REQUEST_CODE = 112233;

//    public Context context;
    public int previewMaxCount = 25;
    public Drawable cameraTileDrawable;
    public Drawable galleryTileDrawable;
//
    public Drawable deSelectIconDrawable;
    public Drawable selectedForegroundDrawable;

    public int spacing = 1;
    public boolean includeEdgeSpacing = false;
//    public TedBottomPicker.OnImageSelectedListener onImageSelectedListener;
//    public TedBottomPicker.OnMultiImageSelectedListener onMultiImageSelectedListener;
//    public TedBottomPicker.OnErrorListener onErrorListener;
    public TedBottomPicker.ImageProvider imageProvider;
    public boolean showCamera = true;
    public boolean showGallery = true;
    public int peekHeight = -1;
    public int cameraTileBackgroundResId = R.color.tedbottompicker_camera;
    public int galleryTileBackgroundResId = R.color.tedbottompicker_gallery;

    public String title;
    public boolean showTitle = true;
    public int titleBackgroundResId;

    public int selectMaxCount = Integer.MAX_VALUE;
    public int selectMinCount = 0;

    public String completeButtonText;
    public String emptySelectionText;
    public String selectMaxCountErrorText;
    public String selectMinCountErrorText;
    public @MediaType
    int mediaType = MediaType.IMAGE;
    ArrayList<Uri> selectedUriList;
    Uri selectedUri;

    public Builder() {
    }

//    public Builder(@NonNull Context context) {
//
//        this.context = context;
//
//        setCameraTile(R.drawable.ic_camera);
//        setGalleryTile(R.drawable.ic_gallery);
//        setSpacingResId(R.dimen.tedbottompicker_grid_layout_margin);
//    }

//    public Builder setCameraTile(@DrawableRes int cameraTileResId) {
//        setCameraTile(ContextCompat.getDrawable(context, cameraTileResId));
//        return this;
//    }
//
//    public Builder setGalleryTile(@DrawableRes int galleryTileResId) {
//        setGalleryTile(ContextCompat.getDrawable(context, galleryTileResId));
//        return this;
//    }
//
//    public Builder setSpacingResId(@DimenRes int dimenResId) {
//        this.spacing = context.getResources().getDimensionPixelSize(dimenResId);
//        return this;
//    }

//    public Builder setCameraTile(Drawable cameraTileDrawable) {
//        this.cameraTileDrawable = cameraTileDrawable;
//        return this;
//    }
//
//    public Builder setGalleryTile(Drawable galleryTileDrawable) {
//        this.galleryTileDrawable = galleryTileDrawable;
//        return this;
//    }

//    public Builder setDeSelectIcon(@DrawableRes int deSelectIconResId) {
//        setDeSelectIcon(ContextCompat.getDrawable(context, deSelectIconResId));
//        return this;
//    }

//    public Builder setDeSelectIcon(Drawable deSelectIconDrawable) {
//        this.deSelectIconDrawable = deSelectIconDrawable;
//        return this;
//    }
//
//    public Builder setSelectedForeground(@DrawableRes int selectedForegroundResId) {
//        setSelectedForeground(ContextCompat.getDrawable(context, selectedForegroundResId));
//        return this;
//    }

//    public Builder setSelectedForeground(Drawable selectedForegroundDrawable) {
//        this.selectedForegroundDrawable = selectedForegroundDrawable;
//        return this;
//    }

    protected Builder(Parcel in) {
        previewMaxCount = in.readInt();
        spacing = in.readInt();
        includeEdgeSpacing = in.readByte() != 0;
        showCamera = in.readByte() != 0;
        showGallery = in.readByte() != 0;
        peekHeight = in.readInt();
        cameraTileBackgroundResId = in.readInt();
        galleryTileBackgroundResId = in.readInt();
        title = in.readString();
        showTitle = in.readByte() != 0;
        titleBackgroundResId = in.readInt();
        selectMaxCount = in.readInt();
        selectMinCount = in.readInt();
        completeButtonText = in.readString();
        emptySelectionText = in.readString();
        selectMaxCountErrorText = in.readString();
        selectMinCountErrorText = in.readString();
        mediaType = in.readInt();
        selectedUriList = in.createTypedArrayList(Uri.CREATOR);
        selectedUri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(previewMaxCount);
        dest.writeInt(spacing);
        dest.writeByte((byte) (includeEdgeSpacing ? 1 : 0));
        dest.writeByte((byte) (showCamera ? 1 : 0));
        dest.writeByte((byte) (showGallery ? 1 : 0));
        dest.writeInt(peekHeight);
        dest.writeInt(cameraTileBackgroundResId);
        dest.writeInt(galleryTileBackgroundResId);
        dest.writeString(title);
        dest.writeByte((byte) (showTitle ? 1 : 0));
        dest.writeInt(titleBackgroundResId);
        dest.writeInt(selectMaxCount);
        dest.writeInt(selectMinCount);
        dest.writeString(completeButtonText);
        dest.writeString(emptySelectionText);
        dest.writeString(selectMaxCountErrorText);
        dest.writeString(selectMinCountErrorText);
        dest.writeInt(mediaType);
        dest.writeTypedList(selectedUriList);
        dest.writeParcelable(selectedUri, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Builder> CREATOR = new Creator<Builder>() {
        @Override
        public Builder createFromParcel(Parcel in) {
            return new Builder(in);
        }

        @Override
        public Builder[] newArray(int size) {
            return new Builder[size];
        }
    };

    public Builder setPreviewMaxCount(int previewMaxCount) {
        this.previewMaxCount = previewMaxCount;
        return this;
    }

    public Builder setSelectMaxCount(int selectMaxCount) {
        this.selectMaxCount = selectMaxCount;
        return this;
    }

    public Builder setSelectMinCount(int selectMinCount) {
        this.selectMinCount = selectMinCount;
        return this;
    }

//    public Builder setOnImageSelectedListener(TedBottomPicker.OnImageSelectedListener onImageSelectedListener) {
//        this.onImageSelectedListener = onImageSelectedListener;
//        return this;
//    }
//
//    public Builder setOnMultiImageSelectedListener(TedBottomPicker.OnMultiImageSelectedListener onMultiImageSelectedListener) {
//        this.onMultiImageSelectedListener = onMultiImageSelectedListener;
//        return this;
//    }

//    public Builder setOnErrorListener(TedBottomPicker.OnErrorListener onErrorListener) {
//        this.onErrorListener = onErrorListener;
//        return this;
//    }

    public Builder showCameraTile(boolean showCamera) {
        this.showCamera = showCamera;
        return this;
    }

    public Builder showGalleryTile(boolean showGallery) {
        this.showGallery = showGallery;
        return this;
    }

    public Builder setSpacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    public Builder setIncludeEdgeSpacing(boolean includeEdgeSpacing){
        this.includeEdgeSpacing = includeEdgeSpacing;
        return this;
    }

    public Builder setPeekHeight(int peekHeight) {
        this.peekHeight = peekHeight;
        return this;
    }
//
//    public Builder setPeekHeightResId(@DimenRes int dimenResId) {
//        this.peekHeight = context.getResources().getDimensionPixelSize(dimenResId);
//        return this;
//    }

    public Builder setCameraTileBackgroundResId(@ColorRes int colorResId) {
        this.cameraTileBackgroundResId = colorResId;
        return this;
    }

    public Builder setGalleryTileBackgroundResId(@ColorRes int colorResId) {
        this.galleryTileBackgroundResId = colorResId;
        return this;
    }

    public Builder setTitle(String title) {
        this.title = title;
        return this;
    }

//    public Builder setTitle(@StringRes int stringResId) {
//        this.title = context.getResources().getString(stringResId);
//        return this;
//    }

    public Builder showTitle(boolean showTitle) {
        this.showTitle = showTitle;
        return this;
    }

    public Builder setCompleteButtonText(String completeButtonText) {
        this.completeButtonText = completeButtonText;
        return this;
    }

//    public Builder setCompleteButtonText(@StringRes int completeButtonResId) {
//        this.completeButtonText = context.getResources().getString(completeButtonResId);
//        return this;
//    }

    public Builder setEmptySelectionText(String emptySelectionText) {
        this.emptySelectionText = emptySelectionText;
        return this;
    }

//    public Builder setEmptySelectionText(@StringRes int emptySelectionResId) {
//        this.emptySelectionText = context.getResources().getString(emptySelectionResId);
//        return this;
//    }

//    public Builder setSelectMaxCountErrorText(String selectMaxCountErrorText) {
//        this.selectMaxCountErrorText = selectMaxCountErrorText;
//        return this;
//    }
//
//    public Builder setSelectMaxCountErrorText(@StringRes int selectMaxCountErrorResId) {
//        this.selectMaxCountErrorText = context.getResources().getString(selectMaxCountErrorResId);
//        return this;
//    }

    public Builder setSelectMinCountErrorText(String selectMinCountErrorText) {
        this.selectMinCountErrorText = selectMinCountErrorText;
        return this;
    }

//    public Builder setSelectMinCountErrorText(@StringRes int selectMinCountErrorResId) {
//        this.selectMinCountErrorText = context.getResources().getString(selectMinCountErrorResId);
//        return this;
//    }

    public Builder setTitleBackgroundResId(@ColorRes int colorResId) {
        this.titleBackgroundResId = colorResId;
        return this;
    }

    public Builder setImageProvider(TedBottomPicker.ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
        return this;
    }

    public Builder setSelectedUriList(ArrayList<Uri> selectedUriList) {
        this.selectedUriList = selectedUriList;
        return this;
    }

    public Builder setSelectedUri(Uri selectedUri) {
        this.selectedUri = selectedUri;
        return this;
    }

    public Builder showVideoMedia() {
        this.mediaType = MediaType.VIDEO;
        return this;
    }

    public TedBottomPicker create(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Missing required WRITE_EXTERNAL_STORAGE permission. Did you remember to request it first?");
        }

//        if (onImageSelectedListener == null && onMultiImageSelectedListener == null) {
//            throw new RuntimeException("You have to use setOnImageSelectedListener() or setOnMultiImageSelectedListener() for receive selected Uri");
//        }

        TedBottomPicker customBottomSheetDialogFragment = new TedBottomPicker();

        customBottomSheetDialogFragment.builder = this;
        return customBottomSheetDialogFragment;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MediaType.IMAGE, MediaType.VIDEO})
    public @interface MediaType {
        int IMAGE = 1;
        int VIDEO = 2;
    }
}