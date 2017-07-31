package com.example.topnewgrid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.topnewgrid.R;
import com.example.topnewgrid.bean.ChannelItem;

import java.util.List;

@SuppressWarnings("ALL")
public class OtherAdapter extends BaseAdapter {

    private Context context;
    private List<ChannelItem> channelList;
    private TextView item_text;
    private ImageView icon_new;

    /**
     * 是否可见
     */
    boolean isVisible = true;

    /**
     * 要删除的position
     */
    public int remove_position = -1;

    public OtherAdapter(Context context, List<ChannelItem> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public ChannelItem getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
        item_text = (TextView) view.findViewById(R.id.text_item);
        icon_new = (ImageView) view.findViewById(R.id.icon_new);
        icon_new.setVisibility(View.VISIBLE);
        ChannelItem channel = getItem(position);
        item_text.setText(channel.getName());
        if (!isVisible && (position == -1 + channelList.size())) {
            item_text.setText("");
        }
        if (remove_position == position) {
            item_text.setText("");
        }
        return view;
    }

    /**
     * 获取频道列表
     */
    public List<ChannelItem> getChannelLst() {
        return this.channelList;
    }

    /**
     * 添加频道列表
     */
    public void addItem(ChannelItem channel) {
        this.channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        this.remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        this.channelList.remove(remove_position);
        this.remove_position = -1;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<ChannelItem> list) {
        this.channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return this.isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}