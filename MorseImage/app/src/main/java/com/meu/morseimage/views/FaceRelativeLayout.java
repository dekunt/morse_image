package com.meu.morseimage.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.meu.morseimage.R;
import com.meu.morseimage.utils.FaceContentUtil;
import com.meu.rubberindicator.RubberIndicator;


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
	private RubberIndicator pagerIndicator;

	/** 表情页界面集合 */
	private ArrayList<View> pageViews;

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
	protected void onFinishInflate()
    {
		super.onFinishInflate();
		facesList = FaceContentUtil.getFaceLists(8);
		onCreate();
	}

	private void onCreate()
    {
		Init_View();
		Init_viewPager();
		Init_Point();
		Init_Data();
	}

	/**
	 * 初始化控件
	 */
	private void Init_View()
    {
		vp_face = (ViewPager) findViewById(R.id.vp_contains);
		pagerIndicator = (RubberIndicator) findViewById(R.id.pager_indicator);
	}


	/**
	 * 初始化显示表情的viewpager
	 */
	private void Init_viewPager()
    {
		pageViews = new ArrayList<>();
		// 左侧添加空页
		View nullView1 = new View(context);
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
		 nullView2.setBackgroundColor(Color.TRANSPARENT);
		 pageViews.add(nullView2);
	}

	/**
	 * 初始化游标
	 */
	private void Init_Point()
    {
        pagerIndicator.setCount(pageViews.size() - 2);
	}

	/**
	 * 填充数据
	 */
	private void Init_Data()
    {
		vp_face.setAdapter(new ViewPagerAdapter(pageViews));
		vp_face.setCurrentItem(1);
		vp_face.addOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int arg0)
            {
                // 如果是第一屏或者是最后一屏禁止滑动
                if (arg0 == pageViews.size() - 1 || arg0 == 0)
                    vp_face.setCurrentItem(arg0 == 0 ? arg0 + 1 : arg0 - 1);
                else
                    pagerIndicator.setCurrentPosition(arg0 - 1);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
	}
}
