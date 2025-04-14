package com.project.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cart {
	
	private List<CartItem> items =  new ArrayList<>();

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}
	
	public void addItem(CartItem item) {
		items.add(item);
	}
	
	public void clear() {
		items.clear();
	}

	public void removeItem(String bookId){
		Iterator<CartItem> iterator = items.iterator();
		while(iterator.hasNext()){
			CartItem item = iterator.next();
			if(item.getBookId().equals(bookId)){
				iterator.remove();
				break;
			}
		}
	}
}
 