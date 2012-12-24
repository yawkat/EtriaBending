package com.etriacraft.etriabending.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class ItemStackSerialize {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List serialize(ItemStack[] iss) {
		final ArrayList<Map<String, Object>> list = new ArrayList();
		for (int i = 0; i < iss.length; i++) {
			final ItemStack is = iss[i];
			if (is != null) list.add(is.serialize());
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ItemStack[] deserialize(List list) {
		final ItemStack[] iss = new ItemStack[list.size()];
		for (int i = 0; i < list.size(); i++) {
			iss [i] = ItemStack.deserialize((Map<String, Object>) list.get(i));
		}
		return iss;
	}

}