package com.example.custommapview;

public class GraphData {

	public static final int BLUE_POINT = 1;
	public static final int RED_POINT = 2;

	private float x;
	private float y;
	private int type;

	public GraphData(float x, float y) {
		super();
		this.x = x;
		this.y = y;
		type = BLUE_POINT;
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
