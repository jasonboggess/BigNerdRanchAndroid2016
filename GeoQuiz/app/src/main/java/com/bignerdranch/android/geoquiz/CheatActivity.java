package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.bignerdranch.android.geoquiz.databinding.ActivityCheatBinding;

public class CheatActivity extends AppCompatActivity {

	private static final String CHEAT_QUESTION_TEXT_KEY = "com.bignerdranch.android.geoquiz.CheatActivity.CHEAT_QUESTION_TEXT_KEY";
	private static final String CHEAT_QUESTION_ANSWER_KEY = "com.bignerdranch.android.geoquiz.CheatActivity.CHEAT_QUESTION_ANSWER_KEY";
	public static final String CHEAT_SHOWN = "com.bignerdranch.android.geoquiz.CheatActivity.CHEAT_SHOWN";

	private ActivityCheatBinding binding;

	public ObservableBoolean isAnswerShowing = new ObservableBoolean();
	public ObservableField<String> buildVersion = new ObservableField<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = (ActivityCheatBinding) DataBindingUtil.setContentView(CheatActivity.this, R.layout.activity_cheat);
		binding.setActionHandler(CheatActivity.this);

		boolean answer = getIntent().getBooleanExtra(CHEAT_QUESTION_ANSWER_KEY, false);
		int questionTextId = getIntent().getIntExtra(CHEAT_QUESTION_TEXT_KEY, 0);
		Question question = new Question(questionTextId, answer);
		isAnswerShowing.set(false);

		binding.setQuestion(question);
		binding.setActionHandler(CheatActivity.this);

		buildVersion.set(String.format("API Level %d", Build.VERSION.SDK_INT));
	}

	public static Intent newIntent(Context packageContext, Question question) {
		Intent intent = new Intent(packageContext, CheatActivity.class);
		intent.putExtra(CHEAT_QUESTION_TEXT_KEY, question.getQuestionTextId());
		intent.putExtra(CHEAT_QUESTION_ANSWER_KEY, question.isAnswerTrue());

		return intent;
	}

	public void onShowAnswerButtonClick(View button) {
		isAnswerShowing.set(true);

		Intent data = new Intent();
		data.putExtra(CHEAT_SHOWN, isAnswerShowing.get());
		setResult(RESULT_OK, data);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int cx = binding.showAnswerButton.getWidth() / 2;
			int cy = binding.showAnswerButton.getHeight() / 2;
			float radius = binding.showAnswerButton.getWidth();
			Animator anim = ViewAnimationUtils.createCircularReveal(
					binding.showAnswerButton,
					cx,
					cy,
					radius,
					0
			);

			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					binding.showAnswerButton.setVisibility(View.INVISIBLE);
				}
			});

			anim.start();
		} else {
			binding.showAnswerButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public static boolean wasAnswerShown(Intent intent) {
		return intent.getBooleanExtra(CHEAT_SHOWN, false);
	}
}
