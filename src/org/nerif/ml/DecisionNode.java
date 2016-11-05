package org.nerif.ml;

import java.util.HashMap;

public class DecisionNode {
	public int col;
	public String value;
	public HashMap<String, Integer> results;
	public DecisionNode tb;
	public DecisionNode fb;

	public void print() {
		printNode("");
	}

	private void printNode(String ident) {
		if (results != null) {
			System.out.println(ident + results);
		} else {
			System.out.println(col + ":" + value + "? ");
			if (tb != null) {
				System.out.println(ident + "T->");
				tb.printNode(ident + "  ");
			}
			if (fb != null) {
				System.out.println(ident + "F->");
				fb.printNode(ident + "  ");
			}
		}

	}
}
