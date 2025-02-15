package it.univaq.swa.webmarket.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {

	private String name;

	private Category parentCategory;

	private List<String> features = new ArrayList<>();

	public Category(String name, List<String> features) {
		this.setName(name);
		this.setFeatures(features);
	}

	public Category(String name, Category parentCategory, List<String> features) {
		this.setName(name);
		this.setParentCategory(parentCategory);
		this.setFeatures(features);
	}

	public List<String> getFeatures() {

		if (!Objects.isNull(parentCategory)) {
			List<String> parentFeatures = parentCategory.getFeatures();
			this.features.addAll(parentFeatures);
		}

		return this.features;
	}
}
