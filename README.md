# CursorTreeAdapter
CursorTreeAdapter 예제 입니다. 
기존의 DatabaseExam 예제에서 몇개를 추가했습니다.

# 1. DB 구조 : Contract 클래스
```java
package com.massivcode.hanokexam.database;

import android.provider.BaseColumns;

/**
 * 생성할 db 테이블의 이름과 데이터 구조에 대한 명세서 같은 클래스 입니다.
 * 이너 클래스인 엔트리 클래스에 테이블의 이름과 컬럼명을 선언해놓습니다.
 */
public class ExamDbContract {

    /**
     * 아래의 엔트리 클래스가 구현하는 BaseColumns 는 모든 테이블이 기본적으로 구현해야 하는
     * 식별자인 id 값과 추후 데이터의 개수를 카운트하는데 사용하는 count 가 담겨있습니다.
     */
    public static class ExamDbEntry implements BaseColumns {
        public static final String TABLE_NAME = "ExamDb";
        public static final String COLUMN_NAME_DATA_GROUP = "group_data";
        public static final String COLUMN_NAME_DATA_CHILD = "child_data";
    }

}

```

# 2. Facade 클래스

```java
/**
     * 초기데이터를 db에 추가하기 위한 메서드입니다.
     * @param strings
     */
    public void insertAll(String... strings) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (String string : strings) {
            values.put(ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA_GROUP, "부모 " + string);
            values.put(ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA_CHILD, "자식 " + string);
        }

        db.insert(ExamDbContract.ExamDbEntry.TABLE_NAME, null, values);
    }

    /**
     * 커서트리어댑터 생성시에 사용되는 부모커서 데이터를 얻기위한 메소드 입니다.
     * @return
     */
    public Cursor getGroupCursor() {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        return db.query(ExamDbContract.ExamDbEntry.TABLE_NAME,
                new String[]{ExamDbContract.ExamDbEntry._ID, ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA_GROUP},
                null, null, null, null, null
        );
    }

    /**
     * 커서트리어댑터에서 자식커서 데이터를 얻기위한 메소드 입니다.
     * @param id
     * @return
     */
    public Cursor getChildCursor(long id) {
        SQLiteDatabase db = mHelper.getReadableDatabase();

        return db.query(ExamDbContract.ExamDbEntry.TABLE_NAME,
                new String[]{ExamDbContract.ExamDbEntry._ID,
                ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA_CHILD},
                ExamDbContract.ExamDbEntry._ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
    }

```

# 3. CursorTreeAdapter
```java
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


```

# 4. 데이터 준비 및 어댑터 설정
```java
package com.massivcode.hanokexam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.massivcode.hanokexam.database.ExamDbFacade;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView mSearchView;
    private ExpandableListView mListView;
    private CustomCursorTreeAdapter mAdapter;
    private ExamDbFacade mFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
    }

    /**
     * 초기 데이터를 설정하고 어댑터를 리스트 뷰에 세팅하는 메소드 입니다.
     */
    private void initData() {

        String[] datas = new String[100];

        for(int i = 0; i < 100; i++) {
            datas[i] = "데이터 : " + i;
        }

        mFacade = new ExamDbFacade(getApplicationContext());

        mFacade.insertAll(datas);

        mListView = (ExpandableListView) findViewById(R.id.listview);
        mAdapter = new CustomCursorTreeAdapter(mFacade.getGroupCursor(), getApplicationContext());
        mListView.setAdapter(mAdapter);
    }
}


```
