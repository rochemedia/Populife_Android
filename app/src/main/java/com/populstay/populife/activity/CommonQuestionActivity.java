package com.populstay.populife.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.adapter.CommonQuestionAdapter;
import com.populstay.populife.base.BaseActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommonQuestionActivity extends BaseActivity {

	private ExpandableListView mExpandableListView;
	private CommonQuestionAdapter mAdapter;

	private List<String> mGroupNames = new ArrayList<>(); // 组元素数据列表（问题的组名）
	private Map<String, List<String>> mQuestionNames = new LinkedHashMap<>(); // 子元素数据列表（具体问题标题）

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_question);

		initView();
		initQuestionData();
		for (int k = 0; k < mGroupNames.size(); k++) {
			mExpandableListView.expandGroup(k);
		}
		mAdapter.notifyDataSetChanged();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.common_questions);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mExpandableListView = findViewById(R.id.eplv_common_question);

		mAdapter = new CommonQuestionAdapter(this, mGroupNames, mQuestionNames);
		mExpandableListView.setAdapter(mAdapter);

		mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView expandableListView, View view,
										int groupPosition, int childPosition, long id) {
				CommonQuestionDetailActivity.actionStart(CommonQuestionActivity.this,
						"" + groupPosition, "" + childPosition);
				return true;
			}
		});
	}

	private void initQuestionData() {
		mGroupNames.add(getString(R.string.common_question_group_lock));
		mGroupNames.add(getString(R.string.common_question_group_keyboard));
		mGroupNames.add(getString(R.string.common_question_group_password));
		mGroupNames.add(getString(R.string.common_question_group_app_unlocking));
		mGroupNames.add(getString(R.string.common_question_group_others));

		//Lock
		List<String> lockQuestions = new ArrayList<>();
		lockQuestions.add(getString(R.string.common_question_title_lock_0));
		lockQuestions.add(getString(R.string.common_question_title_lock_1));
		lockQuestions.add(getString(R.string.common_question_title_lock_2));
		lockQuestions.add(getString(R.string.common_question_title_lock_3));
		mQuestionNames.put(mGroupNames.get(0), lockQuestions);

		//Keyboard
		lockQuestions = new ArrayList<>();
		lockQuestions.add(getString(R.string.common_question_title_keyboard_0));
		lockQuestions.add(getString(R.string.common_question_title_keyboard_1));
		lockQuestions.add(getString(R.string.common_question_title_keyboard_2));
		mQuestionNames.put(mGroupNames.get(1), lockQuestions);

		//Password
		lockQuestions = new ArrayList<>();
		lockQuestions.add(getString(R.string.common_question_title_password_0));
		lockQuestions.add(getString(R.string.common_question_title_password_1));
		lockQuestions.add(getString(R.string.common_question_title_password_2));
		lockQuestions.add(getString(R.string.common_question_title_password_3));
		mQuestionNames.put(mGroupNames.get(2), lockQuestions);

		//App unlocking
		lockQuestions = new ArrayList<>();
		lockQuestions.add(getString(R.string.common_question_title_app_unlocking_0));
		lockQuestions.add(getString(R.string.common_question_title_app_unlocking_1));
		mQuestionNames.put(mGroupNames.get(3), lockQuestions);

		//Others
		lockQuestions = new ArrayList<>();
		lockQuestions.add(getString(R.string.common_question_title_others_0));
		lockQuestions.add(getString(R.string.common_question_title_others_1));
		mQuestionNames.put(mGroupNames.get(4), lockQuestions);
	}
}
