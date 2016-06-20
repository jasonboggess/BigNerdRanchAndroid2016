package com.bignerdranch.android.geoquiz;

/**
 * Created by jboggess on 6/20/16.
 */
public class Question {

	private int questionTextId;
	private boolean answerTrue;

	public Question(int questionTextId, boolean answerTrue) {
		this.questionTextId = questionTextId;
		this.answerTrue = answerTrue;
	}

	public int getQuestionTextId() {
		return questionTextId;
	}

	public void setQuestionTextId(int questionTextId) {
		this.questionTextId = questionTextId;
	}

	public boolean isAnswerTrue() {
		return answerTrue;
	}

	public void setAnswerTrue(boolean answerTrue) {
		this.answerTrue = answerTrue;
	}
}
