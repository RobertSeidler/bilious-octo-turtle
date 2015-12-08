package de.rsp.wdntxml.tools;

import java.util.ArrayList;

import de.rsp.wdntxml.structure.Synset;


@SuppressWarnings("serial")
public class SynsetList extends ArrayList<Synset> {

	@Override
	public boolean contains(Object o) {

		for (Synset syn : this) {
			if(syn.getiD().equals((String)o)) return true;
		}
		
		return false;
	}
	
	@Override
	public int indexOf(Object o) {

		for (int i = 0; i < this.size(); i++) {
			if(this.get(i).getiD().equals((String)o)) return i; 
		}
		
		return -1;
	}
}
