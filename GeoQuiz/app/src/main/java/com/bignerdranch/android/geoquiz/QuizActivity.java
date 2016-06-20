package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bignerdranch.android.geoquiz.databinding.ActivityQuizBinding;

public class QuizActivity extends AppCompatActivity {
	private static final String TAG = "QuizActivity";
	private static final String QUESTION_INDEX_KEY = "QUESTION_INDEX_KEY";
	private static final int REQUEST_CODE_CHEAT = 255;

	private Question[] questions = new Question[]{
			new Question(R.string.question_oceans, true),
			new Question(R.string.question_mideast, false),
			new Question(R.string.question_africa, false),
			new Question(R.string.question_americas, true),
			new Question(R.string.question_asia, true),
	};

	private int currentQuestion = 0;
	private ActivityQuizBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			currentQuestion = savedInstanceState.getInt(QUESTION_INDEX_KEY, 0);
		}
		Log.d(TAG, "onCreate(Bundle) called");
		binding = (ActivityQuizBinding) DataBindingUtil.setContentView(this, R.layout.activity_quiz);
		binding.setQuestion(currentQuestion());
		binding.setHandler(this);
	}

	public void onAnswerClick(View button) {
		boolean correct =
				(button == binding.falseButton ^ currentQuestion().isAnswerTrue());

		Toast.makeText(this, correct ? R.string.correct_toast : R.string.incorrect_toast, Toast.LENGTH_SHORT).show();
	}

	public void onPreviousButtonClick(View button) {
		currentQuestion = (currentQuestion + questions.length - 1) % questions.length;
		updateQuestionInView();
	}

	public void onNextButtonClick(View button) {
		currentQuestion = (currentQuestion + 1) % questions.length;
		updateQuestionInView();
	}

	private void updateQuestionInView() {
		binding.setQuestion(currentQuestion());
	}

	private Question currentQuestion() {
		return questions[currentQuestion];
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(QUESTION_INDEX_KEY, currentQuestion);
	}

	public void onCheatClicked(View cheatView) {
		startActivityForResult(CheatActivity.newIntent(this, currentQuestion()), REQUEST_CODE_CHEAT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_CHEAT) {
			if (data == null) {
				return;
			}

			Toast.makeText(this, R.string.judgement_toast, Toast.LENGTH_SHORT).show();
		}
	}
}
