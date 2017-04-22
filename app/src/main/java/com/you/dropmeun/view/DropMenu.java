package com.you.dropmeun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.you.dropmeun.R;

import java.util.List;

/**
 * Created by youxuan on 2017/4/22 0022.
 * <p>
 * 自定义LinearLayout
 */

public class DropMenu extends LinearLayout {

    //顶部菜单布局
    private LinearLayout tabMenuView;
    //底部容器
    private FrameLayout containerView;
    //弹出菜单布局
    private FrameLayout popupMenuViews;
    //遮罩半透明View.
    private View maskView;

    //tabMenuView里面的tab位置 -1表示未选中
    private int current_tab_postion = -1;

    //分割线颜色
    private int dividerColor = 0xffcccccc;

    //tab选中颜色
    private int textSelectedColor = 0xff890c85;
    //tab未选中颜色
    private int textUnSelectedColor = 0xff111111;

    //遮罩颜色
    private int maskColor = 0x88888888;

    //tab字体大小
    private int menuTextSize = 14;

    //tab选中图标
    private int menuSelectedIcon;
    //tab未选中图标
    private int menuUnSelectedIcon;

    private float menuHeightPercent = 0.5f;

    public DropMenu(Context context) {
        super(context, null);
    }

    public DropMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        //未DropMenu添加自定义数据
        int menuBackgroundColor = 0xfffffff;
        int underlineColor = 0xffccccc;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropMenu);
        underlineColor = a.getColor(R.styleable.DropMenu_underLineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.DropMenu_dividerColor, dividerColor);
        textSelectedColor = a.getColor(R.styleable.DropMenu_textSelectedColor, textSelectedColor);
        textUnSelectedColor = a.getColor(R.styleable.DropMenu_textUnSelectedColor, textUnSelectedColor);
        menuBackgroundColor = a.getColor(R.styleable.DropMenu_menuBackgroundColor, menuBackgroundColor);
        maskColor = a.getColor(R.styleable.DropMenu_maskColor, maskColor);
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropMenu_menuTextSize, menuTextSize);
        menuSelectedIcon = a.getResourceId(R.styleable.DropMenu_menuSelectedIcon, menuSelectedIcon);
        menuUnSelectedIcon = a.getResourceId(R.styleable.DropMenu_menuUnSelectedIcon, menuUnSelectedIcon);
        menuHeightPercent = a.getFloat(R.styleable.DropMenu_menuHeightPercent, menuHeightPercent);

        a.recycle();

        //初始化tabMenuView.并添加到TabMenuView上
        tabMenuView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(params);
        addView(tabMenuView, 0);

        //tabMenuView添加下划线
        View underLine = new View(context);
        underLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx(1.0f)));
        underLine.setBackgroundColor(underlineColor);
        addView(underLine, 1);

        //初始化containerView,并将其添加到DropDownMenu
        containerView = new FrameLayout(context);
        containerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(containerView, 2);
    }


    public int dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }

    public void setDropMenu(@NonNull List<String> tabText, @NonNull List<View> popupViews, @NonNull View contentView) {
        if (tabText.size() != popupViews.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal popupViews.size()");

        }

        for (int i = 0; i < tabText.size(); i++) {
            addTab(tabText, i);
        }

        containerView.addView(contentView, 0);
        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        containerView.addView(maskView, 1);
        maskView.setVisibility(GONE);

        if (containerView.getChildAt(2) != null) {
            containerView.removeViewAt(2);
        }

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (DeviceUtils.getScreenSize(getContext()).y * menuHeightPercent)));
        popupMenuViews.setVisibility(GONE);
        containerView.addView(popupMenuViews, 2);
        for (int i = 0; i < popupViews.size(); i++) {
            popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            popupMenuViews.addView(popupViews.get(i), i);
        }
    }

    private void addTab(List<String> tabText, int i) {

        final TextView tab = new TextView(getContext());
        tab.setSingleLine();
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        tab.setTextColor(textUnSelectedColor);

        Log.d("gssdg", "addTab: "+menuUnSelectedIcon);
        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnSelectedIcon), null);
        tab.setText(tabText.get(i));
        tab.setPadding(dpTpPx(5), dpTpPx(12), dpTpPx(5), dpTpPx(12));
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tab);
            }
        });
        tabMenuView.addView(tab);
        //添加分割线
        if (i < tabText.size() - 1) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(dpTpPx(0.5f), ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(dividerColor);
            tabMenuView.addView(view);
        }
    }

    private void switchMenu(View target) {
        System.out.println(current_tab_postion);

        for (int i = 0; i < tabMenuView.getChildCount(); i++,i++) {
            if (target == tabMenuView.getChildAt(i)) {
                if (current_tab_postion == i) {
                    closeMenu();
                } else {
                    if (current_tab_postion == -1) {
                        popupMenuViews.setVisibility(VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        popupMenuViews.getChildAt(i / 2).setVisibility(VISIBLE);

                    } else {
                        popupMenuViews.getChildAt(i / 2).setVisibility(VISIBLE);
                    }

                    current_tab_postion = i;
                    ((TextView) (tabMenuView.getChildAt(i))).setTextColor(textSelectedColor);
                    ((TextView) (tabMenuView.getChildAt(i))).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            getResources().getDrawable(menuSelectedIcon), null);
                }
            } else {
                ((TextView) tabMenuView.getChildAt(i)).setTextColor(textUnSelectedColor);
                ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(menuUnSelectedIcon), null);
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(String text) {
        if (current_tab_postion != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_postion)).setText(text);
        }
    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            tabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    public void closeMenu() {
        if (current_tab_postion != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_postion)).setTextColor(textUnSelectedColor);
            ((TextView) tabMenuView.getChildAt(current_tab_postion)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(menuUnSelectedIcon), null);
            popupMenuViews.setVisibility(GONE);
            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
            maskView.setVisibility(GONE);
            maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
            current_tab_postion = -1;
        }
    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_postion != -1;
    }

}
