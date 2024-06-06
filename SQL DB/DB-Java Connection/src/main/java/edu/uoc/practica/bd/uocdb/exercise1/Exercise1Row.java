package edu.uoc.practica.bd.uocdb.exercise1;

public class Exercise1Row {

	private String iban;
	private String name_owner;
	private String date;
	private String name_dog;
	private String name_vaccine;
	private double price;
	private String comments;
	
	
	public Exercise1Row(String iban, String name_owner, String date, String name_dog, String name_vaccine,
			double price, String comments) {
		super();
		this.iban = iban;
		this.name_owner = name_owner;
		this.date = date;
		this.name_dog = name_dog;
		this.name_vaccine = name_vaccine;
		this.price = price;
		this.comments = comments;
	}
	public String get_iban() {
		return iban;
	}
	public void set_iban(String iban) {
		this.iban = iban;
	}
	public String get_name_owner() {
		return name_owner;
	}
	public void set_name_owner(String name_owner) {
		this.name_owner = name_owner;
	}
	public String get_date() {
		return date;
	}
	public void set_date(String date) {
		this.date = date;
	}
	public String get_name_dog() {
		return name_dog;
	}
	public void set_name_dog(String name_dog) {
		this.name_dog = name_dog;
	}
	public String get_name_vaccine() {
		return name_vaccine;
	}
	public void set_name_vaccine(String name_vaccine) {
		this.name_vaccine = name_vaccine;
	}
	public double get_price() {
		return price;
	}
	public void set_price(double price) {
		this.price = price;
	}
	public String get_comments() {
		return comments;
	}
	public void set_comments(String comments) {
		this.comments = comments;
	}



	


	
}
