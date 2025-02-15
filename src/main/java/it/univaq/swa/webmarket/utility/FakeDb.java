package it.univaq.swa.webmarket.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import it.univaq.swa.webmarket.exceptions.NotFoundException;
import it.univaq.swa.webmarket.exceptions.WebMarketException;
import it.univaq.swa.webmarket.model.Category;
import it.univaq.swa.webmarket.model.PurchaseRequest;
import it.univaq.swa.webmarket.model.Status;
import it.univaq.swa.webmarket.model.User;
import it.univaq.swa.webmarket.model.UserType;

public class FakeDb {

	private static IDGenerator generator = IDGenerator.getGenerator();
	private static Map<Long, PurchaseRequest> requests = new HashMap<>();

	private static List<String> computerFeatures = new ArrayList<>(Arrays.asList("CPU", "RAM", "HD", "Graphics Card"));
	private static Category computerCategory = new Category("Computer", computerFeatures);

	private static List<String> notebookFeatures = new ArrayList<>(Arrays.asList("Display", "Resolution"));
	private static Category notebookCategory = new Category("Notebook", computerCategory, notebookFeatures);

	private static List<Category> categories = new ArrayList<>(Arrays.asList(computerCategory, notebookCategory));

	private static Map<String, Category> categoriesMap = categories.stream()
			.collect(Collectors.toMap(k -> k.getName(), Function.identity()));

	private static User purchaser1 = new User(1L, "purchaser1", "purch1", "purch1@user.com", UserType.PURCHASER);
	private static User technician1 = new User(2L, "technician1", "tech1", "tech1@user.com", UserType.TECHNICIAN);
	private static List<User> userList = new ArrayList<>(Arrays.asList(purchaser1, technician1));

	private static Map<String, User> userMap = userList.stream()
			.collect(Collectors.toMap(User::getUsername, Function.identity()));

	private static Map<Long, User> techniciansMap = userList.stream()
			.filter(u -> u.getUserType() == UserType.TECHNICIAN)
			.collect(Collectors.toMap(User::getId, Function.identity()));

	public static Long addPurchaseRequest(PurchaseRequest request) throws WebMarketException {

		Map<String, String> defaultFeatures = null;

		if (Objects.isNull(request.getCategory())) {
			throw new WebMarketException("Wrong request format");
		}

		defaultFeatures = categoriesMap.get(request.getCategory()).getFeatures().stream()
				.collect(Collectors.toMap(Function.identity(), k -> "indifferent", (k1, k2) -> k2));

		defaultFeatures.putAll(request.getRequestedFeatures());

		request.setRequestedFeatures(defaultFeatures);

		request.setId(generator.generateID());

		requests.put(request.getId(), request);

		return request.getId();
	}

	public static PurchaseRequest getPurchaseRequest(Long id) throws NotFoundException {
		PurchaseRequest request = requests.get(id);

		if (Objects.isNull(request)) {
			throw new NotFoundException("Purchase Requst not found");
		}

		return request;
	}

	public static void deletePurchaseRequest(Long id) throws NotFoundException {
		if (Objects.isNull(requests.remove(id)))
			throw new NotFoundException("Purchase Requst not found");
	}

	public static PurchaseRequest updatePurchaseRequest(PurchaseRequest request) {
		return requests.put(request.getId(), request);
	}

	public static User getTechnician(Long technicianId) throws NotFoundException {

		User technician = techniciansMap.get(technicianId);

		if (Objects.isNull(technician)) {
			throw new NotFoundException("Technician user not found");
		}

		return technician;
	}

	public static List<PurchaseRequest> getRequestsByStatus(Status status) {
		List<PurchaseRequest> requestList = new ArrayList<>(requests.values());
		requestList.removeIf(r -> r.getStatus() != status);
		return requestList;
	}

	public static List<PurchaseRequest> getRequestsByPurchaser(String username) {
		User purchaser = userMap.get(username);
		List<PurchaseRequest> requestList = new ArrayList<>(requests.values());
		requestList.removeIf(r -> r.getPurchaser() != purchaser);
		return requestList;
	}

	public static List<PurchaseRequest> getInProgressRequestsByPurchaser(String username) {
		User purchaser = userMap.get(username);
		List<PurchaseRequest> requestList = new ArrayList<>(requests.values());
		requestList.removeIf(r -> r.getStatus() != Status.IN_PROGRESS);
		requestList.removeIf(r -> r.getPurchaser() != purchaser);
		return requestList;
	}

	public static List<PurchaseRequest> getRequestsByTechnician(String username) {
		User technician = userMap.get(username);
		List<PurchaseRequest> requestList = new ArrayList<>(requests.values());
		requestList.removeIf(r -> r.getTechnician() != technician);
		return requestList;
	}

	public static Boolean authUser(String username, String password) {
		User user = userMap.get(username);

		return (!Objects.isNull(user) && user.getPassword().equals(password));
	}

	public static User getUserByUsername(String username) {
		return userMap.get(username);
	}

	public static boolean isUserOfType(String username, UserType role) {

		return userMap.get(username).getUserType().equals(role);
	}

}
