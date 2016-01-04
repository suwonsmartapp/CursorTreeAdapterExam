package com.massivcode.hanokexam;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

import com.massivcode.hanokexam.database.ExamDbContract;
import com.massivcode.hanokexam.database.ExamDbFacade;

/**
 * 커서트리어댑터는 ExpandableListView 에서 사용되는 어댑터 입니다.
 *
 * 기존에 사용해봤던 BaseExpandableListAdapter 와는 다르게 데이터를 Cursor 객체를 이용합니다.
 */
public class CustomCursorTreeAdapter extends CursorTreeAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ExamDbFacade mFacade;

    public CustomCursorTreeAdapter(Cursor cursor, Context context) {
        super(cursor, context);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mFacade = new ExamDbFacade(context);
    }

    /**
     * 자식 커서를 얻어올 때 자동으로 호출되는 메소드 입니다.
     * bindChildView 메소드 내부에서 자동으로 호출됩니다.
     * @param groupCursor
     * @return
     */
    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {

        long id = groupCursor.getLong(groupCursor.getColumnIndexOrThrow(ExamDbContract.ExamDbEntry._ID));

        return mFacade.getChildCursor(id);
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        View view = mInflater.inflate(R.layout.item_group, parent, false);
        holder.groupTextView = (TextView) view.findViewById(R.id.item_group_tv);
        view.setTag(holder);


        return view;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String groupString = cursor.getString(cursor.getColumnIndexOrThrow(ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA_GROUP));
        holder.groupTextView.setText(groupString);
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        View view = mInflater.inflate(R.layout.item_child, parent, false);
        holder.childTextView = (TextView) view.findViewById(R.id.item_child_tv);
        view.setTag(holder);


        return view;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String childString = cursor.getString(cursor.getColumnIndexOrThrow(ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA_CHILD));
        holder.childTextView.setText(childString);
    }


    static class ViewHolder {
        TextView groupTextView;
        TextView childTextView;
    }

}
