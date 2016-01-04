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

        mFacade.selectAll();
    }

    /**
     * 선언한 메뉴들을 액션바에 추가하는 메소드 입니다.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) menuItem.getActionView();

        // 서치뷰에 이용하는 리스너
        mSearchView.setOnQueryTextListener(this);

        return true;
    }

    /**
     * 액션바에 위치한 메뉴들이 클릭되었을 때 실행되는 메소드 입니다.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_go_home:
                Uri uri = Uri.parse("http://bukchon.seoul.go.kr/index.jsp");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 서치뷰에 무언가를 입력하고 엔터 버튼을 눌렀을 때 동작하는 리스너
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        System.out.println("onQueryTextSubmit : " + query);
        return false;
    }

    /**
     * 서치뷰에 무언가를 입력할 때마다 동작하는 리스너
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        System.out.println("onQueryTextChange : " + newText);
        return false;
    }



}
