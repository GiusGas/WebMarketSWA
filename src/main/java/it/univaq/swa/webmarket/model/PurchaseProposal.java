package it.univaq.swa.webmarket.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseProposal {

	private String manufacturerName;

	private String productName;

	private String productCode;

	private Float price;

	private String url;

	private String notes;

	private String motivation;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
