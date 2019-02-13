package gun0912.tedbottompicker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class SettingsModel implements Parcelable {

    static final String BUILDER_KEY = "BUILDER_KEY";
    public static final String URI_KEY = "URI_KEY";
    public static final String URI_LIST_KEY = "URI_LIST_KEY";
    public static final int REQUEST_CODE = 112233;

    public int previewMaxCount = 25;
    @DrawableRes
    public int iconCamera;
    @DrawableRes
    public int iconGallery;

    public Drawable deSelectIconDrawable;
    public Drawable selectedForegroundDrawable;

    int spacing = 1;
    boolean includeEdgeSpacing = false;
    public TedBottomPicker.ImageProvider imageProvider;
    public boolean showCamera = true;
    public boolean showGallery = true;
    int peekHeight = -1;
    public int cameraTileBackgroundResId = R.color.tedbottompicker_camera;
    public int galleryTileBackgroundResId = R.color.tedbottompicker_gallery;

    String title;
    int titleBackgroundResId;

    int selectMaxCount = Integer.MAX_VALUE;
    int selectMinCount = 0;
    @StringRes
    int completeButtonText = R.string.done;
    String emptySelectionText;
    String selectMaxCountErrorText;
    String selectMinCountErrorText;
    public @MediaType
    int mediaType = MediaType.IMAGE;
    ArrayList<Uri> selectedUriList = new ArrayList<>();
    Uri selectedUri;

    boolean isMultiSelect = false;

    public SettingsModel() {
        iconCamera = R.drawable.ic_camera;
        iconGallery = R.drawable.ic_gallery;
    }

    protected SettingsModel(Parcel in) {
        previewMaxCount = in.readInt();
        iconCamera = in.readInt();
        iconGallery = in.readInt();
        spacing = in.readInt();
        includeEdgeSpacing = in.readByte() != 0;
        showCamera = in.readByte() != 0;
        showGallery = in.readByte() != 0;
        peekHeight = in.readInt();
        cameraTileBackgroundResId = in.readInt();
        galleryTileBackgroundResId = in.readInt();
        title = in.readString();
        titleBackgroundResId = in.readInt();
        selectMaxCount = in.readInt();
        selectMinCount = in.readInt();
        completeButtonText = in.readInt();
        emptySelectionText = in.readString();
        selectMaxCountErrorText = in.readString();
        selectMinCountErrorText = in.readString();
        mediaType = in.readInt();
        selectedUriList = in.createTypedArrayList(Uri.CREATOR);
        selectedUri = in.readParcelable(Uri.class.getClassLoader());
        isMultiSelect = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(previewMaxCount);
        dest.writeInt(iconCamera);
        dest.writeInt(iconGallery);
        dest.writeInt(spacing);
        dest.writeByte((byte) (includeEdgeSpacing ? 1 : 0));
        dest.writeByte((byte) (showCamera ? 1 : 0));
        dest.writeByte((byte) (showGallery ? 1 : 0));
        dest.writeInt(peekHeight);
        dest.writeInt(cameraTileBackgroundResId);
        dest.writeInt(galleryTileBackgroundResId);
        dest.writeString(title);
        dest.writeInt(titleBackgroundResId);
        dest.writeInt(selectMaxCount);
        dest.writeInt(selectMinCount);
        dest.writeInt(completeButtonText);
        dest.writeString(emptySelectionText);
        dest.writeString(selectMaxCountErrorText);
        dest.writeString(selectMinCountErrorText);
        dest.writeInt(mediaType);
        dest.writeTypedList(selectedUriList);
        dest.writeParcelable(selectedUri, flags);
        dest.writeByte((byte) (isMultiSelect ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SettingsModel> CREATOR = new Creator<SettingsModel>() {
        @Override
        public SettingsModel createFromParcel(Parcel in) {
            return new SettingsModel(in);
        }

        @Override
        public SettingsModel[] newArray(int size) {
            return new SettingsModel[size];
        }
    };

    public SettingsModel setMultiSelect() {
        this.isMultiSelect = true;
        return this;
    }

    public SettingsModel setPreviewMaxCount(int previewMaxCount) {
        this.previewMaxCount = previewMaxCount;
        return this;
    }

    public SettingsModel setSelectMaxCount(int selectMaxCount) {
        this.selectMaxCount = selectMaxCount;
        return this;
    }

    public SettingsModel setSelectMinCount(int selectMinCount) {
        this.selectMinCount = selectMinCount;
        return this;
    }

    public SettingsModel showCameraTile(boolean showCamera) {
        this.showCamera = showCamera;
        return this;
    }

    public SettingsModel showGalleryTile(boolean showGallery) {
        this.showGallery = showGallery;
        return this;
    }

    public SettingsModel setSpacing(int spacing) {
        this.spacing = spacing;
        return this;
    }

    public SettingsModel setIncludeEdgeSpacing(boolean includeEdgeSpacing) {
        this.includeEdgeSpacing = includeEdgeSpacing;
        return this;
    }

    public SettingsModel setPeekHeight(int peekHeight) {
        this.peekHeight = peekHeight;
        return this;
    }

    public SettingsModel setCameraTileBackgroundResId(@ColorRes int colorResId) {
        this.cameraTileBackgroundResId = colorResId;
        return this;
    }

    public SettingsModel setGalleryTileBackgroundResId(@ColorRes int colorResId) {
        this.galleryTileBackgroundResId = colorResId;
        return this;
    }

    public SettingsModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public SettingsModel setCompleteButtonText(@StringRes int completeButtonResId) {
        this.completeButtonText = completeButtonResId;
        return this;
    }

    public SettingsModel setEmptySelectionText(String emptySelectionText) {
        this.emptySelectionText = emptySelectionText;
        return this;
    }

//    public SettingsModel setEmptySelectionText(@StringRes int emptySelectionResId) {
//        this.emptySelectionText = context.getResources().getString(emptySelectionResId);
//        return this;
//    }

//    public SettingsModel setSelectMaxCountErrorText(String selectMaxCountErrorText) {
//        this.selectMaxCountErrorText = selectMaxCountErrorText;
//        return this;
//    }
//
//    public SettingsModel setSelectMaxCountErrorText(@StringRes int selectMaxCountErrorResId) {
//        this.selectMaxCountErrorText = context.getResources().getString(selectMaxCountErrorResId);
//        return this;
//    }

    public SettingsModel setSelectMinCountErrorText(String selectMinCountErrorText) {
        this.selectMinCountErrorText = selectMinCountErrorText;
        return this;
    }

//    public SettingsModel setSelectMinCountErrorText(@StringRes int selectMinCountErrorResId) {
//        this.selectMinCountErrorText = context.getResources().getString(selectMinCountErrorResId);
//        return this;
//    }

    public SettingsModel setTitleBackgroundResId(@ColorRes int colorResId) {
        this.titleBackgroundResId = colorResId;
        return this;
    }

    public SettingsModel setImageProvider(TedBottomPicker.ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
        return this;
    }

    public SettingsModel setSelectedUriList(@Nullable ArrayList<Uri> selectedUriList) {
        if (selectedUriList != null) {
            this.selectedUriList.addAll(selectedUriList);
        }
        return this;
    }

    public SettingsModel setSelectedUri(Uri selectedUri) {
        this.selectedUri = selectedUri;
        return this;
    }

    public SettingsModel showVideoMedia() {
        this.mediaType = MediaType.VIDEO;
        return this;
    }

    public TedBottomPicker create(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Missing required WRITE_EXTERNAL_STORAGE permission. Did you remember to request it first?");
        }

        TedBottomPicker customBottomSheetDialogFragment = new TedBottomPicker();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SettingsModel.BUILDER_KEY, this);
        customBottomSheetDialogFragment.setArguments(bundle);
        return customBottomSheetDialogFragment;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MediaType.IMAGE, MediaType.VIDEO})
    public @interface MediaType {
        int IMAGE = 1;
        int VIDEO = 2;
    }
}