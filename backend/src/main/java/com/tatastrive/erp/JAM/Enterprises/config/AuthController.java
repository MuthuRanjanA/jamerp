package com.tatastrive.erp.JAM.Enterprises.config;

import java.util.Map;

import com.tatastrive.erp.JAM.Enterprises.Entity.AppUser;
import com.tatastrive.erp.JAM.Enterprises.Repository.AppUserRepository;
import com.tatastrive.erp.JAM.Enterprises.Response.ApiResponse;
import com.tatastrive.erp.JAM.Enterprises.Role;
import com.tatastrive.erp.JAM.Enterprises.Service.ServiceImplementation.AuthServiceImplementation;
import com.tatastrive.erp.JAM.Enterprises.dto.AuthResponse;
import com.tatastrive.erp.JAM.Enterprises.dto.ChangePasswordRequest;
import com.tatastrive.erp.JAM.Enterprises.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	JwtService jwtService;
	@Autowired
	private AppUserRepository userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private AuthServiceImplementation authService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
			@RequestBody Map<String, String> request) {

		String email = request.get("email");
		String password = request.get("password");

		Authentication authentication =
				authManager.authenticate(
						new UsernamePasswordAuthenticationToken(email, password)
				);

		AppUser user =
				userRepo.findByEmail(email)
						.orElseThrow(() ->
								new RuntimeException(
										"User not found"
								)
						);

		String token =
				jwtService.generateToken(email);

		Long employeeId = null;
		String employeeName = null;

		if (user.getEmployee() != null) {
			employeeId =
					user.getEmployee().getEmployeeId();

			employeeName =
					user.getEmployee().getEmployeeName();
		}

		LoginResponse response =
				new LoginResponse(
						token,
						user.getRole().name(),
						employeeId,
						employeeName,
						user.isTemporaryPassword()
				);

		return ResponseEntity.ok(response);
	}


	@PostMapping("/change-temporary-password")
	public ResponseEntity<ApiResponse> changeTemporaryPassword(
			@RequestBody ChangePasswordRequest request) {

		try {
			authService.changeTemporaryPassword(request);

			return ResponseEntity.ok(
					new ApiResponse(
							"Password changed successfully",
							null
					)
			);

		} catch (Exception exception) {
			return ResponseEntity.badRequest()
					.body(
							new ApiResponse(
									exception.getMessage(),
									null
							)
					);
		}
	}
}
