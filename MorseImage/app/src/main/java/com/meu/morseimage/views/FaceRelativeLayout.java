package com.meu.morseimage.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meu.morseimage.R;
import com.meu.morseimage.utils.FaceContentUtil;


public class FaceRelativeLayout extends RelativeLayout implements OnItemClickListener
{

	/** 表情的点击事件 */
    public interface OnFaceClickedListener {
        void onFaceClick(int resId);
    }

	private OnFaceClickedListener mOnFaceClickedListener;

	public void setOnFaceClickedListener(OnFaceClickedListener listener) {
		this.mOnFaceClickedListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position,
							long arg3) {
		if (mOnFaceClickedListener != null) {
			mOnFaceClickedListener.onFaceClick(facesList.get(vp_face.getCurrentItem() - 1).get(position));
		}
	}




	private Context context;

	/** 显示表情页的viewpager */
	private ViewPager vp_face;

	/** 游标显示布局 */
	private LinearLayout layout_point;

	/** 表情页界面集合 */
	private ArrayList<View> pageViews;

	/** 游标点集合 */
	private ArrayList<ImageView> pointViews;

	/** 表情集合 */
	private List<List<Integer>> facesList;


	public FaceRelativeLayout(Context context) {
		super(context);
        init(context);
	}

	public FaceRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context);
    }

    public FaceRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
    }

    private void init(Context context) {
		this.context = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		facesList = FaceContentUtil.getFaceLists(8);
		onCreate();
	}

	private void onCreate() {
		Init_View();
		Init_viewPager();
		Init_Point();
		Init_Data();
	}

	/**
	 * 初始化控件
	 */
	private void Init_View() {

		vp_face = (ViewPager) findViewById(R.id.vp_contains);
		layout_point = (LinearLayout) findViewById(R.id.iv_image);
	}


	/**
	 * 初始化显示表情的viewpager
	 */
	private void Init_viewPager() {
		pageViews = new ArrayList<>();
		// 左侧添加空页
		View nullView1 = new View(context);
		// 设置透明背景
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(nullView1);

		// 中间添加表情页
		for (int i = 0; i < facesList.size(); i++) {

			GridView view = (GridView)inflate(context, R.layout.view_face_grid, null);
			FaceAdapter adapter = new FaceAdapter(context, facesList.get(i));
			view.setAdapter(adapter);
			view.setOnItemClickListener(this);
			pageViews.add(view);
		}

		 // 右侧添加空页面
		 View nullView2 = new View(context);
		 // 设置透明背景
		 nullView2.setBackgroundColor(Color.TRANSPARENT);
		 pageViews.add(nullView2);
	}

	/**
	 * 初始化游标
	 */
	private void Init_Point() {

		layout_point.removeAllViews();
		pointViews = new ArrayList<>();
		ImageView imageView;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(context);
			imageView.setBackgroundResource(R.mipmap.write_huidic);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 4;
			layoutParams.rightMargin = 4;
			// layoutParams.width = 8;
			// layoutParams.height = 8;
			layout_point.addView(imageView, layoutParams);
			if (i == 0 || i == pageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.mipmap.write_greendic);
			}
			pointViews.add(imageView);
		}
	}

	/**
	 * 填充数据
	 */
	private void Init_Data() {
		vp_face.setAdapter(new ViewPagerAdapter(pageViews));

		vp_face.setCurrentItem(1);
		vp_face.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 描绘分页点
				draw_Point(arg0);
				// 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
				if (arg0 == pointViews.size() - 1 || arg0 == 0) {
					if (arg0 == 0) {
						vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
						pointViews.get(1).setBackgroundResource(
								R.mipmap.write_greendic);
					} else {
						vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
						pointViews.get(arg0 - 1).setBackgroundResource(
								R.mipmap.write_greendic);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	/**
	 * 绘制游标背景
	 */
	public void draw_Point(int index) {
		for (int i = 1; i < pointViews.size(); i++) {
			if (index == i) {
				pointViews.get(i).setBackgroundResource(
						R.mipmap.write_greendic);
			} else {
				pointViews.get(i)
						.setBackgroundResource(R.mipmap.write_huidic);
			}
		}
	}
}
