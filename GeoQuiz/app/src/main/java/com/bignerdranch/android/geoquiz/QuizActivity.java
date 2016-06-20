package com.bignerdranch.android.geoquiz;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bignerdranch.android.geoquiz.databinding.ActivityQuizBinding;

public class QuizActivity extends AppCompatActivity {
	private static final String TAG = "QuizActivity";
	private static final String QUESTION_INDEX_KEY = "QUESTION_INDEX_KEY";

	Button falseButton;

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
		setContentView(R.layout.activity_quiz);
		falseButton = (Button) findViewById(R.id.false_button);

		binding = (ActivityQuizBinding) DataBindingUtil.setContentView(this, R.layout.activity_quiz);
		binding.setQuestion(questions[currentQuestion]);
		binding.setHandler(this);
	}

	public void onAnswerClick(View button) {
		boolean correct =
				(button == falseButton ^ questions[currentQuestion].isAnswerTrue());

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
		binding.setQuestion(questions[currentQuestion]);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart() called");
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.d(TAG, "onStart() called");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume() called");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop() called");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() called");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(QUESTION_INDEX_KEY, currentQuestion);
	}
}
