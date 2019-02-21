package gun0912.tedbottompicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedonactivityresult.TedOnActivityResult;
import com.gun0912.tedonactivityresult.listener.OnActivityResultListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gun0912.tedbottompicker.adapter.GalleryAdapter;
import gun0912.tedbottompicker.util.RealPathUtil;

public class TedBottomPicker extends BottomSheetDialogFragment {

    public static final String TAG = "TedBottomPicker";
    static final int PERMISSION_WRITE_TO_STORAGE = 1231;
    static final int REQUEST_CODE = 912;

    @NonNull
    public SettingsModel settingsModel = new SettingsModel();
    GalleryAdapter imageGalleryAdapter;
    TextView tv_title;
    Button btn_done;
    FrameLayout selected_photos_container_frame;
    HorizontalScrollView hsv_selected_photos;
    LinearLayout selected_photos_container;

    TextView selected_photos_empty;
    View contentView;
    private RecyclerView rc_gallery;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            Log.d(TAG, "onStateChanged() newState: " + newState);
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismissAllowingStateLoss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            Log.d(TAG, "onSlide() slideOffset: " + slideOffset);
        }
    };

    public static SettingsModel Builder() {
        return new SettingsModel();
    }

    public static TedBottomPicker newInstance(SettingsModel settingsModel, Fragment targetFragment) {
        TedBottomPicker tedBottomPicker = newInstance(settingsModel);
        tedBottomPicker.setTargetFragment(targetFragment, REQUEST_CODE);
        return tedBottomPicker;
    }

    public static TedBottomPicker newInstance(SettingsModel settingsModel) {
        TedBottomPicker tedBottomPicker = new TedBottomPicker();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SettingsModel.BUILDER_KEY, settingsModel);
        tedBottomPicker.setArguments(bundle);
        return tedBottomPicker;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(SettingsModel.BUILDER_KEY)) {
            SettingsModel model = getArguments().getParcelable(SettingsModel.BUILDER_KEY);
            if (model != null) {
                settingsModel = model;
            }
        }

        if (settingsModel.isMultiSelect()) {
           checkImplementationMultiImageSelected();
        } else {
            checkImplementationOnImageSelected();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SettingsModel.BUILDER_KEY, settingsModel);
        super.onSaveInstanceState(outState);
    }

    public void show(FragmentManager fragmentManager) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, getTag());
        ft.commitAllowingStateLoss();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.tedbottompicker_content_view, null);
        dialog.setContentView(contentView);

        if (permissionNotProvided()) {
            contentView.setVisibility(View.GONE);
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, PERMISSION_WRITE_TO_STORAGE);
        }

        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            if (settingsModel != null && settingsModel.peekHeight > 0) {
                ((BottomSheetBehavior) behavior).setPeekHeight(settingsModel.peekHeight);
            }
        }

        initView(contentView);

        setRecyclerView();
        setSelectionView();

        setTitle();
        setDoneButton();

        if (settingsModel.isMultiSelect()) {
            for (Uri uri : settingsModel.selectedUriList) {
                addUrlToGallery(uri);
            }
        }
    }

    private boolean permissionNotProvided() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private void setTitle() {
        if (!TextUtils.isEmpty(settingsModel.title)) {
            tv_title.setText(settingsModel.title);
            if (settingsModel.titleBackgroundResId > 0) {
                tv_title.setBackgroundResource(settingsModel.titleBackgroundResId);
            }
        } else {
            tv_title.setVisibility(View.GONE);
        }
    }

    private void setSelectionView() {
        if (settingsModel.emptySelectionText != null) {
            selected_photos_empty.setText(settingsModel.emptySelectionText);
        }
    }

    private void setDoneButton() {
        if (settingsModel.isMultiSelect()) {
            selected_photos_container_frame.setVisibility(View.VISIBLE);
            btn_done.setText(settingsModel.completeButtonText);

            btn_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMultiSelectComplete();
                }
            });
        } else {
            btn_done.setVisibility(View.GONE);
            selected_photos_container_frame.setVisibility(View.GONE);
        }
    }

    private void onMultiSelectComplete() {

        if (settingsModel.selectedUriList.size() < settingsModel.selectMinCount) {
            String message;
            if (settingsModel.selectMinCountErrorText != null) {
                message = settingsModel.selectMinCountErrorText;
            } else {
                message = String.format(getResources().getString(R.string.select_min_count), settingsModel.selectMinCount);
            }

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        onImagesSelected(settingsModel.selectedUriList);
    }

    private void initView(View contentView) {

        rc_gallery = (RecyclerView) contentView.findViewById(R.id.rc_gallery);
        tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        btn_done = (Button) contentView.findViewById(R.id.btn_done);

        selected_photos_container_frame = (FrameLayout) contentView.findViewById(R.id.selected_photos_container_frame);
        hsv_selected_photos = (HorizontalScrollView) contentView.findViewById(R.id.hsv_selected_photos);
        selected_photos_container = (LinearLayout) contentView.findViewById(R.id.selected_photos_container);
        selected_photos_empty = (TextView) contentView.findViewById(R.id.selected_photos_empty);
    }

    private void setRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rc_gallery.setLayoutManager(gridLayoutManager);
        rc_gallery.addItemDecoration(new GridSpacingItemDecoration(gridLayoutManager.getSpanCount(), settingsModel.spacing, settingsModel.includeEdgeSpacing));
        updateAdapter();
    }

    private void updateAdapter() {

        imageGalleryAdapter = new GalleryAdapter(getActivity(), settingsModel);
        rc_gallery.setAdapter(imageGalleryAdapter);
        imageGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                GalleryAdapter.PickerTile pickerTile = imageGalleryAdapter.getItem(position);

                switch (pickerTile.getTileType()) {
                    case CAMERA:
                        startCameraIntent();
                        break;
                    case GALLERY:
                        startGalleryIntent();
                        break;
                    case IMAGE:
                        complete(pickerTile.getImageUri());
                        break;
                    default:
                        onError("Error");
                }
            }
        });
    }

    private void complete(final Uri uri) {
        Log.d(TAG, "selected uri: " + uri.toString());
        if (settingsModel.isMultiSelect()) {
            if (settingsModel.selectedUriList.contains(uri)) {
                removeImage(uri);
            } else {
                addUri(uri);
            }
        } else {
            returnResult(uri);
        }
    }

    private void addUri(final Uri uri) {
        if (settingsModel.selectedUriList.size() == settingsModel.selectMaxCount) {
            String message;
            if (settingsModel.selectMaxCountErrorText != null) {
                message = settingsModel.selectMaxCountErrorText;
            } else {
                message = String.format(getResources().getString(R.string.select_max_count), settingsModel.selectMaxCount);
            }

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }

        settingsModel.selectedUriList.add(uri);
        addUrlToGallery(uri);
    }

    private void addUrlToGallery(final Uri uri) {
        final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.tedbottompicker_selected_item, null);
        ImageView thumbnail = (ImageView) rootView.findViewById(R.id.selected_photo);
        ImageView iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
        rootView.setTag(uri);

        selected_photos_container.addView(rootView);

        int px = (int) getResources().getDimension(R.dimen.tedbottompicker_selected_image_height);
        thumbnail.setLayoutParams(new FrameLayout.LayoutParams(px, px));
        ImageLoader.loadImageInto(getContext(), uri, thumbnail);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage(uri);

            }
        });
        updateGallery(uri);
    }

    private void removeImage(Uri uri) {
        settingsModel.selectedUriList.remove(uri);
        for (int i = 0; i < selected_photos_container.getChildCount(); i++) {
            View childView = selected_photos_container.getChildAt(i);
            if (childView.getTag().equals(uri)) {
                selected_photos_container.removeViewAt(i);
                break;
            }
        }
        updateGallery(uri);
    }

    private void updateGallery(Uri uri) {
        updateSelectedView();
        imageGalleryAdapter.setSelectedUriList(settingsModel.selectedUriList, uri);
    }

    private void updateSelectedView() {
        if (settingsModel.selectedUriList == null || settingsModel.selectedUriList.size() == 0) {
            selected_photos_empty.setVisibility(View.VISIBLE);
            selected_photos_container.setVisibility(View.GONE);
        } else {
            selected_photos_empty.setVisibility(View.GONE);
            selected_photos_container.setVisibility(View.VISIBLE);
        }
    }

    private void startCameraIntent() {
        Intent cameraInent;
        File mediaFile;

        if (settingsModel.mediaType == SettingsModel.MediaType.IMAGE) {
            cameraInent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mediaFile = getImageFile();
        } else {
            cameraInent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            mediaFile = getVideoFile();
        }

        if (cameraInent.resolveActivity(getActivity().getPackageManager()) == null) {
            onError("This Application do not have Camera Application");
            return;
        }

        Uri photoURI = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", mediaFile);

        List<ResolveInfo> resolvedIntentActivities = getContext().getPackageManager().queryIntentActivities(cameraInent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            getContext().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        cameraInent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        TedOnActivityResult.with(getActivity())
                .setIntent(cameraInent)
                .setListener(new OnActivityResultListener() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if (resultCode == Activity.RESULT_OK) {
                            onActivityResultCamera(settingsModel.selectedUri);
                        }
                    }
                })
                .startActivityForResult();
    }

    private File getImageFile() {
        // Create an image file name
        File imageFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            if (!storageDir.exists())
                storageDir.mkdirs();

            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            settingsModel.selectedUri = Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            onError("Could not create imageFile for camera");
        }
        return imageFile;
    }

    private File getVideoFile() {
        // Create an image file name
        File videoFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "VIDEO_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

            if (!storageDir.exists())
                storageDir.mkdirs();

            videoFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".mp4",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            settingsModel.selectedUri = Uri.fromFile(videoFile);
        } catch (IOException e) {
            e.printStackTrace();
            onError("Could not create imageFile for camera");
        }
        return videoFile;
    }

    private void startGalleryIntent() {
        Intent galleryIntent;
        Uri uri;
        if (settingsModel.mediaType == SettingsModel.MediaType.IMAGE) {
            galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //  galleryIntent.setType("image/*");// TODO check
        } else {
            galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            //   galleryIntent.setType("video/*");// TODO check

        }

        if (galleryIntent.resolveActivity(getActivity().getPackageManager()) == null) {
            onError("This Application do not have Gallery Application");
            return;
        }

        TedOnActivityResult.with(getActivity())
                .setIntent(galleryIntent)
                .setListener(new OnActivityResultListener() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if (resultCode == Activity.RESULT_OK) {
                            onActivityResultGallery(data);
                        }
                    }
                })
                .startActivityForResult();
    }

    private void onActivityResultCamera(final Uri cameraImageUri) {

        MediaScannerConnection.scanFile(getContext(), new String[]{cameraImageUri.getPath()}, new String[]{"image/jpeg"}, new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {

            }

            @Override
            public void onScanCompleted(String s, Uri uri) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateAdapter();
                        complete(cameraImageUri);
                    }
                });
            }
        });
    }

    private void onActivityResultGallery(Intent data) {
        Uri temp = data.getData();

        if (temp == null) {
            onError("onActivityResultGallery Uri is null");
        }

        String realPath = RealPathUtil.getRealPath(getActivity(), temp);

        Uri selectedImageUri;
        try {
            selectedImageUri = Uri.fromFile(new File(realPath));
        } catch (Exception ex) {
            selectedImageUri = Uri.parse(realPath);
        }
        complete(selectedImageUri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (requestCode == PERMISSION_WRITE_TO_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (contentView != null) {
                    contentView.setVisibility(View.VISIBLE);
                }
                setRecyclerView();
            } else {
                dismissAllowingStateLoss();
            }
        }
    }

    public void onImagesSelected(ArrayList<Uri> uriList) {
        getOnMultiImageSelectedListener().onImagesSelected(uriList, settingsModel.tag);
        dismissAllowingStateLoss();
    }

    private void returnResult(Uri uri) {
        getOnImageSelectedListener().onImageSelected(uri, settingsModel.tag);
        dismissAllowingStateLoss();
    }

    @Nullable
    private OnImageSelectedListener getOnImageSelectedListener() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof OnImageSelectedListener) {
            return ((OnImageSelectedListener) targetFragment);
        }
        if (getActivity() instanceof OnImageSelectedListener) {
            return ((OnImageSelectedListener) getActivity());
        }
        return null;
    }

    @Nullable
    private OnMultiImageSelectedListener getOnMultiImageSelectedListener() {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof OnMultiImageSelectedListener) {
            return ((OnMultiImageSelectedListener) targetFragment);
        }
        if (getActivity() instanceof OnMultiImageSelectedListener) {
            return ((OnMultiImageSelectedListener) getActivity());
        }
        return null;
    }

    public void onError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void checkImplementationOnImageSelected() {
        if (getOnImageSelectedListener() == null) {
            throw new RuntimeException("You have implementation OnImageSelectedListener for receive selected Uri");
        }
    }

    public void checkImplementationMultiImageSelected() {
        if (getOnMultiImageSelectedListener() == null) {
            throw new RuntimeException("You have implementation OnMultiImageSelectedListener for receive selected Uri");
        }
    }

    public interface OnImageSelectedListener {
        void onImageSelected(Uri uri, String tag);
    }

    public interface OnMultiImageSelectedListener {
        void onImagesSelected(ArrayList<Uri> uriList, String tag);
    }
}