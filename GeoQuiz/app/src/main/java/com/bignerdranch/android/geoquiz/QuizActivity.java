package com.bignerdranch.android.geoquiz;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bignerdranch.android.geoquiz.databinding.ActivityQuizBinding;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizActivity extends AppCompatActivity {

	private static final String TAG = "QuizActivity";

	@BindView(R.id.true_button)
	Button trueButton;

	@BindView(R.id.false_button)
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
		Log.d(TAG, "onCreate(Bundle) called");
		setContentView(R.layout.activity_quiz);
		ButterKnife.bind(this);
		binding = (ActivityQuizBinding)DataBindingUtil.setContentView(this, R.layout.activity_quiz);
		binding.setQuestion(questions[0]);
	}

	@OnClick({R.id.true_button, R.id.false_button})
	public void onAnswerClick(Button button) {
		boolean correct =
				(button == falseButton ^ questions[currentQuestion].isAnswerTrue());

		Toast.makeText(this, correct ? R.string.correct_toast : R.string.incorrect_toast, Toast.LENGTH_SHORT).show();
	}

	@OnClick(R.id.previous_button)
	public void onPreviousButtonClick(Button button) {
		currentQuestion = (currentQuestion + questions.length - 1) % questions.length;
		updateQuestionInView();
	}

	@OnClick(R.id.next_button)
	public void onNextButtonClick(Button button) {
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
}
