package com.example.custommapview;

public class ShopsData implements GraphDataInterface {

	private float x;
	private float y;
	private String location;

	public ShopsData(float x, float y) {
		this.x = x;
		this.y = y;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
