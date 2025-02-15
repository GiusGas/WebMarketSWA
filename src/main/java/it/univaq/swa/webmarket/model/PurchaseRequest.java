package it.univaq.swa.webmarket.model;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseRequest {

	private Long id;

	private String category;

	private Map<String, String> requestedFeatures;

	private String notes;

	private User purchaser;

	private User technician;

	private PurchaseProposal purchaseProposal;

	private Status status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public PurchaseRequest(Long id, String notes) {
		this.id = id;
		this.notes = notes;
	}
}
