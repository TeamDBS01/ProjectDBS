package com.project.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Setter
@Getter
public class Cart {
	
	private List<CartItem> items =  new ArrayList<>();

    public void addItem(CartItem item) {

		items.add(item);
	}
	
	public void clear() {

		items.clear();
	}

	public boolean removeItem(String bookId){
		Iterator<CartItem> iterator = items.iterator();
		while(iterator.hasNext()){
			CartItem item = iterator.next();
			if(item.getBookId().equals(bookId)){
				iterator.remove();
				return true;
			}
		}
		return false;
	}

}
 