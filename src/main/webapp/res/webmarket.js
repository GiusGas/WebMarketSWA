
let bearerToken = null;

$(document).ready(function() {

	$('#loginForm').on('submit', function(event) {
		event.preventDefault();
		const username = $('#username').val();
		const password = $('#password').val();

		$.ajax({
			url: 'http://localhost:8080/WebMarket/rest/auth/login',
			method: 'POST',
			contentType: 'application/x-www-form-urlencoded',
			data: $.param({ username, password }),
			success: function(response) {
				bearerToken = response;
				alert('Login effettuato con successo!');
				$('#loginForm')[0].reset();
				// Reload DataTables after login
				$('#richiesteTable').DataTable().ajax.reload();
				$('#richiesteInCorsoTable').DataTable().ajax.reload();
			},
			error: function() {
				alert('Errore durante il login. Verifica le credenziali.');
			}
		});
	});

	$('#richiesteTable').DataTable({
		ajax: {
			url: 'http://localhost:8080/WebMarket/rest/requests/byUser',
			type: 'GET',
			beforeSend: function(xhr) {
				xhr.setRequestHeader('Authorization', `Bearer ${bearerToken}`);
			},
			dataSrc: function(json) {
				return json.map(item => {
					const request = item.request;
					
					const features = Object.entries(request.requestedFeatures)
						.map(([key, value]) => `${key}: ${value}`)
						.join(', ');

					const createdAt = new Date(request.createdAt).toLocaleString('it-IT', {
						year: 'numeric',
						month: '2-digit',
						day: '2-digit',
						hour: '2-digit',
						minute: '2-digit',
						second: '2-digit'
					}).replace(',', '');

					const updatedAt = new Date(request.updatedAt).toLocaleString('it-IT', {
						year: 'numeric',
						month: '2-digit',
						day: '2-digit',
						hour: '2-digit',
						minute: '2-digit',
						second: '2-digit'
					}).replace(',', '');

					var status = null;

					switch (request.status) {
						case "UNASSIGNED":
							status = "Non assegnato";
							break;
						case "IN_PROGRESS":
							status = "In corso";
							break;
						case "APPROVED":
							status = "Proposta approvata";
							break;
						case "REJECTED":
							status = "Proposta rifiutata";
							break;
					}

					return {
						category: request.category,
						status: status,
						requestedFeatures: features,
						notes: request.notes,
						createdAt: createdAt,
						updatedAt: updatedAt,
						proposal: request.purchaseProposal,
						url: item.url
					};
				});
			},
			error: function() {
				alert('Effettuare il login per visualizzare i dati');
			}
		},
		columns: [
			{ data: 'category' },
			{ data: 'status' },
			{ data: 'createdAt' },
			{ data: 'updatedAt' },
			{
				data: 'url',
				render: function(data, type, row) {
					return `<button class="btn btn-primary btn-sm" data-category="${row.category}" data-features="${row.requestedFeatures}" data-notes="${row.notes}" data-created="${row.createdAt}" data-proposal='${JSON.stringify(row.proposal || {})}' onclick="handleClick(this)">Dettagli</button>
					<button class="btn btn-danger btn-sm" onclick="deleteRequest('${data}')">Elimina</button>`;
				}
			}
		]
	});

	$('#richiesteInCorsoTable').DataTable({
		ajax: {
			url: 'http://localhost:8080/WebMarket/rest/requests/inProgress',
			type: 'GET',
			beforeSend: function(xhr) {
				xhr.setRequestHeader('Authorization', `Bearer ${bearerToken}`);
			},
			dataSrc: function(json) {
				return json.map(item => {
					const request = item.request;
					
					const features = Object.entries(request.requestedFeatures)
						.map(([key, value]) => `${key}: ${value}`)
						.join(', ');

					const createdAt = new Date(request.createdAt).toLocaleString('it-IT', {
						year: 'numeric',
						month: '2-digit',
						day: '2-digit',
						hour: '2-digit',
						minute: '2-digit',
						second: '2-digit'
					}).replace(',', '');

					const updatedAt = new Date(request.updatedAt).toLocaleString('it-IT', {
						year: 'numeric',
						month: '2-digit',
						day: '2-digit',
						hour: '2-digit',
						minute: '2-digit',
						second: '2-digit'
					}).replace(',', '');

					var status = null;

					switch (request.status) {
						case "UNASSIGNED":
							status = "Non assegnato";
							break;
						case "IN_PROGRESS":
							status = "In corso";
							break;
						case "APPROVED":
							status = "Proposta approvata";
							break;
						case "REJECTED":
							status = "Proposta rifiutata";
							break;
					}

					return {
						category: request.category,
						status: status,
						requestedFeatures: features,
						notes: request.notes,
						createdAt: createdAt,
						updatedAt: updatedAt,
						proposal: request.purchaseProposal,
						url: item.url
					};
				});
			},
			error: function() {
			}
		},
		columns: [
			{ data: 'category' },
			{ data: 'status' },
			{ data: 'createdAt' },
			{ data: 'updatedAt' },
			{
				data: 'url',
				render: function(data, type, row) {
					return `<button class="btn btn-primary btn-sm" onclick="showRequestDetails('${row.category}', '${row.requestedFeatures}', '${row.notes}', '${row.createdAt}')">Dettagli</button>
					<button class="btn btn-danger btn-sm" onclick="deleteRequest('${data}')">Elimina</button>`;
				}
			},
			{
				data: 'url',
				render: function(data, type, row) {
					if (row.proposal != null) {
						return `<button class="btn btn-primary btn-sm" onclick='showProductModal(${JSON.stringify(row.proposal || {})})'>Dettagli</button>
							<button class="btn btn-success btn-sm" onclick="sendApproval('${data}', true)">Approva</button>`;
					} else {
						return 'In corso di definizione';
					}
				}
			}
		]
	});

});

function deleteRequest(url) {
	if (confirm('Sei sicuro di voler eliminare questa richiesta?')) {
		$.ajax({
			url: url,
			type: 'DELETE',
			beforeSend: function(xhr) {
				xhr.setRequestHeader('Authorization', `Bearer ${bearerToken}`);
			},
			success: function() {
				alert('Richiesta eliminata con successo.');
				$('#richiesteTable').DataTable().ajax.reload();
				$('#richiesteInCorsoTable').DataTable().ajax.reload();
			},
			error: function() {
				alert('Errore durante l\'eliminazione della richiesta.');
			}
		});
	}
}

function sendApproval(url, isApproved) {
	$.ajax({
		url: url + '/proposal/approve',
		method: 'PUT',
		beforeSend: function(xhr) {
			xhr.setRequestHeader('Authorization', `Bearer ${bearerToken}`);
		},
		contentType: 'application/json',
		data: JSON.stringify({ approved: isApproved }),
		success: function(response) {
			alert(isApproved ? 'Proposta approvata!' : 'Proposta rifiutata!');
			$('#productModal').modal('hide');
			$('#richiesteTable').DataTable().ajax.reload();
			$('#richiesteInCorsoTable').DataTable().ajax.reload();
		},
		error: function() {
			alert('Errore durante l\'invio della richiesta.');
		}
	});
}

function handleClick(button) {
	const category = button.getAttribute("data-category");
	const features = button.getAttribute("data-features");
	const notes = button.getAttribute("data-notes");
	const createdAt = button.getAttribute("data-created");
	const proposal = JSON.parse(button.getAttribute("data-proposal"));

	showDetails(category, features, notes, createdAt, proposal);
}

function showDetails(category, features, notes, createdAt, proposal) {
	
	$('#modalProposalDetail').html('');
	
	$('#modalCategory').text(category || 'N/A');
	$('#modalFeatures').text(features || 'N/A');
	$('#modalNotes').text(notes || 'N/A');
	$('#modalCreatedAt').text(createdAt || 'N/A');

	if (proposal && JSON.stringify(proposal) !== "{}") {
		$('#modalProposal').text('');
		$('#modalProposalDetail').append(`<p><strong>- Nome Produttore:</strong> <span>'${proposal.manufacturerName || 'N/A'}'</span></p>`
			+ `<p><strong>- Nome Prodotto:</strong> <span>'${proposal.productName || 'N/A'}'</span></p>`
			+ `<p><strong>- Codice Prodotto:</strong> <span>'${proposal.productCode || 'N/A'}'</span></p>`
			+ `<p><strong>- Prezzo:</strong> <span>€'${proposal.price || 'N/A'}'</span></p>`
			+ `<p><strong>- URL:</strong> <a href=# target='_blank'>'${proposal.url || 'N/A'}'</a></p>`
			+ `<p><strong>- Note:</strong> <span>'${proposal.notes || 'N/A'}'</span></p>`
			+ `<p><strong>- Motivazione:</strong> <span>'${proposal.motivation || 'N/A'}'</span></p>`);
	} else {
		$('#modalProposal').text('Nessuna proposta');
	}
	$('#detailsModal').modal('show');
}

function showRequestDetails(category, features, notes, createdAt) {
	
	$('#modalProposalDetail').html('');
    
	$('#modalCategory').text(category || 'N/A');
	$('#modalFeatures').text(features || 'N/A');
	$('#modalNotes').text(notes || 'N/A');
	$('#modalCreatedAt').text(createdAt || 'N/A');
	$('#modalProposal').text('Inserita nella sezione Proposta d\'acquisto');
	
	$('#detailsModal').modal('show');
}

function showProductModal(data) {
	const createdAt = new Date(data.createdAt).toLocaleString('it-IT', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit',
		second: '2-digit'
	}).replace(',', '');

	const updatedAt = new Date(data.updatedAt).toLocaleString('it-IT', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit',
		second: '2-digit'
	}).replace(',', '');

	$('#modalManufacturerName').text(data.manufacturerName || 'N/A');
	$('#modalProductName').text(data.productName || 'N/A');
	$('#modalProductCode').text(data.productCode || 'N/A');
	$('#modalPrice').text(data.price ? `€${data.price}` : 'N/A');
	$('#modalUrl').attr('href', data.url || '#').text(data.url || 'N/A');
	$('#modalProposalNotes').text(data.notes || 'N/A');
	$('#modalMotivation').text(data.motivation || 'N/A');
	$('#modalProductCreatedAt').text(createdAt || 'N/A');
	$('#modalProductUpdatedAt').text(updatedAt || 'N/A');

	$('#productModal').modal('show');
}

