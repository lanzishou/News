package com.t.news;


public class Data {
	public String uniquekey;
	public String title;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Data data = (Data) o;

		return uniquekey != null ? uniquekey.equals(data.uniquekey) : data.uniquekey == null;

	}

	public String date;
	public String category;
	public String author_name;
	public String url;
	public String thumbnail_pic_s;
}
