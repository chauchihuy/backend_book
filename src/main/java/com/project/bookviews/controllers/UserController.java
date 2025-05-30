package com.project.bookviews.controllers;

import com.project.bookviews.components.LocalizationUtils;
import com.project.bookviews.dtos.*;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.exceptions.InvalidPasswordException;
import com.project.bookviews.models.Token;
import com.project.bookviews.models.User;
import com.project.bookviews.responses.LoginResponse;
import com.project.bookviews.responses.RegisterResponse;
import com.project.bookviews.responses.UserListResponse;
import com.project.bookviews.responses.UserResponse;
import com.project.bookviews.services.token.ITokenService;
import com.project.bookviews.services.user.IUserService;
import com.project.bookviews.utils.MessageKeys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.project.bookviews.dtos.UserDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;
    private final ITokenService tokenService;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUser(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        try {
            // Tạo Pageable từ thông tin trang và giới hạn
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    //Sort.by("createdAt").descending()
                    Sort.by("id").descending()
            );
            Page<UserResponse> userPage = userService.findAll(keyword, pageRequest)
                                .map(UserResponse::fromUser);

            // Lấy tổng số trang
            int totalPages = userPage.getTotalPages();
            List<UserResponse> userResponses = userPage.getContent();
            return ResponseEntity.ok(UserListResponse
                    .builder()
                    .users(userResponses)
                    .totalPages(totalPages)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/register")
    //can we register an "admin" user ?
    public ResponseEntity<RegisterResponse> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result) {
        RegisterResponse registerResponse = new RegisterResponse();

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            registerResponse.setMessage(errorMessages.toString());
            return ResponseEntity.badRequest().body(registerResponse);
        }

        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            registerResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH));
            return ResponseEntity.badRequest().body(registerResponse);
        }

        try {
            User user = userService.createUser(userDTO);
            registerResponse.setMessage("Đăng ký tài khoản thành công");
            registerResponse.setUser(user);
            return ResponseEntity.ok(new RegisterResponse());
        } catch (Exception e) {
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(
//            @Valid @RequestBody UserLoginDTO userLoginDTO,
//            HttpServletRequest request
//    ) {
//        // Kiểm tra thông tin đăng nhập và sinh token
//        try {
//            String token = userService.login(
//                    userLoginDTO.getPhoneNumber(),
//                    userLoginDTO.getPassword(),
//                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId()
//            );
//            if (token==null)
//            {
//                return ResponseEntity.badRequest().body(
//                        LoginResponse.builder()
//                                .message("Tài khoản bị khóa. Vui lòng liên hệ với quản trị viên.")
//                                .build()
//                );
//            }
//
//
//            User userDetail = userService.getUserDetailsFromToken(token);
//
//            System.out.println("User active status: " + userDetail.isActive());
//            // Kiểm tra trạng thái tài khoản
//            if (!userDetail.isActive()) {
//                return ResponseEntity.badRequest().body(
//                        LoginResponse.builder()
//                                .message("Tài khoản bị khóa. Vui lòng liên hệ với quản trị viên.")
//                                .build()
//                );
//            }
//            //xác định thiết bị
//            String userAgent = request.getHeader("User-Agent");
//            Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));
//
//            // Trả về token trong response
//            return ResponseEntity.ok(LoginResponse.builder()
//                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
//                            .token(jwtToken.getToken())
//                            .tokenType(jwtToken.getTokenType())
//                            .refreshToken(jwtToken.getRefreshToken())
//                            .username(userDetail.getUsername())
//                            .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
//                            .id(userDetail.getId())
//                            .active(userDetail.isActive())
//
//                    .build());
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(
//                    LoginResponse.builder()
//                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
//                            .build()
//            );
//        }
//    }
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody UserLoginDTO userLoginDTO,
        HttpServletRequest request
) {
    try {
        // Xác thực thông tin đăng nhập và sinh token
        String token = userService.login(
                userLoginDTO.getPhoneNumber(),
                userLoginDTO.getPassword(),
                userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId()
        );

        // Lấy chi tiết người dùng từ token
        User userDetail = userService.getUserDetailsFromToken(token);

        // Kiểm tra trạng thái tài khoản
        if (userDetail.isActive() !=true) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message("Tài khoản bị khóa. Vui lòng liên hệ với quản trị viên.")
                            .build()
            );
        }

        // Xác định thiết bị
        String userAgent = request.getHeader("User-Agent");
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        // Trả về thông tin token trong phản hồi
        return ResponseEntity.ok(LoginResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .id(userDetail.getId())
                .active(userDetail.isActive()) // Đảm bảo 'true' hoặc 'false' được trả về
                .build());
    } catch (DataNotFoundException e) {
        // Xử lý ngoại lệ và trả về thông báo lỗi nếu tài khoản bị khóa
        return ResponseEntity.badRequest().body(
                LoginResponse.builder()
                        .message(e.getMessage()) // Sử dụng thông báo từ ngoại lệ
                        .build()
        );
    } catch (Exception e) {
        // Xử lý lỗi khác và trả về thông báo lỗi chung
        return ResponseEntity.badRequest().body(
                LoginResponse.builder()
                        .message("Đăng nhập không thành công. Vui lòng kiểm tra thông tin đăng nhập của bạn.")
                        .build()
        );
    }
}
    @PostMapping("/login/admin")
    public ResponseEntity<LoginResponse> loginAdmin(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    )
    {
        // Kiểm tra thông tin đăng nhập và sinh token
        try {
            String token = userService.login(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword(),
                    userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId()
            );
            String userAgent = request.getHeader("User-Agent");
            User userDetail = userService.getUserDetailsFromToken(token);
            Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

            // Trả về token trong response
            return ResponseEntity.ok(LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO
    ) {
        try {
            User userDetail = userService.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
            Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Refresh token successfully")
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                    .id(userDetail.getId())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build()
            );
        }
    }
    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }
    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<UserResponse> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/details/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            // Ensure that the user making the request matches the user being updated
            if (user.getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updatedUser = userService.updateUser(userId, updatedUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> resetPassword(@Valid @PathVariable long userId){
        try {
            String newPassword = UUID.randomUUID().toString().substring(0, 5); // Tạo mật khẩu mới
            userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(newPassword);
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body("Invalid password");
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> blockOrEnable(
            @Valid @PathVariable long userId,
            @Valid @PathVariable int active
    ) {
        try {
            userService.blockOrEnable(userId, active > 0);
            String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
            return ResponseEntity.ok().body(message);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
