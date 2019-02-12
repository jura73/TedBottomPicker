package gun0912.tedbottompicker;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class TedBottomPicker extends BottomSheetDialogFragment{

    public static final String TAG = "TedBottomPicker";
    static final String EXTRA_CAMERA_IMAGE_URI = "camera_image_uri";
    static final String EXTRA_CAMERA_SELECTED_IMAGE_URI = "camera_selected_image_uri";
    public static SettingsModel builder;
    GalleryAdapter imageGalleryAdapter;
    TextView tv_title;
    Button btn_done;
    FrameLayout selected_photos_container_frame;
    HorizontalScrollView hsv_selected_photos;
    LinearLayout selected_photos_container;

    TextView selected_photos_empty;
    View contentView;
    ArrayList<Uri> selectedUriList;
    private Uri cameraImageUri;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractArgument();
        setupSavedInstanceState(savedInstanceState);
    }

    private void setupSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            cameraImageUri = builder.selectedUri;
            selectedUriList = builder.selectedUriList;
        } else {
            cameraImageUri = savedInstanceState.getParcelable(EXTRA_CAMERA_IMAGE_URI);
            selectedUriList = savedInstanceState.getParcelableArrayList(EXTRA_CAMERA_SELECTED_IMAGE_URI);
        }
    }

    void extractArgument(){
        if(getArguments() != null && getArguments().containsKey(SettingsModel.BUILDER_KEY)) {
            builder = getArguments().getParcelable(SettingsModel.BUILDER_KEY);
        }
        else {
            builder = new SettingsModel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EXTRA_CAMERA_IMAGE_URI, cameraImageUri);
        outState.putParcelableArrayList(EXTRA_CAMERA_SELECTED_IMAGE_URI, selectedUriList);
        outState.putParcelable(SettingsModel.BUILDER_KEY, builder);
        super.onSaveInstanceState(outState);
    }

    public void show(FragmentManager fragmentManager) {

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, getTag());
        ft.commitAllowingStateLoss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(View contentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(contentView, savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.tedbottompicker_content_view, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            if (builder != null && builder.peekHeight > 0) {
                ((BottomSheetBehavior) behavior).setPeekHeight(builder.peekHeight);
            }
        }

        initView(contentView);

        setRecyclerView();
        setSelectionView();

        setTitle();
        setDoneButton();

        if(builder.isMultiSelect){
            for (Uri uri : selectedUriList){
                addUrlToGallery(uri);
            }
        }
    }

    private void setTitle(){
        if(!TextUtils.isEmpty(builder.title)){
            tv_title.setText(builder.title);
            if (builder.titleBackgroundResId > 0) {
                tv_title.setBackgroundResource(builder.titleBackgroundResId);
            }
        }
        else {
            tv_title.setVisibility(View.GONE);
        }
    }

    private void setSelectionView() {
        if (builder.emptySelectionText != null) {
            selected_photos_empty.setText(builder.emptySelectionText);
        }
    }

    private void setDoneButton() {
        if(builder.isMultiSelect) {
            selected_photos_container_frame.setVisibility(View.VISIBLE);
            if (builder.completeButtonText != null) {
                btn_done.setText(builder.completeButtonText);
            }

            btn_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMultiSelectComplete();
                }
            });
        }
        else {
            btn_done.setVisibility(View.GONE);
            selected_photos_container_frame.setVisibility(View.GONE);
        }
    }

    private void onMultiSelectComplete() {

        if (selectedUriList.size() < builder.selectMinCount) {
            String message;
            if (builder.selectMinCountErrorText != null) {
                message = builder.selectMinCountErrorText;
            } else {
                message = String.format(getResources().getString(R.string.select_min_count), builder.selectMinCount);
            }

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        onImagesSelected(selectedUriList);
        dismissAllowingStateLoss();
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
        rc_gallery.addItemDecoration(new GridSpacingItemDecoration(gridLayoutManager.getSpanCount(), builder.spacing, builder.includeEdgeSpacing));
        updateAdapter();
    }

    private void updateAdapter() {

        imageGalleryAdapter = new GalleryAdapter(getActivity(), builder);
        rc_gallery.setAdapter(imageGalleryAdapter);
        imageGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                GalleryAdapter.PickerTile pickerTile = imageGalleryAdapter.getItem(position);

                switch (pickerTile.getTileType()) {
                    case GalleryAdapter.PickerTile.CAMERA:
                        startCameraIntent();
                        break;
                    case GalleryAdapter.PickerTile.GALLERY:
                        startGalleryIntent();
                        break;
                    case GalleryAdapter.PickerTile.IMAGE:
                        complete(pickerTile.getImageUri());

                        break;

                    default:
                        errorMessage();
                }

            }
        });
    }

    private void complete(final Uri uri) {
        Log.d(TAG, "selected uri: " + uri.toString());
        //uri = Uri.parse(uri.toString());
        if (builder.isMultiSelect) {
            if (selectedUriList.contains(uri)) {
                removeImage(uri);
            } else {
                addUri(uri);
            }
        } else {
            onImageSelected(uri);
        }
    }

    private boolean addUri(final Uri uri) {
        if (selectedUriList.size() == builder.selectMaxCount) {
            String message;
            if (builder.selectMaxCountErrorText != null) {
                message = builder.selectMaxCountErrorText;
            } else {
                message = String.format(getResources().getString(R.string.select_max_count), builder.selectMaxCount);
            }

            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return false;
        }

        selectedUriList.add(uri);
        addUrlToGallery(uri);
        return true;
    }

    private void addUrlToGallery(final Uri uri){
        final View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.tedbottompicker_selected_item, null);
        ImageView thumbnail = (ImageView) rootView.findViewById(R.id.selected_photo);
        ImageView iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
        rootView.setTag(uri);

        selected_photos_container.addView(rootView);

        int px = (int) getResources().getDimension(R.dimen.tedbottompicker_selected_image_height);
        thumbnail.setLayoutParams(new FrameLayout.LayoutParams(px, px));

        if (builder.imageProvider == null) {
            Glide.with(getActivity())
                    .load(uri)
                    .thumbnail(0.1f)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.ic_gallery)
                            .error(R.drawable.img_error))
                    .into(thumbnail);
        } else {
            builder.imageProvider.onProvideImage(thumbnail, uri);
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage(uri);

            }
        });
        updateGallery(uri);
    }

    private void removeImage(Uri uri) {
        selectedUriList.remove(uri);
        for (int i = 0; i < selected_photos_container.getChildCount(); i++) {
            View childView = selected_photos_container.getChildAt(i);
            if (childView.getTag().equals(uri)) {
                selected_photos_container.removeViewAt(i);
                break;
            }
        }
        updateGallery(uri);
    }

    private void updateGallery(Uri uri){
        updateSelectedView();
        imageGalleryAdapter.setSelectedUriList(selectedUriList, uri);
    }

    private void updateSelectedView() {
        if (selectedUriList == null || selectedUriList.size() == 0) {
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

        if (builder.mediaType == SettingsModel.MediaType.IMAGE) {
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
                            onActivityResultCamera(cameraImageUri);
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
            cameraImageUri = Uri.fromFile(imageFile);
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
            cameraImageUri = Uri.fromFile(videoFile);
        } catch (IOException e) {
            e.printStackTrace();
            onError("Could not create imageFile for camera");
        }
        return videoFile;
    }

    private void startGalleryIntent() {
        Intent galleryIntent;
        Uri uri;
        if (builder.mediaType == SettingsModel.MediaType.IMAGE) {
            galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
        } else {
            galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("video/*");

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

    private void errorMessage() {
        onError("Error");
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
            errorMessage();
        }

        String realPath = RealPathUtil.getRealPath(getActivity(), temp);

        Uri selectedImageUri = null;
        try {
            selectedImageUri = Uri.fromFile(new File(realPath));
        } catch (Exception ex) {
            selectedImageUri = Uri.parse(realPath);
        }
        complete(selectedImageUri);
    }

    public void onImageSelected(Uri uri) {
        Intent intent = new Intent();
        intent.putExtra(SettingsModel.URI_KEY, uri);
        returnResultIntent(intent);
    }

    public void onImagesSelected(ArrayList<Uri> uriList) {
        Intent intent = new Intent();
        intent.putExtra(SettingsModel.URI_LIST_KEY, uriList);
        returnResultIntent(intent);
    }

    private void returnResultIntent(Intent intent){
        Fragment fragment = getTargetFragment();
        if(fragment != null){
            fragment.onActivityResult(SettingsModel.REQUEST_CODE, Activity.RESULT_OK, intent);
        }
        else  {
            if(getActivity() instanceof TedBottomPickerResult){
                ((TedBottomPickerResult) getActivity()).onTedBottomPickerResult(SettingsModel.REQUEST_CODE, Activity.RESULT_OK, intent);
            }
            else {
                throw new RuntimeException("Activity not implement onActivityResult");
            }
        }
        onActivityResult(SettingsModel.REQUEST_CODE, Activity.RESULT_OK, intent);
        dismissAllowingStateLoss();
    }

    public void onError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface ImageProvider {
        void onProvideImage(ImageView imageView, Uri imageUri);
    }

    public interface TedBottomPickerResult {
        void onTedBottomPickerResult(int requestCode, int resultCode, Intent data);
    }
}