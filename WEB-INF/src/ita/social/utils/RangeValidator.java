package ita.social.utils;

import com.vaadin.data.validator.AbstractValidator;

@SuppressWarnings("rawtypes")
public class RangeValidator<T extends Comparable> {

	private T minValue = null;
	private boolean minValueIncluded = true;
	private T maxValue = null;
	private boolean maxValueIncluded = true;
	private Class<T> type;

	public RangeValidator(String errorMessage, Class<T> type, T minValue,
			T maxValue) {
		super();
		this.type = type;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public boolean isMinValueIncluded() {
		return minValueIncluded;
	}

	public void setMinValueIncluded(boolean minValueIncluded) {
		this.minValueIncluded = minValueIncluded;
	}

	public boolean isMaxValueIncluded() {
		return maxValueIncluded;
	}

	public void setMaxValueIncluded(boolean maxValueIncluded) {
		this.maxValueIncluded = maxValueIncluded;
	}

	public T getMinValue() {
		return minValue;
	}

	public void setMinValue(T minValue) {
		this.minValue = minValue;
	}

	public T getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(T maxValue) {
		this.maxValue = maxValue;
	}

	protected boolean isValidValue(T value) {
		if (value == null) {
			return true;
		}
		if (getMinValue() != null) {
			// Ensure that the min limit is ok
			int result = value.compareTo(getMinValue());
			if (result < 0) {
				// value less than min value
				return false;
			} else if (result == 0 && !isMinValueIncluded()) {
				// values equal and min value not included
				return false;
			}
		}
		if (getMaxValue() != null) {
			// Ensure that the Max limit is ok
			int result = value.compareTo(getMaxValue());
			if (result > 0) {
				// value greater than max value
				return false;
			} else if (result == 0 && !isMaxValueIncluded()) {
				// values equal and max value not included
				return false;
			}
		}
		return true;
	}

	public Class<T> getType() {
		return type;
	}

}
