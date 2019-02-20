package gun0912.tedbottompickerdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import gun0912.tedbottompicker.ImageLoader;
import gun0912.tedbottompicker.TedBottomPicker;

public class MainActivity extends AppCompatActivity implements TedBottomPicker.OnImageSelectedListener, TedBottomPicker.OnMultiImageSelectedListener {
    ImageView iv_image;
    ArrayList<Uri> selectedUriList;
    Uri selectedUri;
    private ViewGroup mSelectedImagesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_image = (ImageView) findViewById(R.id.iv_image);
        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);
        setSingleShowButton();
        setMultiShowButton();
    }

    private void setSingleShowButton() {
        Button btn_single_show = (Button) findViewById(R.id.btn_single_show);
        btn_single_show.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TedBottomPicker bottomSheetDialogFragment = TedBottomPicker.Builder()
                                //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                                .setSelectedUri(selectedUri)
                                //.showVideoMedia()
                                .setPeekHeight(1200)
                                .create(MainActivity.this);

                        bottomSheetDialogFragment.show(getSupportFragmentManager());
                    }
                }
        );
    }

    private void setMultiShowButton() {

        Button btn_multi_show = (Button) findViewById(R.id.btn_multi_show);
        btn_multi_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedBottomPicker bottomSheetDialogFragment = TedBottomPicker.Builder()
                        .setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2)
//                                .setPeekHeight(1600)
                        .setCompleteButtonText(R.string.btn_done)
                        .setEmptySelectionText("No Select")
                        .setSelectedUriList(selectedUriList)
                        .createMultiSelect(MainActivity.this);

                bottomSheetDialogFragment.show(getSupportFragmentManager());
            }
        });
    }

    public void onImageSelected(final Uri uri, String tag) {
        Log.d("ted", "uri: " + uri);
        if (uri != null) {
            Log.d("ted", "uri.getPath(): " + uri.getPath());
            selectedUri = uri;

            iv_image.setVisibility(View.VISIBLE);
            mSelectedImagesContainer.setVisibility(View.GONE);

            ImageLoader.loadImageInto(this, uri, iv_image);
        }
    }

    public void onImagesSelected(ArrayList<Uri> uriList, String tag) {
        // Remove all views before
        // adding the new ones.
        selectedUriList = uriList;
        mSelectedImagesContainer.removeAllViews();

        iv_image.setVisibility(View.GONE);
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        for (Uri uri : uriList) {
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);

            ImageLoader.loadImageInto(this, uri, thumbnail);
            mSelectedImagesContainer.addView(imageHolder);
            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));
        }
    }
}