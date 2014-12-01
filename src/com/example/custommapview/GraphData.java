package com.example.custommapview;

public class GraphData {

	public static final int SHOPS_POINT = 1;
	public static final int LIFT_POINT = 2;
	public static final int STAIR_POINT = 3;
	public static final int ELEVATOR_POINT = 4;
	public static final int WC_POINT = 5;

	private float x;
	private float y;
	private int type;

	public GraphData(float x, float y) {
		super();
		this.x = x;
		this.y = y;
		type = SHOPS_POINT;
	}

	public GraphData(float x, float y, int type) {
		super();
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public GraphData() {

	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
