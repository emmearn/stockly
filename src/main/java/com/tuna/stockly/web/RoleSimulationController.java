package com.tuna.stockly.web;

import java.net.URI;

import com.tuna.stockly.service.RoleSimulationService;
import com.tuna.stockly.service.SimulatedRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoleSimulationController {

	private final RoleSimulationService roleSimulationService;

	public RoleSimulationController(RoleSimulationService roleSimulationService) {
		this.roleSimulationService = roleSimulationService;
	}

	@PostMapping("/role-simulation")
	public String switchRole(@RequestParam SimulatedRole role, HttpSession session, HttpServletRequest request) {
		roleSimulationService.setCurrentRole(session, role);
		return "redirect:" + redirectTarget(request);
	}

	private String redirectTarget(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		if (referer == null || referer.isBlank()) {
			return "/stock";
		}
		try {
			URI uri = URI.create(referer);
			String path = uri.getPath();
			if (path == null || path.isBlank()) {
				return "/stock";
			}
			String query = uri.getQuery();
			return query == null ? path : path + "?" + query;
		}
		catch (IllegalArgumentException ex) {
			return "/stock";
		}
	}
}
