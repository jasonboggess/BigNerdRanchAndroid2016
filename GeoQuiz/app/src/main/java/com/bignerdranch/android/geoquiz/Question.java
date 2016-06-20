package com.bignerdranch.android.geoquiz;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by jboggess on 6/20/16.
 */
public class Question extends BaseObservable {

	private int questionTextId;
	private boolean answerTrue;

	public Question(int questionTextId, boolean answerTrue) {
		this.questionTextId = questionTextId;
		this.answerTrue = answerTrue;
	}

	@Bindable
	public int getQuestionTextId() {
		return questionTextId;
	}

	public void setQuestionTextId(int questionTextId) {
		this.questionTextId = questionTextId;
		notifyPropertyChanged(com.bignerdranch.android.geoquiz.BR.answerTrue);
	}

	@Bindable
	public boolean isAnswerTrue() {
		return answerTrue;
	}

	public void setAnswerTrue(boolean answerTrue) {
		this.answerTrue = answerTrue;
		notifyPropertyChanged(com.bignerdranch.android.geoquiz.BR.questionTextId);
	}
}
