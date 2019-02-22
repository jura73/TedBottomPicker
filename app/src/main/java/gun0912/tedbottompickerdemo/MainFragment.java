package gun0912.tedbottompickerdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import gun0912.tedbottompicker.ImageLoader;
import gun0912.tedbottompicker.TedBottomPicker;

public class MainFragment extends Fragment implements TedBottomPicker.OnImageSelectedListener {

    ImageView iv_fragment_image;
    ImageView iv_fragment_image_2;
    static final String IMAGE_2_TAG = "IMAGE_2_TAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv_fragment_image = (ImageView) view.findViewById(R.id.iv_fragment_image);
        iv_fragment_image_2 = (ImageView) view.findViewById(R.id.iv_fragment_image_2);
        Button btn_single_show = (Button) view.findViewById(R.id.btn_fragment_single_show);
        btn_single_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedBottomPicker bottomSheetDialogFragment = TedBottomPicker.Builder().create(MainFragment.this);
                bottomSheetDialogFragment.show(getFragmentManager());
            }
        });

        Button btn_fragment_single_show_2 = (Button) view.findViewById(R.id.btn_fragment_single_show_2);
        btn_fragment_single_show_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedBottomPicker bottomSheetDialogFragment = TedBottomPicker.Builder()
                        .setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                        .setTag(IMAGE_2_TAG)
                        .create(MainFragment.this);
                bottomSheetDialogFragment.show(getFragmentManager());
            }
        });

    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onImageSelected(Uri uri, String tag) {
        if(IMAGE_2_TAG.equals(tag)){
            ImageLoader.loadImageInto(getActivity(), uri, iv_fragment_image_2);
        }
        else {
            ImageLoader.loadImageInto(getActivity(), uri, iv_fragment_image);
        }
    }
}