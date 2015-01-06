package com.example.erman.photomapnavigation.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.example.erman.photomapnavigation.R;
import com.example.erman.photomapnavigation.presenters.DisplayImagePresenter;
import com.example.erman.photomapnavigation.presenters.DisplayImagePresenterImpl;
import com.example.erman.photomapnavigation.presenters.Presenter;
import com.example.erman.photomapnavigation.views.DisplayImageView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class DisplayImageActivity extends ActionBarActivity implements DisplayImageView{

    private DisplayImagePresenter presenter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_image);

        progressDialog = new ProgressDialog(this);
        presenter = new DisplayImagePresenterImpl(this);

        Bundle bundle = getIntent().getExtras();

        presenter.downloadPhotos(bundle);
        presenter = new DisplayImagePresenterImpl(this);
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void alertNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_connection_title).setMessage(R.string.no_connection_message).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void displayPhotos(Bitmap[] sources) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter(sources);
        viewPager.setAdapter(adapter);
    }


    private class ImagePagerAdapter extends PagerAdapter {

        private Bitmap[] bitmaps;

        public ImagePagerAdapter(Bitmap[] bitmaps) {
            this.bitmaps = bitmaps;
        }

        @Override
        public int getCount() {

            return bitmaps.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = DisplayImageActivity.this;
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageBitmap(bitmaps[position]);
            container.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

}
