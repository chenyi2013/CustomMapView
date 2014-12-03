package com.example.custommapview;

public class PublicFacilityData implements GraphDataInterface {

	private float x;
	private float y;
	private int type;

	/**
	 * �̳����
	 */
	public static final int MALLENTRANCE = 1;
	/**
	 * ����
	 */
	public static final int ELEVATOR = 2;
	/**
	 * ����
	 */
	public static final int ESCALATOR = 3;
	/**
	 * ����
	 */
	public static final int STAIRWAY = 4;
	/**
	 * ϴ�ּ�
	 */
	public static final int TOILET = 5;
	/**
	 * ����
	 */
	public static final int SUBWAY = 6;
	/**
	 * ����̨
	 */
	public static final int CASHIER = 7;
	/**
	 * ��������
	 */
	public static final int MY_LOCATION = 8;

	public PublicFacilityData() {
	}

	public PublicFacilityData(float x, float y, int type) {
		super();
		this.x = x;
		this.y = y;
		this.type = type;
	}

	@Override
	public void setX(float x) {

		this.x = x;

	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public void setY(float y) {

		this.y = y;

	}

	@Override
	public float getY() {
		return y;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
